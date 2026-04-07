package cc.bamboo.module.project.controller.admin.test;

import cc.bamboo.framework.common.pojo.CommonResult;
import cc.bamboo.framework.tenant.core.aop.TenantIgnore;
import cc.bamboo.module.project.controller.admin.projectorder.vo.ProjectOrderAuditReqVO;
import cc.bamboo.module.project.dal.dataobject.projectinfo.ProjectInfoDO;
import cc.bamboo.module.project.dal.dataobject.projectorder.ProjectOrderDO;
import cc.bamboo.module.project.dal.dataobject.projectorderbalance.ProjectOrderBalanceDO;
import cc.bamboo.module.project.dal.mysql.projectinfo.ProjectInfoMapper;
import cc.bamboo.module.project.dal.mysql.projectorder.ProjectOrderMapper;
import cc.bamboo.module.project.dal.mysql.projectorderbalance.ProjectOrderBalanceMapper;
import cc.bamboo.module.project.enums.OrderPayEnum;
import cc.bamboo.module.project.enums.OrderStatusEnum;
import cc.bamboo.module.project.service.projectorder.ProjectOrderService;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

import static cc.bamboo.framework.common.pojo.CommonResult.success;

/**
 * 测试 Controller - 模拟下单全流程
 * 仅在 local/dev 环境生效，用于跳过验证码、文件上传等外部依赖
 *
 * @author test
 */
@Tag(name = "测试 - 模拟下单全流程")
@RestController
@RequestMapping("/project/test")
@Profile({ "local", "dev" })
@Slf4j
public class ProjectTestController {

    @Resource
    private ProjectInfoMapper projectInfoMapper;
    @Resource
    private ProjectOrderMapper projectOrderMapper;
    @Resource
    private ProjectOrderBalanceMapper projectOrderBalanceMapper;
    @Resource
    private ProjectOrderService projectOrderService;

    /**
     * 固定的支付凭证图片地址（测试用）
     */
    private static final String DEFAULT_PAY_VOUCHER_URL = "https://test.bamboo.cc/static/test-pay-voucher.png";

    // ==================== 模拟下单 ====================

