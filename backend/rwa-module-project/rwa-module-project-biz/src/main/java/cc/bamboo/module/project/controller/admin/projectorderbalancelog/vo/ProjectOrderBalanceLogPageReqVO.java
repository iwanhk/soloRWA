package cc.bamboo.module.project.controller.admin.projectorderbalancelog.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cc.bamboo.framework.common.pojo.PageParam;
import java.math.BigDecimal;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cc.bamboo.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 用户项目余额记录分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ProjectOrderBalanceLogPageReqVO extends PageParam {

    @Schema(description = "用户ID", example = "16668")
    private Long userId;

    @Schema(description = "项目ID", example = "31372")
    private Long projectId;

    @Schema(description = "订单ID", example = "20226")
    private Long orderId;

    @Schema(description = "操作金额")
    private BigDecimal amount;

    @Schema(description = "操作后金额")
    private BigDecimal afterAmount;

    @Schema(description = "类型", example = "2")
    private Integer type;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}