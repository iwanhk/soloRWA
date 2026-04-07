package cc.bamboo.module.project.service.projectsnapshot;

import cc.bamboo.framework.common.pojo.PageResult;
import cc.bamboo.module.project.controller.admin.projectsnapshot.vo.ProjectSnapshotPageReqVO;
import cc.bamboo.module.project.dal.dataobject.projectinfo.ProjectInfoDO;
import cc.bamboo.module.project.dal.dataobject.projectsnapshot.ProjectSnapshotDO;
import cc.bamboo.module.project.dal.mysql.projectsnapshot.ProjectSnapshotMapper;
import cc.bamboo.module.project.enums.SnapshotStatusEnum;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 项目快照 Service 实现类
 *
 * @author Swolf
 */
@Service
@Slf4j
public class ProjectSnapshotServiceImpl implements ProjectSnapshotService {

    @Resource
    private ProjectSnapshotMapper snapshotMapper;

    @Override
    public Long createPendingSnapshot(ProjectInfoDO projectData) {
        log.info("[createPendingSnapshot] 创建待审核快照，项目ID: {}", projectData.getProjectId());

        // 获取版本号
        Integer maxVersion = snapshotMapper.selectMaxVersion(projectData.getProjectId());
        int newVersion = (maxVersion == null ? 0 : maxVersion) + 1;

        // 创建快照
        ProjectSnapshotDO snapshot = ProjectSnapshotDO.builder()
                .projectId(projectData.getProjectId())
                .snapshotVersion(newVersion)
                .snapshotStatus(SnapshotStatusEnum.PENDING.getStatus())
                .projectData(JSON.toJSONString(projectData))
                .build();
        snapshot.setTenantId(projectData.getTenantId());

        snapshotMapper.insert(snapshot);
        log.info("[createPendingSnapshot] 快照创建成功，快照ID: {}, 版本: {}", snapshot.getId(), newVersion);
        return snapshot.getId();
    }

    @Override
    public Long createApprovedSnapshot(ProjectInfoDO projectData, Long operatorId, String operatorName) {
        log.info("[createApprovedSnapshot] 创建已通过快照，项目ID: {}", projectData.getProjectId());

        // 获取版本号
        Integer maxVersion = snapshotMapper.selectMaxVersion(projectData.getProjectId());
        int newVersion = (maxVersion == null ? 0 : maxVersion) + 1;

        // 创建快照
        ProjectSnapshotDO snapshot = ProjectSnapshotDO.builder()
                .projectId(projectData.getProjectId())
                .snapshotVersion(newVersion)
                .snapshotStatus(SnapshotStatusEnum.APPROVED.getStatus())
                .operatorId(operatorId)
                .operatorName(operatorName)
                .projectData(JSON.toJSONString(projectData))
                .build();
        snapshot.setTenantId(projectData.getTenantId());

        snapshotMapper.insert(snapshot);
        log.info("[createApprovedSnapshot] 快照创建成功，快照ID: {}, 版本: {}", snapshot.getId(), newVersion);
        return snapshot.getId();
    }

    @Override
    public void updateStatus(Long snapshotId, Integer status, Long operatorId, String operatorName,
            String auditRemark) {
        log.info("[updateStatus] 更新快照状态，快照ID: {}, 状态: {}", snapshotId, status);

        ProjectSnapshotDO update = ProjectSnapshotDO.builder()
                .id(snapshotId)
                .snapshotStatus(status)
                .operatorId(operatorId)
                .operatorName(operatorName)
                .auditRemark(auditRemark)
                .build();

        snapshotMapper.updateById(update);
    }

    @Override
    public boolean hasPendingSnapshot(Long projectId) {
        return snapshotMapper.selectPendingByProjectId(projectId) != null;
    }

    @Override
    public ProjectSnapshotDO getPendingSnapshot(Long projectId) {
        return snapshotMapper.selectPendingByProjectId(projectId);
    }

    @Override
    public ProjectSnapshotDO getLatestSnapshot(Long projectId) {
        return snapshotMapper.selectLatestByProjectId(projectId);
    }

    @Override
    public ProjectSnapshotDO getSnapshot(Long id) {
        return snapshotMapper.selectById(id);
    }

    @Override
    public PageResult<ProjectSnapshotDO> getSnapshotPage(ProjectSnapshotPageReqVO pageReqVO) {
        return snapshotMapper.selectPage(pageReqVO);
    }

}
