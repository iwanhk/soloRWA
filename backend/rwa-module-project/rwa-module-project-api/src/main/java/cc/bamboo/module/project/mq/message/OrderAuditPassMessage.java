package cc.bamboo.module.project.mq.message;

import lombok.Data;

import java.io.Serializable;

/**
 * 订单审核通过消息
 * 用于通知 chain 模块为用户铸造 Token
 *
 * @author Swolf
 */
@Data
public class OrderAuditPassMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 任务编号（幂等性标识）
     */
    private String taskNo;

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
     * 项目名称
     */
    private String projectName;

    /**
     * Token 合约地址
     */
    private String tokenAddress;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户钱包地址
     */
    private String userAddress;

    /**
     * 铸造数量
     */
    private String mintAmount;

    /**
     * 当前重试次数
     */
    private Integer retryCount;

}
