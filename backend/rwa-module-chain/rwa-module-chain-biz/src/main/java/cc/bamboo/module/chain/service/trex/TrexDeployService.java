package cc.bamboo.module.chain.service.trex;

import cc.bamboo.module.chain.dal.dataobject.contractdeployments.ContractDeploymentsDO;
import cc.bamboo.module.chain.dal.dataobject.identityregistrystorages.IdentityRegistryStoragesDO;
import cc.bamboo.module.chain.dal.dataobject.tokens.TokensDO;
import cc.bamboo.module.chain.service.trex.dto.DeployTokenReqDTO;
import cc.bamboo.module.chain.service.trex.dto.InitStatusRespDTO;

import java.util.Map;

/**
 * TREX 部署服务接口
 * 负责系统初始化和 Token 部署
 * 
 * @author Swolf
 */
public interface TrexDeployService {
    
    /**
     * 获取系统初始化状态
     * 
     * @return 初始化状态信息
     */
    InitStatusRespDTO getInitializationStatus();
    
    /**
     * 初始化系统
     * 部署 Token 之前的所有基础设施合约
     * 支持断点续传，如果中断可以继续执行
     * 
     * @param claimIssuerManagementKey ClaimIssuer 管理密钥（可选，默认使用 deployer 地址）
     * @return 部署结果，包含所有合约地址
     */
    Map<String, Object> initializeSystem(String claimIssuerManagementKey);
    
    /**
     * 部署 Token
     * 
     * @param reqDTO 部署请求参数
     * @return 部署的 Token 信息
     */
    TokensDO deployToken(DeployTokenReqDTO reqDTO);
    
    /**
     * 部署新的 IdentityRegistryStorage
     * 
     * @return 部署的 IRS 信息
     */
    IdentityRegistryStoragesDO deployIdentityRegistryStorage();
    
    /**
     * 获取已部署的合约
     * 
     * @param contractType 合约类型
     * @return 合约部署信息
     */
    ContractDeploymentsDO getDeployedContract(String contractType);
    
    /**
     * 检查合约是否已部署
     * 
     * @param contractType 合约类型
     * @return 是否已部署
     */
    boolean isContractDeployed(String contractType);
}
