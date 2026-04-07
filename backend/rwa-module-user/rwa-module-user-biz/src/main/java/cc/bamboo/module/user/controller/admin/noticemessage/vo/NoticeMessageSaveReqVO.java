package cc.bamboo.module.user.controller.admin.noticemessage.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import javax.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 用户消息新增/修改 Request VO")
@Data
public class NoticeMessageSaveReqVO {

    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "12139")
    private Long id;

    @Schema(description = "用户id", requiredMode = Schema.RequiredMode.REQUIRED, example = "15301")

    private Long userId;

    @Schema(description = "消息类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @NotNull(message = "消息类型不能为空")
    private Integer noticeType;

    @Schema(description = "模版编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1040")
    private Long templateId;

    private String templateTitle;

    @Schema(description = "模板编码", requiredMode = Schema.RequiredMode.REQUIRED)

    private String templateCode;

    @Schema(description = "模版发送人名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "赵六")

    private String templateNickname;

    @Schema(description = "模版内容", requiredMode = Schema.RequiredMode.REQUIRED)

    private String templateContent;

    @Schema(description = "模版类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")

    private Integer templateType;

    @Schema(description = "模版参数", requiredMode = Schema.RequiredMode.REQUIRED)

    private String templateParams;

    @Schema(description = "订单id", example = "3333")
    private Long orderId;

    @Schema(description = "消息地址", example = "https://www.iocoder.cn")
    private String noticeUrl;

    @Schema(description = "是否已读", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")

    private Boolean readStatus;

    @Schema(description = "阅读时间")
    private LocalDateTime readTime;

}