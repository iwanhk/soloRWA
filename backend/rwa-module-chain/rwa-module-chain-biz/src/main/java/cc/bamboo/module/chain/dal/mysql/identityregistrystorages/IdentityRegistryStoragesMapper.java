package cc.bamboo.module.chain.dal.mysql.identityregistrystorages;

import java.util.*;

import cc.bamboo.framework.common.pojo.PageResult;
import cc.bamboo.framework.mybatis.core.query.LambdaQueryWrapperX;
import cc.bamboo.framework.mybatis.core.mapper.BaseMapperX;
import cc.bamboo.module.chain.dal.dataobject.identityregistrystorages.IdentityRegistryStoragesDO;
import org.apache.ibatis.annotations.Mapper;
import cc.bamboo.module.chain.controller.admin.identityregistrystorages.vo.*;

/**
 * 身份注册表存储 Mapper
 *
 * @author Swolf
 */
@Mapper
public interface IdentityRegistryStoragesMapper extends BaseMapperX<IdentityRegistryStoragesDO> {

    default PageResult<IdentityRegistryStoragesDO> selectPage(IdentityRegistryStoragesPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<IdentityRegistryStoragesDO>()
                .eqIfPresent(IdentityRegistryStoragesDO::getAddress, reqVO.getAddress())
                .eqIfPresent(IdentityRegistryStoragesDO::getDeployerAddress, reqVO.getDeployerAddress())
                .eqIfPresent(IdentityRegistryStoragesDO::getTransactionHash, reqVO.getTransactionHash())
                .eqIfPresent(IdentityRegistryStoragesDO::getBlockNumber, reqVO.getBlockNumber())
                .eqIfPresent(IdentityRegistryStoragesDO::getBoundTokenCount, reqVO.getBoundTokenCount())
                .eqIfPresent(IdentityRegistryStoragesDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(IdentityRegistryStoragesDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(IdentityRegistryStoragesDO::getId));
    }

}