package cc.bamboo.module.user.controller.admin.userchain;

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

import cc.bamboo.module.user.controller.admin.userchain.vo.*;
import cc.bamboo.module.user.dal.dataobject.userchain.UserChainDO;
import cc.bamboo.module.user.service.userchain.UserChainService;

@Tag(name = "管理后台 - 用户链地址表=")
@RestController
@RequestMapping("/user/chain")
@Validated
public class UserChainController {

    @Resource
    private UserChainService chainService;

    @PostMapping("/create")
    @Operation(summary = "创建用户链地址表=")
    @PreAuthorize("@ss.hasPermission('user:chain:create')")
    public CommonResult<Long> createChain(@Valid @RequestBody UserChainSaveReqVO createReqVO) {
        return success(chainService.createChain(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新用户链地址表=")
    @PreAuthorize("@ss.hasPermission('user:chain:update')")
    public CommonResult<Boolean> updateChain(@Valid @RequestBody UserChainSaveReqVO updateReqVO) {
        chainService.updateChain(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除用户链地址表=")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('user:chain:delete')")
    public CommonResult<Boolean> deleteChain(@RequestParam("id") Long id) {
        chainService.deleteChain(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得用户链地址表=")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('user:chain:query')")
    public CommonResult<UserChainRespVO> getChain(@RequestParam("id") Long id) {
        UserChainDO chain = chainService.getChain(id);
        return success(BeanUtils.toBean(chain, UserChainRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得用户链地址表=分页")
    @PreAuthorize("@ss.hasPermission('user:chain:query')")
    public CommonResult<PageResult<UserChainRespVO>> getChainPage(@Valid UserChainPageReqVO pageReqVO) {
        PageResult<UserChainDO> pageResult = chainService.getChainPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, UserChainRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出用户链地址表= Excel")
    @PreAuthorize("@ss.hasPermission('user:chain:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportChainExcel(@Valid UserChainPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<UserChainDO> list = chainService.getChainPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "用户链地址表=.xls", "数据", UserChainRespVO.class,
                        BeanUtils.toBean(list, UserChainRespVO.class));
    }

}