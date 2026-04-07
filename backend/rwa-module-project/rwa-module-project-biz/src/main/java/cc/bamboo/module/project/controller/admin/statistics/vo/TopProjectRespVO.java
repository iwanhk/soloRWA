package cc.bamboo.module.project.controller.admin.statistics.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 热门项目排行数据 VO
 */
@Schema(description = "管理后台 - 热门项目排行 Response VO")
@Data
public class TopProjectRespVO {

    @Schema(description = "项目ID", example = "1")
    private Long projectId;

    @Schema(description = "项目名称", example = "城市广场商铺A区")
    private String name;

    @Schema(description = "订单数量", example = "328")
    private Long orderCount;

    @Schema(description = "交易金额", example = "328000.00")
    private BigDecimal amount;
}
