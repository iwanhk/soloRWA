package cc.bamboo.module.chain.controller.admin.claimtopics;

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

import cc.bamboo.module.chain.controller.admin.claimtopics.vo.*;
import cc.bamboo.module.chain.dal.dataobject.claimtopics.ClaimTopicsDO;
import cc.bamboo.module.chain.service.claimtopics.ClaimTopicsService;

@Tag(name = "管理后台 - 声明主题")
@RestController
@RequestMapping("/chain/claim-topics")
@Validated
public class ClaimTopicsController {

    @Resource
    private ClaimTopicsService claimTopicsService;

    @PostMapping("/create")
    @Operation(summary = "创建声明主题")
    @PreAuthorize("@ss.hasPermission('chain:claim-topics:create')")
    public CommonResult<Long> createClaimTopics(@Valid @RequestBody ClaimTopicsSaveReqVO createReqVO) {
        return success(claimTopicsService.createClaimTopics(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新声明主题")
    @PreAuthorize("@ss.hasPermission('chain:claim-topics:update')")
    public CommonResult<Boolean> updateClaimTopics(@Valid @RequestBody ClaimTopicsSaveReqVO updateReqVO) {
        claimTopicsService.updateClaimTopics(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除声明主题")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('chain:claim-topics:delete')")
    public CommonResult<Boolean> deleteClaimTopics(@RequestParam("id") Long id) {
        claimTopicsService.deleteClaimTopics(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得声明主题")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('chain:claim-topics:query')")
    public CommonResult<ClaimTopicsRespVO> getClaimTopics(@RequestParam("id") Long id) {
        ClaimTopicsDO claimTopics = claimTopicsService.getClaimTopics(id);
        return success(BeanUtils.toBean(claimTopics, ClaimTopicsRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得声明主题分页")
    @PreAuthorize("@ss.hasPermission('chain:claim-topics:query')")
    public CommonResult<PageResult<ClaimTopicsRespVO>> getClaimTopicsPage(@Valid ClaimTopicsPageReqVO pageReqVO) {
        PageResult<ClaimTopicsDO> pageResult = claimTopicsService.getClaimTopicsPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, ClaimTopicsRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出声明主题 Excel")
    @PreAuthorize("@ss.hasPermission('chain:claim-topics:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportClaimTopicsExcel(@Valid ClaimTopicsPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<ClaimTopicsDO> list = claimTopicsService.getClaimTopicsPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "声明主题.xls", "数据", ClaimTopicsRespVO.class,
                        BeanUtils.toBean(list, ClaimTopicsRespVO.class));
    }

}