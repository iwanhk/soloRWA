package cc.bamboo.module.user.controller.admin.noticetemplate.vo;

import cc.bamboo.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cc.bamboo.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 消息模板分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class NoticeTemplatePageReqVO extends PageParam {

    @Schema(description = "模板名称", example = "审核通过通知")
    private String name;

    @Schema(description = "模板编码", example = "AUDIT_PASS")
    private String code;

    @Schema(description = "类型", example = "1")
    private Integer type;

    @Schema(description = "状态", example = "1")
    private Integer status;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}
