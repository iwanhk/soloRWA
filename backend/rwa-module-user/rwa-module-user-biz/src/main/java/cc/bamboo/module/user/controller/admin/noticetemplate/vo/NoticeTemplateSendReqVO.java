package cc.bamboo.module.user.controller.admin.noticetemplate.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Map;

@Schema(description = "管理后台 - 发送消息 Request VO")
@Data
public class NoticeTemplateSendReqVO {

    @Schema(description = "用户ID", example = "1024")
    private Long userId;

    @Schema(description = "模板编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "AUDIT_PASS")
    @NotEmpty(message = "模板编码不能为空")
    private String templateCode;

    @Schema(description = "模板参数", example = "{\"userName\":\"张三\",\"auditTime\":\"2024-01-01\"}")
    private Map<String, Object> templateParams;

    @Schema(description = "消息类型: 1-系统消息 2-个人消息", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @NotNull(message = "消息类型不能为空")
    private Integer messageType;

}
