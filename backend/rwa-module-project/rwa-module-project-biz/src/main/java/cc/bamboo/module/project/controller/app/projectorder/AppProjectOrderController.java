package cc.bamboo.module.project.controller.app.projectorder;

import cc.bamboo.framework.common.pojo.CommonResult;
import cc.bamboo.framework.common.pojo.PageResult;
import cc.bamboo.framework.common.util.object.BeanUtils;
import cc.bamboo.framework.idempotent.core.annotation.Idempotent;
import cc.bamboo.framework.ratelimiter.core.annotation.RateLimiter;
import cc.bamboo.framework.tenant.core.aop.TenantIgnore;
import cc.bamboo.module.project.controller.app.projectinfo.vo.AppProjectPaymentInfoRespVO;
import cc.bamboo.module.project.controller.app.projectorder.vo.*;
import cc.bamboo.module.project.dal.dataobject.projectorder.ProjectOrderDO;
import cc.bamboo.module.project.service.projectbill.ProjectBillService;
import cc.bamboo.module.project.service.projectorder.AppProjectOrderService;
import cc.bamboo.module.user.api.userinfo.UserInfoApi;
import cc.bamboo.module.user.api.userinfo.dto.Verify2FAReqDTO;
import com.alibaba.fastjson.JSONObject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

import static cc.bamboo.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cc.bamboo.framework.common.pojo.CommonResult.success;
import static cc.bamboo.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;
import static cc.bamboo.module.project.enums.ErrorCodeConstants.FILE_SIZE_EXCEEDED;
import static cc.bamboo.module.user.enums.ErrorCodeConstants.F2A_VERIFY_CODE_ERROR;

/**
 * 用户 APP - 项目订单管理
 *
 * @author Swolf
 */
@Tag(name = "用户 APP - 项目订单管理")
@RestController
@RequestMapping("/project/project-order")
@Validated
@Slf4j
public class AppProjectOrderController {

    @Resource
    private AppProjectOrderService appProjectOrderService;

    @Resource
    private ProjectBillService projectBillService;

    @Resource
    private UserInfoApi userInfoApi;

    private static final long MAX_FILE_SIZE = 15 * 1024 * 1024;

    @PostMapping("/create")
    @Operation(summary = "创建订单")
    @TenantIgnore
    @Idempotent(timeout = 3, timeUnit = TimeUnit.SECONDS)
    public CommonResult<AppProjectOrderRespVO> createOrder(@Valid @RequestBody AppCreateOrderReqVO reqVO) {
        log.info("[createOrder] 用户请求创建订单，项目ID: {}, 钱包地址: {}, 购买数量: {}, ",
                reqVO.getProjectId(), reqVO.getAddressId(), reqVO.getQuantity());

        // 获取当前登录用户ID
        Long userId = getLoginUserId();

        // 创建订单
        ProjectOrderDO order = appProjectOrderService.createOrder(
                userId,
                reqVO.getProjectId(),
                reqVO.getAddressId(),
                reqVO.getQuantity());

        // 转换为响应VO
        AppProjectOrderRespVO respVO = AppProjectOrderRespVO.builder()
                .id(order.getId())
                .orderNo(order.getOrderNo())
                .projectId(order.getProjectId())
                .projectName(order.getProjectName())
                .subscribeQuantity(order.getSubscribeQuantity())
                .price(order.getPrice())
                .totalAmount(order.getTotalAmount())
                .orderStatus(order.getOrderStatus())
                .chainAddress(order.getChainAddress())
                .expireTime(order.getExpireTime())
                .applyDate(order.getApplyDate())
                .build();

        log.info("[createOrder] 订单创建成功，订单ID: {}, 订单号: {}", order.getId(), order.getOrderNo());

        return success(respVO);
    }

    @PostMapping("/pay")
    @Operation(summary = "支付确认")
    @TenantIgnore
    @Idempotent(timeout = 3, timeUnit = TimeUnit.SECONDS)
    public CommonResult<Boolean> payOrder(@Valid @ModelAttribute AppPayOrderReqVO reqVO) {
        log.info("[payOrder] 用户请求支付确认，订单ID: {}, 合同号: {}",
                reqVO.getOrderId(), reqVO.getContractNo());

        // 获取当前登录用户ID
        Long userId = getLoginUserId();

        // 验证F2A（如果用户开启了）
        Verify2FAReqDTO verify2FAReqDTO = new Verify2FAReqDTO();
        verify2FAReqDTO.setUserId(userId);
        verify2FAReqDTO.setCode(reqVO.getFtaCode());
        Boolean f2aResult = userInfoApi.verify2FAIfEnabled(verify2FAReqDTO).getData();
        if (f2aResult == null || !f2aResult) {
            throw exception(F2A_VERIFY_CODE_ERROR);
        }

        // 1. 先判断文件是否为空（替代@NotNull注解的逻辑）
        if (reqVO.getPayVoucher() == null || reqVO.getPayVoucher().isEmpty()) {
            throw exception(FILE_SIZE_EXCEEDED);
        }

        // 2. 直接判断文件大小
        long fileSize = reqVO.getPayVoucher().getSize();
        if (fileSize > MAX_FILE_SIZE) {
            throw exception(FILE_SIZE_EXCEEDED);
        }

        // 支付确认
        appProjectOrderService.payOrder(
                userId,
                reqVO);

        log.info("[payOrder] 支付确认成功，订单ID: {}", reqVO.getOrderId());

        return success(true);
    }

    @GetMapping("/list")
    @Operation(summary = "查询用户的订单列表")
    @TenantIgnore
    public CommonResult<PageResult<AppOrderListRespVO>> getUserOrderList(@Valid AppOrderListReqVO reqVO) {

        // 查询订单列表
        PageResult<AppOrderListRespVO> orderList = appProjectOrderService.getUserOrderList(reqVO);

        return success(orderList);
    }

