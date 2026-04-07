package cc.bamboo.module.project.api.purchase.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Schema(description = "RPC 服务 - 用户购买项目金额 Response DTO")
@Data
public class UserPurchaseProjectAmountRespDTO {

    @Schema(description = "用户 ID", example = "1024")
    private Long userId;

    @Schema(description = "项目 ID", example = "1001")
    private Long projectId;

    @Schema(description = "项目名称", example = "项目A")
    private String projectName;

    @Schema(description = "购买金额（按订单金额汇总）", example = "1234.56")
    private BigDecimal amount;

    @Schema(description = "投资币种", example = "USDT")
    private String investmentCurrency;
}
