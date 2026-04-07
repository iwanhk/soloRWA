package cc.bamboo.module.project.dal.dataobject.systemcoin;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cc.bamboo.framework.mybatis.core.dataobject.BaseDO;

/**
 * 币种管理 DO
 *
 * @author swolf
 */
@TableName("biz_system_coin")
@KeySequence("biz_system_coin_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemCoinDO extends BaseDO {

    /**
     * ID
     */
    @TableId
    private Long id;
    /**
     * 币种标识
     */
    private String coinCode;
    /**
     * 币种名称
     */
    private String coinName;
    /**
     * 英文名称
     */
    private String coinNameEn;
    /**
     * 币种类型
     *
     * 枚举 {@link TODO biz_coin_type 对应的类}
     */
    private Integer coinType;
    /**
     * 显示符号
     */
    private String symbol;
    /**
     * 币安交易对符号(如BTCUSDT)
     */
    private String binanceSymbol;
    /**
     * 矿池币种符号(如btc)
     */
    private String poolSymbol;
    /**
     * 币种图标
     */
    private String iconUrl;
    /**
     * 小数精度
     */
    private Integer decimals;
    /**
     * 排序(越小越前)
     */
    private Integer sort;
    /**
     * 状态:0-禁用 1-启用
     */
    private Integer status;
    /**
     * 备注
     */
    private String remark;

}