    @GetMapping("/detail")
    @Operation(summary = "查询用户的订单详情")
    @Parameter(name = "id", description = "订单ID", required = true, example = "1")
    @TenantIgnore
    public CommonResult<AppOrderDetailRespVO> getUserOrderDetail(
            @RequestParam("id") @NotNull(message = "订单ID不能为空") Long id) {

        // 获取当前登录用户ID
        Long userId = getLoginUserId();

        // 查询订单详情
        AppOrderDetailRespVO respVO = appProjectOrderService.getUserOrderDetail(userId, id);

        return success(respVO);
    }

    @PostMapping("/early-redemption")
    @Operation(summary = "提前赎回")
    @TenantIgnore
    @Idempotent(timeout = 3, timeUnit = TimeUnit.SECONDS)
    public CommonResult<Boolean> earlyRedemption(@Valid @RequestBody AppEarlyRedemptionReqVO reqVO) {
        log.info("[earlyRedemption] 用户请求提前赎回，余额ID: {}, 赎回份额: {}",
                reqVO.getOrderId(), reqVO.getRedemptionQuantity());

        Long userId = getLoginUserId();

        // 验证F2A（如果用户开启了）
        Verify2FAReqDTO verify2FAReqDTO = new Verify2FAReqDTO();
        verify2FAReqDTO.setUserId(userId);
        verify2FAReqDTO.setCode(reqVO.getFtaCode());
        Boolean f2aResult = userInfoApi.verify2FAIfEnabled(verify2FAReqDTO).getData();
        if (f2aResult == null || !f2aResult) {
            throw exception(F2A_VERIFY_CODE_ERROR);
        }

        appProjectOrderService.earlyRedemption(userId, reqVO);

        log.info("[earlyRedemption] 提前赎回成功");
        return success(true);
    }

    @PostMapping("/dividend")
    @Operation(summary = "分红申请")
    @TenantIgnore
    @Idempotent(timeout = 3, timeUnit = TimeUnit.SECONDS)
    public CommonResult<Boolean> applyDividend(@Valid @RequestBody AppDividendReqVO reqVO) {
        log.info("[applyDividend] 用户请求分红，余额ID: {}, 分红金额: {}",
                reqVO.getOrderId(), reqVO.getDividendAmount());

        Long userId = getLoginUserId();

        // 验证F2A（如果用户开启了）
        Verify2FAReqDTO verify2FAReqDTO = new Verify2FAReqDTO();
        verify2FAReqDTO.setUserId(userId);
        verify2FAReqDTO.setCode(reqVO.getFtaCode());
        Boolean f2aResult = userInfoApi.verify2FAIfEnabled(verify2FAReqDTO).getData();
        if (f2aResult == null || !f2aResult) {
            throw exception(F2A_VERIFY_CODE_ERROR);
        }

        appProjectOrderService.applyDividend(userId, reqVO);

        log.info("[applyDividend] 分红申请成功");
        return success(true);
    }

    @PostMapping("/maturity-redemption")
    @Operation(summary = "到期赎回")
    @TenantIgnore
    @Idempotent(timeout = 3, timeUnit = TimeUnit.SECONDS)
    public CommonResult<Boolean> maturityRedemption(@Valid @RequestBody AppMaturityRedemptionReqVO reqVO) {
        log.info("[maturityRedemption] 用户请求到期赎回，余额ID: {}", reqVO.getOrderId());

        Long userId = getLoginUserId();

        // 验证F2A（如果用户开启了）
        Verify2FAReqDTO verify2FAReqDTO = new Verify2FAReqDTO();
        verify2FAReqDTO.setUserId(userId);
        verify2FAReqDTO.setCode(reqVO.getFtaCode());
        Boolean f2aResult = userInfoApi.verify2FAIfEnabled(verify2FAReqDTO).getData();
        if (f2aResult == null || !f2aResult) {
            throw exception(F2A_VERIFY_CODE_ERROR);
        }

        appProjectOrderService.maturityRedemption(userId, reqVO);

        log.info("[maturityRedemption] 到期赎回成功");
        return success(true);
    }

    @GetMapping("/getBillList")
    @Operation(summary = "获取用户账单列表")
    @TenantIgnore
    public CommonResult<PageResult<AppProjectBillRespVO>> getBillList(@Valid AppProjectBillReqVO reqVO) {

        return success(projectBillService.getAppBillPage(reqVO));
    }

    @GetMapping("/order-status-count")
    @Operation(summary = "获取用户订单状态统计")
    @TenantIgnore
    public CommonResult<AppOrderStatusCountRespVO> getOrderStatusCount() {
        Long userId = getLoginUserId();
        return success(appProjectOrderService.getOrderStatusCount(userId));
    }

    @GetMapping("/bill-status-count")
    @Operation(summary = "获取用户账单状态统计")
    @TenantIgnore
    public CommonResult<AppBillStatusCountRespVO> getBillStatusCount() {
        Long userId = getLoginUserId();
        return success(projectBillService.getBillStatusCount(userId));
    }

    @GetMapping("/get-redemption-fee-rate")
    @Operation(summary = "获取当前订单赎回手续费率")
    @Parameter(name = "orderId", description = "订单ID", required = true)
    @TenantIgnore
    public CommonResult<BigDecimal> getRedemptionFeeRate(@RequestParam("orderId") Long orderId) {
        Long userId = getLoginUserId();
        return success(appProjectOrderService.getRedemptionFeeRate(userId, orderId));
    }
}
