package cc.bamboo.module.project.service.projectorder;

import cc.bamboo.framework.common.pojo.PageResult;
import cc.bamboo.framework.common.util.object.BeanUtils;
import cc.bamboo.framework.common.util.servlet.ServletUtils;
import cc.bamboo.framework.mybatis.core.query.LambdaQueryWrapperX;
import cc.bamboo.framework.redis.service.RedisService;
import cc.bamboo.module.infra.api.file.FileApi;
import cc.bamboo.module.project.controller.app.orderbalance.vo.AppAssetByCurrencyRespVO;
import cc.bamboo.module.project.controller.app.projectinfo.vo.AppProjectPaymentInfoRespVO;
import cc.bamboo.module.project.controller.app.projectorder.vo.*;
import cc.bamboo.module.project.dal.dataobject.projectinfo.ProjectInfoDO;
import cc.bamboo.module.project.dal.dataobject.projectorder.ProjectOrderDO;
import cc.bamboo.module.project.dal.dataobject.projectorderbalance.ProjectOrderBalanceDO;
import cc.bamboo.module.project.dal.dataobject.userchain.UserChainDO;
import cc.bamboo.module.project.dal.mysql.orderdailyincome.OrderDailyIncomeMapper;
import cc.bamboo.module.project.dal.mysql.projectdividendperiod.ProjectDividendPeriodMapper;
import cc.bamboo.module.project.dal.mysql.projectinfo.ProjectInfoMapper;
import cc.bamboo.module.project.dal.mysql.projectorder.ProjectOrderMapper;
import cc.bamboo.module.project.dal.mysql.projectorderbalance.ProjectOrderBalanceMapper;
import cc.bamboo.module.project.dal.mysql.userchain.UserChainMapper;
import cc.bamboo.module.project.enums.*;
import cc.bamboo.module.project.mq.message.OrderCancelMessage;
import cc.bamboo.module.project.mq.producer.OrderCancelProducer;
import cc.bamboo.module.project.service.chainoperation.ChainOperationTaskService;
import cc.bamboo.module.project.util.Web3SignUtils;
import cc.bamboo.module.system.api.mail.MailSendApi;
import cc.bamboo.module.system.api.mail.dto.MailCodeUseReqDTO;
import cc.bamboo.module.system.api.sms.SmsCodeApi;
import cc.bamboo.module.system.api.sms.dto.code.SmsCodeUseReqDTO;
import cc.bamboo.module.system.enums.sms.SmsSceneEnum;
import cc.bamboo.module.user.api.userinfo.UserInfoApi;
import cc.bamboo.module.user.api.userinfo.dto.UserInfoRespDTO;
import cc.bamboo.module.user.enums.ChainAddressStatusEnum;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.util.StringUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static cc.bamboo.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cc.bamboo.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;
import static cc.bamboo.module.project.enums.ApiConstants.*;
import static cc.bamboo.module.project.enums.ErrorCodeConstants.*;
import static cc.bamboo.module.user.enums.ErrorCodeConstants.CHAIN_ADDRESS_NOT_EXISTS;
import static cc.bamboo.module.user.enums.ErrorCodeConstants.CHAIN_ADDRESS_STATUS_ERROR;

/**
 * 用户端 - 项目订单 Service 实现类
 *
 * @author Swolf
 */
@Service
@Validated
@Slf4j
public class AppProjectOrderServiceImpl implements AppProjectOrderService {

    @Resource
    private ProjectInfoMapper projectInfoMapper;

    @Resource
    private ProjectOrderMapper projectOrderMapper;

    @Resource
    private ProjectOrderService projectOrderService;

    @Resource
    private UserInfoApi userInfoApi;

    @Resource
    private FileApi fileApi;

    @Resource
    private SmsCodeApi smsCodeApi;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private OrderCancelProducer orderCancelProducer;

    @Resource
    private ChainOperationTaskService chainOperationTaskService;

    @Resource
    private UserChainMapper userChainMapper;

    @Resource
    private ProjectOrderBalanceMapper projectOrderBalanceMapper;

    @Resource
    private RedisService redisService;

    @Resource
    private MailSendApi mailSendApi;

    @Value("${order.delay-time}")
    private long DELAY_TIME;
    /**
     * Redis 缓存 Key 前缀：订单签名信息
     * 格式：project:order:signature:{userId}:{orderNo}
     * 值：{projectId}:{chainAddress}:{quantity}
     * 过期时间：30分钟
     */
    private static final String REDIS_KEY_ORDER_SIGNATURE = "project:order:signature:%s:%s";
    private static final long REDIS_EXPIRE_MINUTES = 30;

