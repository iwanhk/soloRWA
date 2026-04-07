package cc.bamboo.module.project.service.projectbill;

import cc.bamboo.module.project.controller.app.projectorder.vo.AppBillStatusCountRespVO;
import cc.bamboo.module.project.controller.app.projectorder.vo.AppProjectBillReqVO;
import cc.bamboo.module.project.controller.app.projectorder.vo.AppProjectBillRespVO;
import cc.bamboo.module.project.dal.dataobject.projectorder.ProjectOrderDO;
import cc.bamboo.module.project.dal.dataobject.projectorderbalance.ProjectOrderBalanceDO;
import cc.bamboo.module.project.dal.dataobject.projectorderbalancelog.ProjectOrderBalanceLogDO;
import cc.bamboo.module.project.dal.mysql.projectbill.ProjectBillMapper;
import cc.bamboo.module.project.dal.mysql.projectoperation.ProjectOperationMapper;
import cc.bamboo.module.project.dal.mysql.projectorder.ProjectOrderMapper;
import cc.bamboo.module.project.dal.mysql.projectorderbalance.ProjectOrderBalanceMapper;
import cc.bamboo.module.project.dal.mysql.projectorderbalancelog.ProjectOrderBalanceLogMapper;
import cc.bamboo.module.project.enums.BalanceLogTypeEnum;
import cc.bamboo.module.project.enums.OrderStatusEnum;
import cc.bamboo.module.project.service.message.ProjectMessageService;
import cc.bamboo.module.user.enums.notice.NoticeTemplateEnum;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import cc.bamboo.module.project.controller.admin.projectbill.vo.*;
import cc.bamboo.module.project.dal.dataobject.projectbill.ProjectBillDO;
import cc.bamboo.module.project.enums.AuditStatusEnum;
import cc.bamboo.module.project.enums.BillTypeEnum;
import cc.bamboo.framework.common.pojo.PageResult;
import cc.bamboo.framework.common.util.object.BeanUtils;

import cc.bamboo.module.user.api.userinfo.UserInfoApi;
import cc.bamboo.module.user.api.userinfo.dto.UserInfoRespDTO;

import static cc.bamboo.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cc.bamboo.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;
import static cc.bamboo.module.project.enums.ErrorCodeConstants.*;
import static cc.bamboo.module.user.enums.ApiConstants.*;

/**
 * 项目账单管理表 Service 实现类
 *
 * @author Swolf
 */
@Service
@Validated
@Slf4j
public class ProjectBillServiceImpl implements ProjectBillService {

    @Resource
    private ProjectBillMapper billMapper;

    @Resource
    private UserInfoApi userInfoApi;

    @Resource
    private ProjectOperationMapper projectOperationMapper;

    @Resource
    private ProjectOrderBalanceMapper projectOrderBalanceMapper;

    @Resource
    private ProjectOrderBalanceLogMapper projectOrderBalanceLogMapper;

    @Resource
    private ProjectMessageService projectMessageService;

    @Resource
    private ProjectOrderMapper projectOrderMapper;

    @Override
    public Long createBill(ProjectBillSaveReqVO createReqVO) {
        // 插入
        ProjectBillDO bill = BeanUtils.toBean(createReqVO, ProjectBillDO.class);
        billMapper.insert(bill);
        // 返回
        return bill.getId();
    }

    @Override
    public void updateBill(ProjectBillSaveReqVO updateReqVO) {
        // 校验存在
        validateBillExists(updateReqVO.getId());
        // 更新
        ProjectBillDO updateObj = BeanUtils.toBean(updateReqVO, ProjectBillDO.class);
        billMapper.updateById(updateObj);
    }

    @Override
    public void deleteBill(Long id) {
        // 校验存在
        validateBillExists(id);
        // 删除
        billMapper.deleteById(id);
    }

    private void validateBillExists(Long id) {
        if (billMapper.selectById(id) == null) {
            throw exception(BILL_NOT_EXISTS);
        }
    }

    @Override
    public ProjectBillDO getBill(Long id) {
        return billMapper.selectById(id);
    }

    @Override
    public PageResult<ProjectBillDO> getBillPage(ProjectBillPageReqVO pageReqVO) {
        return billMapper.selectPage(pageReqVO);
    }

