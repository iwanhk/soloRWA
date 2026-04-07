package cc.bamboo.module.chain.dal.dataobject.contractdeployments;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cc.bamboo.framework.mybatis.core.dataobject.BaseDO;

/**
 * 合约部署 DO
 *
 * @author Swolf
 */
@TableName("biz_contract_deployments")
@KeySequence("biz_contract_deployments_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContractDeploymentsDO extends BaseDO {

    /**
     * ID
     */
    @TableId
    private Long id;
    /**
     * 合约类型
     */
    private String contractType;
    /**
     * 部署地址
     */
    private String deploymentAddress;
    /**
     * 部署者地址
     */
    private String deployerAddress;
    /**
     * 部署信息，JSON格式
     */
    private String deploymentInfo;
    /**
     * 交易哈希
     */
    private String transactionHash;
    /**
     * 区块号
     */
    private Long blockNumber;
    /**
     * 状态，如deployed-已部署
     */
    private String status;

}