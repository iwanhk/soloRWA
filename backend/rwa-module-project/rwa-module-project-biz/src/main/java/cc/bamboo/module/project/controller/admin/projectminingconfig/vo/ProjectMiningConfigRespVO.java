package cc.bamboo.module.project.controller.admin.projectminingconfig.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.math.BigDecimal;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import com.alibaba.excel.annotation.*;

@Schema(description = "管理后台 - 挖矿项目配置 Response VO")
@Data
@ExcelIgnoreUnannotated
public class ProjectMiningConfigRespVO {

    @Schema(description = "项目ID(主键)", requiredMode = Schema.RequiredMode.REQUIRED, example = "30510")
    @ExcelProperty("项目ID(主键)")
    private Long projectId;

    @Schema(description = "币种", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("币种")
    private String coin;

    @Schema(description = "矿池AccessKey(加密)")
    @ExcelProperty("矿池AccessKey(加密)")
    private String poolAccessKey;

    @Schema(description = "矿池私钥(加密)")
    @ExcelProperty("矿池私钥(加密)")
    private String poolPrivateKey;

    @Schema(description = "矿池子账号", example = "赵六")
    @ExcelProperty("矿池子账号")
    private String poolName;

    @Schema(description = "总算力(T)")
    @ExcelProperty("总算力(T)")
    private BigDecimal computingPower;

    @Schema(description = "单T功耗(W)")
    @ExcelProperty("单T功耗(W)")
    private BigDecimal powerConsumption;

    @Schema(description = "电价(元/度)", example = "102")
    @ExcelProperty("电价(元/度)")
    private BigDecimal electricityPrice;

    @Schema(description = "月运维成本")
    @ExcelProperty("月运维成本")
    private BigDecimal operationCost;

    @Schema(description = "运维成本类型:0-固定值/月 1-按天", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty("运维成本类型:0-固定值/月 1-按天")
    private Integer operationCostType;

    @Schema(description = "团队分成比例(%)", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("团队分成比例(%)")
    private BigDecimal teamShareRatio;

    @Schema(description = "最低收益阈值(日)")
    @ExcelProperty("最低收益阈值(日)")
    private BigDecimal thresholdMin;

    @Schema(description = "最高收益阈值(日)")
    @ExcelProperty("最高收益阈值(日)")
    private BigDecimal thresholdMax;

    @Schema(description = "阈值预警:0-关 1-开", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("阈值预警:0-关 1-开")
    private Integer alertEnabled;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}