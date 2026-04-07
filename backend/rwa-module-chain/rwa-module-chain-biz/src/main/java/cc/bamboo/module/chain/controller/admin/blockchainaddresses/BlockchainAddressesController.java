package cc.bamboo.module.chain.controller.admin.blockchainaddresses;

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

import cc.bamboo.module.chain.controller.admin.blockchainaddresses.vo.*;
import cc.bamboo.module.chain.dal.dataobject.blockchainaddresses.BlockchainAddressesDO;
import cc.bamboo.module.chain.service.blockchainaddresses.BlockchainAddressesService;

@Tag(name = "管理后台 - 区块链地址")
@RestController
@RequestMapping("/chain/blockchain-addresses")
@Validated
public class BlockchainAddressesController {

    @Resource
    private BlockchainAddressesService blockchainAddressesService;

    @PostMapping("/create")
    @Operation(summary = "创建区块链地址")
    @PreAuthorize("@ss.hasPermission('chain:blockchain-addresses:create')")
    public CommonResult<Long> createBlockchainAddresses(@Valid @RequestBody BlockchainAddressesSaveReqVO createReqVO) {
        return success(blockchainAddressesService.createBlockchainAddresses(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新区块链地址")
    @PreAuthorize("@ss.hasPermission('chain:blockchain-addresses:update')")
    public CommonResult<Boolean> updateBlockchainAddresses(@Valid @RequestBody BlockchainAddressesSaveReqVO updateReqVO) {
        blockchainAddressesService.updateBlockchainAddresses(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除区块链地址")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('chain:blockchain-addresses:delete')")
    public CommonResult<Boolean> deleteBlockchainAddresses(@RequestParam("id") Long id) {
        blockchainAddressesService.deleteBlockchainAddresses(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得区块链地址")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('chain:blockchain-addresses:query')")
    public CommonResult<BlockchainAddressesRespVO> getBlockchainAddresses(@RequestParam("id") Long id) {
        BlockchainAddressesDO blockchainAddresses = blockchainAddressesService.getBlockchainAddresses(id);
        return success(BeanUtils.toBean(blockchainAddresses, BlockchainAddressesRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得区块链地址分页")
    @PreAuthorize("@ss.hasPermission('chain:blockchain-addresses:query')")
    public CommonResult<PageResult<BlockchainAddressesRespVO>> getBlockchainAddressesPage(@Valid BlockchainAddressesPageReqVO pageReqVO) {
        PageResult<BlockchainAddressesDO> pageResult = blockchainAddressesService.getBlockchainAddressesPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, BlockchainAddressesRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出区块链地址 Excel")
    @PreAuthorize("@ss.hasPermission('chain:blockchain-addresses:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportBlockchainAddressesExcel(@Valid BlockchainAddressesPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<BlockchainAddressesDO> list = blockchainAddressesService.getBlockchainAddressesPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "区块链地址.xls", "数据", BlockchainAddressesRespVO.class,
                        BeanUtils.toBean(list, BlockchainAddressesRespVO.class));
    }

}