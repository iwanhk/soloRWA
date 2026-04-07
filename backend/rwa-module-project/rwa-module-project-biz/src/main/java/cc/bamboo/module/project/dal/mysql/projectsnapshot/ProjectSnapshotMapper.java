package cc.bamboo.module.project.dal.mysql.projectsnapshot;

import cc.bamboo.framework.common.pojo.PageResult;
import cc.bamboo.framework.mybatis.core.mapper.BaseMapperX;
import cc.bamboo.framework.mybatis.core.query.LambdaQueryWrapperX;
import cc.bamboo.module.project.controller.admin.projectsnapshot.vo.ProjectSnapshotPageReqVO;
import cc.bamboo.module.project.dal.dataobject.projectsnapshot.ProjectSnapshotDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 项目快照 Mapper
 *
 * @author Swolf
 */
@Mapper
public interface ProjectSnapshotMapper extends BaseMapperX<ProjectSnapshotDO> {

    default PageResult<ProjectSnapshotDO> selectPage(ProjectSnapshotPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ProjectSnapshotDO>()
                .eqIfPresent(ProjectSnapshotDO::getProjectId, reqVO.getProjectId())
                .eqIfPresent(ProjectSnapshotDO::getSnapshotStatus, reqVO.getSnapshotStatus())
                .orderByDesc(ProjectSnapshotDO::getSnapshotVersion));
    }

    @Select("SELECT MAX(snapshot_version) FROM biz_project_snapshot WHERE project_id = #{projectId} AND deleted = 0")
    Integer selectMaxVersion(Long projectId);

    default ProjectSnapshotDO selectLatestByProjectId(Long projectId) {
        return selectOne(new LambdaQueryWrapperX<ProjectSnapshotDO>()
                .eq(ProjectSnapshotDO::getProjectId, projectId)
                .orderByDesc(ProjectSnapshotDO::getSnapshotVersion)
                .last("LIMIT 1"));
    }

    default ProjectSnapshotDO selectPendingByProjectId(Long projectId) {
        return selectOne(new LambdaQueryWrapperX<ProjectSnapshotDO>()
                .eq(ProjectSnapshotDO::getProjectId, projectId)
                .eq(ProjectSnapshotDO::getSnapshotStatus, 1) // PENDING
                .orderByDesc(ProjectSnapshotDO::getSnapshotVersion)
                .last("LIMIT 1"));
    }

    default ProjectSnapshotDO selectLatestApprovedByProjectId(Long projectId) {
        return selectOne(new LambdaQueryWrapperX<ProjectSnapshotDO>()
                .eq(ProjectSnapshotDO::getProjectId, projectId)
                .eq(ProjectSnapshotDO::getSnapshotStatus, 2) // APPROVED
                .orderByDesc(ProjectSnapshotDO::getSnapshotVersion)
                .last("LIMIT 1"));
    }

}
