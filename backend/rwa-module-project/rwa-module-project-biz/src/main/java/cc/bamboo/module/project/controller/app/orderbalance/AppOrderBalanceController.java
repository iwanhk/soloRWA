package cc.bamboo.module.project.controller.app.orderbalance;

import cc.bamboo.framework.common.pojo.CommonResult;
import cc.bamboo.framework.common.pojo.PageResult;
import cc.bamboo.framework.common.util.object.BeanUtils;
import cc.bamboo.framework.tenant.core.aop.TenantIgnore;
import cc.bamboo.module.project.controller.app.orderbalance.vo.*;
import cc.bamboo.module.project.controller.app.projectorder.vo.*;
import cc.bamboo.module.project.dal.dataobject.projectorder.ProjectOrderDO;
import cc.bamboo.module.project.service.projectorder.AppProjectOrderService;
import cc.bamboo.module.project.service.projectorderbalance.ProjectOrderBalanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

import static cc.bamboo.framework.common.pojo.CommonResult.success;
import static cc.bamboo.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

/**
 * 用户 APP - 项目订单管理
 *
 * @author Swolf
 */
@Tag(name = "用户 APP - 资产")
@RestController
@RequestMapping("/project/order-balance")
@Validated
@Slf4j
public class AppOrderBalanceController {

    @Resource
    private ProjectOrderBalanceService projectOrderBalanceService;
    @Resource
    private AppProjectOrderService appProjectOrderService;



    @GetMapping("/list")
    @Operation(summary = "查询资产列表")
    @TenantIgnore
    public CommonResult<PageResult<AppProjectOrderBalanceRespVO>> getUserOrderList(@Valid AppProjectOrderBalancePageReqVO pageReqVO) {
        return success(projectOrderBalanceService.getAppOrderBalancePage(pageReqVO));
    }

    @GetMapping("/asset-detail")
    @Operation(summary = "获得资产详情")
    @TenantIgnore
    public CommonResult<AppAssetDetailRespVO> getAssetDetail(@Valid AppAssetDetailReqVO reqVO) {
        AppAssetDetailRespVO respVO = projectOrderBalanceService.getAppAssetDetail(getLoginUserId(), reqVO);
        // 4. Calculate withdrawable balance
       if(reqVO.getOrderId() != null){
           AppAssetByCurrencyRespVO withdrawableBalance = appProjectOrderService.calculateWithdrawableBalance(
                   reqVO.getOrderId(), reqVO.getProjectId(), getLoginUserId());
           respVO.setWithdrawableBalance(withdrawableBalance);
       }

        return success(respVO);
    }

    @GetMapping("/daily-income-detail")
    @Operation(summary = "获得每日收益明细")
    @TenantIgnore
    public CommonResult<List<AppDailyIncomeRespVO>> getDailyIncomeDetail(@Valid AppDailyIncomeDetailReqVO reqVO) {
        return success(projectOrderBalanceService.getAppDailyIncomeDetail(getLoginUserId(), reqVO));
    }

    @GetMapping("/income-calendar")
    @Operation(summary = "获得收益日历")
    @TenantIgnore
    public CommonResult<List<AppIncomeCalendarRespVO>> getIncomeCalendar(@Valid AppIncomeCalendarReqVO reqVO) {
        return success(projectOrderBalanceService.getAppIncomeCalendar(getLoginUserId(), reqVO));
    }

}
