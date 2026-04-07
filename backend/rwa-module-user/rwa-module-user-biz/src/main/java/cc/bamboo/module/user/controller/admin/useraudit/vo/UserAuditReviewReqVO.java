package cc.bamboo.module.user.controller.admin.useraudit.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Schema(description = "管理后台 - 审核用户认证 Request VO")
@Data
public class UserAuditReviewReqVO {

    @Schema(description = "审核记录ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "审核记录ID不能为空")
    private Long id;

    @Schema(description = "审核状态: 2-审核通过, 3-审核驳回", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @NotNull(message = "审核状态不能为空")
    private Integer auditStatus;

    @Schema(description = "审核备注(驳回原因)", example = "证件照片不清晰")
    private String auditRemark;

}
