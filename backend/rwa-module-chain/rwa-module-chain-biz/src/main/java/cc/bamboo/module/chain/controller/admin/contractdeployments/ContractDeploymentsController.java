package cc.bamboo.module.chain.controller.admin.contractdeployments;

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

import cc.bamboo.module.chain.controller.admin.contractdeployments.vo.*;
import cc.bamboo.module.chain.dal.dataobject.contractdeployments.ContractDeploymentsDO;
import cc.bamboo.module.chain.service.contractdeployments.ContractDeploymentsService;

@Tag(name = "管理后台 - 合约部署")
@RestController
@RequestMapping("/chain/contract-deployments")
@Validated
public class ContractDeploymentsController {

    @Resource
    private ContractDeploymentsService contractDeploymentsService;

    @PostMapping("/create")
    @Operation(summary = "创建合约部署")
    @PreAuthorize("@ss.hasPermission('chain:contract-deployments:create')")
    public CommonResult<Long> createContractDeployments(@Valid @RequestBody ContractDeploymentsSaveReqVO createReqVO) {
        return success(contractDeploymentsService.createContractDeployments(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新合约部署")
    @PreAuthorize("@ss.hasPermission('chain:contract-deployments:update')")
    public CommonResult<Boolean> updateContractDeployments(@Valid @RequestBody ContractDeploymentsSaveReqVO updateReqVO) {
        contractDeploymentsService.updateContractDeployments(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除合约部署")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('chain:contract-deployments:delete')")
    public CommonResult<Boolean> deleteContractDeployments(@RequestParam("id") Long id) {
        contractDeploymentsService.deleteContractDeployments(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得合约部署")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('chain:contract-deployments:query')")
    public CommonResult<ContractDeploymentsRespVO> getContractDeployments(@RequestParam("id") Long id) {
        ContractDeploymentsDO contractDeployments = contractDeploymentsService.getContractDeployments(id);
        return success(BeanUtils.toBean(contractDeployments, ContractDeploymentsRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得合约部署分页")
    @PreAuthorize("@ss.hasPermission('chain:contract-deployments:query')")
    public CommonResult<PageResult<ContractDeploymentsRespVO>> getContractDeploymentsPage(@Valid ContractDeploymentsPageReqVO pageReqVO) {
        PageResult<ContractDeploymentsDO> pageResult = contractDeploymentsService.getContractDeploymentsPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, ContractDeploymentsRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出合约部署 Excel")
    @PreAuthorize("@ss.hasPermission('chain:contract-deployments:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportContractDeploymentsExcel(@Valid ContractDeploymentsPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<ContractDeploymentsDO> list = contractDeploymentsService.getContractDeploymentsPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "合约部署.xls", "数据", ContractDeploymentsRespVO.class,
                        BeanUtils.toBean(list, ContractDeploymentsRespVO.class));
    }

}