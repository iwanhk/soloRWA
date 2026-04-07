package cc.bamboo.module.system.controller.admin.tenant.vo.tenant;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 租户审核请求 VO
 * 超级管理员审核租户
 */
@Schema(description = "管理后台 - 租户审核 Request VO")
@Data
public class TenantAuditReqVO {

    @Schema(description = "租户ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "租户ID不能为空")
    private Long tenantId;

    @Schema(description = "审核结果：2-通过 3-拒绝", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @NotNull(message = "审核结果不能为空")
    private Integer auditResult;

    @Schema(description = "审核备注（拒绝时必填）", example = "营业执照不清晰，请重新上传")
    private String auditRemark;

}
