package cc.bamboo.module.project.mq.message;

import lombok.Data;

import java.io.Serializable;

/**
 * 订单取消延时消息
 * 用于30分钟后自动取消未支付订单并恢复库存
 *
 * @author Swolf
 */
@Data
public class OrderCancelMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 项目ID
     */
    private Long projectId;

    /**
     * 购买数量（用于恢复库存）
     */
    private Integer quantity;

    /**
     * 用户ID
     */
    private Long userId;

}
