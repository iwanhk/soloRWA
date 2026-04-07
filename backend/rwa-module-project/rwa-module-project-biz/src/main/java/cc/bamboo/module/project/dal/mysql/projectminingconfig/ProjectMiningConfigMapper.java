package cc.bamboo.module.project.dal.mysql.projectminingconfig;

import java.util.*;

import cc.bamboo.framework.common.pojo.PageResult;
import cc.bamboo.framework.mybatis.core.query.LambdaQueryWrapperX;
import cc.bamboo.framework.mybatis.core.mapper.BaseMapperX;
import cc.bamboo.module.project.dal.dataobject.projectminingconfig.ProjectMiningConfigDO;
import org.apache.ibatis.annotations.Mapper;
import cc.bamboo.module.project.controller.admin.projectminingconfig.vo.*;

/**
 * 挖矿项目配置 Mapper
 *
 * @author swolf
 */
@Mapper
public interface ProjectMiningConfigMapper extends BaseMapperX<ProjectMiningConfigDO> {

    default PageResult<ProjectMiningConfigDO> selectPage(ProjectMiningConfigPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ProjectMiningConfigDO>()
                .eqIfPresent(ProjectMiningConfigDO::getPoolAccessKey, reqVO.getPoolAccessKey())
                .eqIfPresent(ProjectMiningConfigDO::getPoolPrivateKey, reqVO.getPoolPrivateKey())
                .likeIfPresent(ProjectMiningConfigDO::getPoolName, reqVO.getPoolName())
                .eqIfPresent(ProjectMiningConfigDO::getPowerConsumption, reqVO.getPowerConsumption())
                .eqIfPresent(ProjectMiningConfigDO::getElectricityPrice, reqVO.getElectricityPrice())
                .eqIfPresent(ProjectMiningConfigDO::getOperationCost, reqVO.getOperationCost())
                .eqIfPresent(ProjectMiningConfigDO::getOperationCostType, reqVO.getOperationCostType())
                .eqIfPresent(ProjectMiningConfigDO::getTeamShareRatio, reqVO.getTeamShareRatio())
                .eqIfPresent(ProjectMiningConfigDO::getThresholdMin, reqVO.getThresholdMin())
                .eqIfPresent(ProjectMiningConfigDO::getThresholdMax, reqVO.getThresholdMax())
                .eqIfPresent(ProjectMiningConfigDO::getAlertEnabled, reqVO.getAlertEnabled())
                .betweenIfPresent(ProjectMiningConfigDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(ProjectMiningConfigDO::getProjectId));
    }

}