package cc.bamboo.module.chain.dal.dataobject.identityregistrystorages;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cc.bamboo.framework.mybatis.core.dataobject.BaseDO;

/**
 * 身份注册表存储 DO
 *
 * @author Swolf
 */
@TableName("biz_identity_registry_storages")
@KeySequence("biz_identity_registry_storages_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdentityRegistryStoragesDO extends BaseDO {

    /**
     * ID
     */
    @TableId
    private Long id;
    /**
     * 存储合约地址
     */
    private String address;
    /**
     * 部署者地址
     */
    private String deployerAddress;
    /**
     * 交易哈希
     */
    private String transactionHash;
    /**
     * 区块号
     */
    private Long blockNumber;
    /**
     * 绑定的代币数量
     */
    private Long boundTokenCount;
    /**
     * 状态，如deployed-已部署，active-活跃
     */
    private String status;

}