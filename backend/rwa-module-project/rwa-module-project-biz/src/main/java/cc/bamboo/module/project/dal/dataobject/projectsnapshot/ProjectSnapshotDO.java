package cc.bamboo.module.project.dal.dataobject.projectsnapshot;

import cc.bamboo.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 项目快照 DO
 *
 * @author Swolf
 */
@TableName("biz_project_snapshot")
@KeySequence("biz_project_snapshot_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectSnapshotDO extends TenantBaseDO {

    /**
     * 快照ID
     */
    @TableId
    private Long id;

    /**
     * 关联项目ID
     */
    private Long projectId;

    /**
     * 版本号（项目维度自增）
     */
    private Integer snapshotVersion;

    /**
     * 快照状态: 0=待审核 1=已通过 2=已拒绝
     *
     * @see cc.bamboo.module.project.enums.SnapshotStatusEnum
     */
    private Integer snapshotStatus;

    /**
     * 审核人ID
     */
    private Long operatorId;

    /**
     * 审核人名称
     */
    private String operatorName;

    /**
     * 审核备注
     */
    private String auditRemark;

    /**
     * 项目完整数据JSON（ProjectInfoDO序列化）
     */
    private String projectData;

}
