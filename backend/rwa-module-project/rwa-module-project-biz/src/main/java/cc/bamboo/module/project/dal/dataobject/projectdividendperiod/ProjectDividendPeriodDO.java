package cc.bamboo.module.project.dal.dataobject.projectdividendperiod;

import lombok.*;

import java.time.LocalDate;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cc.bamboo.framework.mybatis.core.dataobject.BaseDO;

/**
 * 分红周期 DO
 *
 * @author Swolf
 */
@TableName("biz_project_dividend_period")
@KeySequence("biz_project_dividend_period_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDividendPeriodDO extends BaseDO {

    /**
     * ID
     */
    @TableId
    private Long id;
    /**
     * 项目id
     */
    private Long projectId;
    /**
     * 分红期数
     */
    private Integer periodSeq;
    /**
     * 解锁日期
     */
    private LocalDate unlockDate;

    private Integer periodDuration;

}