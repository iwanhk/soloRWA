package cc.bamboo.module.project.controller.admin.statistics.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 订单审核统计 Response VO
 *
 * @author Swolf
 */
@Schema(description = "管理后台 - 订单审核统计 Response VO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderAuditStatisticsRespVO {

    @Schema(description = "订单审核数", requiredMode = Schema.RequiredMode.REQUIRED, example = "8")
    private Long orderAuditCount;

}
