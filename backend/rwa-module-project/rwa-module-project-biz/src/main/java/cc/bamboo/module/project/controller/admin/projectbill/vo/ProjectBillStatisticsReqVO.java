package cc.bamboo.module.project.controller.admin.projectbill.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cc.bamboo.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

/**
 * 账单统计请求 VO
 *
 * @author Swolf
 */
@Schema(description = "管理后台 - 账单统计 Request VO")
@Data
public class ProjectBillStatisticsReqVO {

    @Schema(description = "时间范围")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] timeRange;

}
