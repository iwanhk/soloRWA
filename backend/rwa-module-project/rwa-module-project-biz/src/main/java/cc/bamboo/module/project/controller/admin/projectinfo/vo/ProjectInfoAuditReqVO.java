package cc.bamboo.module.project.controller.admin.projectinfo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Schema(description = "管理后台 - 项目审核 Request VO")
@Data
public class ProjectInfoAuditReqVO {

    @Schema(description = "项目ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "项目ID不能为空")
    private Long projectId;

    @Schema(description = "是否通过审核", requiredMode = Schema.RequiredMode.REQUIRED, example = "true")
    @NotNull(message = "审核结果不能为空")
    private Boolean approved;

    @Schema(description = "审核备注", example = "审核通过")
    private String auditRemark;

}
