package cc.bamboo.module.user.controller.app.noticemessage.vo;

import cc.bamboo.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "用户 APP - 消息分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class AppNoticeMessagePageReqVO extends PageParam {

    @Schema(description = "消息类型", example = "1")
    private Integer noticeType;

    @Schema(description = "是否已读", example = "false")
    private Boolean readStatus;

}
