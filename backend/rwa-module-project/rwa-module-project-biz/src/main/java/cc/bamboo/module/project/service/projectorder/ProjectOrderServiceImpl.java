package cc.bamboo.module.project.service.projectorder;

import cc.bamboo.framework.common.exception.ServiceException;
import cc.bamboo.framework.common.pojo.CommonResult;
import cc.bamboo.framework.redis.service.RedisService;
import cc.bamboo.framework.tenant.core.aop.TenantIgnore;
import cc.bamboo.module.chain.enums.ChainTaskStatusEnum;
import cc.bamboo.module.project.controller.app.projectinfo.vo.AppUserBankRespVO;
import cc.bamboo.module.project.enums.AuditStatusEnum;
import cc.bamboo.module.system.api.mail.MailSendApi;
import cc.bamboo.module.system.api.mail.dto.MailCodeUseReqDTO;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.util.StringUtil;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import cc.bamboo.framework.common.util.servlet.ServletUtils;
import cc.bamboo.module.project.controller.admin.projectorder.vo.*;
import cc.bamboo.module.project.controller.app.projectorder.vo.*;
import cc.bamboo.module.project.dal.dataobject.projectorder.ProjectOrderDO;
import cc.bamboo.module.project.dal.dataobject.projectorderbalance.ProjectOrderBalanceDO;
import cc.bamboo.module.project.dal.dataobject.projectorderbalancelog.ProjectOrderBalanceLogDO;
import cc.bamboo.module.project.dal.dataobject.projectoperation.ProjectOperationDO;
import cc.bamboo.module.project.dal.dataobject.projectinfo.ProjectInfoDO;
import cc.bamboo.module.project.dal.dataobject.projectbill.ProjectBillDO;
import cc.bamboo.module.project.enums.OrderStatusEnum;
import cc.bamboo.module.project.enums.BillTypeEnum;
import cc.bamboo.module.project.enums.BalanceLogTypeEnum;
import cc.bamboo.framework.common.pojo.PageResult;
import cc.bamboo.framework.common.util.object.BeanUtils;

import cc.bamboo.module.project.dal.mysql.projectorder.ProjectOrderMapper;
import cc.bamboo.module.project.dal.mysql.projectorderbalance.ProjectOrderBalanceMapper;
import cc.bamboo.module.project.dal.mysql.projectorderbalancelog.ProjectOrderBalanceLogMapper;
import cc.bamboo.module.project.dal.mysql.projectoperation.ProjectOperationMapper;
import cc.bamboo.module.project.dal.mysql.projectinfo.ProjectInfoMapper;
import cc.bamboo.module.project.dal.mysql.projectbill.ProjectBillMapper;
import cc.bamboo.module.project.service.chainoperation.ChainOperationTaskService;
import cc.bamboo.module.system.api.sms.SmsCodeApi;
import cc.bamboo.module.system.api.sms.dto.code.SmsCodeUseReqDTO;
import cc.bamboo.module.system.enums.sms.SmsSceneEnum;
import cc.bamboo.module.user.api.userinfo.UserInfoApi;
import cc.bamboo.module.user.api.userinfo.dto.UserInfoRespDTO;
import org.springframework.data.redis.core.StringRedisTemplate;
import cc.bamboo.module.project.dal.redis.RedisKeyConstants;

import static cc.bamboo.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cc.bamboo.module.project.enums.ApiConstants.*;
import static cc.bamboo.module.project.enums.ErrorCodeConstants.*;

/**
 * 项目认购订单 Service 实现类
 *
 * @author Swolf
 */
@Service
@Validated
@Slf4j
public class ProjectOrderServiceImpl implements ProjectOrderService {

    @Resource
    private ProjectOrderMapper orderMapper;

    @Resource
    private ProjectOrderBalanceMapper projectOrderBalanceMapper;

    @Resource
    private ProjectOrderBalanceLogMapper projectOrderBalanceLogMapper;

    @Resource
    private ProjectOperationMapper projectOperationMapper;

    @Resource
    private ProjectInfoMapper projectInfoMapper;

    @Resource
    private ProjectBillMapper projectBillMapper;

    @Resource
    private UserInfoApi userInfoApi;

    @Resource
    private SmsCodeApi smsCodeApi;

    @Resource
    private ChainOperationTaskService chainOperationTaskService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private RedisService redisService;

    @Resource
    private MailSendApi mailSendApi;



    @Override
    public Long createOrder(ProjectOrderSaveReqVO createReqVO) {
        // 插入
        ProjectOrderDO order = BeanUtils.toBean(createReqVO, ProjectOrderDO.class);
        orderMapper.insert(order);
        // 返回
        return order.getId();
    }

    @Override
    public void updateOrder(ProjectOrderSaveReqVO updateReqVO) {
        // 校验存在
        validateOrderExists(updateReqVO.getId());
        // 更新
        ProjectOrderDO updateObj = BeanUtils.toBean(updateReqVO, ProjectOrderDO.class);
        orderMapper.updateById(updateObj);
    }

    @Override
    public void deleteOrder(Long id) {
        // 校验存在
        validateOrderExists(id);
        // 删除
        orderMapper.deleteById(id);
    }

    private void validateOrderExists(Long id) {
        if (orderMapper.selectById(id) == null) {
            throw exception(ORDER_NOT_EXISTS);
        }
    }

