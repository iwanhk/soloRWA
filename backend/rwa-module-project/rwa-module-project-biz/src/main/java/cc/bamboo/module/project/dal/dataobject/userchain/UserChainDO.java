package cc.bamboo.module.project.dal.dataobject.userchain;

import cc.bamboo.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 用户链地址表= DO
 *
 * @author Swolf
 */
@TableName("biz_user_chain")
@KeySequence("biz_user_chain_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserChainDO extends BaseDO {

    /**
     * ID
     */
    @TableId
    private Long id;
    /**
     * 用户ID
     */
    private Long userId;
    /**
     * 关联链ID（chain_manage.id）
     */
    private Long chainId;
    /**
     * 用户在该链上的地址（如ETH地址：0x...）
     */
    private String chainAddress;
    /**
     * identity_id
     */
    private Long identityId;
    /**
     * 链上状态
     *
     * 枚举 {@link TODO biz_user_chain_status 对应的类}
     */
    private Integer chainStatus;
    /**
     * 是否默认地址：1-是 0-否（同一链下仅1个默认）
     */
    private Boolean isDefault;
    /**
     * 地址备注（如“常用钱包”）
     */
    private String addressRemark;

}