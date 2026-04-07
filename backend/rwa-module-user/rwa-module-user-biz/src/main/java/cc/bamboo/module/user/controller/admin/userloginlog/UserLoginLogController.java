package cc.bamboo.module.user.controller.admin.userloginlog;

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

import cc.bamboo.module.user.controller.admin.userloginlog.vo.*;
import cc.bamboo.module.user.dal.dataobject.userloginlog.UserLoginLogDO;
import cc.bamboo.module.user.service.userloginlog.UserLoginLogService;

@Tag(name = "管理后台 - 用户登录日志")
@RestController
@RequestMapping("/user/login-log")
@Validated
public class UserLoginLogController {

    @Resource
    private UserLoginLogService loginLogService;

    @PostMapping("/create")
    @Operation(summary = "创建用户登录日志")
    @PreAuthorize("@ss.hasPermission('user:login-log:create')")
    public CommonResult<Long> createLoginLog(@Valid @RequestBody UserLoginLogSaveReqVO createReqVO) {
        return success(loginLogService.createLoginLog(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新用户登录日志")
    @PreAuthorize("@ss.hasPermission('user:login-log:update')")
    public CommonResult<Boolean> updateLoginLog(@Valid @RequestBody UserLoginLogSaveReqVO updateReqVO) {
        loginLogService.updateLoginLog(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除用户登录日志")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('user:login-log:delete')")
    public CommonResult<Boolean> deleteLoginLog(@RequestParam("id") Long id) {
        loginLogService.deleteLoginLog(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得用户登录日志")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('user:login-log:query')")
    public CommonResult<UserLoginLogRespVO> getLoginLog(@RequestParam("id") Long id) {
        UserLoginLogDO loginLog = loginLogService.getLoginLog(id);
        return success(BeanUtils.toBean(loginLog, UserLoginLogRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得用户登录日志分页")
    @PreAuthorize("@ss.hasPermission('user:login-log:query')")
    public CommonResult<PageResult<UserLoginLogRespVO>> getLoginLogPage(@Valid UserLoginLogPageReqVO pageReqVO) {
        PageResult<UserLoginLogDO> pageResult = loginLogService.getLoginLogPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, UserLoginLogRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出用户登录日志 Excel")
    @PreAuthorize("@ss.hasPermission('user:login-log:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportLoginLogExcel(@Valid UserLoginLogPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<UserLoginLogDO> list = loginLogService.getLoginLogPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "用户登录日志.xls", "数据", UserLoginLogRespVO.class,
                        BeanUtils.toBean(list, UserLoginLogRespVO.class));
    }

}