    @Override
    public AppGenerateSignatureRespVO generateSignature(Long userId, Long projectId, String chainAddress,
            Integer quantity) {
        log.info("[generateSignature] 开始生成签名，用户ID: {}, 项目ID: {}, 钱包地址: {}, 购买数量: {}",
                userId, projectId, chainAddress, quantity);

        // 1. 验证钱包地址格式
        if (!Web3SignUtils.validateWalletAddress(chainAddress)) {
            log.warn("[generateSignature] 钱包地址格式无效，钱包地址: {}", chainAddress);
            throw exception(INVALID_WALLET_ADDRESS);
        }

        // 2. 查询项目并验证存在性
        ProjectInfoDO project = projectInfoMapper.selectById(projectId);
        if (project == null) {
            log.warn("[generateSignature] 项目不存在，项目ID: {}", projectId);
            throw exception(PROJECT_NOT_FOUND);
        }

        // 3. 验证项目状态（必须是上线通过且上架中）
        if (!AuditStatusEnum.APPROVED.getStatus().equals(project.getAuditStatus())
                || !Integer.valueOf(1).equals(project.getSellStatus())) {
            log.warn("[generateSignature] 项目未上架，项目ID: {}, 审核状态: {}, 上架状态: {}",
                    projectId, project.getAuditStatus(), project.getSellStatus());
            throw exception(PROJECT_NOT_ON_SALE);
        }

        // 4. 验证购买数量
        // 4.1 验证购买数量大于0（已通过 @Min 注解验证）

        // 4.2 验证购买数量不超过剩余库存
        if (quantity > project.getRemainingQuantity()) {
            log.warn("[generateSignature] 购买数量超过剩余库存，购买数量: {}, 剩余库存: {}",
                    quantity, project.getRemainingQuantity());
            throw exception(INSUFFICIENT_STOCK);
        }

        // 4.3 验证购买数量满足起购量要求
        if (quantity < project.getMinimumPurchase()) {
            log.warn("[generateSignature] 购买数量低于起购量，购买数量: {}, 起购量: {}",
                    quantity, project.getMinimumPurchase());
            throw exception(BELOW_MINIMUM_PURCHASE);
        }

        // 5. 预生成订单号
        // 格式：ORD + 年月日时分秒 + 6位随机数
        String orderNo = generateOrderNo();
        log.info("[generateSignature] 预生成订单号: {}", orderNo);

        // 6. 将订单信息缓存到 Redis（用于下单时验证）
        String redisKey = String.format(REDIS_KEY_ORDER_SIGNATURE, userId, orderNo);
        String redisValue = projectId + ":" + chainAddress + ":" + quantity;
        stringRedisTemplate.opsForValue().set(redisKey, redisValue, REDIS_EXPIRE_MINUTES, TimeUnit.MINUTES);
        log.info("[generateSignature] 订单信息已缓存到Redis，key: {}, value: {}, 过期时间: {}分钟",
                redisKey, redisValue, REDIS_EXPIRE_MINUTES);

        // 7. 生成签名字符串
        String signature = Web3SignUtils.generateSignature(chainAddress, orderNo, quantity);

        log.info("[generateSignature] 签名生成成功，订单号: {}, 签名: {}", orderNo, signature);

        // 8. 返回签名信息
        return AppGenerateSignatureRespVO.builder()
                .signature(signature)
                .orderNo(orderNo)
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProjectOrderDO createOrder(Long userId, Long projectId, String chainAddress, Integer quantity,
            String orderNo) {
        log.info("[createOrder] 开始创建订单，用户ID: {}, 项目ID: {}, 钱包地址: {}, 购买数量: {}, 订单号: {}",
                userId, projectId, chainAddress, quantity, orderNo);

        // 1. 从 Redis 验证订单信息（防止篡改）
        String redisKey = String.format(REDIS_KEY_ORDER_SIGNATURE, userId, orderNo);
        String cachedValue = stringRedisTemplate.opsForValue().get(redisKey);

        if (cachedValue == null) {
            log.warn("[createOrder] 订单信息未找到或已过期，用户ID: {}, 订单号: {}", userId, orderNo);
            throw exception(ORDER_NOT_FOUND);
        }

        // 2. 解析并验证缓存的订单信息
        String[] parts = cachedValue.split(":");
        if (parts.length != 3) {
            log.error("[createOrder] Redis缓存数据格式错误，用户ID: {}, 订单号: {}, 缓存值: {}",
                    userId, orderNo, cachedValue);
            throw exception(ORDER_NOT_FOUND);
        }

        Long cachedProjectId = Long.parseLong(parts[0]);
        String cachedChainAddress = parts[1];
        Integer cachedQuantity = Integer.parseInt(parts[2]);

        // 3. 验证订单信息是否被篡改
        if (!cachedProjectId.equals(projectId) ||
                !cachedChainAddress.equals(chainAddress) ||
                !cachedQuantity.equals(quantity)) {
            log.warn("[createOrder] 订单信息不匹配，用户ID: {}, 订单号: {}, 缓存: {}:{}:{}, 请求: {}:{}:{}",
                    userId, orderNo, cachedProjectId, cachedChainAddress, cachedQuantity,
                    projectId, chainAddress, quantity);
            throw exception(ORDER_NOT_BELONG_TO_USER);
        }

        log.info("[createOrder] 订单信息验证通过，用户ID: {}, 订单号: {}", userId, orderNo);

        // 4. 验证项目存在性和状态（必须是"出售中"）
        ProjectInfoDO project = projectInfoMapper.selectById(projectId);
        if (project == null) {
            log.warn("[createOrder] 项目不存在，项目ID: {}", projectId);
            throw exception(PROJECT_NOT_FOUND);
        }

        if (!AuditStatusEnum.APPROVED.getStatus().equals(project.getAuditStatus())
                || !Integer.valueOf(1).equals(project.getSellStatus())) {
            log.warn("[createOrder] 项目未上架，项目ID: {}, 审核状态: {}, 上架状态: {}",
                    projectId, project.getAuditStatus(), project.getSellStatus());
            throw exception(PROJECT_NOT_ON_SALE);
        }

        // 5. 验证购买数量（满足起购量）
        if (quantity < project.getMinimumPurchase()) {
            log.warn("[createOrder] 购买数量低于起购量，购买数量: {}, 起购量: {}",
                    quantity, project.getMinimumPurchase());
            throw exception(BELOW_MINIMUM_PURCHASE);
        }

        // 6. 使用乐观锁更新库存
        int updateCount = projectInfoMapper.updateStockWithOptimisticLock(projectId, quantity);

        // 7. 检查更新结果（0行表示库存不足）
        if (updateCount == 0) {
            log.warn("[createOrder] 库存不足或并发更新失败，项目ID: {}, 购买数量: {}", projectId, quantity);
            throw exception(INSUFFICIENT_STOCK);
        }

        log.info("[createOrder] 库存扣减成功，项目ID: {}, 扣减数量: {}", projectId, quantity);

        // 8. 计算订单总金额（数量 × 单价）
        BigDecimal totalAmount = project.getIssueUnitPrice().multiply(new BigDecimal(quantity));

        // 9. 设置订单过期时间（创建时间 + 24小时）
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expireTime = now.plusHours(24);

        // 10. 创建订单记录（状态为"待支付"）
        ProjectOrderDO order = ProjectOrderDO.builder()
                .orderNo(orderNo)
                .applyDate(now)
                .userId(userId)
                .orderStatus(OrderStatusEnum.PENDING_PAYMENT.getStatus())
                .projectId(projectId)
                .projectName(project.getProjectName())
                .subscribeQuantity(quantity)
                .price(project.getIssueUnitPrice())
                .totalAmount(totalAmount)
                .chainAddress(chainAddress)
                .expireTime(expireTime)
                .payType(OrderPayEnum.BANK_PAY.getType()) // 默认支付方式
                .build();

        projectOrderMapper.insert(order);

        // 11. 删除 Redis 缓存（订单已创建，缓存信息不再需要）
        stringRedisTemplate.delete(redisKey);
        log.info("[createOrder] 已删除Redis缓存，key: {}", redisKey);

        // 12. 发送延时消息（30分钟后自动取消订单）
        OrderCancelMessage cancelMessage = new OrderCancelMessage();
        cancelMessage.setOrderId(order.getId());
        cancelMessage.setOrderNo(orderNo);
        cancelMessage.setProjectId(projectId);
        cancelMessage.setQuantity(quantity);
        cancelMessage.setUserId(userId);

        orderCancelProducer.sendOrderCancelMessage(cancelMessage);
        log.info("[createOrder] 已发送订单取消延时消息，订单ID: {}, 订单号: {}", order.getId(), orderNo);

        log.info("[createOrder] 订单创建成功，订单ID: {}, 订单号: {}, 总金额: {}, 过期时间: {}",
                order.getId(), orderNo, totalAmount, expireTime);

        return order;
    }

    @Override
    public ProjectOrderDO createOrder(Long userId, Long projectId, Long addressId, Integer quantity) {
        log.info("[createOrder] 开始创建订单，用户ID: {}, 项目ID: {}, 钱包地址: {}, 购买数量: {}",
                userId, projectId, addressId, quantity);
        // 查询链地址
        UserChainDO userChain = userChainMapper.selectById(addressId);
        if (userChain == null || !userChain.getUserId().equals(userId)) {
            log.warn("[createOrder] 链地址不存在，链地址ID: {}", addressId);
            throw exception(CHAIN_ADDRESS_NOT_EXISTS);
        }
        // 如果不是可用状态也不行
        if (!ChainAddressStatusEnum.SUCCESS.getStatus().equals(userChain.getChainStatus())) {
            log.warn("[createOrder] 链地址状态错误，链地址ID: {}", addressId);
            throw exception(CHAIN_ADDRESS_STATUS_ERROR);
        }

        String chainAddress = userChain.getChainAddress();

        String orderNo = generateOrderNo();
        // 4. 验证项目存在性和状态（必须是"出售中"）
        ProjectInfoDO project = projectInfoMapper.selectById(projectId);
        if (project == null) {
            log.warn("[createOrder] 项目不存在，项目ID: {}", projectId);
            throw exception(PROJECT_NOT_FOUND);
        }

        // 验证项目审核通过且上架
        if (!AuditStatusEnum.APPROVED.getStatus().equals(project.getAuditStatus())
                || !ApiConstants.SELL_STATUS_ON_SALE.equals(project.getSellStatus())) {
            log.warn("[createOrder] 项目未上架，项目ID: {}, 审核状态: {}, 上架状态: {}",
                    projectId, project.getAuditStatus(), project.getSellStatus());
            throw exception(PROJECT_NOT_ON_SALE);
        }

        // 验证购买时间：根据资产类型判断
        LocalDate now = LocalDate.now();

        if (ApiConstants.ASSET_TYPE_OPEN_FUND.equals(project.getProjectType())) {
            // 开放型基金：可购买至锁定结束期
            if (project.getLockEndTime() != null && now.isAfter(project.getLockEndTime())) {
                log.warn("[createOrder] 开放型基金已过锁定结束期，项目ID: {}, 锁定结束期: {}, 当前日期: {}",
                        projectId, project.getLockEndTime(), now);
                throw exception(PROJECT_NOT_ON_SALE);
            }
        } else if (ApiConstants.ASSET_TYPE_CLOSED_FUND.equals(project.getProjectType())) {
            // 封闭型基金：可购买至锁定开始期
            if (project.getLockStartTime() != null && now.isAfter(project.getLockStartTime())) {
                log.warn("[createOrder] 封闭型基金已过锁定开始期，项目ID: {}, 锁定开始期: {}, 当前日期: {}",
                        projectId, project.getLockStartTime(), now);
                throw exception(PROJECT_NOT_ON_SALE);
            }
        }

        // 5. 验证购买数量（满足起购量）
        if (quantity < project.getMinimumPurchase()) {
            log.warn("[createOrder] 购买数量低于起购量，购买数量: {}, 起购量: {}",
                    quantity, project.getMinimumPurchase());
            throw exception(BELOW_MINIMUM_PURCHASE);
        }

        // 6. 使用乐观锁更新库存
        int updateCount = projectInfoMapper.updateStockWithOptimisticLock(projectId, quantity);

        // 7. 检查更新结果（0行表示库存不足）
        if (updateCount == 0) {
            log.warn("[createOrder] 库存不足或并发更新失败，项目ID: {}, 购买数量: {}", projectId, quantity);
            throw exception(INSUFFICIENT_STOCK);
        }

        log.info("[createOrder] 库存扣减成功，项目ID: {}, 扣减数量: {}", projectId, quantity);

        // 8. 计算订单总金额（数量 × 单价）
        BigDecimal totalAmount = project.getIssueUnitPrice().multiply(new BigDecimal(quantity));

        // 9. 设置订单过期时间（创建时间 + 24小时）
        LocalDateTime nowDateTime = LocalDateTime.now();
        LocalDateTime expireTime = nowDateTime.plusSeconds(DELAY_TIME / 1000);

        // 10. 验证支付银行信息是否存在
        AppProjectPaymentInfoRespVO respVO = projectInfoMapper.getPublisherBankInfoByTenantId(project.getTenantId());
        if (respVO == null) {
            log.warn("[createOrder] 发行商银行信息不存在，租户ID: {}", project.getTenantId());
            throw exception(PUBLISHER_BANK_INFO_NOT_FOUND);
        }
        String bankJson = JSON.toJSONString(respVO);

        // 10. 创建订单记录（状态为"待支付"）
        ProjectOrderDO order = ProjectOrderDO.builder()
                .orderNo(orderNo)
                .applyDate(nowDateTime)
                .userId(userId)
                .orderStatus(OrderStatusEnum.PENDING_PAYMENT.getStatus())
                .projectId(projectId)
                .projectName(project.getProjectName())
                .subscribeQuantity(quantity)
                .price(project.getIssueUnitPrice())
                .totalAmount(totalAmount)
                .chainAddress(chainAddress)
                .expireTime(expireTime)
                .payType(OrderPayEnum.BANK_PAY.getType()) // 默认支付方式
                .payBank(bankJson)
                .investmentCurrency(project.getInvestmentCurrency())
                .projectConfigType(project.getProjectConfigType())
                .earningCurrency(project.getEarningCurrency())
                .build();
        order.setTenantId(project.getTenantId());
        projectOrderMapper.insert(order);

        // 12. 发送延时消息（30分钟后自动取消订单）
        OrderCancelMessage cancelMessage = new OrderCancelMessage();
        cancelMessage.setOrderId(order.getId());
        cancelMessage.setOrderNo(orderNo);
        cancelMessage.setProjectId(projectId);
        cancelMessage.setQuantity(quantity);
        cancelMessage.setUserId(userId);

        orderCancelProducer.sendOrderCancelMessage(cancelMessage);
        log.info("[createOrder] 已发送订单取消延时消息，订单ID: {}, 订单号: {}", order.getId(), orderNo);

        log.info("[createOrder] 订单创建成功，订单ID: {}, 订单号: {}, 总金额: {}, 过期时间: {}",
                order.getId(), orderNo, totalAmount, expireTime);

        return order;
    }

    /**
     * 生成订单号
     * 格式：ORD + 年月日时分秒(14位) + 6位随机数
     * 例如：ORD20231201123456ABCDEF
     *
     * @return 订单号
     */
    private String generateOrderNo() {
        // 获取当前时间戳（年月日时分秒）
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

        // 生成6位随机字符串（使用UUID的前6位）
        String randomStr = IdUtil.fastSimpleUUID().substring(0, 6).toUpperCase();

        return "ORD" + timestamp + randomStr;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void payOrder(Long userId, AppPayOrderReqVO reqVO) {
        Long orderId = reqVO.getOrderId();
        MultipartFile payVoucher = reqVO.getPayVoucher();
        String contractNo = reqVO.getContractNo();
        String smsCode = reqVO.getSmsCode();

        log.info("[payOrder] 开始支付确认，用户ID: {}, 订单ID: {}, 合同号: {}", userId, orderId, contractNo);

        // 1. 验证订单存在性
        ProjectOrderDO order = projectOrderMapper.selectById(orderId);
        if (order == null) {
            log.warn("[payOrder] 订单不存在，订单ID: {}", orderId);
            throw exception(ORDER_NOT_FOUND);
        }

        // 2. 验证订单状态（必须是"待支付"）
        if (!OrderStatusEnum.PENDING_PAYMENT.getStatus().equals(order.getOrderStatus())) {
            log.warn("[payOrder] 订单状态不是待支付，订单ID: {}, 订单状态: {}", orderId, order.getOrderStatus());
            throw exception(ORDER_NOT_PENDING);
        }

        // 3. 验证订单归属（必须属于当前用户）
        if (!userId.equals(order.getUserId())) {
            log.warn("[payOrder] 订单不属于当前用户，订单ID: {}, 订单用户ID: {}, 当前用户ID: {}",
                    orderId, order.getUserId(), userId);
            throw exception(ORDER_NOT_BELONG_TO_USER);
        }

        // 4. 验证订单是否过期
        LocalDateTime now = LocalDateTime.now();
        if (order.getExpireTime() != null && now.isAfter(order.getExpireTime())) {
            log.warn("[payOrder] 订单已过期，订单ID: {}, 过期时间: {}, 当前时间: {}",
                    orderId, order.getExpireTime(), now);
            throw exception(ORDER_EXPIRED);
        }

        // 5. 验证手机验证码
        // 5.1 查询用户信息（从 biz_user_info 表，通过 Feign 调用）
        UserInfoRespDTO user = userInfoApi.getUserInfo(userId).getCheckedData();
        if (user == null) {
            log.error("[payOrder] 用户不存在或手机号为空，用户ID: {}", userId);
            throw exception(INVALID_SMS_CODE);
        }

        if(StringUtil.isNotBlank(reqVO.getEmailCode()) && StringUtil.isNotBlank(user.getEmail())){
            // 校验邮箱验证码
            MailCodeUseReqDTO mailCodeUseReq = new MailCodeUseReqDTO();
            mailCodeUseReq.setMail(user.getEmail());
            mailCodeUseReq.setScene(SmsSceneEnum.MEMBER_AUDIT.getScene());
            mailCodeUseReq.setCode(reqVO.getEmailCode());

            try {
                mailSendApi.verifyMailCode(mailCodeUseReq).checkError();
                log.info("[payOrder] 验证码验证成功，用户ID: {}, 邮箱: {}", userId, user.getEmail());
            } catch (Exception e) {
                log.error("[payOrder] 验证码验证失败，用户ID: {}, 邮箱: {}, 错误信息: {}",
                        userId, user.getMobile(), e.getMessage(), e);
                throw exception(INVALID_SMS_CODE);
            }
        }else{
            // 5.2 使用验证码（调用 useSmsCode 方法）
            SmsCodeUseReqDTO smsCodeUseReq = new SmsCodeUseReqDTO();
            smsCodeUseReq.setMobile(user.getMobile());
            smsCodeUseReq.setScene(SmsSceneEnum.MEMBER_AUDIT.getScene()); // 使用用户认证场景
            smsCodeUseReq.setCode(smsCode);
            smsCodeUseReq.setUsedIp(getClientIP());

            try {
                smsCodeApi.useSmsCode(smsCodeUseReq).checkError();
                log.info("[payOrder] 验证码验证成功，用户ID: {}, 手机号: {}", userId, user.getMobile());
            } catch (Exception e) {
                log.error("[payOrder] 验证码验证失败，用户ID: {}, 手机号: {}, 错误信息: {}",
                        userId, user.getMobile(), e.getMessage(), e);
                throw exception(INVALID_SMS_CODE);
            }
        }



        // 6. 上传支付凭证到文件服务器
        String payVoucherUrl;
        try {

            String fileName = payVoucher.getOriginalFilename();

            log.info("[payOrder] 开始上传支付凭证，订单ID: {}, 文件名: {}", orderId, fileName);
            payVoucherUrl = fileApi.createFile(payVoucher.getOriginalFilename(),
                    IoUtil.readBytes(payVoucher.getInputStream()));

            if (payVoucherUrl == null || payVoucherUrl.isEmpty()) {
                log.error("[payOrder] 支付凭证上传失败，返回URL为空，订单ID: {}", orderId);
                throw exception(UPLOAD_VOUCHER_FAILED);
            }

            log.info("[payOrder] 支付凭证上传成功，订单ID: {}, 文件URL: {}", orderId, payVoucherUrl);
        } catch (Exception e) {
            log.error("[payOrder] 支付凭证上传失败，订单ID: {}, 错误信息: {}", orderId, e.getMessage(), e);
            throw exception(UPLOAD_VOUCHER_FAILED);
        }

        // 7. 更新订单信息
        ProjectOrderDO updateOrder = ProjectOrderDO.builder()
                .id(orderId)
                .payVoucherUrl(payVoucherUrl)
                .contractNo(contractNo)
                .confirmPurchaseTime(now)
                .orderStatus(OrderStatusEnum.UNDER_REVIEW.getStatus())
                .build();

        projectOrderMapper.updateById(updateOrder);

        log.info("[payOrder] 订单支付确认成功，订单ID: {}, 订单状态更新为: {}",
                orderId, OrderStatusEnum.UNDER_REVIEW.getName());

        // 8. 查询项目信息获取 tokenAddress
        ProjectInfoDO project = projectInfoMapper.selectById(order.getProjectId());
        if (project == null) {
            log.error("[payOrder] 项目不存在，项目ID: {}", order.getProjectId());
            throw exception(PROJECT_NOT_FOUND);
        }
    }

    /**
     * 获取客户端 IP
     */
    private String getClientIP() {
        return ServletUtils.getClientIP();
    }

    @Override
    public PageResult<AppOrderListRespVO> getUserOrderList(AppOrderListReqVO appOrderListReqVO) {

        Long userId = getLoginUserId();
        // 查询用户的所有订单，按创建时间倒序
        PageResult<ProjectOrderDO> pageResult = projectOrderMapper.selectPage(appOrderListReqVO,
                new LambdaQueryWrapperX<ProjectOrderDO>().eq(ProjectOrderDO::getUserId, userId)
                        .eqIfPresent(ProjectOrderDO::getOrderStatus, appOrderListReqVO.getOrderStatus())
                        .orderByDesc(ProjectOrderDO::getCreateTime));
        List<AppOrderListRespVO> respVOList = new ArrayList<>();
        PageResult<AppOrderListRespVO> result = BeanUtils.toBean(pageResult, AppOrderListRespVO.class);
        if (pageResult.getList() != null && !pageResult.getList().isEmpty()) {
            List<Long> orderIds = pageResult.getList().stream().map(ProjectOrderDO::getId).collect(Collectors.toList());
            LambdaQueryWrapper<ProjectOrderBalanceDO> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.in(ProjectOrderBalanceDO::getOrderId, orderIds);
            List<ProjectOrderBalanceDO> projectOrderBalanceDOS = projectOrderBalanceMapper.selectList(queryWrapper);
            Map<Long, ProjectOrderBalanceDO> projectOrderBalanceDOMap = projectOrderBalanceDOS.stream()
                    // 第一个参数：Map的key（这里用id，需替换为你实际要作为key的字段）
                    // 第二个参数：Map的value（当前对象本身）
                    .collect(Collectors.toMap(
                            ProjectOrderBalanceDO::getOrderId, // 核心：指定Map的key（替换为你的实际字段，如orderId等）
                            item -> item, // Map的value为对象本身
                            // 解决key重复的冲突策略（可选，避免主键重复时报错）
                            (existing, replacement) -> existing // 重复时保留原有值，也可根据业务选replacement
                    ));
            for (ProjectOrderDO projectOrderDO : pageResult.getList()) {
                AppOrderListRespVO appOrderListRespVO = BeanUtils.toBean(projectOrderDO, AppOrderListRespVO.class);
                ProjectOrderBalanceDO projectOrderBalanceDO = projectOrderBalanceDOMap.get(projectOrderDO.getId());
                if (projectOrderBalanceDO == null) {
                    appOrderListRespVO.setTotalIncome(BigDecimal.ZERO);
                } else {
                    appOrderListRespVO.setTotalIncome(projectOrderBalanceDO.getTotalIncome());
                }
                if (StringUtils.isNotBlank(projectOrderDO.getPayBank())) {
                    AppProjectPaymentInfoRespVO appProjectPaymentInfoRespVO = JSONObject
                            .parseObject(projectOrderDO.getPayBank(), AppProjectPaymentInfoRespVO.class);
                    appOrderListRespVO.setBankName(appProjectPaymentInfoRespVO.getBankName());
                    appOrderListRespVO.setBankAccount(appProjectPaymentInfoRespVO.getBankAccount());
                    appOrderListRespVO.setBankAccountName(appProjectPaymentInfoRespVO.getBankAccountName());
                }
                respVOList.add(appOrderListRespVO);
            }
        }
        result.setList(respVOList);
        return result;
    }

    @Override
    public AppOrderDetailRespVO getUserOrderDetail(Long userId, Long orderId) {
        log.info("[getUserOrderDetail] 开始查询用户订单详情，用户ID: {}, 订单ID: {}", userId, orderId);

        // 1. 查询订单
        ProjectOrderDO order = projectOrderMapper.selectById(orderId);
        if (order == null) {
            log.warn("[getUserOrderDetail] 订单不存在，订单ID: {}", orderId);
            throw exception(ORDER_NOT_FOUND);
        }

        // 2. 验证订单归属
        if (!userId.equals(order.getUserId())) {
            log.warn("[getUserOrderDetail] 订单不属于当前用户，订单ID: {}, 订单用户ID: {}, 当前用户ID: {}",
                    orderId, order.getUserId(), userId);
            throw exception(ORDER_NOT_BELONG_TO_USER);
        }

        log.info("[getUserOrderDetail] 查询成功，订单ID: {}, 订单号: {}", orderId, order.getOrderNo());
        AppOrderDetailRespVO respVO = BeanUtils.toBean(order, AppOrderDetailRespVO.class);
        // 如果是已支付
        LambdaQueryWrapper<ProjectOrderBalanceDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ProjectOrderBalanceDO::getOrderId, orderId);
        ProjectOrderBalanceDO projectOrderBalanceDO = projectOrderBalanceMapper.selectOne(queryWrapper);
        if (projectOrderBalanceDO != null) {
            respVO.setHoldAmount(projectOrderBalanceDO.getHoldAmount());
            respVO.setHoldQuantity(projectOrderBalanceDO.getHoldQuantity());
        }

        if (StringUtils.isNotBlank(order.getPayBank())) {
            AppProjectPaymentInfoRespVO paymentInfo = JSONObject.parseObject(order.getPayBank(),
                    AppProjectPaymentInfoRespVO.class);
            respVO.setBankAccountName(paymentInfo.getBankAccountName());
            respVO.setBankAccount(paymentInfo.getBankAccount());
            respVO.setBankName(paymentInfo.getBankName());
        }

        // 如果是支付成功的订单（状态 >= 2 审核中/审核通过），计算可提现余额
        if (order.getOrderStatus() != null && order.getOrderStatus() >= 2) {
            AppAssetByCurrencyRespVO withdrawableBalance = calculateWithdrawableBalance(
                    order.getId(), order.getProjectId(), userId);
            respVO.setWithdrawableBalance(withdrawableBalance.getTotal());
            log.info("[getUserOrderDetail] 订单可提现余额: {}", withdrawableBalance);
        }

        // 判断状态
        if (order.getOrderStatus().equals(OrderStatusEnum.UNDER_REVIEW.getStatus())) {
            respVO.setStatus(AssetStatusEnum.CONFIRMING.getStatus());
        } else if (order.getOrderStatus().equals(OrderStatusEnum.APPROVED.getStatus())) {
            ProjectInfoDO projectInfoDO = projectInfoMapper.selectById(order.getProjectId());
            LocalDate now = LocalDate.now();
            if (projectInfoDO != null) {
                if (Objects.equals(projectInfoDO.getProjectType(), ASSET_TYPE_OPEN_FUND)) {
                    if (projectInfoDO.getLockStartTime() != null) {
                        if (now.isAfter(projectInfoDO.getLockStartTime())) {
                            respVO.setStatus(AssetStatusEnum.PROFIT.getStatus());
                        } else if (projectInfoDO.getLockEndTime() != null  && now.isAfter(projectInfoDO.getLockEndTime())) {
                            // 如果当前时间在锁定期之后则是可赎回
                            respVO.setStatus(AssetStatusEnum.REDEEMABLE.getStatus());
                        } else {
                            respVO.setStatus(AssetStatusEnum.WAIT.getStatus());
                        }

                    }
                } else {
                    // 如果有锁定时间，并且在锁定期内，则是收益中状态
                    if (projectInfoDO.getLockStartTime() != null && projectInfoDO.getLockEndTime() != null) {
                        if (now.isAfter(projectInfoDO.getLockStartTime())
                                && now.isBefore(projectInfoDO.getLockEndTime())) {
                            respVO.setStatus(AssetStatusEnum.PROFIT.getStatus());
                        } else if (now.isAfter(projectInfoDO.getLockEndTime())) {
                            // 如果当前时间在锁定期之后则是可赎回
                            respVO.setStatus(AssetStatusEnum.REDEEMABLE.getStatus());
                        } else {
                            respVO.setStatus(AssetStatusEnum.WAIT.getStatus());
                        }

                    } else {
                        respVO.setStatus(AssetStatusEnum.WAIT.getStatus());
                    }
                }

            }

        } else if (order.getOrderStatus().equals(OrderStatusEnum.ENDED.getStatus())) {
            respVO.setStatus(AssetStatusEnum.ENDED.getStatus());
        }

        return respVO;
    }

    @Override
    public void earlyRedemption(Long userId, AppEarlyRedemptionReqVO reqVO) {
        projectOrderService.earlyRedemption(userId, reqVO);
        redisService.deleteObject(String.format(ASSET_DETAIL_KEY, userId, reqVO.getOrderId()));
        redisService.deleteObject(String.format(ASSET_DETAIL_KEY_ALL, userId));
    }

    @Override
    public void applyDividend(Long userId, AppDividendReqVO reqVO) {
        projectOrderService.applyDividend(userId, reqVO);
        redisService.deleteObject(String.format(ASSET_DETAIL_KEY, userId, reqVO.getOrderId()));
        redisService.deleteObject(String.format(ASSET_DETAIL_KEY_ALL, userId));
    }

    @Override
    public void maturityRedemption(Long userId, AppMaturityRedemptionReqVO reqVO) {
        projectOrderService.maturityRedemption(userId, reqVO);
        redisService.deleteObject(String.format(ASSET_DETAIL_KEY, userId, reqVO.getOrderId()));
        redisService.deleteObject(String.format(ASSET_DETAIL_KEY_ALL, userId));
    }

    @Resource
    private ProjectDividendPeriodMapper dividendPeriodMapper;

    @Resource
    private OrderDailyIncomeMapper orderDailyIncomeMapper;

    @Override
    public AppAssetByCurrencyRespVO calculateWithdrawableBalance(Long orderId, Long projectId, Long userId) {
        log.info("[calculateWithdrawableBalance] 计算可提现余额，订单ID: {}, 项目ID: {}, 用户ID: {}", orderId, projectId, userId);
        ProjectOrderDO projectOrderDO = projectOrderMapper.selectById(orderId);
        if (projectOrderDO == null || !Objects.equals(userId, projectOrderDO.getUserId())) {
            return null;
        }
        AppAssetByCurrencyRespVO respVO = new AppAssetByCurrencyRespVO();
        respVO.setCoinCode(projectOrderDO.getEarningCurrency());
        if (projectOrderDO == null) {
            respVO.setTotal(BigDecimal.ZERO);
            return respVO;
        }
        if (!userId.equals(projectOrderDO.getUserId())) {
            respVO.setTotal(BigDecimal.ZERO);
            return respVO;
        }
        if (projectId == null) {
            projectId = projectOrderDO.getProjectId();
        }
        // 3. 获取已提取金额
        ProjectOrderBalanceDO balance = projectOrderBalanceMapper.getProjectOrderBalance(orderId, userId);
        BigDecimal withdrawnAmount = BigDecimal.ZERO;
        if (balance != null && balance.getWithdrawnDividend() != null) {
            withdrawnAmount = balance.getWithdrawnDividend();
        }

        ProjectInfoDO projectInfoDO = projectInfoMapper.selectById(projectId);
        // 开放型基金的话，收益金额就是可提现余额
        if (projectInfoDO != null
                && projectInfoDO.getProjectConfigType().equals(PROJECT_CONFIG_TYPE_FUND)
                && projectInfoDO.getProjectType().equals(ASSET_TYPE_OPEN_FUND)) {
            respVO.setTotal(balance.getUnwithdrawnDividend());
            return respVO;
        }

        // 1. 获取当前已解锁的最大分红日期
        LocalDate today = LocalDate.now();
        LocalDate currentUnlockDate = dividendPeriodMapper.selectCurrentUnlockDateByProjectId(projectId,
                today);

        if (currentUnlockDate == null) {
            log.info("[calculateWithdrawableBalance] 当前无已解锁的分红期，项目ID: {}", projectId);
            respVO.setTotal(BigDecimal.ZERO);
            return respVO;
        }

        log.info("[calculateWithdrawableBalance] 当前解锁日期: {}", currentUnlockDate);

        // 2. 计算解锁日期之前的已发放收益总和
        BigDecimal totalUnlockedIncome = orderDailyIncomeMapper.selectSumIncomeBeforeDate(orderId, currentUnlockDate);
        if (totalUnlockedIncome == null) {
            totalUnlockedIncome = BigDecimal.ZERO;
        }

        log.info("[calculateWithdrawableBalance] 解锁收益总和: {}", totalUnlockedIncome);

        log.info("[calculateWithdrawableBalance] 已提取金额: {}", withdrawnAmount);

        // 4. 可提现余额 = 已解锁收益 - 已提取金额
        BigDecimal withdrawableBalance = totalUnlockedIncome.subtract(withdrawnAmount);

        // 确保不为负数
        if (withdrawableBalance.compareTo(BigDecimal.ZERO) < 0) {
            withdrawableBalance = BigDecimal.ZERO;
        }

        log.info("[calculateWithdrawableBalance] 可提现余额: {}", withdrawableBalance);
        respVO.setTotal(withdrawableBalance);
        return respVO;
    }

    @Override
    public AppOrderStatusCountRespVO getOrderStatusCount(Long userId) {
        log.info("[getOrderStatusCount] 获取用户订单状态统计，用户ID: {}", userId);

        AppOrderStatusCountRespVO result = new AppOrderStatusCountRespVO();

        // 查询用户所有订单
        List<ProjectOrderDO> orders = projectOrderMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ProjectOrderDO>()
                        .eq(ProjectOrderDO::getUserId, userId));

        int pendingPayment = 0;
        int underReview = 0;
        int approved = 0;
        int rejected = 0;
        int cancelled = 0;

        for (ProjectOrderDO order : orders) {
            if (order.getOrderStatus() == null)
                continue;
            switch (order.getOrderStatus()) {
                case 0:
                    pendingPayment++;
                    break;
                case 1:
                    underReview++;
                    break;
                case 2:
                    approved++;
                    break;
                case 3:
                    rejected++;
                    break;
                case 4:
                    cancelled++;
                    break;
            }
        }

        result.setPendingPaymentCount(pendingPayment);
        result.setUnderReviewCount(underReview);
        result.setApprovedCount(approved);
        result.setRejectedCount(rejected);
        result.setCancelledCount(cancelled);
        result.setTotalCount(orders.size());

        return result;
    }

