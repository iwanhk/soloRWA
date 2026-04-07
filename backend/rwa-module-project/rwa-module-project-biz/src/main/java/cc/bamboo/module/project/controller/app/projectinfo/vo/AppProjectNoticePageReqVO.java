package cc.bamboo.module.project.controller.app.projectinfo.vo;

import cc.bamboo.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

import static cc.bamboo.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 项目通告表（含全局通告）分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class AppProjectNoticePageReqVO extends PageParam {

    @Schema(description = "项目ID", example = "27973")
    @NotNull(message = "项目ID不能为空")
    private Long projectId;

}