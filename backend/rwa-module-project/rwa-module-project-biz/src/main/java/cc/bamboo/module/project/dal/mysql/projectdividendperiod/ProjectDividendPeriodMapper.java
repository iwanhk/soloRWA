package cc.bamboo.module.project.dal.mysql.projectdividendperiod;

import java.time.LocalDate;
import java.util.*;

import cc.bamboo.framework.common.pojo.PageResult;
import cc.bamboo.framework.mybatis.core.query.LambdaQueryWrapperX;
import cc.bamboo.framework.mybatis.core.mapper.BaseMapperX;
import cc.bamboo.module.project.dal.dataobject.projectdividendperiod.ProjectDividendPeriodDO;
import org.apache.ibatis.annotations.Mapper;
import cc.bamboo.module.project.controller.admin.projectdividendperiod.vo.*;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 分红周期 Mapper
 *
 * @author Swolf
 */
@Mapper
public interface ProjectDividendPeriodMapper extends BaseMapperX<ProjectDividendPeriodDO> {

    default PageResult<ProjectDividendPeriodDO> selectPage(ProjectDividendPeriodPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ProjectDividendPeriodDO>()
                .eqIfPresent(ProjectDividendPeriodDO::getProjectId, reqVO.getProjectId())
                .eqIfPresent(ProjectDividendPeriodDO::getPeriodSeq, reqVO.getPeriodSeq())
                .betweenIfPresent(ProjectDividendPeriodDO::getUnlockDate, reqVO.getUnlockDate())
                .betweenIfPresent(ProjectDividendPeriodDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(ProjectDividendPeriodDO::getId));
    }

    /**
     * 查询项目最早的分红日期
     */
    @Select("SELECT MIN(unlock_date) FROM biz_project_dividend_period WHERE project_id = #{projectId} AND deleted = 0")
    LocalDate selectFirstUnlockDateByProjectId(@Param("projectId") Long projectId);

    /**
     * 查询项目当前已解锁的最大分红日期
     */
    @Select("SELECT MAX(unlock_date) FROM biz_project_dividend_period WHERE project_id = #{projectId} AND unlock_date <= #{currentDate} AND deleted = 0")
    LocalDate selectCurrentUnlockDateByProjectId(@Param("projectId") Long projectId,
            @Param("currentDate") LocalDate currentDate);

}