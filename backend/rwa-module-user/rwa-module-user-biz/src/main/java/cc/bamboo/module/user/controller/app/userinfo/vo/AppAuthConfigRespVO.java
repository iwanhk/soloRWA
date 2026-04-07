package cc.bamboo.module.user.controller.app.userinfo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Schema(description = "用户 APP - 系统配置信息 Response VO")
@Data
@Builder
public class AppAuthConfigRespVO {

    @Schema(description = "单个文件大小限制 (MB)", requiredMode = Schema.RequiredMode.REQUIRED, example = "5")
    private Long maxFileSize;

    @Schema(description = "短信发送频率 (分钟)", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer smsSendFrequency;

    @Schema(description = "每日短信发送最大数量", requiredMode = Schema.RequiredMode.REQUIRED, example = "20")
    private Integer smsSendMaximumQuantityPerDay;

}
