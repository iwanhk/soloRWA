package cc.bamboo.module.chain.controller.admin.chain;

import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;

import javax.annotation.security.PermitAll;
import javax.validation.constraints.*;
import javax.validation.*;
import javax.servlet.http.*;
import java.util.*;
import java.io.IOException;

import cc.bamboo.framework.common.pojo.PageResult;
import cc.bamboo.framework.common.pojo.CommonResult;
import static cc.bamboo.framework.common.pojo.CommonResult.success;

import cc.bamboo.framework.common.util.object.BeanUtils;
import static cc.bamboo.framework.common.util.collection.CollectionUtils.convertList;

import cc.bamboo.module.chain.controller.admin.chain.vo.*;
import cc.bamboo.module.chain.dal.dataobject.chain.ChainDO;
import cc.bamboo.module.chain.service.chain.ChainService;

@Tag(name = "管理后台 - 区块链信息")
@RestController
@RequestMapping("/chain/chain")
@Validated
public class ChainController {

    @Resource
    private ChainService chainService;

    @PostMapping("/create")
    @Operation(summary = "创建区块链信息")
    @PreAuthorize("@ss.hasPermission('chain:chain:create')")
    public CommonResult<Long> createChain(@Valid @RequestBody ChainSaveReqVO createReqVO) {
        return success(chainService.createChain(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新区块链信息")
    @PreAuthorize("@ss.hasPermission('chain:chain:update')")
    public CommonResult<Boolean> updateChain(@Valid @RequestBody ChainSaveReqVO updateReqVO) {
        chainService.updateChain(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除区块链信息")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('chain:chain:delete')")
    public CommonResult<Boolean> deleteChain(@RequestParam("id") Long id) {
        chainService.deleteChain(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得区块链信息")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('chain:chain:query')")
    public CommonResult<ChainRespVO> getChain(@RequestParam("id") Long id) {
        ChainDO chain = chainService.getChain(id);
        return success(BeanUtils.toBean(chain, ChainRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得区块链信息分页")
    @PreAuthorize("@ss.hasPermission('chain:chain:query')")
    public CommonResult<PageResult<ChainRespVO>> getChainPage(@Valid ChainPageReqVO pageReqVO) {
        PageResult<ChainDO> pageResult = chainService.getChainPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, ChainRespVO.class));
    }

    @GetMapping("/list-active")
    @Operation(summary = "获得开启的区块链列表")
    //@PreAuthorize("@ss.hasPermission('chain:chain:query')")
    @PermitAll
    public CommonResult<List<ChainRespVO>> getActiveChainList() {
        List<ChainDO> list = chainService.getActiveChainList();
        return success(BeanUtils.toBean(list, ChainRespVO.class));
    }

}
