package cc.bamboo.module.project.dal.mysql.projectorderbalancelog;

import java.util.*;

import cc.bamboo.framework.common.pojo.PageResult;
import cc.bamboo.framework.mybatis.core.query.LambdaQueryWrapperX;
import cc.bamboo.framework.mybatis.core.mapper.BaseMapperX;
import cc.bamboo.module.project.dal.dataobject.projectorderbalancelog.ProjectOrderBalanceLogDO;
import org.apache.ibatis.annotations.Mapper;
import cc.bamboo.module.project.controller.admin.projectorderbalancelog.vo.*;

/**
 * 用户项目余额记录 Mapper
 *
 * @author Swolf
 */
@Mapper
public interface ProjectOrderBalanceLogMapper extends BaseMapperX<ProjectOrderBalanceLogDO> {

    default PageResult<ProjectOrderBalanceLogDO> selectPage(ProjectOrderBalanceLogPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ProjectOrderBalanceLogDO>()
                .eqIfPresent(ProjectOrderBalanceLogDO::getUserId, reqVO.getUserId())
                .eqIfPresent(ProjectOrderBalanceLogDO::getProjectId, reqVO.getProjectId())
                .eqIfPresent(ProjectOrderBalanceLogDO::getOrderId, reqVO.getOrderId())
                .eqIfPresent(ProjectOrderBalanceLogDO::getAmount, reqVO.getAmount())
                .eqIfPresent(ProjectOrderBalanceLogDO::getAfterAmount, reqVO.getAfterAmount())
                .eqIfPresent(ProjectOrderBalanceLogDO::getType, reqVO.getType())
                .betweenIfPresent(ProjectOrderBalanceLogDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(ProjectOrderBalanceLogDO::getId));
    }

}