package cc.bamboo.module.project.mq.message;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户下单消息
 * 用于通知 chain 模块添加用户并签发 Claim
 *
 * @author Swolf
 */
@Data
public class OrderCreateMessage implements Serializable {

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
     * 国家代码
     */
    private Integer countryCode;

    /**
     * 当前重试次数
     */
    private Integer retryCount;

}
