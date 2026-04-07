package cc.bamboo.module.user.controller.admin.noticemessage.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cc.bamboo.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cc.bamboo.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 用户消息分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class NoticeMessagePageReqVO extends PageParam {

    @Schema(description = "用户id", example = "15301")
    private Long userId;

    @Schema(description = "消息类型", example = "2")
    private Integer noticeType;

    @Schema(description = "模版编号", example = "1040")
    private Long templateId;

    @Schema(description = "模板编码")
    private String templateCode;

    @Schema(description = "模版发送人名称", example = "赵六")
    private String templateNickname;

    @Schema(description = "模版内容")
    private String templateContent;

    @Schema(description = "模版类型", example = "2")
    private Integer templateType;

    @Schema(description = "模版参数")
    private String templateParams;

    @Schema(description = "订单id", example = "3333")
    private Long orderId;

    @Schema(description = "消息地址", example = "https://www.iocoder.cn")
    private String noticeUrl;

    @Schema(description = "是否已读", example = "1")
    private Boolean readStatus;

    @Schema(description = "阅读时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] readTime;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}