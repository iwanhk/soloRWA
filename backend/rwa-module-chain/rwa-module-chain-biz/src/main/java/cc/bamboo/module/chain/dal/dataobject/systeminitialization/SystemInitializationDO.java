package cc.bamboo.module.chain.dal.dataobject.systeminitialization;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cc.bamboo.framework.mybatis.core.dataobject.BaseDO;

/**
 * 系统初始化 DO
 *
 * @author Swolf
 */
@TableName("biz_system_initialization")
@KeySequence("biz_system_initialization_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemInitializationDO extends BaseDO {

    /**
     * ID
     */
    @TableId
    private Long id;
    /**
     * 初始化步骤名称
     */
    private String step;
    /**
     * 状态，如pending-待处理，completed-已完成
     */
    private String status;
    /**
     * 相关合约地址
     */
    private String contractAddress;
    /**
     * 交易哈希
     */
    private String transactionHash;
    /**
     * 区块号
     */
    private Long blockNumber;
    /**
     * 错误信息，若有
     */
    private String errorMessage;
    /**
     * 元数据，JSON格式
     */
    private String metadata;

}