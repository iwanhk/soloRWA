package cc.bamboo.module.project.controller.app.projectorder.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户 APP - 订单信息 Response VO
 *
 * @author Swolf
 */
@Schema(description = "用户 APP - 订单信息 Response VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppProjectOrderRespVO {

    @Schema(description = "订单ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "订单号", requiredMode = Schema.RequiredMode.REQUIRED, example = "ORD20231201123456")
    private String orderNo;

    @Schema(description = "申请日期", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime applyDate;

    @Schema(description = "订单状态：1-待支付 2-审核中 3-审核通过 4-审核未通过 5-已取消", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer orderStatus;

    @Schema(description = "项目ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long projectId;

    @Schema(description = "项目名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "某某RWA项目")
    private String projectName;

    @Schema(description = "申购份额(份)", requiredMode = Schema.RequiredMode.REQUIRED, example = "10")
    private Integer subscribeQuantity;

    @Schema(description = "单价", requiredMode = Schema.RequiredMode.REQUIRED, example = "1000.00")
    private BigDecimal price;

    @Schema(description = "金额(元)", requiredMode = Schema.RequiredMode.REQUIRED, example = "10000.00")
    private BigDecimal totalAmount;

    @Schema(description = "链地址", requiredMode = Schema.RequiredMode.REQUIRED, example = "0x1234567890abcdef1234567890abcdef12345678")
    private String chainAddress;

    @Schema(description = "订单过期时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime expireTime;

}
