package cc.bamboo.module.chain.controller.admin.identityregistrystorages;

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

import cc.bamboo.module.chain.controller.admin.identityregistrystorages.vo.*;
import cc.bamboo.module.chain.dal.dataobject.identityregistrystorages.IdentityRegistryStoragesDO;
import cc.bamboo.module.chain.service.identityregistrystorages.IdentityRegistryStoragesService;

@Tag(name = "管理后台 - 身份注册表存储")
@RestController
@RequestMapping("/chain/identity-registry-storages")
@Validated
public class IdentityRegistryStoragesController {

    @Resource
    private IdentityRegistryStoragesService identityRegistryStoragesService;

    @PostMapping("/create")
    @Operation(summary = "创建身份注册表存储")
    @PreAuthorize("@ss.hasPermission('chain:identity-registry-storages:create')")
    public CommonResult<Long> createIdentityRegistryStorages(@Valid @RequestBody IdentityRegistryStoragesSaveReqVO createReqVO) {
        return success(identityRegistryStoragesService.createIdentityRegistryStorages(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新身份注册表存储")
    @PreAuthorize("@ss.hasPermission('chain:identity-registry-storages:update')")
    public CommonResult<Boolean> updateIdentityRegistryStorages(@Valid @RequestBody IdentityRegistryStoragesSaveReqVO updateReqVO) {
        identityRegistryStoragesService.updateIdentityRegistryStorages(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除身份注册表存储")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('chain:identity-registry-storages:delete')")
    public CommonResult<Boolean> deleteIdentityRegistryStorages(@RequestParam("id") Long id) {
        identityRegistryStoragesService.deleteIdentityRegistryStorages(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得身份注册表存储")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('chain:identity-registry-storages:query')")
    public CommonResult<IdentityRegistryStoragesRespVO> getIdentityRegistryStorages(@RequestParam("id") Long id) {
        IdentityRegistryStoragesDO identityRegistryStorages = identityRegistryStoragesService.getIdentityRegistryStorages(id);
        return success(BeanUtils.toBean(identityRegistryStorages, IdentityRegistryStoragesRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得身份注册表存储分页")
    @PreAuthorize("@ss.hasPermission('chain:identity-registry-storages:query')")
    public CommonResult<PageResult<IdentityRegistryStoragesRespVO>> getIdentityRegistryStoragesPage(@Valid IdentityRegistryStoragesPageReqVO pageReqVO) {
        PageResult<IdentityRegistryStoragesDO> pageResult = identityRegistryStoragesService.getIdentityRegistryStoragesPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, IdentityRegistryStoragesRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出身份注册表存储 Excel")
    @PreAuthorize("@ss.hasPermission('chain:identity-registry-storages:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportIdentityRegistryStoragesExcel(@Valid IdentityRegistryStoragesPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<IdentityRegistryStoragesDO> list = identityRegistryStoragesService.getIdentityRegistryStoragesPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "身份注册表存储.xls", "数据", IdentityRegistryStoragesRespVO.class,
                        BeanUtils.toBean(list, IdentityRegistryStoragesRespVO.class));
    }

}