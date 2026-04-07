package cc.bamboo.module.project.controller.admin.projectfundconfig.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cc.bamboo.framework.common.pojo.PageParam;
import java.math.BigDecimal;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cc.bamboo.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 基金项目配置分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ProjectFundConfigPageReqVO extends PageParam {

    @Schema(description = "月运维成本")
    private BigDecimal operationCost;

    @Schema(description = "运维成本类型:0-固定值/月 1-按天", example = "2")
    private Integer operationCostType;

    @Schema(description = "团队分成比例(%)")
    private BigDecimal teamShareRatio;

    @Schema(description = "最低收益阈值(日)")
    private BigDecimal thresholdMin;

    @Schema(description = "最高收益阈值(日)")
    private BigDecimal thresholdMax;

    @Schema(description = "阈值预警:0-关 1-开")
    private Integer alertEnabled;

    @Schema(description = "基金公司配置")
    private String fundJson;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}