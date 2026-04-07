package cc.bamboo.module.project.controller.admin.userprojectbalance;

import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;

import javax.validation.constraints.*;
import javax.validation.*;
import javax.servlet.http.*;
import java.util.*;
import java.io.IOException;

import cc.bamboo.framework.common.pojo.PageParam;
import cc.bamboo.framework.common.pojo.PageResult;
import cc.bamboo.framework.common.pojo.CommonResult;
import cc.bamboo.framework.common.util.object.BeanUtils;
import static cc.bamboo.framework.common.pojo.CommonResult.success;

import cc.bamboo.framework.excel.core.util.ExcelUtils;

import cc.bamboo.framework.apilog.core.annotation.ApiAccessLog;
import static cc.bamboo.framework.apilog.core.enums.OperateTypeEnum.*;

import cc.bamboo.module.project.controller.admin.userprojectbalance.vo.*;
import cc.bamboo.module.project.dal.dataobject.userprojectbalance.UserProjectBalanceDO;
import cc.bamboo.module.project.service.userprojectbalance.UserProjectBalanceService;

@Tag(name = "管理后台 - 用户项目余额表（本金/收益汇总）")
@RestController
@RequestMapping("/project/user-project-balance")
@Validated
public class UserProjectBalanceController {

    @Resource
    private UserProjectBalanceService userProjectBalanceService;

    @PostMapping("/create")
    @Operation(summary = "创建用户项目余额表（本金/收益汇总）")
    @PreAuthorize("@ss.hasPermission('project:user-project-balance:create')")
    public CommonResult<Long> createUserProjectBalance(@Valid @RequestBody UserProjectBalanceSaveReqVO createReqVO) {
        return success(userProjectBalanceService.createUserProjectBalance(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新用户项目余额表（本金/收益汇总）")
    @PreAuthorize("@ss.hasPermission('project:user-project-balance:update')")
    public CommonResult<Boolean> updateUserProjectBalance(@Valid @RequestBody UserProjectBalanceSaveReqVO updateReqVO) {
        userProjectBalanceService.updateUserProjectBalance(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除用户项目余额表（本金/收益汇总）")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('project:user-project-balance:delete')")
    public CommonResult<Boolean> deleteUserProjectBalance(@RequestParam("id") Long id) {
        userProjectBalanceService.deleteUserProjectBalance(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得用户项目余额表（本金/收益汇总）")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('project:user-project-balance:query')")
    public CommonResult<UserProjectBalanceRespVO> getUserProjectBalance(@RequestParam("id") Long id) {
        UserProjectBalanceDO userProjectBalance = userProjectBalanceService.getUserProjectBalance(id);
        return success(BeanUtils.toBean(userProjectBalance, UserProjectBalanceRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得用户项目余额表（本金/收益汇总）分页")
    @PreAuthorize("@ss.hasPermission('project:user-project-balance:query')")
    public CommonResult<PageResult<UserProjectBalanceRespVO>> getUserProjectBalancePage(@Valid UserProjectBalancePageReqVO pageReqVO) {
        PageResult<UserProjectBalanceDO> pageResult = userProjectBalanceService.getUserProjectBalancePage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, UserProjectBalanceRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出用户项目余额表（本金/收益汇总） Excel")
    @PreAuthorize("@ss.hasPermission('project:user-project-balance:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportUserProjectBalanceExcel(@Valid UserProjectBalancePageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<UserProjectBalanceDO> list = userProjectBalanceService.getUserProjectBalancePage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "用户项目余额表（本金/收益汇总）.xls", "数据", UserProjectBalanceRespVO.class,
                        BeanUtils.toBean(list, UserProjectBalanceRespVO.class));
    }

}