package cc.bamboo.module.chain.dal.mysql.useridentities;

import cc.bamboo.framework.common.pojo.PageResult;
import cc.bamboo.framework.mybatis.core.query.LambdaQueryWrapperX;
import cc.bamboo.framework.mybatis.core.mapper.BaseMapperX;
import cc.bamboo.module.chain.dal.dataobject.useridentities.UserIdentitiesDO;
import org.apache.ibatis.annotations.Mapper;
import cc.bamboo.module.chain.controller.admin.useridentities.vo.*;

/**
 * 用户身份 Mapper
 *
 * @author Swolf
 */
@Mapper
public interface UserIdentitiesMapper extends BaseMapperX<UserIdentitiesDO> {

    default PageResult<UserIdentitiesDO> selectPage(UserIdentitiesPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<UserIdentitiesDO>()
                .eqIfPresent(UserIdentitiesDO::getAddress, reqVO.getAddress())
                .eqIfPresent(UserIdentitiesDO::getContractAddress, reqVO.getContractAddress())
                .eqIfPresent(UserIdentitiesDO::getManagementKey, reqVO.getManagementKey())
                .eqIfPresent(UserIdentitiesDO::getBlockchainAddressId, reqVO.getBlockchainAddressId())
                .eqIfPresent(UserIdentitiesDO::getSalt, reqVO.getSalt())
                .eqIfPresent(UserIdentitiesDO::getTransactionHash, reqVO.getTransactionHash())
                .eqIfPresent(UserIdentitiesDO::getBlockNumber, reqVO.getBlockNumber())
                .eqIfPresent(UserIdentitiesDO::getContractDeployed, reqVO.getContractDeployed())
                .eqIfPresent(UserIdentitiesDO::getClaimKeySetup, reqVO.getClaimKeySetup())
                .eqIfPresent(UserIdentitiesDO::getCountryCode, reqVO.getCountryCode())
                .eqIfPresent(UserIdentitiesDO::getAssociatedTokenIds, reqVO.getAssociatedTokenIds())
                .eqIfPresent(UserIdentitiesDO::getPendingTokenIds, reqVO.getPendingTokenIds())
                .eqIfPresent(UserIdentitiesDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(UserIdentitiesDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(UserIdentitiesDO::getId));
    }

}