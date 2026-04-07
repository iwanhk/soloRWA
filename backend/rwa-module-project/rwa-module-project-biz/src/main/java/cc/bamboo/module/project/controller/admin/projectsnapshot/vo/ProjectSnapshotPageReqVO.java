package cc.bamboo.module.project.controller.admin.projectsnapshot.vo;

import cc.bamboo.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "管理后台 - 项目快照分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ProjectSnapshotPageReqVO extends PageParam {

    @Schema(description = "项目ID", example = "1")
    private Long projectId;

    @Schema(description = "快照状态: 0=待审核 1=已通过 2=已拒绝", example = "1")
    private Integer snapshotStatus;

}
