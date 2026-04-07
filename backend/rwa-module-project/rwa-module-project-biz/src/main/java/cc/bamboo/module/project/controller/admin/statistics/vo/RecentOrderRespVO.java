package cc.bamboo.module.project.controller.admin.statistics.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 最新订单数据 VO
 */
@Schema(description = "管理后台 - 最新订单 Response VO")
@Data
public class RecentOrderRespVO {

    @Schema(description = "订单ID", example = "1")
    private Long id;

    @Schema(description = "订单号", example = "ORD202601200001")
    private String orderNo;

    @Schema(description = "项目名称", example = "城市广场商铺A区")
    private String projectName;

    @Schema(description = "金额", example = "10000.00")
    private BigDecimal amount;

    @Schema(description = "订单状态", example = "3")
    private Integer status;

    @Schema(description = "创建时间", example = "2026-01-20 14:32:15")
    private String createTime;
}
