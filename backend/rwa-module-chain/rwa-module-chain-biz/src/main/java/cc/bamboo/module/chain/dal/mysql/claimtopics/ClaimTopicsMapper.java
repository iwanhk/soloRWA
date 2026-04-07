package cc.bamboo.module.chain.dal.mysql.claimtopics;

import java.util.*;

import cc.bamboo.framework.common.pojo.PageResult;
import cc.bamboo.framework.mybatis.core.query.LambdaQueryWrapperX;
import cc.bamboo.framework.mybatis.core.mapper.BaseMapperX;
import cc.bamboo.module.chain.dal.dataobject.claimtopics.ClaimTopicsDO;
import org.apache.ibatis.annotations.Mapper;
import cc.bamboo.module.chain.controller.admin.claimtopics.vo.*;

/**
 * 声明主题 Mapper
 *
 * @author Swolf
 */
@Mapper
public interface ClaimTopicsMapper extends BaseMapperX<ClaimTopicsDO> {

    default PageResult<ClaimTopicsDO> selectPage(ClaimTopicsPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ClaimTopicsDO>()
                .likeIfPresent(ClaimTopicsDO::getName, reqVO.getName())
                .eqIfPresent(ClaimTopicsDO::getValue, reqVO.getValue())
                .eqIfPresent(ClaimTopicsDO::getTopic, reqVO.getTopic())
                .betweenIfPresent(ClaimTopicsDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(ClaimTopicsDO::getId));
    }

}