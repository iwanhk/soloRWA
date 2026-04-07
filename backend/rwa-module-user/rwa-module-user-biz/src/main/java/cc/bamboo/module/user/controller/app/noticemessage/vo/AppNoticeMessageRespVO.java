package cc.bamboo.module.user.controller.app.noticemessage.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "用户 APP - 消息 Response VO")
@Data
public class AppNoticeMessageRespVO {

    @Schema(description = "消息ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "消息类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer noticeType;

    @Schema(description = "模板编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "AUDIT_PASS")
    private String templateCode;

    private String templateTitle;

    @Schema(description = "模板内容", requiredMode = Schema.RequiredMode.REQUIRED, example = "您的认证已通过")
    private String templateContent;

    @Schema(description = "订单ID", example = "1024")
    private Long orderId;

    @Schema(description = "消息地址", example = "/order/detail/1024")
    private String noticeUrl;

    @Schema(description = "是否已读", requiredMode = Schema.RequiredMode.REQUIRED, example = "false")
    private Boolean readStatus;

    @Schema(description = "阅读时间", example = "2024-01-01 12:00:00")
    private LocalDateTime readTime;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED, example = "2024-01-01 12:00:00")
    private LocalDateTime createTime;

}
