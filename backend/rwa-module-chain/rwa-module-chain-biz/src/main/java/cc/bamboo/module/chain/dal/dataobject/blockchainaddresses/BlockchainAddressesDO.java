package cc.bamboo.module.chain.dal.dataobject.blockchainaddresses;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cc.bamboo.framework.mybatis.core.dataobject.BaseDO;

/**
 * 区块链地址 DO
 *
 * @author Swolf
 */
@TableName("biz_blockchain_addresses")
@KeySequence("biz_blockchain_addresses_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlockchainAddressesDO extends BaseDO {

    /**
     * ID
     */
    @TableId
    private Long id;
    /**
     * 地址名称
     */
    private String name;
    /**
     * 区块链地址
     */
    private String address;
    /**
     * 地址私钥
     */
    private String privateKey;
    /**
     * 地址描述
     */
    private String description;

}