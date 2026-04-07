package cc.bamboo.module.project.controller.admin.projectfundconfig.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import javax.validation.constraints.*;
import java.math.BigDecimal;

@Schema(description = "管理后台 - 基金项目配置新增/修改 Request VO")
@Data
public class ProjectFundConfigSaveReqVO {

    @Schema(description = "项目ID(主键)", requiredMode = Schema.RequiredMode.REQUIRED, example = "21850")
    private Long projectId;

    @Schema(description = "月运维成本")
    private BigDecimal operationCost;

    @Schema(description = "运维成本类型:0-固定值/月 1-按天", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @NotNull(message = "运维成本类型:0-固定值/月 1-按天不能为空")
    private Integer operationCostType;

    @Schema(description = "团队分成比例(%)", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "团队分成比例(%)不能为空")
    private BigDecimal teamShareRatio;

    @Schema(description = "最低收益阈值(日)")
    private BigDecimal thresholdMin;

    @Schema(description = "最高收益阈值(日)")
    private BigDecimal thresholdMax;

    @Schema(description = "阈值预警:0-关 1-开", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "阈值预警:0-关 1-开不能为空")
    private Integer alertEnabled;

    @Schema(description = "基金公司配置")
    private String fundJson;

}