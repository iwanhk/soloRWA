package cc.bamboo.module.project.controller.app.projectorder.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 用户 APP - 账单状态统计 Response VO
 *
 * @author Swolf
 */
@Schema(description = "用户 APP - 账单状态统计 Response VO")
@Data
public class AppBillStatusCountRespVO {

    @Schema(description = "待审核数量", example = "1")
    private Integer pendingCount;

    @Schema(description = "审核通过数量", example = "5")
    private Integer approvedCount;

    @Schema(description = "审核不通过数量", example = "0")
    private Integer rejectedCount;

    @Schema(description = "总数量", example = "6")
    private Integer totalCount;

}
