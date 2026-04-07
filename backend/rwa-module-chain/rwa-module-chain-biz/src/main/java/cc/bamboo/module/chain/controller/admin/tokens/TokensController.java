package cc.bamboo.module.chain.controller.admin.tokens;

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

import cc.bamboo.module.chain.controller.admin.tokens.vo.*;
import cc.bamboo.module.chain.dal.dataobject.tokens.TokensDO;
import cc.bamboo.module.chain.service.tokens.TokensService;

@Tag(name = "管理后台 - 代币")
@RestController
@RequestMapping("/chain/tokens")
@Validated
public class TokensController {

    @Resource
    private TokensService tokensService;

    @PostMapping("/create")
    @Operation(summary = "创建代币")
    @PreAuthorize("@ss.hasPermission('chain:tokens:create')")
    public CommonResult<Long> createTokens(@Valid @RequestBody TokensSaveReqVO createReqVO) {
        return success(tokensService.createTokens(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新代币")
    @PreAuthorize("@ss.hasPermission('chain:tokens:update')")
    public CommonResult<Boolean> updateTokens(@Valid @RequestBody TokensSaveReqVO updateReqVO) {
        tokensService.updateTokens(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除代币")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('chain:tokens:delete')")
    public CommonResult<Boolean> deleteTokens(@RequestParam("id") Long id) {
        tokensService.deleteTokens(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得代币")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('chain:tokens:query')")
    public CommonResult<TokensRespVO> getTokens(@RequestParam("id") Long id) {
        TokensDO tokens = tokensService.getTokens(id);
        return success(BeanUtils.toBean(tokens, TokensRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得代币分页")
    @PreAuthorize("@ss.hasPermission('chain:tokens:query')")
    public CommonResult<PageResult<TokensRespVO>> getTokensPage(@Valid TokensPageReqVO pageReqVO) {
        PageResult<TokensDO> pageResult = tokensService.getTokensPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, TokensRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出代币 Excel")
    @PreAuthorize("@ss.hasPermission('chain:tokens:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportTokensExcel(@Valid TokensPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<TokensDO> list = tokensService.getTokensPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "代币.xls", "数据", TokensRespVO.class,
                        BeanUtils.toBean(list, TokensRespVO.class));
    }

}