    @PostMapping("/simulate-order")
    @Operation(summary = "模拟下单（下单 + 支付 + 审核通过，一步到位）")
    @TenantIgnore
    public CommonResult<SimulateOrderResult> simulateOrder(
            @RequestParam("userId") @Parameter(description = "用户ID") Long userId,
            @RequestParam("projectId") @Parameter(description = "项目ID") Long projectId,
            @RequestParam("quantity") @Parameter(description = "购买数量") Integer quantity) {

        log.info("[simulateOrder] 开始模拟下单，用户ID: {}, 项目ID: {}, 数量: {}", userId, projectId, quantity);
        SimulateOrderResult result = new SimulateOrderResult();

        // ========== Step 1: 验证项目并计算金额 ==========
        ProjectInfoDO project = projectInfoMapper.selectById(projectId);
        if (project == null) {
            result.setSuccess(false);
            result.setError("项目不存在，projectId=" + projectId);
            return success(result);
        }
        result.setProjectName(project.getProjectName());

        BigDecimal totalAmount = project.getIssueUnitPrice().multiply(new BigDecimal(quantity));

        // ========== Step 2: 扣减库存（乐观锁）==========
        int stockUpdate = projectInfoMapper.updateStockWithOptimisticLock(projectId, quantity);
        if (stockUpdate == 0) {
            result.setSuccess(false);
            result.setError("库存不足，剩余库存: " + project.getRemainingQuantity());
            return success(result);
        }
        log.info("[simulateOrder] 库存扣减成功，项目ID: {}, 扣减数量: {}", projectId, quantity);

        // ========== Step 3: 创建订单（状态直接设为审核中，跳过待支付）==========
        String orderNo = generateOrderNo();
        String contractNo = "CT" + System.currentTimeMillis();
        LocalDateTime now = LocalDateTime.now();

        ProjectOrderDO order = ProjectOrderDO.builder()
                .orderNo(orderNo)
                .applyDate(now)
                .userId(userId)
                .orderStatus(OrderStatusEnum.UNDER_REVIEW.getStatus()) // 直接审核中
                .projectId(projectId)
                .projectName(project.getProjectName())
                .subscribeQuantity(quantity)
                .price(project.getIssueUnitPrice())
                .totalAmount(totalAmount)
                .chainAddress("0x0000000000000000000000000000000000000000") // 测试地址
                .expireTime(now.plusHours(24))
                .payType(OrderPayEnum.BANK_PAY.getType())
                .payVoucherUrl(DEFAULT_PAY_VOUCHER_URL)
                .contractNo(contractNo)
                .confirmPurchaseTime(now)
                .investmentCurrency(project.getInvestmentCurrency())
                .earningCurrency(project.getEarningCurrency())
                .projectConfigType(project.getProjectConfigType())
                .build();
        order.setTenantId(project.getTenantId());
        projectOrderMapper.insert(order);

        log.info("[simulateOrder] 订单创建成功，订单ID: {}, 订单号: {}, 金额: {}",
                order.getId(), orderNo, totalAmount);

        // ========== Step 4: 调用审核通过（走正式流程，创建余额记录+更新统计）==========
        try {
            ProjectOrderAuditReqVO auditReqVO = new ProjectOrderAuditReqVO();
            auditReqVO.setId(order.getId());
            auditReqVO.setApproved(true);
            auditReqVO.setAuditRemark("测试自动审核通过");
            auditReqVO.setContractFileUrls("[\"" + DEFAULT_PAY_VOUCHER_URL + "\"]");

            // 审核人用固定的测试管理员
            projectOrderService.auditOrder(auditReqVO, 1L, "测试管理员");
            log.info("[simulateOrder] 订单审核通过，订单ID: {}", order.getId());
        } catch (Exception e) {
            log.error("[simulateOrder] 审核失败: {}", e.getMessage(), e);
            result.setSuccess(false);
            result.setError("审核失败: " + e.getMessage());
            result.setOrderId(order.getId());
            result.setOrderNo(orderNo);
            return success(result);
        }

        // ========== 查询结果 ==========
        ProjectOrderDO finalOrder = projectOrderMapper.selectById(order.getId());
        ProjectOrderBalanceDO balance = projectOrderBalanceMapper.selectOne(
                new LambdaQueryWrapper<ProjectOrderBalanceDO>()
                        .eq(ProjectOrderBalanceDO::getOrderId, order.getId())
                        .eq(ProjectOrderBalanceDO::getUserId, userId));

        result.setSuccess(true);
        result.setOrderId(order.getId());
        result.setOrderNo(orderNo);
        result.setContractNo(contractNo);
        result.setTotalAmount(totalAmount);
        result.setPrice(project.getIssueUnitPrice());
        result.setQuantity(quantity);
        result.setOrderStatus(finalOrder.getOrderStatus());
        result.setOrderStatusText("审核通过");

        if (balance != null) {
            result.setBalanceId(balance.getId());
            result.setHoldQuantity(balance.getHoldQuantity());
            result.setHoldAmount(balance.getHoldAmount());
            result.setPrincipalAmount(balance.getPrincipalAmount());
        }

        log.info("[simulateOrder] 模拟下单完成！订单号: {}, 余额ID: {}", orderNo,
                balance != null ? balance.getId() : "无");

        return success(result);
    }

    // ==================== 仅创建订单（不自动审核）====================