    @Override
    public BigDecimal getRedemptionFeeRate(Long userId, Long orderId) {
        // 1. 查询订单
        ProjectOrderDO order = projectOrderMapper.selectById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw exception(ORDER_NOT_FOUND);
        }

        // 2. 查询项目
        ProjectInfoDO project = projectInfoMapper.selectById(order.getProjectId());
        if (project == null) {
            throw exception(PROJECT_NOT_FOUND);
        }
        // 3.如果项目结束了，则不需要手续费
        if (RunStatusEnum.ENDED.getStatus().equals(project.getProjectStatus())) {
            return BigDecimal.ZERO;
        }

        // 3. 确定开始日期
        // 如果是开放型基金(ProjectConfigType=1且AssetType=2)，持有时间从订单中的lockStartTime开始算
        LocalDate startTime;
        if (PROJECT_CONFIG_TYPE_FUND.equals(project.getProjectConfigType())
                && ASSET_TYPE_OPEN_FUND.equals(project.getProjectType())) {
            startTime = order.getLockStartTime();
        } else {
            startTime = project.getLockStartTime();
        }

        // 3.1 如果已经到期，费率为0
        if (project.getLockEndTime() != null && LocalDate.now().isAfter(project.getLockEndTime())) {
            return BigDecimal.ZERO;
        }

        // 4. 计算持有天数
        long holdDays;
        if (startTime == null || startTime.isAfter(LocalDate.now())) {
            holdDays = 0;
        } else {
            holdDays = ChronoUnit.DAYS.between(startTime, LocalDate.now());
        }

        // 5. 计算费率
        BigDecimal redemptionFeeRate = projectOrderService.calculateEarlyRedemptionFeeRate(project.getEarlyRedemptionFeeJson(), holdDays);
        return redemptionFeeRate != null ? redemptionFeeRate.multiply(BigDecimal.valueOf(100)) : redemptionFeeRate;
    }

}
