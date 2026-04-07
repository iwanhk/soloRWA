package cc.bamboo.module.project.controller.admin.projectrevenue.vo;

import lombok.*;

import java.time.LocalDate;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cc.bamboo.framework.common.pojo.PageParam;
import java.math.BigDecimal;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cc.bamboo.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 项目收益分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ProjectRevenuePageReqVO extends PageParam {

    @Schema(description = "收益日期")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDate[] revenueDate;

    @Schema(description = "总收益")
    private BigDecimal revenue;

    @Schema(description = "产品id", example = "22676")
    private Long projectId;

    @Schema(description = "产品名称", example = "李四")
    private String projectName;

    @Schema(description = "是否发放收益")
    private Integer isSend;

    @Schema(description = "是否同步收益")
    private Integer isSync;

    @Schema(description = "备注", example = "你猜")
    private String remark;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}