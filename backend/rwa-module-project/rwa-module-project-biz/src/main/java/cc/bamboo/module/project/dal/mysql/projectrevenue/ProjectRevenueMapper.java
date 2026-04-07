package cc.bamboo.module.project.dal.mysql.projectrevenue;

import java.util.*;

import cc.bamboo.framework.common.pojo.PageResult;
import cc.bamboo.framework.mybatis.core.query.LambdaQueryWrapperX;
import cc.bamboo.framework.mybatis.core.mapper.BaseMapperX;
import cc.bamboo.module.project.dal.dataobject.projectrevenue.ProjectRevenueDO;
import org.apache.ibatis.annotations.Mapper;
import cc.bamboo.module.project.controller.admin.projectrevenue.vo.*;

/**
 * 项目收益 Mapper
 *
 * @author Swolf
 */
@Mapper
public interface ProjectRevenueMapper extends BaseMapperX<ProjectRevenueDO> {

    default PageResult<ProjectRevenueDO> selectPage(ProjectRevenuePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ProjectRevenueDO>()
                .betweenIfPresent(ProjectRevenueDO::getRevenueDate, reqVO.getRevenueDate())
                .eqIfPresent(ProjectRevenueDO::getProjectId, reqVO.getProjectId())
                .likeIfPresent(ProjectRevenueDO::getProjectName, reqVO.getProjectName())
                .eqIfPresent(ProjectRevenueDO::getIsSend, reqVO.getIsSend())
                .eqIfPresent(ProjectRevenueDO::getIsSync, reqVO.getIsSync())
                .eqIfPresent(ProjectRevenueDO::getRemark, reqVO.getRemark())
                .betweenIfPresent(ProjectRevenueDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(ProjectRevenueDO::getId));
    }

}