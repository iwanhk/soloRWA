package cc.bamboo.module.project.controller.admin.projectbill.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 账单上传支付凭证请求 VO
 *
 * @author Swolf
 */
@Schema(description = "管理后台 - 账单上传支付凭证 Request VO")
@Data
public class ProjectBillUploadVoucherReqVO {

    @Schema(description = "账单ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "账单ID不能为空")
    private Long id;

    @Schema(description = "支付凭证URL", requiredMode = Schema.RequiredMode.REQUIRED, example = "https://example.com/voucher.jpg")
    @NotBlank(message = "支付凭证不能为空")
    private String payVoucherUrl;

    private LocalDateTime payTime;

    /**
     * 到账币种
     */
    private String actualCoin;
    /**
     * 到账汇率
     */
    private BigDecimal actualExchangeRate;

    private BigDecimal actualAmount;


}