    @Override
    public PageResult<ProjectBillRespVO> getBillPageWithUserName(ProjectBillPageReqVO pageReqVO) {
        // 查询账单分页
        PageResult<ProjectBillDO> billPage = billMapper.selectPage(pageReqVO);

        // 转换为 VO 并填充额外信息
        List<ProjectBillRespVO> voList = billPage.getList().stream()
                .map(this::convertToRespVO)
                .collect(Collectors.toList());

        return new PageResult<>(voList, billPage.getTotal());
    }

    @Override
    public ProjectBillRespVO getBillDetail(Long id) {
        ProjectBillDO bill = billMapper.selectById(id);
        if (bill == null) {
            throw exception(BILL_NOT_FOUND);
        }
        return convertToRespVO(bill);
    }

    /**
     * 转换为响应 VO 并填充额外信息
     */
    private ProjectBillRespVO convertToRespVO(ProjectBillDO bill) {
        ProjectBillRespVO vo = BeanUtils.toBean(bill, ProjectBillRespVO.class);

        // 填充账单类型名称
        for (BillTypeEnum typeEnum : BillTypeEnum.values()) {
            if (typeEnum.getType().equals(bill.getBillType())) {
                vo.setBillTypeName(typeEnum.getName());
                break;
            }
        }

        // 填充审核状态名称
        for (AuditStatusEnum statusEnum : AuditStatusEnum.values()) {
            if (statusEnum.getStatus().equals(bill.getAuditStatus())) {
                vo.setAuditStatusName(statusEnum.getName());
                break;
            }
        }

        // 查询用户名称
        try {
            UserInfoRespDTO user = userInfoApi.getUserInfo(bill.getUserId()).getCheckedData();
            if (user != null) {
                vo.setUserName(user.getRealName());
            }
        } catch (Exception e) {
            log.warn("[convertToRespVO] 查询用户信息失败，用户ID: {}", bill.getUserId(), e);
        }

        // 判断是否已支付
        vo.setIsPaid(StrUtil.isNotBlank(bill.getPayVoucherUrl()));

        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void auditBill(ProjectBillAuditReqVO auditReqVO, Long auditUserId, String auditUserName) {
        log.info("[auditBill] 开始审核账单，账单ID: {}, 审核结果: {}, 审核人ID: {}",
                auditReqVO.getId(), auditReqVO.getApproved(), auditUserId);

        // 1. 验证账单存在性
        ProjectBillDO bill = billMapper.selectById(auditReqVO.getId());
        bill.setArrivalDay(auditReqVO.getArrivalDays());
        if (bill == null) {
            log.warn("[auditBill] 账单不存在，账单ID: {}", auditReqVO.getId());
            throw exception(BILL_NOT_FOUND);
        }

        // 2. 验证账单状态（必须是"待审核"）
        if (!AuditStatusEnum.PENDING.getStatus().equals(bill.getAuditStatus())) {
            log.warn("[auditBill] 账单不是待审核状态，账单ID: {}, 账单状态: {}",
                    auditReqVO.getId(), bill.getAuditStatus());
            throw exception(BILL_NOT_PENDING);
        }

        // 3. 验证审核不通过时必须填写原因
        if (!auditReqVO.getApproved() && StrUtil.isBlank(auditReqVO.getAuditRemark())) {
            log.warn("[auditBill] 审核不通过时必须填写原因，账单ID: {}", auditReqVO.getId());
            throw exception(AUDIT_REMARK_REQUIRED_FOR_REJECTION);
        }

        LocalDateTime now = LocalDateTime.now();
        Integer auditStatus = auditReqVO.getApproved() ? AuditStatusEnum.APPROVED.getStatus()
                : AuditStatusEnum.REJECTED.getStatus();

        // 4. 处理支付凭证
        String payVoucherUrl = null;
        LocalDateTime payTime = null;
        if (auditReqVO.getApproved() && StrUtil.isNotBlank(auditReqVO.getPayVoucherUrl())) {
            payVoucherUrl = auditReqVO.getPayVoucherUrl();
            payTime = now;
            log.info("[auditBill] 审核通过并上传支付凭证，账单ID: {}", auditReqVO.getId());
        }
        bill.setAuditRemark(auditReqVO.getAuditRemark());
        // 5. 使用乐观锁更新账单审核信息
        int updateRows = billMapper.updateAuditStatus(
                auditReqVO.getId(),
                auditStatus,
                auditUserId,
                auditUserName,
                now,
                auditReqVO.getAuditRemark(),
                auditReqVO.getActualAmount(),
                payVoucherUrl,
                payTime,
                auditReqVO.getArrivalDays());

        if (updateRows == 0) {
            log.error("[auditBill] 账单状态更新失败（状态已变更或并发冲突），账单ID: {}", auditReqVO.getId());
            throw exception(BILL_AUDIT_CONFLICT);
        }

        // 6. 根据审核结果处理后续逻辑
        if (auditReqVO.getApproved()) {
            // 6.1 审核通过：更新项目运营统计
            handleBillApproved(bill);
            //处理订单状态
            dealOrderStatus(bill);
        } else {
            // 6.2 审核不通过：退还扣除的金额
            handleBillRejected(bill);
        }

        // 6.3 发送审核消息
        sendMessage(auditReqVO.getApproved(), bill);
        log.info("[auditBill] 账单审核完成，账单ID: {}, 审核结果: {}, 是否上传凭证: {}",
                auditReqVO.getId(), auditReqVO.getApproved() ? "通过" : "不通过", payVoucherUrl != null);
    }

    /**
     * 发送审核消息
     *
     * @param isApproved
     * @param bill
     * @author: Hus
     * @date: 2026/1/16 15:11
     * @return: void
     * @description
     */
    private void sendMessage(Boolean isApproved, ProjectBillDO bill) {
        NoticeTemplateEnum noticeTemplateEnum = null;
        Map<String, Object> templateParams = new HashMap<>();
        templateParams.put(PARAM_PROJECT, bill.getProjectName());
        // 根据账单类型更新项目运营统计
        if (BillTypeEnum.DIVIDEND.getType().equals(bill.getBillType())) {

            if (isApproved) {
                noticeTemplateEnum = NoticeTemplateEnum.DIVIDENDS_APPROVED;
                templateParams.put(PARAM_DAY, bill.getArrivalDay());
            } else {
                noticeTemplateEnum = NoticeTemplateEnum.DIVIDENDS_REJECTED;
                templateParams.put(PARAM_REASON, bill.getAuditRemark());
            }

        } else if (BillTypeEnum.EARLY_REDEMPTION.getType().equals(bill.getBillType())) {

            if (isApproved) {
                noticeTemplateEnum = NoticeTemplateEnum.REDEMPTION_ADVANCE_APPROVED;
                templateParams.put(PARAM_DAY, bill.getArrivalDay());
            } else {
                noticeTemplateEnum = NoticeTemplateEnum.REDEMPTION_ADVANCE_REJECTED;
                templateParams.put(PARAM_REASON, bill.getAuditRemark());
            }

        } else if (BillTypeEnum.MATURITY_REDEMPTION.getType().equals(bill.getBillType())) {
            if (isApproved) {
                noticeTemplateEnum = NoticeTemplateEnum.REDEMPTION_DUE_APPROVED;
                templateParams.put(PARAM_DAY, bill.getArrivalDay());
            } else {
                noticeTemplateEnum = NoticeTemplateEnum.REDEMPTION_DUE_REJECTED;
                templateParams.put(PARAM_REASON, bill.getAuditRemark());
            }
        }
        // 发送分红成功消息
        projectMessageService.sendOrderMessageAsync(
                bill.getUserId(),
                bill.getOrderId(),
                noticeTemplateEnum.getCode(), templateParams);
    }

    /**
     * 处理账单审核通过：更新项目运营统计
     */
    private void handleBillApproved(ProjectBillDO bill) {
        log.info("[handleBillApproved] 处理账单审核通过，账单ID: {}, 账单类型: {}",
                bill.getId(), bill.getBillType());

        // 根据账单类型更新项目运营统计
        if (BillTypeEnum.DIVIDEND.getType().equals(bill.getBillType())) {
            // 分红：更新分红统计
            int updateRows = projectOperationMapper.incrementDividend(bill.getProjectId(), bill.getBillAmount());
            if (updateRows == 0) {
                log.warn("[handleBillApproved] 项目运营统计不存在，项目ID: {}", bill.getProjectId());
            }
            log.info("[handleBillApproved] 分红统计已更新，项目ID: {}, 分红金额: {}",
                    bill.getProjectId(), bill.getBillAmount());

        } else if (BillTypeEnum.EARLY_REDEMPTION.getType().equals(bill.getBillType())) {
            // 提前赎回：更新提前赎回统计
            // 需要从账单金额反推赎回份额（账单金额 / 单价）
            // 这里简化处理，直接使用金额，份额在申请时已经扣除
            int updateRows = projectOperationMapper.incrementEarlyRedemption(
                    bill.getProjectId(), 0, bill.getBillAmount());
            if (updateRows == 0) {
                log.warn("[handleBillApproved] 项目运营统计不存在，项目ID: {}", bill.getProjectId());
            }
            log.info("[handleBillApproved] 提前赎回统计已更新，项目ID: {}, 赎回金额: {}",
                    bill.getProjectId(), bill.getBillAmount());

        } else if (BillTypeEnum.MATURITY_REDEMPTION.getType().equals(bill.getBillType())) {
            // 到期赎回：更新到期赎回统计
            int updateRows = projectOperationMapper.incrementMaturityRedemption(
                    bill.getProjectId(), bill.getBillAmount());
            if (updateRows == 0) {
                log.warn("[handleBillApproved] 项目运营统计不存在，项目ID: {}", bill.getProjectId());
            }
            log.info("[handleBillApproved] 到期赎回统计已更新，项目ID: {}, 赎回金额: {}",
                    bill.getProjectId(), bill.getBillAmount());
        }
    }

    /**
     * 处理账单审核不通过：退还扣除的金额
     */
    private void handleBillRejected(ProjectBillDO bill) {
        log.info("[handleBillRejected] 处理账单审核不通过，账单ID: {}, 账单类型: {}",
                bill.getId(), bill.getBillType());

        // 查询订单余额（通过userId和OrderId查询）
        ProjectOrderBalanceDO balance = projectOrderBalanceMapper.selectOne(
                ProjectOrderBalanceDO::getUserId, bill.getUserId(),
                ProjectOrderBalanceDO::getOrderId, bill.getOrderId());

        if (balance == null) {
            log.error("[handleBillRejected] 未找到订单余额，用户ID: {}, 项目ID: {}",
                    bill.getUserId(), bill.getOrderId());
            return;
        }

        // 根据账单类型退还金额
        if (BillTypeEnum.DIVIDEND.getType().equals(bill.getBillType())) {
            // 分红审核不通过：退还未提取分红
            BigDecimal refundAmount = bill.getBillAmount();
            int updateRows = projectOrderBalanceMapper.refundDividend(balance.getId(), refundAmount);

            if (updateRows == 0) {
                log.error("[handleBillRejected] 退还分红失败，余额ID: {}", balance.getId());
                return;
            }

            // 记录余额变动日志（分红退还，收益增加）
            BigDecimal afterAmount = balance.getUnwithdrawnDividend().add(refundAmount);
            recordBalanceLog(bill.getUserId(), bill.getProjectId(), balance.getOrderId(),
                    refundAmount, afterAmount, BalanceLogTypeEnum.DIVIDEND_REFUND);

            log.info("[handleBillRejected] 分红已退还，余额ID: {}, 退还金额: {}",
                    balance.getId(), refundAmount);

        } else if (BillTypeEnum.EARLY_REDEMPTION.getType().equals(bill.getBillType()) ||
                BillTypeEnum.MATURITY_REDEMPTION.getType().equals(bill.getBillType())) {
            // 赎回审核不通过：退还本金和份额
            BigDecimal refundAmount = bill.getBillAmount();

            int updateRows = projectOrderBalanceMapper.refundRedemption(
                    balance.getId(), bill.getQuantity(), refundAmount);

            if (updateRows == 0) {
                log.error("[handleBillRejected] 退还赎回失败，余额ID: {}", balance.getId());
                return;
            }

            // 记录余额变动日志（赎回退还，本金增加）
            BigDecimal afterAmount = balance.getHoldAmount().add(refundAmount);
            BalanceLogTypeEnum logType = BillTypeEnum.EARLY_REDEMPTION.getType().equals(bill.getBillType())
                    ? BalanceLogTypeEnum.EARLY_REDEMPTION_REFUND
                    : BalanceLogTypeEnum.MATURITY_REDEMPTION_REFUND;
            recordBalanceLog(bill.getUserId(), bill.getProjectId(), balance.getOrderId(),
                    refundAmount, afterAmount, logType);

            log.info("[handleBillRejected] 赎回已退还，余额ID: {}, 退还金额: {}",
                    balance.getId(), refundAmount);
        }
    }

    /**
     * 记录余额变动日志
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
    public void uploadPayVoucher(ProjectBillUploadVoucherReqVO uploadReqVO) {
        log.info("[uploadPayVoucher] 开始上传支付凭证，账单ID: {}", uploadReqVO.getId());

        // 1. 验证账单存在性
        ProjectBillDO bill = billMapper.selectById(uploadReqVO.getId());
        if (bill == null) {
            log.warn("[uploadPayVoucher] 账单不存在，账单ID: {}", uploadReqVO.getId());
            throw exception(BILL_NOT_FOUND);
        }

        // 2. 验证账单审核状态为审核通过
        if (!AuditStatusEnum.APPROVED.getStatus().equals(bill.getAuditStatus())) {
            log.warn("[uploadPayVoucher] 账单不是审核通过状态，账单ID: {}, 账单状态: {}",
                    uploadReqVO.getId(), bill.getAuditStatus());
            throw exception(BILL_NOT_APPROVED);
        }

        // 3. 验证账单尚未上传支付凭证
        if (StrUtil.isNotBlank(bill.getPayVoucherUrl())) {
            log.warn("[uploadPayVoucher] 账单已上传支付凭证，账单ID: {}", uploadReqVO.getId());
            throw exception(BILL_ALREADY_PAID);
        }

        // 4. 使用乐观锁更新支付凭证

        LambdaUpdateWrapper<ProjectBillDO> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(ProjectBillDO::getId, uploadReqVO.getId());
        updateWrapper.eq(ProjectBillDO::getAuditStatus, AuditStatusEnum.APPROVED.getStatus());
        updateWrapper.set(ProjectBillDO::getPayTime, uploadReqVO.getPayTime());
        updateWrapper.set(ProjectBillDO::getActualAmount, uploadReqVO.getActualAmount());
        updateWrapper.set(ProjectBillDO::getActualCoin, uploadReqVO.getActualCoin());
        updateWrapper.set(ProjectBillDO::getActualExchangeRate, uploadReqVO.getActualExchangeRate());
        updateWrapper.set(ProjectBillDO::getAuditStatus, AuditStatusEnum.PAY.getStatus());
        updateWrapper.set(ProjectBillDO::getPayVoucherUrl, uploadReqVO.getPayVoucherUrl());
        int updateRows = billMapper.update(updateWrapper);
        if (updateRows == 0) {
            log.error("[uploadPayVoucher] 支付凭证更新失败（状态已变更或并发冲突），账单ID: {}", uploadReqVO.getId());
            throw exception(BILL_AUDIT_CONFLICT);
        }

        log.info("[uploadPayVoucher] 支付凭证上传成功，账单ID: {}", uploadReqVO.getId());
    }

    @Override
    public ProjectBillStatisticsRespVO getBillStatistics(ProjectBillStatisticsReqVO reqVO) {
        LocalDateTime startTime = null;
        LocalDateTime endTime = null;

        if (reqVO.getTimeRange() != null && reqVO.getTimeRange().length == 2) {
            startTime = reqVO.getTimeRange()[0];
            endTime = reqVO.getTimeRange()[1];
        }

        // 统计各项数据
        Long pendingCount = billMapper.countPending(startTime, endTime);
        Long approvedUnpaidCount = billMapper.countApprovedUnpaid(startTime, endTime);
        Long paidCount = billMapper.countPaid(startTime, endTime);
        BigDecimal dividendAmount = billMapper.sumDividendAmount(startTime, endTime);
        BigDecimal redemptionAmount = billMapper.sumRedemptionAmount(startTime, endTime);

        return ProjectBillStatisticsRespVO.builder()
                .pendingCount(pendingCount)
                .approvedUnpaidCount(approvedUnpaidCount)
                .paidCount(paidCount)
                .dividendAmount(dividendAmount)
                .redemptionAmount(redemptionAmount)
                .build();
    }

    @Override
    public PageResult<AppProjectBillRespVO> getAppBillPage(AppProjectBillReqVO pageReqVO) {
        Long userId = getLoginUserId();
        LambdaQueryWrapper<ProjectBillDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ProjectBillDO::getUserId, userId);
        queryWrapper.eq(pageReqVO.getProjectId() != null, ProjectBillDO::getProjectId, pageReqVO.getProjectId());
        queryWrapper.eq(pageReqVO.getAuditStatus() != null, ProjectBillDO::getAuditStatus, pageReqVO.getAuditStatus());
        queryWrapper.eq(pageReqVO.getBillType() != null, ProjectBillDO::getBillType, pageReqVO.getBillType());
        queryWrapper.eq(pageReqVO.getOrderId() != null, ProjectBillDO::getOrderId, pageReqVO.getOrderId());
        queryWrapper.orderByDesc(ProjectBillDO::getCreateTime);
        PageResult<ProjectBillDO> pageResult = billMapper.selectPage(pageReqVO, queryWrapper);
        return BeanUtils.toBean(pageResult, AppProjectBillRespVO.class);
    }

    @Override
    public AppBillStatusCountRespVO getBillStatusCount(
            Long userId) {
        log.info("[getBillStatusCount] 获取用户账单状态统计，用户ID: {}", userId);

        cc.bamboo.module.project.controller.app.projectorder.vo.AppBillStatusCountRespVO result = new cc.bamboo.module.project.controller.app.projectorder.vo.AppBillStatusCountRespVO();

        // 查询用户所有账单
        List<ProjectBillDO> bills = billMapper.selectList(
                new LambdaQueryWrapper<ProjectBillDO>()
                        .eq(ProjectBillDO::getUserId, userId));

        int pending = 0; // auditStatus = 1
        int approved = 0; // auditStatus = 2
        int rejected = 0; // auditStatus = 3

        for (ProjectBillDO bill : bills) {
            if (bill.getAuditStatus() == null)
                continue;
            switch (bill.getAuditStatus()) {
                case 1:
                    pending++;
                    break;
                case 2:
                    approved++;
                    break;
                case 3:
                    rejected++;
                    break;
            }
        }

        result.setPendingCount(pending);
        result.setApprovedCount(approved);
        result.setRejectedCount(rejected);
        result.setTotalCount(bills.size());

        return result;
    }

    //处理订单状态
    public void dealOrderStatus(ProjectBillDO bill){
        if(BillTypeEnum.EARLY_REDEMPTION.getType().equals(bill.getBillType())){
            //查看是否还有份额，如果没有了，则将订单改为结束
            //查询订单是否还有份额
            LambdaQueryWrapper<ProjectOrderBalanceDO>  queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(ProjectOrderBalanceDO::getProjectId, bill.getProjectId());
            queryWrapper.eq(ProjectOrderBalanceDO::getOrderId, bill.getOrderId());
            queryWrapper.eq(ProjectOrderBalanceDO::getUserId, bill.getUserId());
            queryWrapper.last("limit 1");
            ProjectOrderBalanceDO projectOrderBalanceDO = projectOrderBalanceMapper.selectOne(queryWrapper);
            if(0 == projectOrderBalanceDO.getHoldQuantity()){
                //结束订单
                ProjectOrderDO projectOrderDO = new ProjectOrderDO();
                projectOrderDO.setUserId(bill.getUserId());
                projectOrderDO.setId(bill.getOrderId());
                projectOrderDO.setOrderStatus(OrderStatusEnum.ENDED.getStatus());
                projectOrderMapper.updateById(projectOrderDO);
            }
        }else if(BillTypeEnum.MATURITY_REDEMPTION.getType().equals(bill.getBillType())){
            //结束订单
            ProjectOrderDO projectOrderDO = new ProjectOrderDO();
            projectOrderDO.setUserId(bill.getUserId());
            projectOrderDO.setId(bill.getOrderId());
            projectOrderDO.setOrderStatus(OrderStatusEnum.ENDED.getStatus());
            projectOrderMapper.updateById(projectOrderDO);
        }
    }
}