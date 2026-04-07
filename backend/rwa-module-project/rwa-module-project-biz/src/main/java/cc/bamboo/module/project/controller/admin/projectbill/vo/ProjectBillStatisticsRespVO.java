package cc.bamboo.module.project.controller.admin.projectbill.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 账单统计响应 VO
 *
 * @author Swolf
 */
@Schema(description = "管理后台 - 账单统计 Response VO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectBillStatisticsRespVO {

    @Schema(description = "待审核数量", example = "10")
    private Long pendingCount;

    @Schema(description = "已审核未支付数量", example = "5")
    private Long approvedUnpaidCount;

    @Schema(description = "已支付数量", example = "20")
    private Long paidCount;

    @Schema(description = "分红总金额", example = "100000.00")
    private BigDecimal dividendAmount;

    @Schema(description = "赎回总金额", example = "500000.00")
    private BigDecimal redemptionAmount;

}
