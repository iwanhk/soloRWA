package cc.bamboo.module.project.dal.mysql.projectnotice;

import java.util.*;

import cc.bamboo.framework.common.pojo.PageResult;
import cc.bamboo.framework.mybatis.core.query.LambdaQueryWrapperX;
import cc.bamboo.framework.mybatis.core.mapper.BaseMapperX;
import cc.bamboo.module.project.dal.dataobject.projectnotice.ProjectNoticeDO;
import org.apache.ibatis.annotations.Mapper;
import cc.bamboo.module.project.controller.admin.projectnotice.vo.*;

/**
 * 项目通告表（含全局通告） Mapper
 *
 * @author Swolf
 */
@Mapper
public interface ProjectNoticeMapper extends BaseMapperX<ProjectNoticeDO> {

    default PageResult<ProjectNoticeDO> selectPage(ProjectNoticePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ProjectNoticeDO>()
                .eqIfPresent(ProjectNoticeDO::getNoticeNo, reqVO.getNoticeNo())
                .eqIfPresent(ProjectNoticeDO::getProjectId, reqVO.getProjectId())
                .likeIfPresent(ProjectNoticeDO::getProjectName, reqVO.getProjectName())
                .eqIfPresent(ProjectNoticeDO::getNoticeTitle, reqVO.getNoticeTitle())
                .eqIfPresent(ProjectNoticeDO::getNoticeType, reqVO.getNoticeType())
                .eqIfPresent(ProjectNoticeDO::getNoticeContent, reqVO.getNoticeContent())
                .eqIfPresent(ProjectNoticeDO::getAttachUrls, reqVO.getAttachUrls())
                .eqIfPresent(ProjectNoticeDO::getPublishUserId, reqVO.getPublishUserId())
                .likeIfPresent(ProjectNoticeDO::getPublishUserName, reqVO.getPublishUserName())
                .betweenIfPresent(ProjectNoticeDO::getPublishTime, reqVO.getPublishTime())
                .betweenIfPresent(ProjectNoticeDO::getShowStartTime, reqVO.getShowStartTime())
                .betweenIfPresent(ProjectNoticeDO::getShowEndTime, reqVO.getShowEndTime())
                .eqIfPresent(ProjectNoticeDO::getIsTop, reqVO.getIsTop())
                .eqIfPresent(ProjectNoticeDO::getNoticeStatus, reqVO.getNoticeStatus())
                .eqIfPresent(ProjectNoticeDO::getReadCount, reqVO.getReadCount())
                .eqIfPresent(ProjectNoticeDO::getIsPopup, reqVO.getIsPopup())
                .eqIfPresent(ProjectNoticeDO::getRemark, reqVO.getRemark())
                .betweenIfPresent(ProjectNoticeDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(ProjectNoticeDO::getId));
    }

}