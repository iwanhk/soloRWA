package cc.bamboo.module.project.controller.app.projectorder.vo;

import cc.bamboo.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户 APP - 订单列表 Response VO
 *
 * @author Swolf
 */
@Schema(description = "用户 APP - 订单列表 Response VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppOrderListReqVO extends PageParam {

    @Schema(description = "订单状态：0-待支付 1-审核中 2-审核通过 3-审核未通过 4-已取消", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer orderStatus;

}
