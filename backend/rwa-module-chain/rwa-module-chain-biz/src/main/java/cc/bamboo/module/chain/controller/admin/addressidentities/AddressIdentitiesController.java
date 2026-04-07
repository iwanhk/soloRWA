package cc.bamboo.module.chain.controller.admin.addressidentities;

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

import cc.bamboo.module.chain.controller.admin.addressidentities.vo.*;
import cc.bamboo.module.chain.dal.dataobject.addressidentities.AddressIdentitiesDO;
import cc.bamboo.module.chain.service.addressidentities.AddressIdentitiesService;

@Tag(name = "管理后台 - 地址身份关联")
@RestController
@RequestMapping("/chain/address-identities")
@Validated
public class AddressIdentitiesController {

    @Resource
    private AddressIdentitiesService addressIdentitiesService;

    @PostMapping("/create")
    @Operation(summary = "创建地址身份关联")
    @PreAuthorize("@ss.hasPermission('chain:address-identities:create')")
    public CommonResult<Long> createAddressIdentities(@Valid @RequestBody AddressIdentitiesSaveReqVO createReqVO) {
        return success(addressIdentitiesService.createAddressIdentities(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新地址身份关联")
    @PreAuthorize("@ss.hasPermission('chain:address-identities:update')")
    public CommonResult<Boolean> updateAddressIdentities(@Valid @RequestBody AddressIdentitiesSaveReqVO updateReqVO) {
        addressIdentitiesService.updateAddressIdentities(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除地址身份关联")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('chain:address-identities:delete')")
    public CommonResult<Boolean> deleteAddressIdentities(@RequestParam("id") Long id) {
        addressIdentitiesService.deleteAddressIdentities(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得地址身份关联")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('chain:address-identities:query')")
    public CommonResult<AddressIdentitiesRespVO> getAddressIdentities(@RequestParam("id") Long id) {
        AddressIdentitiesDO addressIdentities = addressIdentitiesService.getAddressIdentities(id);
        return success(BeanUtils.toBean(addressIdentities, AddressIdentitiesRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得地址身份关联分页")
    @PreAuthorize("@ss.hasPermission('chain:address-identities:query')")
    public CommonResult<PageResult<AddressIdentitiesRespVO>> getAddressIdentitiesPage(@Valid AddressIdentitiesPageReqVO pageReqVO) {
        PageResult<AddressIdentitiesDO> pageResult = addressIdentitiesService.getAddressIdentitiesPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, AddressIdentitiesRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出地址身份关联 Excel")
    @PreAuthorize("@ss.hasPermission('chain:address-identities:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportAddressIdentitiesExcel(@Valid AddressIdentitiesPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<AddressIdentitiesDO> list = addressIdentitiesService.getAddressIdentitiesPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "地址身份关联.xls", "数据", AddressIdentitiesRespVO.class,
                        BeanUtils.toBean(list, AddressIdentitiesRespVO.class));
    }

}