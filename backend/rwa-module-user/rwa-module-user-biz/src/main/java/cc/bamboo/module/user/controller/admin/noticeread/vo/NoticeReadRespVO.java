package cc.bamboo.module.user.controller.admin.noticeread.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import com.alibaba.excel.annotation.*;

@Schema(description = "管理后台 - 系统消息已读记录 Response VO")
@Data
@ExcelIgnoreUnannotated
public class NoticeReadRespVO {

    @Schema(description = "记录ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "29269")
    @ExcelProperty("记录ID")
    private Long id;

    @Schema(description = "关联消息主表的系统消息ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "18116")
    @ExcelProperty("关联消息主表的系统消息ID")
    private Long noticeId;

    @Schema(description = "已读用户ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "19225")
    @ExcelProperty("已读用户ID")
    private Long userId;

    @Schema(description = "阅读状态：0-未读 1-已读", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @ExcelProperty("阅读状态：0-未读 1-已读")
    private Integer readStatus;

    @Schema(description = "阅读时间")
    @ExcelProperty("阅读时间")
    private LocalDateTime readTime;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}