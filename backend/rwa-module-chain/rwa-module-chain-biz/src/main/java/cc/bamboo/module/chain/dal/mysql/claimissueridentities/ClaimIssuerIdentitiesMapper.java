package cc.bamboo.module.chain.dal.mysql.claimissueridentities;

import java.util.*;

import cc.bamboo.framework.common.pojo.PageResult;
import cc.bamboo.framework.mybatis.core.query.LambdaQueryWrapperX;
import cc.bamboo.framework.mybatis.core.mapper.BaseMapperX;
import cc.bamboo.module.chain.dal.dataobject.claimissueridentities.ClaimIssuerIdentitiesDO;
import org.apache.ibatis.annotations.Mapper;
import cc.bamboo.module.chain.controller.admin.claimissueridentities.vo.*;

/**
 * 声明发行者身份 Mapper
 *
 * @author Swolf
 */
@Mapper
public interface ClaimIssuerIdentitiesMapper extends BaseMapperX<ClaimIssuerIdentitiesDO> {

    default PageResult<ClaimIssuerIdentitiesDO> selectPage(ClaimIssuerIdentitiesPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ClaimIssuerIdentitiesDO>()
                .eqIfPresent(ClaimIssuerIdentitiesDO::getAddress, reqVO.getAddress())
                .eqIfPresent(ClaimIssuerIdentitiesDO::getContractAddress, reqVO.getContractAddress())
                .eqIfPresent(ClaimIssuerIdentitiesDO::getManagementKey, reqVO.getManagementKey())
                .eqIfPresent(ClaimIssuerIdentitiesDO::getBlockchainAddressId, reqVO.getBlockchainAddressId())
                .eqIfPresent(ClaimIssuerIdentitiesDO::getSalt, reqVO.getSalt())
                .eqIfPresent(ClaimIssuerIdentitiesDO::getTransactionHash, reqVO.getTransactionHash())
                .eqIfPresent(ClaimIssuerIdentitiesDO::getBlockNumber, reqVO.getBlockNumber())
                .eqIfPresent(ClaimIssuerIdentitiesDO::getContractDeployed, reqVO.getContractDeployed())
                .eqIfPresent(ClaimIssuerIdentitiesDO::getClaimKeySetup, reqVO.getClaimKeySetup())
                .eqIfPresent(ClaimIssuerIdentitiesDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(ClaimIssuerIdentitiesDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(ClaimIssuerIdentitiesDO::getId));
    }

}