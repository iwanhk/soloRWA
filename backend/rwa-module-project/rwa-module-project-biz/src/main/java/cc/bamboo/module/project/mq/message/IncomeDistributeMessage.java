package cc.bamboo.module.project.mq.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 收益发放消息
 * 用于异步发放订单收益
 *
 * @author Swolf
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IncomeDistributeMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 项目收益ID
     */
    private Long projectRevenueId;

    /**
     * 订单每日收益ID
     */
    private Long orderDailyIncomeId;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 项目ID
     */
    private Long projectId;

    /**
     * 订单每日收益金额
     */
    private BigDecimal orderDailyIncome;

}
