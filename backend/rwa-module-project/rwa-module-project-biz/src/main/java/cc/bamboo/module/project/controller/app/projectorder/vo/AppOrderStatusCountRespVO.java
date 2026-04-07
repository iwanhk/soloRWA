package cc.bamboo.module.project.controller.app.projectorder.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 用户 APP - 订单状态统计 Response VO
 *
 * @author Swolf
 */
@Schema(description = "用户 APP - 订单状态统计 Response VO")
@Data
public class AppOrderStatusCountRespVO {

    @Schema(description = "待支付数量", example = "1")
    private Integer pendingPaymentCount;

    @Schema(description = "审核中数量", example = "2")
    private Integer underReviewCount;

    @Schema(description = "审核通过数量", example = "5")
    private Integer approvedCount;

    @Schema(description = "审核未通过数量", example = "0")
    private Integer rejectedCount;

    @Schema(description = "已取消数量", example = "1")
    private Integer cancelledCount;

    @Schema(description = "总数量", example = "9")
    private Integer totalCount;

}
