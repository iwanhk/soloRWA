package cc.bamboo.module.project.service.projectsnapshot;

import cc.bamboo.framework.common.pojo.PageResult;
import cc.bamboo.module.project.controller.admin.projectsnapshot.vo.ProjectSnapshotPageReqVO;
import cc.bamboo.module.project.dal.dataobject.projectinfo.ProjectInfoDO;
import cc.bamboo.module.project.dal.dataobject.projectsnapshot.ProjectSnapshotDO;

/**
 * 项目快照 Service 接口
 *
 * @author Swolf
 */
public interface ProjectSnapshotService {

    /**
     * 创建待审核快照
     *
     * @param projectData 项目数据
     * @return 快照ID
     */
    Long createPendingSnapshot(ProjectInfoDO projectData);

    /**
     * 创建已通过快照（用于首次审核通过留档）
     *
     * @param projectData  项目数据
     * @param operatorId   审核人ID
     * @param operatorName 审核人名称
     * @return 快照ID
     */
    Long createApprovedSnapshot(ProjectInfoDO projectData, Long operatorId, String operatorName);

    /**
     * 更新快照状态
     *
     * @param snapshotId   快照ID
     * @param status       状态
     * @param operatorId   审核人ID
     * @param operatorName 审核人名称
     * @param auditRemark  审核备注
     */
    void updateStatus(Long snapshotId, Integer status, Long operatorId, String operatorName, String auditRemark);

    /**
     * 检查项目是否有待审核快照
     *
     * @param projectId 项目ID
     * @return 是否存在
     */
    boolean hasPendingSnapshot(Long projectId);

    /**
     * 获取项目待审核快照
     *
     * @param projectId 项目ID
     * @return 快照
     */
    ProjectSnapshotDO getPendingSnapshot(Long projectId);

    /**
     * 获取项目最新快照
     *
     * @param projectId 项目ID
     * @return 快照
     */
    ProjectSnapshotDO getLatestSnapshot(Long projectId);

    /**
     * 获取快照详情
     *
     * @param id 快照ID
     * @return 快照
     */
    ProjectSnapshotDO getSnapshot(Long id);

    /**
     * 分页查询快照
     *
     * @param pageReqVO 分页参数
     * @return 快照分页
     */
    PageResult<ProjectSnapshotDO> getSnapshotPage(ProjectSnapshotPageReqVO pageReqVO);

}
