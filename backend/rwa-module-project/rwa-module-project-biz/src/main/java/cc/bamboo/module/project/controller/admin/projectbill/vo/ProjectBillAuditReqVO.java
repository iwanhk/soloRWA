package cc.bamboo.module.project.controller.admin.projectbill.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 账单审核请求 VO
 *
 * @author Swolf
 */
@Schema(description = "管理后台 - 账单审核 Request VO")
@Data
public class ProjectBillAuditReqVO {

    @Schema(description = "账单ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "账单ID不能为空")
    private Long id;

    @Schema(description = "审核结果：true-通过，false-不通过", requiredMode = Schema.RequiredMode.REQUIRED, example = "true")
    @NotNull(message = "审核结果不能为空")
    private Boolean approved;

    @Schema(description = "审核备注", example = "审核通过")
    private String auditRemark;

    @Schema(description = "实际到账金额")
    private BigDecimal actualAmount;

    @Schema(description = "支付凭证URL（审核通过时可选）", example = "https://example.com/voucher.jpg")
    private String payVoucherUrl;

    @NotNull(message = "到账天数不能为空")
    @Min(value = 0, message = "到账天数不能小于0")
    private Integer arrivalDays;


    /**
     * 到账币种
     */
    private String actualCoin;
    /**
     * 到账汇率
     */
    private BigDecimal actualExchangeRate;

}
