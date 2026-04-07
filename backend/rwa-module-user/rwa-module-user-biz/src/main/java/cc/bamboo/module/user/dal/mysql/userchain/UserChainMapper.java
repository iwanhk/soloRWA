package cc.bamboo.module.user.dal.mysql.userchain;

import java.util.*;

import cc.bamboo.framework.common.pojo.PageResult;
import cc.bamboo.framework.mybatis.core.query.LambdaQueryWrapperX;
import cc.bamboo.framework.mybatis.core.mapper.BaseMapperX;
import cc.bamboo.module.user.controller.app.userchain.vo.AppChainRespVO;
import cc.bamboo.module.user.dal.dataobject.userchain.UserChainDO;
import org.apache.ibatis.annotations.Mapper;
import cc.bamboo.module.user.controller.admin.userchain.vo.*;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * 用户链地址表= Mapper
 *
 * @author Swolf
 */
@Mapper
public interface UserChainMapper extends BaseMapperX<UserChainDO> {

        default PageResult<UserChainDO> selectPage(UserChainPageReqVO reqVO) {
                return selectPage(reqVO, new LambdaQueryWrapperX<UserChainDO>()
                                .eqIfPresent(UserChainDO::getUserId, reqVO.getUserId())
                                .eqIfPresent(UserChainDO::getChainId, reqVO.getChainId())
                                .eqIfPresent(UserChainDO::getChainAddress, reqVO.getChainAddress())
                                .eqIfPresent(UserChainDO::getIdentityId, reqVO.getIdentityId())
                                .eqIfPresent(UserChainDO::getChainStatus, reqVO.getChainStatus())
                                .eqIfPresent(UserChainDO::getIsDefault, reqVO.getIsDefault())
                                .eqIfPresent(UserChainDO::getAddressRemark, reqVO.getAddressRemark())
                                .betweenIfPresent(UserChainDO::getCreateTime, reqVO.getCreateTime())
                                .orderByDesc(UserChainDO::getId));
        }

        @Select("SELECT id from biz_user_identities " +
                        "WHERE user_id = #{userId} limit 1")
        Long selectUserIdentity(@Param("userId") Long userId);

        @Select("SELECT id, name, chain_id as chainId, rpc_url as rpcUrl, browser_url as browserUrl, status, sort " +
                        "FROM biz_chain WHERE status = 0 ORDER BY sort ASC")
        List<AppChainRespVO> selectActiveChainList();

}