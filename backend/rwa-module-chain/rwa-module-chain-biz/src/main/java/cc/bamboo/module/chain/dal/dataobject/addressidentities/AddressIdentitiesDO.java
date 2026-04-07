package cc.bamboo.module.chain.dal.dataobject.addressidentities;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cc.bamboo.framework.mybatis.core.dataobject.BaseDO;

/**
 * 地址身份关联 DO
 *
 * @author Swolf
 */
@TableName("biz_address_identities")
@KeySequence("biz_address_identities_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressIdentitiesDO extends BaseDO {

    /**
     * ID
     */
    @TableId
    private Long id;
    /**
     * 区块链地址
     */
    private String address;
    /**
     * 身份ID，关联到对应的身份表
     */
    private Long identityId;
    /**
     * 身份类型，如ClaimIssuer、User等
     */
    private String type;
    /**
     * 合约地址
     */
    private String contractAddress;

}