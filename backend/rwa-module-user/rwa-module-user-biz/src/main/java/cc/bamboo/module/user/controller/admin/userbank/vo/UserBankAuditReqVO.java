package cc.bamboo.module.user.controller.admin.userbank.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Schema(description = "管理后台 - 用户银行卡审核 Request VO")
@Data
public class UserBankAuditReqVO {

    @Schema(description = "银行卡记录ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "银行卡记录ID不能为空")
    private Long id;

    @Schema(description = "审核状态（2-通过，3-不通过）", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @NotNull(message = "审核状态不能为空")
    private Integer auditStatus;

    @Schema(description = "审核备注", example = "审核通过")
    private String auditRemark;

}
