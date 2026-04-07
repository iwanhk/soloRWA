package cc.bamboo.module.project.controller.app.orderbalance.vo;

import cc.bamboo.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static cc.bamboo.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = " 用户项目余额表分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class AppProjectOrderBalancePageReqVO extends PageParam {

    @Schema(description = "用户ID", example = "21626")
    private Long userId;

    @Schema(description = "项目ID", example = "335")
    private Long projectId;



}