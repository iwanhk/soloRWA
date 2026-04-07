package cc.bamboo.module.project.controller.admin.statistics.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 订单状态分布数据 VO
 */
@Schema(description = "管理后台 - 订单状态分布 Response VO")
@Data
public class OrderStatusDistRespVO {

    @Schema(description = "状态名称", example = "待支付")
    private String name;

    @Schema(description = "状态值", example = "1")
    private Integer status;

    @Schema(description = "数量", example = "320")
    private Long count;
}
