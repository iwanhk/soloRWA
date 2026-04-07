package cc.bamboo.module.chain.controller.admin.trex;

import cc.bamboo.framework.common.pojo.CommonResult;
import cc.bamboo.module.chain.controller.admin.trex.vo.DeployTokenReqVO;
import cc.bamboo.module.chain.controller.admin.trex.vo.InitializeReqVO;
import cc.bamboo.module.chain.dal.dataobject.identityregistrystorages.IdentityRegistryStoragesDO;
import cc.bamboo.module.chain.dal.dataobject.tokens.TokensDO;
import cc.bamboo.module.chain.service.trex.TrexDeployService;
import cc.bamboo.module.chain.service.trex.dto.DeployTokenReqDTO;
import cc.bamboo.module.chain.service.trex.dto.InitStatusRespDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import java.util.Map;

import static cc.bamboo.framework.common.pojo.CommonResult.success;

/**
 * TREX 部署管理 Controller
 * 
 * @author Swolf
 */
@Tag(name = "管理后台 - TREX 部署管理")
@RestController
@RequestMapping("/chain/trex")
@Validated
public class TrexController {
    
    @Resource
    private TrexDeployService trexDeployService;
    
    @GetMapping("/status")
    @Operation(summary = "获取系统初始化状态")
    public CommonResult<InitStatusRespDTO> getInitializationStatus() {
        return success(trexDeployService.getInitializationStatus());
    }
    
    @PostMapping("/initialize")
    @Operation(summary = "初始化系统")
    public CommonResult<Map<String, Object>> initializeSystem(@RequestBody(required = false) InitializeReqVO reqVO) {
        String claimIssuerManagementKey = reqVO != null ? reqVO.getClaimIssuerManagementKey() : null;
        return success(trexDeployService.initializeSystem(claimIssuerManagementKey));
    }
    
    @PostMapping("/deploy-token")
    @Operation(summary = "部署 Token")
    @PermitAll
    public CommonResult<TokensDO> deployToken(@RequestBody @Validated DeployTokenReqVO reqVO) {
        DeployTokenReqDTO reqDTO = new DeployTokenReqDTO();
        reqDTO.setSalt(reqVO.getSalt());
        reqDTO.setOwnerAddress(reqVO.getOwnerAddress());
        reqDTO.setName(reqVO.getName());
        reqDTO.setSymbol(reqVO.getSymbol());
        reqDTO.setDecimals(reqVO.getDecimals());
        reqDTO.setTokenAgents(reqVO.getTokenAgents());
        reqDTO.setClaimTopics(reqVO.getClaimTopics());
        reqDTO.setIssuers(reqVO.getIssuers());
        reqDTO.setIssuerClaims(reqVO.getIssuerClaims());
        return success(trexDeployService.deployToken(reqDTO));
    }
    
    @PostMapping("/deploy-identity-registry-storage")
    @Operation(summary = "部署新的 IdentityRegistryStorage")
    public CommonResult<IdentityRegistryStoragesDO> deployIdentityRegistryStorage() {
        return success(trexDeployService.deployIdentityRegistryStorage());
    }
}
