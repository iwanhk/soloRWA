package cc.bamboo.module.project.controller.admin.projectrevenue.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;
import java.util.*;
import javax.validation.constraints.*;
import java.math.BigDecimal;

@Schema(description = "管理后台 - 项目收益新增/修改 Request VO")
@Data
public class ProjectRevenueSaveReqVO {

    private Long id;

    @Schema(description = "收益日期")
    private LocalDate revenueDate;

    @Schema(description = "币种收益")
    private BigDecimal coinRevenue;

    @Schema(description = "汇率")
    private BigDecimal exchangeRate;

    @Schema(description = "算力")
    private BigDecimal computingPower;

    private Long projectId;

    @Schema(description = "电力成本")
    private BigDecimal electricityCost;

    @Schema(description = "人力成本")
    private BigDecimal peopleCost;

    @Schema(description = "项目收益")
    private BigDecimal projectRevenue;

    @Schema(description = "可发放收益")
    private BigDecimal availableRevenue;

/*    @Schema(description = "是否发放收益")
    private Integer isSend;

    @Schema(description = "是否同步收益")
    private Integer isSync;*/

    @Schema(description = "备注", example = "你说的对")
    private String remark;


}