    @PostMapping("/create-order-only")
    @Operation(summary = "仅模拟下单+支付（不审核，状态为审核中）")
    @TenantIgnore
    public CommonResult<Map<String, Object>> createOrderOnly(
            @RequestParam("userId") Long userId,
            @RequestParam("projectId") Long projectId,
            @RequestParam("quantity") Integer quantity) {

        ProjectInfoDO project = projectInfoMapper.selectById(projectId);
        if (project == null) {
            Map<String, Object> errMap = new LinkedHashMap<>();
            errMap.put("error", "项目不存在");
            return success(errMap);
        }

        BigDecimal totalAmount = project.getIssueUnitPrice().multiply(new BigDecimal(quantity));

        // 扣减库存
        int stockUpdate = projectInfoMapper.updateStockWithOptimisticLock(projectId, quantity);
        if (stockUpdate == 0) {
            Map<String, Object> errMap = new LinkedHashMap<>();
            errMap.put("error", "库存不足");
            return success(errMap);
        }

        // 创建订单
        String orderNo = generateOrderNo();
        String contractNo = "CT" + System.currentTimeMillis();
        LocalDateTime now = LocalDateTime.now();

        ProjectOrderDO order = ProjectOrderDO.builder()
                .orderNo(orderNo)
                .applyDate(now)
                .userId(userId)
                .orderStatus(OrderStatusEnum.UNDER_REVIEW.getStatus())
                .projectId(projectId)
                .projectName(project.getProjectName())
                .subscribeQuantity(quantity)
                .price(project.getIssueUnitPrice())
                .totalAmount(totalAmount)
                .chainAddress("0x0000000000000000000000000000000000000000")
                .expireTime(now.plusHours(24))
                .payType(OrderPayEnum.BANK_PAY.getType())
                .payVoucherUrl(DEFAULT_PAY_VOUCHER_URL)
                .contractNo(contractNo)
                .confirmPurchaseTime(now)
                .investmentCurrency(project.getInvestmentCurrency())
                .earningCurrency(project.getEarningCurrency())
                .projectConfigType(project.getProjectConfigType())
                .build();
        order.setTenantId(project.getTenantId());
        projectOrderMapper.insert(order);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("orderId", order.getId());
        result.put("orderNo", orderNo);
        result.put("contractNo", contractNo);
        result.put("totalAmount", totalAmount);
        result.put("orderStatus", OrderStatusEnum.UNDER_REVIEW.getStatus());
        result.put("message", "订单已创建（审核中），可调用管理后台审核接口进行审核");
        return success(result);
    }

    // ==================== 批量下单 ====================

    @PostMapping("/batch-simulate-order")
    @Operation(summary = "批量模拟下单（为同一用户创建多个订单）")
    @TenantIgnore
    public CommonResult<Map<String, Object>> batchSimulateOrder(
            @RequestParam("userId") Long userId,
            @RequestParam("projectId") Long projectId,
            @RequestParam("quantity") Integer quantity,
            @RequestParam(value = "count", defaultValue = "3") Integer count) {

        Map<String, Object> results = new LinkedHashMap<>();
        int successCount = 0;
        int failCount = 0;

        for (int i = 0; i < count; i++) {
            try {
                CommonResult<SimulateOrderResult> orderResult = simulateOrder(userId, projectId, quantity);
                SimulateOrderResult data = orderResult.getData();
                if (data.isSuccess()) {
                    successCount++;
                    Map<String, Object> orderMap = new LinkedHashMap<>();
                    orderMap.put("orderId", data.getOrderId());
                    orderMap.put("orderNo", data.getOrderNo());
                    orderMap.put("balanceId", data.getBalanceId());
                    results.put("order_" + (i + 1), orderMap);
                } else {
                    failCount++;
                    Map<String, Object> errMap = new LinkedHashMap<>();
                    errMap.put("error", data.getError());
                    results.put("order_" + (i + 1), errMap);
                    break; // 失败则停止
                }
            } catch (Exception e) {
                failCount++;
                Map<String, Object> errMap2 = new LinkedHashMap<>();
                errMap2.put("error", e.getMessage());
                results.put("order_" + (i + 1), errMap2);
                break;
            }
        }

        Map<String, Object> summaryMap = new LinkedHashMap<>();
        summaryMap.put("total", count);
        summaryMap.put("success", successCount);
        summaryMap.put("fail", failCount);
        results.put("summary", summaryMap);

        return success(results);
    }

    // ==================== 工具方法 ====================

    private String generateOrderNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String randomStr = IdUtil.fastSimpleUUID().substring(0, 6).toUpperCase();
        return "TEST" + timestamp + randomStr;
    }

    // ==================== 结果 DTO ====================

    @Data
    public static class SimulateOrderResult {
        private boolean success;
        private String error;

        // 订单信息
        private Long orderId;
        private String orderNo;
        private String contractNo;
        private String projectName;
        private BigDecimal price;
        private Integer quantity;
        private BigDecimal totalAmount;
        private Integer orderStatus;
        private String orderStatusText;

        // 余额信息（审核通过后才有）
        private Long balanceId;
        private Integer holdQuantity;
        private BigDecimal holdAmount;
        private BigDecimal principalAmount;
    }
}
