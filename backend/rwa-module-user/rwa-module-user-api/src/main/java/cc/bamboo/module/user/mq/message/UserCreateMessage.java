package cc.bamboo.module.user.mq.message;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户下单消息
 * 用于通知 chain 模块添加用户并签发 Claim
 *
 * @author Swolf
 */
@Data
public class UserCreateMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 任务编号（幂等性标识）
     */
    private String taskNo;


    /**
     * 用户ID
     */
    private Long userId;

    private Long userChainId;

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
