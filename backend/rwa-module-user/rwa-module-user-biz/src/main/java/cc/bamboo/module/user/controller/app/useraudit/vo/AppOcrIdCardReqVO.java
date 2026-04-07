package cc.bamboo.module.user.controller.app.useraudit.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Schema(description = "用户 APP - OCR识别证件 Request VO")
@Data
public class AppOcrIdCardReqVO {

    @Schema(description = "证件图片文件", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "证件图片文件不能为空")
    private MultipartFile file;

    @Schema(description = "证件面类型: front-人像面, back-国徽面", requiredMode = Schema.RequiredMode.REQUIRED, example = "front")
    @NotEmpty(message = "证件面类型不能为空")
    private String side;

}
