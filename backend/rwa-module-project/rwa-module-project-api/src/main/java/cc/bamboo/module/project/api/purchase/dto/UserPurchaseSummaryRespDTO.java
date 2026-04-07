package cc.bamboo.module.project.api.purchase.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Schema(description = "RPC 服务 - 用户购买汇总 Response DTO")
@Data
public class UserPurchaseSummaryRespDTO {

    @Schema(description = "用户 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long userId;

    @Schema(description = "购买项目明细")
    private List<UserPurchaseProjectAmountRespDTO> projectAmounts;

    @Schema(description = "购买总金额", example = "9999.99")
    private BigDecimal totalAmount;
}

