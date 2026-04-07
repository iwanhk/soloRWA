package cc.bamboo.module.project.controller.admin.projectsnapshot.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 项目快照 Response VO")
@Data
public class ProjectSnapshotRespVO {

    @Schema(description = "快照ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "项目ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long projectId;

    @Schema(description = "版本号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer snapshotVersion;

    @Schema(description = "快照状态: 0=待审核 1=已通过 2=已拒绝", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer snapshotStatus;

    @Schema(description = "审核人ID", example = "1")
    private Long operatorId;

    @Schema(description = "审核人名称", example = "张三")
    private String operatorName;

    @Schema(description = "审核备注", example = "审核通过")
    private String auditRemark;

    @Schema(description = "项目数据JSON", requiredMode = Schema.RequiredMode.REQUIRED)
    private String projectData;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}
