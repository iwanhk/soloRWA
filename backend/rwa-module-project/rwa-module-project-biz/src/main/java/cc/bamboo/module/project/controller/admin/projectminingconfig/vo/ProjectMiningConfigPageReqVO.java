package cc.bamboo.module.project.controller.admin.projectminingconfig.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cc.bamboo.framework.common.pojo.PageParam;
import java.math.BigDecimal;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cc.bamboo.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 挖矿项目配置分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ProjectMiningConfigPageReqVO extends PageParam {

    @Schema(description = "币种")
    private String coin;

    @Schema(description = "矿池AccessKey(加密)")
    private String poolAccessKey;

    @Schema(description = "矿池私钥(加密)")
    private String poolPrivateKey;

    @Schema(description = "矿池子账号", example = "赵六")
    private String poolName;

    @Schema(description = "总算力(T)")
    private BigDecimal computingPower;

    @Schema(description = "单T功耗(W)")
    private BigDecimal powerConsumption;

    @Schema(description = "电价(元/度)", example = "102")
    private BigDecimal electricityPrice;

    @Schema(description = "月运维成本")
    private BigDecimal operationCost;

    @Schema(description = "运维成本类型:0-固定值/月 1-按天", example = "1")
    private Integer operationCostType;

    @Schema(description = "团队分成比例(%)")
    private BigDecimal teamShareRatio;

    @Schema(description = "最低收益阈值(日)")
    private BigDecimal thresholdMin;

    @Schema(description = "最高收益阈值(日)")
    private BigDecimal thresholdMax;

    @Schema(description = "阈值预警:0-关 1-开")
    private Integer alertEnabled;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}