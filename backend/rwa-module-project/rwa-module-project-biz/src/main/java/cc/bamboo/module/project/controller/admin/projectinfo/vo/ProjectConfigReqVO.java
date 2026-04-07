package cc.bamboo.module.project.controller.admin.projectinfo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 项目配置请求 VO
 */
@Schema(description = "管理后台 - 项目配置 Request VO")
@Data
public class ProjectConfigReqVO {

    @Schema(description = "项目ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "项目ID不能为空")
    private Long projectId;

    @Schema(description = "单T功耗(W)", example = "3000")
    private BigDecimal powerConsumption;

    @Schema(description = "电费(元/度)", example = "0.35")
    private BigDecimal electricityPrice;

    @Schema(description = "月运维成本", example = "5000")
    private BigDecimal operationCost;

    @Schema(description = "运维成本类型:0-固定值/月 1-按天", example = "0")
    private Integer operationCostType;

    @Schema(description = "团队分成比例(%)", example = "20")
    private BigDecimal teamShareRatio;

    @Schema(description = "最低收益阈值(日)", example = "0.001")
    private BigDecimal thresholdMin;

    @Schema(description = "最高收益阈值(日)", example = "1.0")
    private BigDecimal thresholdMax;

    @Schema(description = "阈值预警:0-关 1-开", example = "1")
    private Integer alertEnabled;

    /**
     * 结算token
     */
    private String poolAccessKey;

    private String poolPrivateKey;
    /**
     * 结算账户
     */
    private String poolName;
}
