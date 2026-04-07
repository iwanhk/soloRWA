package cc.bamboo.module.system.controller.admin.publisherinfo;

import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;

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

import cc.bamboo.module.system.controller.admin.publisherinfo.vo.*;
import cc.bamboo.module.system.dal.dataobject.publisherinfo.PublisherInfoDO;
import cc.bamboo.module.system.service.publisherinfo.PublisherInfoService;

@Tag(name = "管理后台 - 发行商")
@RestController
@RequestMapping("/system/publisher-info")
@Validated
public class PublisherInfoController {

    @Resource
    private PublisherInfoService publisherInfoService;

    @PostMapping("/create")
    @Operation(summary = "创建发行商")
    @PreAuthorize("@ss.hasPermission('system:publisher-info:create')")
    public CommonResult<Long> createPublisherInfo(@Valid @RequestBody PublisherInfoSaveReqVO createReqVO) {
        return success(publisherInfoService.createPublisherInfo(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新发行商")
    @PreAuthorize("@ss.hasPermission('system:publisher-info:update')")
    public CommonResult<Boolean> updatePublisherInfo(@Valid @RequestBody PublisherInfoSaveReqVO updateReqVO) {
        publisherInfoService.updatePublisherInfo(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除发行商")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('system:publisher-info:delete')")
    public CommonResult<Boolean> deletePublisherInfo(@RequestParam("id") Long id) {
        publisherInfoService.deletePublisherInfo(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得发行商")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('system:publisher-info:query')")
    public CommonResult<PublisherInfoRespVO> getPublisherInfo(@RequestParam("id") Long id) {
        PublisherInfoDO publisherInfo = publisherInfoService.getPublisherInfo(id);
        return success(BeanUtils.toBean(publisherInfo, PublisherInfoRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得发行商分页")
    @PreAuthorize("@ss.hasPermission('system:publisher-info:query')")
    public CommonResult<PageResult<PublisherInfoRespVO>> getPublisherInfoPage(@Valid PublisherInfoPageReqVO pageReqVO) {
        PageResult<PublisherInfoDO> pageResult = publisherInfoService.getPublisherInfoPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, PublisherInfoRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出发行商 Excel")
    @PreAuthorize("@ss.hasPermission('system:publisher-info:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportPublisherInfoExcel(@Valid PublisherInfoPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<PublisherInfoDO> list = publisherInfoService.getPublisherInfoPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "发行商.xls", "数据", PublisherInfoRespVO.class,
                        BeanUtils.toBean(list, PublisherInfoRespVO.class));
    }

}