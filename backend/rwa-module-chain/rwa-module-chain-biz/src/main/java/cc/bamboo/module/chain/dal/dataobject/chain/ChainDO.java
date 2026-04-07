package cc.bamboo.module.chain.dal.dataobject.chain;

import lombok.*;
import com.baomidou.mybatisplus.annotation.*;
import cc.bamboo.framework.mybatis.core.dataobject.BaseDO;

/**
 * 区块链信息 DO
 *
 * @author Trae
 */
@TableName("biz_chain")
@KeySequence("biz_chain_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChainDO extends BaseDO {

    /**
     * 主键ID
     */
    @TableId
    private Long id;
    /**
     * 链名称
     */
    private String name;
    /**
     * 链ID
     */
    private Integer chainId;
    /**
     * RPC URL
     */
    private String rpcUrl;
    /**
     * 浏览器 URL
     */
    private String browserUrl;
    /**
     * 状态：0-开启，1-关闭
     */
    private Integer status;
    /**
     * 排序
     */
    private Integer sort;

}
