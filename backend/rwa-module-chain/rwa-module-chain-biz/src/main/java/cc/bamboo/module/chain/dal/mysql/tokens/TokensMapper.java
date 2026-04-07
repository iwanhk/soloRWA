package cc.bamboo.module.chain.dal.mysql.tokens;

import java.util.*;

import cc.bamboo.framework.common.pojo.PageResult;
import cc.bamboo.framework.mybatis.core.query.LambdaQueryWrapperX;
import cc.bamboo.framework.mybatis.core.mapper.BaseMapperX;
import cc.bamboo.module.chain.dal.dataobject.tokens.TokensDO;
import org.apache.ibatis.annotations.Mapper;
import cc.bamboo.module.chain.controller.admin.tokens.vo.*;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * 代币 Mapper
 *
 * @author Swolf
 */
@Mapper
public interface TokensMapper extends BaseMapperX<TokensDO> {


    default PageResult<TokensDO> selectPage(TokensPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<TokensDO>()
                .likeIfPresent(TokensDO::getName, reqVO.getName())
                .eqIfPresent(TokensDO::getSymbol, reqVO.getSymbol())
                .eqIfPresent(TokensDO::getDecimals, reqVO.getDecimals())
                .eqIfPresent(TokensDO::getAddress, reqVO.getAddress())
                .eqIfPresent(TokensDO::getOwnerAddress, reqVO.getOwnerAddress())
                .eqIfPresent(TokensDO::getDeployerAddress, reqVO.getDeployerAddress())
                .eqIfPresent(TokensDO::getIdentityRegistryStorageId, reqVO.getIdentityRegistryStorageId())
                .eqIfPresent(TokensDO::getIdentityRegistryAddress, reqVO.getIdentityRegistryAddress())
                .eqIfPresent(TokensDO::getClaimTopicsRegistryAddress, reqVO.getClaimTopicsRegistryAddress())
                .eqIfPresent(TokensDO::getTrustedIssuersRegistryAddress, reqVO.getTrustedIssuersRegistryAddress())
                .eqIfPresent(TokensDO::getModularComplianceAddress, reqVO.getModularComplianceAddress())
                .eqIfPresent(TokensDO::getTokenOnchainIdAddress, reqVO.getTokenOnchainIdAddress())
                .eqIfPresent(TokensDO::getTransactionHash, reqVO.getTransactionHash())
                .eqIfPresent(TokensDO::getBlockNumber, reqVO.getBlockNumber())
                .eqIfPresent(TokensDO::getStatus, reqVO.getStatus())
                .eqIfPresent(TokensDO::getSalt, reqVO.getSalt())
                .eqIfPresent(TokensDO::getTokenAgents, reqVO.getTokenAgents())
                .eqIfPresent(TokensDO::getClaimTopics, reqVO.getClaimTopics())
                .eqIfPresent(TokensDO::getIssuers, reqVO.getIssuers())
                .eqIfPresent(TokensDO::getIssuerClaims, reqVO.getIssuerClaims())
                .eqIfPresent(TokensDO::getDeploymentInfo, reqVO.getDeploymentInfo())
                .eqIfPresent(TokensDO::getErrorMessage, reqVO.getErrorMessage())
                .betweenIfPresent(TokensDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(TokensDO::getId));
    }

    default TokensDO getSignToken(){
        return selectOne(new  LambdaQueryWrapperX<TokensDO>().eq(TokensDO::getProjectId,0));
    }

    @Update("UPDATE biz_project_info SET chain_status = #{chainStatus}, chain_tokens_id = #{chainTokensId}, chain_token_address = #{chainTokenAddress} " +
            "WHERE project_id = #{projectId}")
    int updateChainInfo(@Param("projectId") Long projectId,
                        @Param("chainStatus") Integer chainStatus,
                        @Param("chainTokensId") Long chainTokensId,
                        @Param("chainTokenAddress") String chainTokenAddress);

    @Update("UPDATE biz_project_order SET chain_status = #{chainStatus}, chain_hash = #{chainHash} " +
            "WHERE id = #{orderId}")
    int updateOrder(@Param("orderId") Long orderId,
                        @Param("chainStatus") Integer chainStatus,
                        @Param("chainHash") String chainHash);

    @Update("UPDATE biz_user_chain SET chain_status = #{chainStatus},identity_id=#{identityId} " +
            "WHERE id = #{userChainId}")
    int createUserChainAddress(@Param("chainStatus") Integer chainStatus,@Param("userChainId") Long userChainId,@Param("identityId") Long identityId);


    @Update("UPDATE biz_user_chain SET chain_status = #{chainStatus}" +
            "WHERE id = #{userChainId}")
    int updateUserChainAddress(@Param("chainStatus") Integer chainStatus,@Param("userChainId") Long userChainId);


    @Update("UPDATE biz_user_chain SET chain_status = #{chainStatus},deleted = 1" +
            "WHERE id = #{userChainId}")
    int removeUserChainAddress(@Param("chainStatus") Integer chainStatus,@Param("userChainId") Long userChainId);


    @Select("SELECT audit_status FROM biz_user_info WHERE id = #{userId}")
    int getUserAuditStatus(@Param("userId") Long userId);
}