package cc.bamboo.module.chain.dal.dataobject.tokens;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cc.bamboo.framework.mybatis.core.dataobject.BaseDO;

/**
 * 代币 DO
 *
 * @author Swolf
 */
@TableName("biz_tokens")
@KeySequence("biz_tokens_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokensDO extends BaseDO {

    /**
     * ID
     */
    @TableId
    private Long id;

     /**
     * 项目ID
     */
    private Long projectId;
    /**
     * 代币名称
     */
    private String name;
    /**
     * 代币符号
     */
    private String symbol;
    /**
     * 小数位数
     */
    private Long decimals;
    /**
     * 代币合约地址
     */
    private String address;
    /**
     * 所有者地址
     */
    private String ownerAddress;
    /**
     * 部署者地址
     */
    private String deployerAddress;
    /**
     * 身份注册表存储ID
     */
    private Long identityRegistryStorageId;
    /**
     * 身份注册表地址
     */
    private String identityRegistryAddress;
    /**
     * 声明主题注册表地址
     */
    private String claimTopicsRegistryAddress;
    /**
     * 可信发行者注册表地址
     */
    private String trustedIssuersRegistryAddress;
    /**
     * 模块化合规合约地址
     */
    private String modularComplianceAddress;
    /**
     * 代币链上ID地址
     */
    private String tokenOnchainIdAddress;
    /**
     * 交易哈希
     */
    private String transactionHash;
    /**
     * 区块号
     */
    private Long blockNumber;
    /**
     * 状态，如pending-待处理，deployed-已部署
     */
    private String status;
    /**
     * 盐值，用于加密或哈希计算
     */
    private String salt;
    /**
     * 代币代理，JSON格式
     */
    private String tokenAgents;
    /**
     * 声明主题，JSON格式
     */
    private String claimTopics;
    /**
     * 发行者，JSON格式
     */
    private String issuers;
    /**
     * 发行者声明，JSON格式
     */
    private String issuerClaims;
    /**
     * 部署信息，JSON格式
     */
    private String deploymentInfo;
    /**
     * 错误信息，若有
     */
    private String errorMessage;

}