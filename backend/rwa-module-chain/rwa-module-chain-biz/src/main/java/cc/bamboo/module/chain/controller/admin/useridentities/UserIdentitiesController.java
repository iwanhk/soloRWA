package cc.bamboo.module.chain.controller.admin.useridentities;

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

import cc.bamboo.module.chain.controller.admin.useridentities.vo.*;
import cc.bamboo.module.chain.dal.dataobject.useridentities.UserIdentitiesDO;
import cc.bamboo.module.chain.service.useridentities.UserIdentitiesService;

@Tag(name = "管理后台 - 用户身份")
@RestController
@RequestMapping("/chain/user-identities")
@Validated
public class UserIdentitiesController {

    @Resource
    private UserIdentitiesService userIdentitiesService;

    @PostMapping("/create")
    @Operation(summary = "创建用户身份")
    @PreAuthorize("@ss.hasPermission('chain:user-identities:create')")
    public CommonResult<Long> createUserIdentities(@Valid @RequestBody UserIdentitiesSaveReqVO createReqVO) {
        return success(userIdentitiesService.createUserIdentities(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新用户身份")
    @PreAuthorize("@ss.hasPermission('chain:user-identities:update')")
    public CommonResult<Boolean> updateUserIdentities(@Valid @RequestBody UserIdentitiesSaveReqVO updateReqVO) {
        userIdentitiesService.updateUserIdentities(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除用户身份")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('chain:user-identities:delete')")
    public CommonResult<Boolean> deleteUserIdentities(@RequestParam("id") Long id) {
        userIdentitiesService.deleteUserIdentities(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得用户身份")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('chain:user-identities:query')")
    public CommonResult<UserIdentitiesRespVO> getUserIdentities(@RequestParam("id") Long id) {
        UserIdentitiesDO userIdentities = userIdentitiesService.getUserIdentities(id);
        return success(BeanUtils.toBean(userIdentities, UserIdentitiesRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得用户身份分页")
    @PreAuthorize("@ss.hasPermission('chain:user-identities:query')")
    public CommonResult<PageResult<UserIdentitiesRespVO>> getUserIdentitiesPage(@Valid UserIdentitiesPageReqVO pageReqVO) {
        PageResult<UserIdentitiesDO> pageResult = userIdentitiesService.getUserIdentitiesPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, UserIdentitiesRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出用户身份 Excel")
    @PreAuthorize("@ss.hasPermission('chain:user-identities:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportUserIdentitiesExcel(@Valid UserIdentitiesPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<UserIdentitiesDO> list = userIdentitiesService.getUserIdentitiesPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "用户身份.xls", "数据", UserIdentitiesRespVO.class,
                        BeanUtils.toBean(list, UserIdentitiesRespVO.class));
    }

}