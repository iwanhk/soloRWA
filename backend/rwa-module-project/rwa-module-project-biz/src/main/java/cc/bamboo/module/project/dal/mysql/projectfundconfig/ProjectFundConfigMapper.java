package cc.bamboo.module.project.dal.mysql.projectfundconfig;

import java.util.*;

import cc.bamboo.framework.common.pojo.PageResult;
import cc.bamboo.framework.mybatis.core.query.LambdaQueryWrapperX;
import cc.bamboo.framework.mybatis.core.mapper.BaseMapperX;
import cc.bamboo.module.project.dal.dataobject.projectfundconfig.ProjectFundConfigDO;
import org.apache.ibatis.annotations.Mapper;
import cc.bamboo.module.project.controller.admin.projectfundconfig.vo.*;

/**
 * 基金项目配置 Mapper
 *
 * @author Swolf
 */
@Mapper
public interface ProjectFundConfigMapper extends BaseMapperX<ProjectFundConfigDO> {

    default PageResult<ProjectFundConfigDO> selectPage(ProjectFundConfigPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ProjectFundConfigDO>()
                .eqIfPresent(ProjectFundConfigDO::getOperationCost, reqVO.getOperationCost())
                .eqIfPresent(ProjectFundConfigDO::getOperationCostType, reqVO.getOperationCostType())
                .eqIfPresent(ProjectFundConfigDO::getTeamShareRatio, reqVO.getTeamShareRatio())
                .eqIfPresent(ProjectFundConfigDO::getThresholdMin, reqVO.getThresholdMin())
                .eqIfPresent(ProjectFundConfigDO::getThresholdMax, reqVO.getThresholdMax())
                .eqIfPresent(ProjectFundConfigDO::getAlertEnabled, reqVO.getAlertEnabled())
                .eqIfPresent(ProjectFundConfigDO::getFundJson, reqVO.getFundJson())
                .betweenIfPresent(ProjectFundConfigDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(ProjectFundConfigDO::getProjectId));
    }

}