    @Override
    public ProjectOrderRespVO getOrder(Long id) {
        ProjectOrderDO order = orderMapper.selectById(id);
        ProjectOrderRespVO respVO = BeanUtils.toBean(order, ProjectOrderRespVO.class);
        CommonResult<UserInfoRespDTO> userInfoResult = userInfoApi.getUserInfo(order.getUserId());
        if (userInfoResult.isSuccess()) {
            respVO.setUserName(userInfoResult.getData().getRealName());
        }
        return respVO;
    }

    @Override
    public PageResult<ProjectOrderRespVO> getOrderPage(ProjectOrderPageReqVO pageReqVO) {
        PageResult<ProjectOrderDO> pageResult = orderMapper.selectPage(pageReqVO);
        PageResult<ProjectOrderRespVO> result = BeanUtils.toBean(pageResult, ProjectOrderRespVO.class);
        List<ProjectOrderDO> list = pageResult.getList();
        if (!list.isEmpty()) {
            List<Long> userIds = list.stream().map(ProjectOrderDO::getUserId).collect(Collectors.toList());
            CommonResult<List<UserInfoRespDTO>> userInfoResult = userInfoApi.getUserInfoList(userIds);
            if (userInfoResult.isSuccess()) {
                List<UserInfoRespDTO> userInfoList = userInfoResult.getData();
                Map<Long, String> userInfoMap = userInfoList.stream()
                        .collect(Collectors.toMap(UserInfoRespDTO::getId, UserInfoRespDTO::getRealName));
                result.getList().forEach(item -> item.setUserName(userInfoMap.get(item.getUserId())));
            }
        }

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void auditOrder(ProjectOrderAuditReqVO auditReqVO, Long auditUserId, String auditUserName) {
        log.info("[auditOrder] 开始审核订单，订单ID: {}, 审核结果: {}, 审核人ID: {}",
                auditReqVO.getId(), auditReqVO.getApproved(), auditUserId);

        // 1. 验证订单存在性
        ProjectOrderDO order = orderMapper.selectById(auditReqVO.getId());
        if (order == null) {
            log.warn("[auditOrder] 订单不存在，订单ID: {}", auditReqVO.getId());
            throw exception(ORDER_NOT_FOUND);
        }

        // 2. 验证订单状态（必须是"审核中"）
        if (!OrderStatusEnum.UNDER_REVIEW.getStatus().equals(order.getOrderStatus())) {
            log.warn("[auditOrder] 订单状态不是审核中，订单ID: {}, 订单状态: {}",
                    auditReqVO.getId(), order.getOrderStatus());
            throw exception(ORDER_NOT_UNDER_REVIEW);
        }

        // 3. 验证审核不通过时必须填写原因
        if (!auditReqVO.getApproved() && StrUtil.isBlank(auditReqVO.getAuditRemark())) {
            log.warn("[auditOrder] 审核不通过时必须填写原因，订单ID: {}", auditReqVO.getId());
            throw exception(AUDIT_REMARK_REQUIRED);
        }
        // 3. 验证审核通过必须上传合同附件
        if (auditReqVO.getApproved() && StrUtil.isBlank(auditReqVO.getContractFileUrls())) {
            log.warn("[auditOrder] 审核通过时必须上传合同附件，订单ID: {}", auditReqVO.getId());
            throw exception(AUDIT_FILES_REQUIRED);
        }

        // 3.1 查询项目信息
        ProjectInfoDO project = projectInfoMapper.selectById(order.getProjectId());
        if (project == null) {
            log.error("[auditOrder] 项目不存在，项目ID: {}", order.getProjectId());
            throw exception(PROJECT_NOT_FOUND);
        }

        LocalDateTime now = LocalDateTime.now();

        // 4. 更新订单审核信息
        ProjectOrderDO updateOrder = ProjectOrderDO.builder()
                .id(auditReqVO.getId())
                .orderStatus(auditReqVO.getApproved() ? OrderStatusEnum.APPROVED.getStatus()
                        : OrderStatusEnum.REJECTED.getStatus())
                .auditTime(now)
                .auditUserId(auditUserId)
                .auditUserName(auditUserName)
                .auditRemark(auditReqVO.getAuditRemark())
                .chainStatus(auditReqVO.getApproved() ? ChainTaskStatusEnum.PENDING.getStatus()
                        : ChainTaskStatusEnum.CANCELLED.getStatus())
                .build();
        LambdaUpdateWrapper<ProjectOrderDO> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(ProjectOrderDO::getOrderStatus, updateOrder.getOrderStatus());
        updateWrapper.set(ProjectOrderDO::getAuditFiles, auditReqVO.getContractFileUrls());
        updateWrapper.set(ProjectOrderDO::getAuditTime, updateOrder.getAuditTime());
        updateWrapper.set(ProjectOrderDO::getAuditUserId, updateOrder.getAuditUserId());
        updateWrapper.set(ProjectOrderDO::getAuditUserName, updateOrder.getAuditUserName());
        updateWrapper.set(ProjectOrderDO::getAuditRemark, updateOrder.getAuditRemark());
        updateWrapper.set(ProjectOrderDO::getChainStatus,
                auditReqVO.getApproved() ? ChainTaskStatusEnum.PENDING.getStatus()
                        : ChainTaskStatusEnum.CANCELLED.getStatus());
        updateWrapper.eq(ProjectOrderDO::getId, auditReqVO.getId());
        updateWrapper.eq(ProjectOrderDO::getOrderStatus, OrderStatusEnum.UNDER_REVIEW.getStatus());
        //设置订单的收益日期
        updateWrapper.set(ProjectOrderDO::getLockStartTime, project.getLockStartTime());
        //如果是开放型基金，当前已经到了收益日期的话，收益开始日期为第二天
        if (Objects.equals(project.getProjectType(), ASSET_TYPE_OPEN_FUND) &&
                Objects.equals(project.getProjectConfigType(), PROJECT_CONFIG_TYPE_FUND) &&
                project.getLockStartTime().isBefore(LocalDate.now())) {
            updateWrapper.set(ProjectOrderDO::getLockStartTime, project.getLockStartTime().plusDays(1L));
        }

        //设置订单的收益结束日期
        updateWrapper.set(ProjectOrderDO::getLockEndTime, project.getLockEndTime());

        orderMapper.update(null, updateWrapper);
        log.info("[auditOrder] 订单审核信息已更新，订单ID: {}, 审核状态: {}",
                auditReqVO.getId(), auditReqVO.getApproved() ? "通过" : "不通过");

        // 5. 如果审核通过，创建订单余额记录并更新项目运营统计
        if (auditReqVO.getApproved()) {
            // 5.1 创建订单余额记录
            ProjectOrderBalanceDO balance = ProjectOrderBalanceDO.builder()
                    .userId(order.getUserId())
                    .projectId(order.getProjectId())
                    .orderId(order.getId())
                    .principalAmount(order.getTotalAmount())
                    .holdAmount(order.getTotalAmount())
                    .buyQuantity(order.getSubscribeQuantity())
                    .holdQuantity(order.getSubscribeQuantity())
                    .totalIncome(BigDecimal.ZERO)
                    .withdrawnDividend(BigDecimal.ZERO)
                    .unwithdrawnDividend(BigDecimal.ZERO)
                    .totalRedemptionAmount(BigDecimal.ZERO)
                    .lastIncomeCalcTime(now)
                    .investmentCurrency(project.getInvestmentCurrency())
                    .earningCurrency(project.getEarningCurrency())
                    .build();
            balance.setTenantId(project.getTenantId());
            projectOrderBalanceMapper.insert(balance);

            log.info("[auditOrder] 订单余额记录已创建，订单ID: {}, 余额ID: {}",
                    auditReqVO.getId(), balance.getId());

            // 5.1.1 记录余额变动日志（购买成功，本金增加）
            recordBalanceLog(order.getUserId(), order.getProjectId(), order.getId(),
                    order.getTotalAmount(), order.getTotalAmount(), BalanceLogTypeEnum.PURCHASE);

            // 5.2 更新项目运营统计
            ProjectOperationDO operation = projectOperationMapper.selectById(order.getProjectId());

            if (operation == null) {
                // 如果不存在，创建新记录
                operation = ProjectOperationDO.builder()
                        .projectId(order.getProjectId())
                        .investorCount(1)
                        .dividendApplyCount(0)
                        .dividendApplyAmount(BigDecimal.ZERO)
                        .earlyRedemptionPeople(0)
                        .earlyRedemptionAmount(BigDecimal.ZERO)
                        .earlyRedemptionCount(0)
                        .maturityRedemptionCount(0)
                        .maturityRedemptionAmount(BigDecimal.ZERO)
                        .totalInvestorIncome(BigDecimal.ZERO)
                        .totalInvestorYield(BigDecimal.ZERO)
                        .build();
                operation.setTenantId(order.getTenantId());
                projectOperationMapper.insert(operation);

                log.info("[auditOrder] 项目运营统计记录已创建，项目ID: {}", order.getProjectId());
            } else {
                // 如果存在，更新投资人数（+1）
                ProjectOperationDO updateOperation = ProjectOperationDO.builder()
                        .projectId(order.getProjectId())
                        .totalInvertor(operation.getTotalInvertor().add(order.getTotalAmount()))
                        .investorCount(operation.getInvestorCount() + 1)
                        .build();

                projectOperationMapper.updateById(updateOperation);

                log.info("[auditOrder] 项目运营统计已更新，项目ID: {}, 投资人数: {}",
                        order.getProjectId(), operation.getInvestorCount() + 1);
            }

            // Clear user balance cache
            clearUserBalanceCache(order.getUserId(),order.getId());

        } else {
            // 需要退回库存
            projectInfoMapper.restoreStock(order.getProjectId(), order.getSubscribeQuantity());
        }

        log.info("[auditOrder] 订单审核完成，订单ID: {}, 审核结果: {}",
                auditReqVO.getId(), auditReqVO.getApproved() ? "通过" : "不通过");
    }

    private void clearUserBalanceCache(Long userId,Long orderId) {
        // Clear asset detail cache

        redisService.deleteObject(String.format(ASSET_DETAIL_KEY, userId, orderId));
        redisService.deleteObject(String.format(ASSET_DETAIL_KEY_ALL, userId));
        // Clear daily income cache
        deleteKeysByPattern(String.format(RedisKeyConstants.PROJECT_ORDER_BALANCE_DAILY_INCOME,userId,"*","*"));
        // Clear income calendar cache
        deleteKeysByPattern(String.format(RedisKeyConstants.PROJECT_ORDER_BALANCE_INCOME_CALENDAR,userId,"*","*","*"));
    }

    private void deleteKeysByPattern(String pattern) {
        Set<String> keys = stringRedisTemplate.keys("*" + pattern);
        if (keys != null && !keys.isEmpty()) {
            stringRedisTemplate.delete(keys);
        }
    }

    @Override
    public void sendMintToken(Long orderId) {
        ProjectOrderDO order = orderMapper.selectById(orderId);
        ProjectInfoDO info = projectInfoMapper.selectById(order.getProjectId());
        // 1. 验证订单状态,只有待处理和失败状态才可以发送铸造 Token 任务
        if (!order.getChainStatus().equals(ChainTaskStatusEnum.PENDING.getStatus()) &&
                !order.getChainStatus().equals(ChainTaskStatusEnum.FAILED.getStatus())) {
            log.error("[sendMintToken] 订单链上状态错误，订单ID: {}, 状态: {}", orderId, order.getChainStatus());
            return;
        }
        // 更新状态
        LambdaUpdateWrapper<ProjectOrderDO> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(ProjectOrderDO::getId, orderId);
        updateWrapper.eq(ProjectOrderDO::getChainStatus, order.getChainStatus());
        updateWrapper.set(ProjectOrderDO::getChainStatus, ChainTaskStatusEnum.PROCESSING.getStatus());
        int result = orderMapper.update(null, updateWrapper);
        if (result == 0) {
            log.error("[sendMintToken] 更新订单链上状态失败，数据库状态与当前状态不一致，订单ID: {}, 当前状态: {}", orderId, order.getChainStatus());
            return;
        }

        // 5.4 创建铸造 Token 任务
        try {
            // 将购买数量转换为 Token 数量（需要乘以 10^18，因为 Token 使用 18 位小数）
            String mintAmount = order.getSubscribeQuantity().toString();

            String taskNo = chainOperationTaskService.createMintTokenTask(
                    order.getId(),
                    order.getOrderNo(),
                    order.getProjectId(),
                    order.getProjectName(),
                    info.getChainTokenAddress(),
                    order.getUserId(),
                    order.getChainAddress(),
                    mintAmount);

            log.info("[auditOrder] 铸造 Token 任务创建成功，订单ID: {}, taskNo: {}",
                    order.getId(), taskNo);

        } catch (Exception e) {
            log.error("[auditOrder] 铸造 Token 任务创建失败，订单ID: {}, 错误: {}",
                    order.getId(), e.getMessage(), e);
            updateWrapper.clear();
            // 更新订单链上状态为失败
            updateWrapper.eq(ProjectOrderDO::getId, orderId);
            updateWrapper.eq(ProjectOrderDO::getChainStatus, order.getChainStatus());
            updateWrapper.set(ProjectOrderDO::getChainStatus, ChainTaskStatusEnum.FAILED.getStatus());
            orderMapper.update(null, updateWrapper);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void earlyRedemption(Long userId, AppEarlyRedemptionReqVO reqVO) {
        log.info("[earlyRedemption] 开始提前赎回，用户ID: {}, 余额ID: {}, 赎回份额: {}",
                userId, reqVO.getOrderId(), reqVO.getRedemptionQuantity());

        // 1. 验证手机验证码
        validateSmsCode(userId, reqVO.getSmsCode(), reqVO.getEmailCode());

        // 2. 查询并验证订单余额
        ProjectOrderBalanceDO balance = projectOrderBalanceMapper.getProjectOrderBalance(reqVO.getOrderId(), userId);
        if (balance == null) {
            throw exception(BALANCE_NOT_FOUND);
        }

        if (balance.getHoldQuantity() < reqVO.getRedemptionQuantity()) {
            throw exception(INSUFFICIENT_HOLD_QUANTITY);
        }
        // 查询用户银行卡
        AppUserBankRespVO appUserBankRespVO = projectInfoMapper.getUserBank(userId);
        if (appUserBankRespVO == null
                || !AuditStatusEnum.APPROVED.getStatus().equals(appUserBankRespVO.getAuditStatus())) {
            throw new ServiceException(NOT_BANK);
        }

        // 3. 查询订单和项目信息
        ProjectOrderDO order = orderMapper.selectById(balance.getOrderId());
        ProjectInfoDO project = projectInfoMapper.selectById(balance.getProjectId());

        // 4. 计算持有天数（从订单审核通过时间到现在）
        long holdDays;
        //如果项目没有收益期，持有天数为0,或者没到收益开始时间
        if(project.getLockStartTime() == null || project.getLockStartTime().isAfter(LocalDate.now())){
            holdDays = 0;
        }else{
            holdDays = ChronoUnit.DAYS.between(project.getLockStartTime(), LocalDate.now());
        }

        // 5. 计算手续费率
        BigDecimal feeRate = calculateEarlyRedemptionFeeRate(project.getEarlyRedemptionFeeJson(), holdDays);

        // 如果当前在锁定期后则没有手续费
        // 3.1 如果已经到期，费率为0
        if (project.getLockEndTime() != null && LocalDate.now().isAfter(project.getLockEndTime())) {
            feeRate =  BigDecimal.ZERO;
        }

        // 6. 计算赎回金额
        BigDecimal redemptionPrincipal = order.getPrice().multiply(new BigDecimal(reqVO.getRedemptionQuantity()));
        BigDecimal fee = redemptionPrincipal.multiply(feeRate).setScale(2, RoundingMode.HALF_UP);
        BigDecimal actualAmount = redemptionPrincipal.subtract(fee);

        // 7. 使用乐观锁更新订单余额
        int updateRows = projectOrderBalanceMapper.updateBalanceForEarlyRedemption(
                balance.getId(),
                reqVO.getRedemptionQuantity(),
                redemptionPrincipal,
                userId.toString());
        if (updateRows == 0) {
            log.error("[earlyRedemption] 余额更新失败（余额不足或并发冲突），余额ID: {}", balance.getId());
            throw exception(INSUFFICIENT_HOLD_QUANTITY);
        }

        // 7.1 记录余额变动日志（提前赎回，本金减少）
        BigDecimal afterAmount = balance.getHoldAmount().subtract(redemptionPrincipal);
        recordBalanceLog(userId, balance.getProjectId(), balance.getOrderId(),
                redemptionPrincipal.negate(), afterAmount, BalanceLogTypeEnum.EARLY_REDEMPTION);
        LocalDateTime now = LocalDateTime.now();
        // 8. 创建账单记录
        String billNo = generateBillNo();
        ProjectBillDO bill = ProjectBillDO.builder()
                .billNo(billNo)
                .billType(BillTypeEnum.EARLY_REDEMPTION.getType())
                .applyTime(now)
                .userId(userId)
                .orderId(order.getId())
                .orderNo(order.getOrderNo())
                .quantity(reqVO.getRedemptionQuantity())
                .projectId(balance.getProjectId())
                .projectName(order.getProjectName())
                .bankAccountName(appUserBankRespVO.getBankAccountName())
                .bankAccount(appUserBankRespVO.getBankAccount())
                .bankName(appUserBankRespVO.getBankName())
                .billAmount(redemptionPrincipal)
                .actualAmount(actualAmount)
                .commissionRate(feeRate.multiply(BigDecimal.valueOf(100)).toString())
                .auditStatus(AuditStatusEnum.PENDING.getStatus())// 待审核
                .billCoin(project.getInvestmentCurrency())
                .actualCoin(project.getInvestmentCurrency())
                .build();
        bill.setTenantId(order.getTenantId());
        projectBillMapper.insert(bill);

        // 计算分红金额
        BigDecimal dividendAmount = balance.getUnwithdrawnDividend();

        //如果提前赎回了所有份额，则将分红也提取
        if (balance.getHoldQuantity().equals(reqVO.getRedemptionQuantity()) && dividendAmount.compareTo(BigDecimal.ZERO) > 0) {
            // 进行分红提取
            updateRows = projectOrderBalanceMapper.updateBalanceForDividend(
                    balance.getId(),
                    dividendAmount,
                    userId.toString());
            if (updateRows == 0) {
                log.error("[maturityRedemption] 余额更新失败（余额不足或并发冲突），余额ID: {}", balance.getId());
                throw exception(DIVIDEND_AMOUNT_EXCEEDS_LIMIT);
            }
            recordBalanceLog(userId, balance.getProjectId(), balance.getOrderId(),
                    dividendAmount.negate(), BigDecimal.ZERO, BalanceLogTypeEnum.DIVIDEND_WITHDRAW);
            String dividendBillNo = generateBillNo();
            ProjectBillDO dividendBill = ProjectBillDO.builder()
                    .billNo(dividendBillNo)
                    .billType(BillTypeEnum.DIVIDEND.getType())
                    .applyTime(now)
                    .userId(userId)
                    .orderId(order.getId())
                    .orderNo(order.getOrderNo())
                    .projectId(balance.getProjectId())
                    .projectName(order.getProjectName())
                    .quantity(balance.getHoldQuantity())
                    .bankAccountName(appUserBankRespVO.getBankAccountName())
                    .bankAccount(appUserBankRespVO.getBankAccount())
                    .bankName(appUserBankRespVO.getBankName())
                    .billAmount(dividendAmount)
                    .auditStatus(AuditStatusEnum.PENDING.getStatus()) // 待审核
                    .billCoin(balance.getEarningCurrency())
                    .actualCoin(balance.getInvestmentCurrency())
                    .build();
            dividendBill.setTenantId(order.getTenantId());
            projectBillMapper.insert(dividendBill);
        }

        log.info("[earlyRedemption] 提前赎回申请成功，账单号: {}, 赎回本金: {}, 手续费: {}, 实际到账: {}，等待审核",
                billNo, redemptionPrincipal, fee, actualAmount);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void applyDividend(Long userId, AppDividendReqVO reqVO) {
        log.info("[applyDividend] 开始分红申请，用户ID: {}, 余额ID: {}, 分红金额: {}",
                userId, reqVO.getOrderId(), reqVO.getDividendAmount());

        // 1. 验证手机验证码
        validateSmsCode(userId, reqVO.getSmsCode(), reqVO.getEmailCode());

        // 2. 查询并验证订单余额
        ProjectOrderBalanceDO balance = projectOrderBalanceMapper.getProjectOrderBalance(reqVO.getOrderId(), userId);
        if (balance == null) {
            throw exception(BALANCE_NOT_FOUND);
        }
        if (!balance.getUserId().equals(userId)) {
            throw exception(BALANCE_NOT_BELONG_TO_USER);
        }
        if (balance.getUnwithdrawnDividend().compareTo(reqVO.getDividendAmount()) < 0) {
            throw exception(DIVIDEND_AMOUNT_EXCEEDS_LIMIT);
        }

        // 查询用户银行卡
        AppUserBankRespVO appUserBankRespVO = projectInfoMapper.getUserBank(userId);
        if (appUserBankRespVO == null
                || !AuditStatusEnum.APPROVED.getStatus().equals(appUserBankRespVO.getAuditStatus())) {
            throw new ServiceException(NOT_BANK);
        }

        // 3. 查询订单信息
        ProjectOrderDO order = orderMapper.selectById(balance.getOrderId());

        // 4. 使用乐观锁更新订单余额
        int updateRows = projectOrderBalanceMapper.updateBalanceForDividend(
                balance.getId(),
                reqVO.getDividendAmount(),
                userId.toString());
        if (updateRows == 0) {
            log.error("[applyDividend] 余额更新失败（余额不足或并发冲突），余额ID: {}", balance.getId());
            throw exception(DIVIDEND_AMOUNT_EXCEEDS_LIMIT);
        }

        // 4.1 记录余额变动日志（分红提取，收益减少）
        BigDecimal afterAmount = balance.getUnwithdrawnDividend().subtract(reqVO.getDividendAmount());
        recordBalanceLog(userId, balance.getProjectId(), balance.getOrderId(),
                reqVO.getDividendAmount().negate(), afterAmount, BalanceLogTypeEnum.DIVIDEND_WITHDRAW);

        // 5. 创建账单记录
        String billNo = generateBillNo();
        ProjectBillDO bill = ProjectBillDO.builder()
                .billNo(billNo)
                .billType(BillTypeEnum.DIVIDEND.getType())
                .applyTime(LocalDateTime.now())
                .userId(userId)
                .orderId(order.getId())
                .orderNo(order.getOrderNo())
                .projectId(balance.getProjectId())
                .projectName(order.getProjectName())
                .bankAccountName(appUserBankRespVO.getBankAccountName())
                .bankAccount(appUserBankRespVO.getBankAccount())
                .bankName(appUserBankRespVO.getBankName())
                .billAmount(reqVO.getDividendAmount())
                .billCoin(balance.getEarningCurrency())
                .actualCoin(balance.getInvestmentCurrency())
                .auditStatus(AuditStatusEnum.PENDING.getStatus()) // 待审核
                .build();
        bill.setTenantId(order.getTenantId());
        projectBillMapper.insert(bill);

        log.info("[applyDividend] 分红申请成功，账单号: {}, 分红金额: {}，等待审核", billNo, reqVO.getDividendAmount());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void maturityRedemption(Long userId, AppMaturityRedemptionReqVO reqVO) {
        log.info("[maturityRedemption] 开始到期赎回，用户ID: {}, 余额ID: {}", userId, reqVO.getOrderId());

        // 1. 验证手机验证码
        validateSmsCode(userId, reqVO.getSmsCode(), reqVO.getEmailCode());

        // 2. 查询并验证订单余额
        ProjectOrderBalanceDO balance = projectOrderBalanceMapper.getProjectOrderBalance(reqVO.getOrderId(), userId);
        if (balance == null) {
            throw exception(BALANCE_NOT_FOUND);
        }
        if (!balance.getUserId().equals(userId)) {
            throw exception(BALANCE_NOT_BELONG_TO_USER);
        }
        // 持有金额如果为0则说明已经赎回了
        if (balance.getHoldAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw exception(INSUFFICIENT_HOLD_QUANTITY);
        }

        // 查询用户银行卡
        AppUserBankRespVO appUserBankRespVO = projectInfoMapper.getUserBank(userId);
        if (appUserBankRespVO == null
                || !AuditStatusEnum.APPROVED.getStatus().equals(appUserBankRespVO.getAuditStatus())) {
            throw new ServiceException(NOT_BANK);
        }

        // 3. 查询项目信息并验证是否到期
        ProjectInfoDO project = projectInfoMapper.selectById(balance.getProjectId());
        if (project.getLockEndTime() != null && LocalDate.now().isBefore(project.getLockEndTime())) {
            throw exception(PROJECT_NOT_MATURED);
        }

        // 4. 查询订单信息
        ProjectOrderDO order = orderMapper.selectById(balance.getOrderId());

        // 5. 计算赎回金额（到期赎回无手续费）
        BigDecimal redemptionAmount = balance.getHoldAmount();
        // 计算分红金额(全部赎回包含本金和分红)
        BigDecimal dividendAmount = balance.getUnwithdrawnDividend();

        // 6. 使用乐观锁更新订单余额
        int updateRows = projectOrderBalanceMapper.updateBalanceForMaturityRedemption(
                balance.getId(),
                redemptionAmount,
                userId.toString());
        if (updateRows == 0) {
            log.error("[maturityRedemption] 余额更新失败（余额不足或并发冲突），余额ID: {}", balance.getId());
            throw exception(INSUFFICIENT_HOLD_QUANTITY);
        }

        // 6.1 记录余额变动日志（到期赎回，本金减少）
        recordBalanceLog(userId, balance.getProjectId(), balance.getOrderId(),
                redemptionAmount.negate(), BigDecimal.ZERO, BalanceLogTypeEnum.MATURITY_REDEMPTION);

        if (dividendAmount.compareTo(BigDecimal.ZERO) > 0) {
            // 进行分红提取
            updateRows = projectOrderBalanceMapper.updateBalanceForDividend(
                    balance.getId(),
                    dividendAmount,
                    userId.toString());
            if (updateRows == 0) {
                log.error("[maturityRedemption] 余额更新失败（余额不足或并发冲突），余额ID: {}", balance.getId());
                throw exception(DIVIDEND_AMOUNT_EXCEEDS_LIMIT);
            }
            recordBalanceLog(userId, balance.getProjectId(), balance.getOrderId(),
                    dividendAmount.negate(), BigDecimal.ZERO, BalanceLogTypeEnum.DIVIDEND_WITHDRAW);
        }
        LocalDateTime now = LocalDateTime.now();
        // 7. 创建账单记录
        String billNo = generateBillNo();
        ProjectBillDO bill = ProjectBillDO.builder()
                .billNo(billNo)
                .billType(BillTypeEnum.MATURITY_REDEMPTION.getType())
                .applyTime(now)
                .userId(userId)
                .orderId(order.getId())
                .orderNo(order.getOrderNo())
                .projectId(balance.getProjectId())
                .projectName(order.getProjectName())
                .quantity(balance.getHoldQuantity())
                .bankAccountName(appUserBankRespVO.getBankAccountName())
                .bankAccount(appUserBankRespVO.getBankAccount())
                .bankName(appUserBankRespVO.getBankName())
                .billAmount(redemptionAmount)
                .actualAmount(redemptionAmount)
                .billCoin(balance.getInvestmentCurrency())
                .actualCoin(balance.getInvestmentCurrency())
                .auditStatus(AuditStatusEnum.PENDING.getStatus()) // 待审核
                .build();
        bill.setTenantId(order.getTenantId());
        projectBillMapper.insert(bill);
        if (dividendAmount.compareTo(BigDecimal.ZERO) > 0) {
            String dividendBillNo = generateBillNo();
            ProjectBillDO dividendBill = ProjectBillDO.builder()
                    .billNo(dividendBillNo)
                    .billType(BillTypeEnum.DIVIDEND.getType())
                    .applyTime(now)
                    .userId(userId)
                    .orderId(order.getId())
                    .orderNo(order.getOrderNo())
                    .projectId(balance.getProjectId())
                    .projectName(order.getProjectName())
                    .quantity(balance.getHoldQuantity())
                    .bankAccountName(appUserBankRespVO.getBankAccountName())
                    .bankAccount(appUserBankRespVO.getBankAccount())
                    .bankName(appUserBankRespVO.getBankName())
                    .billAmount(dividendAmount)
                    .auditStatus(AuditStatusEnum.PENDING.getStatus()) // 待审核
                    .billCoin(balance.getEarningCurrency())
                    .actualCoin(balance.getInvestmentCurrency())
                    .build();
            dividendBill.setTenantId(order.getTenantId());
            projectBillMapper.insert(dividendBill);
        }


        log.info("[maturityRedemption] 到期赎回申请成功，账单号: {}, 赎回金额: {}，等待审核", billNo, redemptionAmount);
    }

    /**
     * 验证手机验证码
     */
    private void validateSmsCode(Long userId, String smsCode,String emailCode) {
        UserInfoRespDTO user = userInfoApi.getUserInfo(userId).getCheckedData();
        if (user == null ) {
            throw exception(INVALID_SMS_CODE);
        }
        if(StringUtil.isNotBlank(emailCode) && StringUtil.isNotBlank(user.getEmail())){
            // 校验邮箱验证码
            MailCodeUseReqDTO mailCodeUseReq = new MailCodeUseReqDTO();
            mailCodeUseReq.setMail(user.getEmail());
            mailCodeUseReq.setScene(SmsSceneEnum.MEMBER_AUDIT.getScene());
            mailCodeUseReq.setCode(emailCode);

            try {
                mailSendApi.verifyMailCode(mailCodeUseReq).checkError();
                log.info("[payOrder] 验证码验证成功，用户ID: {}, 邮箱: {}", userId, user.getEmail());
            } catch (Exception e) {
                log.error("[payOrder] 验证码验证失败，用户ID: {}, 邮箱: {}, 错误信息: {}",
                        userId, user.getMobile(), e.getMessage(), e);
                throw exception(INVALID_SMS_CODE);
            }
        }else{
            SmsCodeUseReqDTO smsCodeUseReq = new SmsCodeUseReqDTO();
            smsCodeUseReq.setMobile(user.getMobile());
            smsCodeUseReq.setScene(SmsSceneEnum.MEMBER_AUDIT.getScene());
            smsCodeUseReq.setCode(smsCode);
            smsCodeUseReq.setUsedIp(ServletUtils.getClientIP());

            try {
                smsCodeApi.useSmsCode(smsCodeUseReq).checkError();
            } catch (Exception e) {
                log.error("[validateSmsCode] 验证码验证失败，用户ID: {}, 错误: {}", userId, e.getMessage());
                throw exception(INVALID_SMS_CODE);
            }
        }

    }

    /**
     * 计算提前赎回手续费率
     */
    @Override
    public BigDecimal calculateEarlyRedemptionFeeRate(String feeJson, long holdDays) {
        if (StrUtil.isBlank(feeJson)) {
            return BigDecimal.ZERO;
        }

        try {
            JSONArray feeArray = JSONUtil.parseArray(feeJson);
            BigDecimal feeRate = BigDecimal.ZERO;

            for (int i = 0; i < feeArray.size(); i++) {
                JSONObject feeConfig = feeArray.getJSONObject(i);
                int days = feeConfig.getInt("day");
                if (holdDays <= days) {
                    feeRate = feeConfig.getBigDecimal("value");
                    break;
                }
                // 如果超过所有配置，则手续费为0
                if (i == feeArray.size() - 1) {
                    //feeRate = feeConfig.getBigDecimal("value");
                    feeRate = BigDecimal.ZERO;
                }
            }
            log.info("[calculateEarlyRedemptionFeeRate] 计算提前赎回手续费率，配置: {}, 持仓天数: {}, 手续费率: {}", feeJson, holdDays,
                    feeRate);
            return feeRate.divide(BigDecimal.valueOf(100));
        } catch (Exception e) {
            log.error("[calculateEarlyRedemptionFeeRate] 解析手续费配置失败: {}", e.getMessage());
            return BigDecimal.ZERO;
        }
    }

    /**
     * 生成账单号
     */
    private String generateBillNo() {
        return "B" + System.currentTimeMillis();
    }

    /**
     * 更新项目运营统计 - 提前赎回
     */
    private void updateProjectOperationForEarlyRedemption(Long projectId, Integer quantity, BigDecimal amount) {
        // 使用原子性更新，避免并发问题
        int updateRows = projectOperationMapper.incrementEarlyRedemption(projectId, quantity, amount);
        if (updateRows == 0) {
            log.warn("[updateProjectOperationForEarlyRedemption] 项目运营统计不存在，项目ID: {}", projectId);
        }
    }

    /**
     * 更新项目运营统计 - 分红
     */
    private void updateProjectOperationForDividend(Long projectId, BigDecimal amount) {
        // 使用原子性更新，避免并发问题
        int updateRows = projectOperationMapper.incrementDividend(projectId, amount);
        if (updateRows == 0) {
            log.warn("[updateProjectOperationForDividend] 项目运营统计不存在，项目ID: {}", projectId);
        }
    }

    /**
     * 更新项目运营统计 - 到期赎回
     */
    private void updateProjectOperationForMaturityRedemption(Long projectId, BigDecimal amount) {
        // 使用原子性更新，避免并发问题
        int updateRows = projectOperationMapper.incrementMaturityRedemption(projectId, amount);
        if (updateRows == 0) {
            log.warn("[updateProjectOperationForMaturityRedemption] 项目运营统计不存在，项目ID: {}", projectId);
        }
    }

    /**
     * 记录余额变动日志
     * 
     * @param userId      用户ID
     * @param projectId   项目ID
     * @param orderId     订单ID
     * @param amount      变动金额（正数表示增加，负数表示减少）
     * @param afterAmount 变动后金额
     * @param type        变动类型
     */
    private void recordBalanceLog(Long userId, Long projectId, Long orderId,
            BigDecimal amount, BigDecimal afterAmount, BalanceLogTypeEnum type) {
        ProjectOrderBalanceLogDO logDO = ProjectOrderBalanceLogDO.builder()
                .userId(userId)
                .projectId(projectId)
                .orderId(orderId)
                .amount(amount)
                .afterAmount(afterAmount)
                .type(type.getType())
                .build();

        projectOrderBalanceLogMapper.insert(logDO);

        log.info("[recordBalanceLog] 余额变动记录已创建，用户ID: {}, 项目ID: {}, 订单ID: {}, 变动金额: {}, 变动后金额: {}, 类型: {}",
                userId, projectId, orderId, amount, afterAmount, type.getName());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @TenantIgnore
    public void cancelUnpaidOrder(Long orderId, Long projectId, Integer quantity) {
        log.info("[cancelUnpaidOrder] 开始取消未支付订单，订单ID: {}, 项目ID: {}, 数量: {}",
                orderId, projectId, quantity);

        // 1. 查询订单当前状态
        ProjectOrderDO order = orderMapper.selectById(orderId);

        if (order == null) {
            log.warn("[cancelUnpaidOrder] 订单不存在，订单ID: {}", orderId);
            return;
        }

        // 2. 检查订单状态，只有"待支付"状态的订单才需要取消
        if (!OrderStatusEnum.PENDING_PAYMENT.getStatus().equals(order.getOrderStatus())) {
            log.info("[cancelUnpaidOrder] 订单状态不是待支付，无需取消，订单ID: {}, 当前状态: {}",
                    orderId, order.getOrderStatus());
            return;
        }

        // 3. 更新订单状态为"已取消"
        ProjectOrderDO updateOrder = ProjectOrderDO.builder()
                .id(orderId)
                .orderStatus(OrderStatusEnum.CANCELLED.getStatus())
                .build();

        orderMapper.updateById(updateOrder);

        log.info("[cancelUnpaidOrder] 订单状态已更新为已取消，订单ID: {}", orderId);

        // 4. 恢复项目库存（增加剩余数量）
        projectInfoMapper.restoreStock(projectId, quantity);

        log.info("[cancelUnpaidOrder] 库存恢复成功，项目ID: {}, 恢复数量: {}", projectId, quantity);
        log.info("[cancelUnpaidOrder] 订单取消处理完成，订单ID: {}", orderId);
    }

}