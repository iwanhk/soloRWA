package cc.bamboo.module.project.controller.admin.projectorder.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 管理后台 - 项目订单审核 Request VO
 *
 * @author Swolf
 */
@Schema(description = "管理后台 - 项目订单审核 Request VO")
@Data
public class ProjectOrderAuditReqVO {

    @Schema(description = "订单ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "订单ID不能为空")
    private Long id;

    @Schema(description = "审核结果：true-通过 false-不通过", requiredMode = Schema.RequiredMode.REQUIRED, example = "true")
    @NotNull(message = "审核结果不能为空")
    private Boolean approved;

    @Schema(description = "审核备注（审核不通过时必填）", example = "支付凭证不清晰")
    private String auditRemark;

    @Schema(description = "合同附件URL列表", example = "[\"https://example.com/contract1.pdf\"]")
    private String contractFileUrls;

}
