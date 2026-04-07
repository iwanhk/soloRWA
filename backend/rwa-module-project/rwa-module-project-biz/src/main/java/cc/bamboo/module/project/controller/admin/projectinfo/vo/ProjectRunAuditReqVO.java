package cc.bamboo.module.project.controller.admin.projectinfo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 项目运行审核 Request VO
 *
 * @author Kiro
 */
@Schema(description = "管理后台 - 项目运行审核 Request VO")
@Data
public class ProjectRunAuditReqVO {

    @Schema(description = "项目ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "项目ID不能为空")
    private Long projectId;

    @Schema(description = "是否通过", requiredMode = Schema.RequiredMode.REQUIRED, example = "true")
    @NotNull(message = "审核结果不能为空")
    private Boolean approved;

    @Schema(description = "审核备注", example = "不通过原因")
    private String auditRemark;

}
