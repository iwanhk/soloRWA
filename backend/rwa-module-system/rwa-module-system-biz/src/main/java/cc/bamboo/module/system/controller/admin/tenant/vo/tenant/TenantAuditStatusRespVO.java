package cc.bamboo.module.system.controller.admin.tenant.vo.tenant;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 租户审核状态响应 VO
 */
@Schema(description = "管理后台 - 租户审核状态 Response VO")
@Data
public class TenantAuditStatusRespVO {

    @Schema(description = "审核状态：0-待提交 1-待审核 2-审核通过 3-审核拒绝", example = "0")
    private Integer auditStatus;

    @Schema(description = "审核备注（拒绝原因）", example = "营业执照不清晰")
    private String auditRemark;

    @Schema(description = "是否已提交过发行商信息", example = "true")
    private Boolean hasPublisherInfo;

}
