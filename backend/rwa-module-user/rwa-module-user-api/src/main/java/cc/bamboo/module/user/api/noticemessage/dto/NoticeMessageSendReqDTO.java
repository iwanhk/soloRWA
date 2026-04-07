package cc.bamboo.module.user.api.noticemessage.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import java.util.Map;

/**
 * RPC 服务 - 消息发送 Request DTO
 *
 * @author Kiro
 */
@Schema(description = "RPC 服务 - 消息发送 Request DTO")
@Data
@Accessors(chain = true)
public class NoticeMessageSendReqDTO {

    @Schema(description = "用户ID", example = "1")
    private Long userId;

    @Schema(description = "订单ID", example = "1001")
    private Long orderId;

    @Schema(description = "模板编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "order_success")
    @NotBlank(message = "模板编码不能为空")
    private String templateCode;

    @Schema(description = "模板参数")
    private Map<String, Object> templateParams;

}
