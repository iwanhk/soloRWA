package cc.bamboo.module.chain.dal.mysql.contractdeployments;

import java.util.*;

import cc.bamboo.framework.common.pojo.PageResult;
import cc.bamboo.framework.mybatis.core.query.LambdaQueryWrapperX;
import cc.bamboo.framework.mybatis.core.mapper.BaseMapperX;
import cc.bamboo.module.chain.dal.dataobject.contractdeployments.ContractDeploymentsDO;
import org.apache.ibatis.annotations.Mapper;
import cc.bamboo.module.chain.controller.admin.contractdeployments.vo.*;

/**
 * 合约部署 Mapper
 *
 * @author Swolf
 */
@Mapper
public interface ContractDeploymentsMapper extends BaseMapperX<ContractDeploymentsDO> {

    default PageResult<ContractDeploymentsDO> selectPage(ContractDeploymentsPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ContractDeploymentsDO>()
                .eqIfPresent(ContractDeploymentsDO::getContractType, reqVO.getContractType())
                .eqIfPresent(ContractDeploymentsDO::getDeploymentAddress, reqVO.getDeploymentAddress())
                .eqIfPresent(ContractDeploymentsDO::getDeployerAddress, reqVO.getDeployerAddress())
                .eqIfPresent(ContractDeploymentsDO::getDeploymentInfo, reqVO.getDeploymentInfo())
                .eqIfPresent(ContractDeploymentsDO::getTransactionHash, reqVO.getTransactionHash())
                .eqIfPresent(ContractDeploymentsDO::getBlockNumber, reqVO.getBlockNumber())
                .eqIfPresent(ContractDeploymentsDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(ContractDeploymentsDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(ContractDeploymentsDO::getId));
    }

}