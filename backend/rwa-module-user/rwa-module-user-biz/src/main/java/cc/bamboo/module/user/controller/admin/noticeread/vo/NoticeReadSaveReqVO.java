package cc.bamboo.module.user.controller.admin.noticeread.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import javax.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 系统消息已读记录新增/修改 Request VO")
@Data
public class NoticeReadSaveReqVO {

    @Schema(description = "记录ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "29269")
    private Long id;

    @Schema(description = "关联消息主表的系统消息ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "18116")
    @NotNull(message = "关联消息主表的系统消息ID不能为空")
    private Long noticeId;

    @Schema(description = "已读用户ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "19225")
    @NotNull(message = "已读用户ID不能为空")
    private Long userId;

    @Schema(description = "阅读状态：0-未读 1-已读", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @NotNull(message = "阅读状态：0-未读 1-已读不能为空")
    private Integer readStatus;

    @Schema(description = "阅读时间")
    private LocalDateTime readTime;

}