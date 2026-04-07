package cc.bamboo.module.chain.service.trex.dto;

import cc.bamboo.module.chain.dal.dataobject.contractdeployments.ContractDeploymentsDO;
import cc.bamboo.module.chain.dal.dataobject.systeminitialization.SystemInitializationDO;
import lombok.Data;

import java.util.List;

/**
 * 系统初始化状态响应 DTO
 * 
 * @author Swolf
 */
@Data
public class InitStatusRespDTO {
    
    /**
     * 是否已完全初始化
     */
    private Boolean isFullyInitialized;
    
    /**
     * 初始化进度，如 "16/16"
     */
    private String progress;
    
    /**
     * 初始化步骤列表
     */
    private List<SystemInitializationDO> steps;
    
    /**
     * 已部署的合约列表
     */
    private List<ContractDeploymentsDO> contracts;
    
    /**
     * IdentityRegistryStorage 数量
     */
    private Long identityRegistryStorageCount;
}
