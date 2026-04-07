package cc.bamboo.module.chain.dal.dataobject.useridentities;

import lombok.*;
import com.baomidou.mybatisplus.annotation.*;
import cc.bamboo.framework.mybatis.core.dataobject.BaseDO;

/**
 * 用户身份 DO
 *
 * @author Swolf
 */
@TableName("biz_user_identities")
@KeySequence("biz_user_identities_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserIdentitiesDO extends BaseDO {

    /**
     * ID
     */
    @TableId
    private Long id;

    private Long userId;
    /**
     * 用户地址
     */
    private String address;
    /**
     * 用户合约地址
     */
    private String contractAddress;
    /**
     * 管理密钥地址
     */
    private String managementKey;
    /**
     * 关联的区块链地址ID
     */
    private Long blockchainAddressId;
    /**
     * 盐值，用于加密或哈希计算
     */
    private String salt;
    /**
     * 交易哈希
     */
    private String transactionHash;
    /**
     * 区块号
     */
    private Long blockNumber;
    /**
     * 合约是否已部署，0-未部署，1-已部署
     */
    private Boolean contractDeployed;
    /**
     * 声明密钥是否已设置，0-未设置，1-已设置
     */
    private Boolean claimKeySetup;
    /**
     * 国家代码
     */
    private Long countryCode;
    /**
     * 关联的代币ID，JSON格式
     */
    private String associatedTokenIds;
    /**
     * 待处理的代币ID，JSON格式
     */
    private String pendingTokenIds;

    private String topicIds;
    /**
     * 状态，如active-活跃
     */
    private String status;

}