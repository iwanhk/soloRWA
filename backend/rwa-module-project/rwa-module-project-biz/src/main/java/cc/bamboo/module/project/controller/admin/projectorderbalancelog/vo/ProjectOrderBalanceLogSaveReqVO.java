package cc.bamboo.module.project.controller.admin.projectorderbalancelog.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import javax.validation.constraints.*;
import java.math.BigDecimal;

@Schema(description = "管理后台 - 用户项目余额记录新增/修改 Request VO")
@Data
public class ProjectOrderBalanceLogSaveReqVO {

    @Schema(description = "记录ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "22623")
    private Long id;

    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "16668")
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @Schema(description = "项目ID", example = "31372")
    private Long projectId;

    @Schema(description = "订单ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "20226")
    @NotNull(message = "订单ID不能为空")
    private Long orderId;

    @Schema(description = "操作金额")
    private BigDecimal amount;

    @Schema(description = "操作后金额", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "操作后金额不能为空")
    private BigDecimal afterAmount;

    @Schema(description = "类型", example = "2")
    private Integer type;

}