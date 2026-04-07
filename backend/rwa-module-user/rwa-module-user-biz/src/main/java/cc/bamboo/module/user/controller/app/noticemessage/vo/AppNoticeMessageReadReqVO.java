package cc.bamboo.module.user.controller.app.noticemessage.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Schema(description = "用户 APP - 消息已读 Request VO")
@Data
public class AppNoticeMessageReadReqVO {

    @Schema(description = "消息ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "消息ID不能为空")
    private Long id;

}
