package cc.bamboo.module.infra.controller.admin.file.vo.file;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 文件 Response VO,不返回 content 字段，太大")
@Data
public class FileUploadRespVO {

    @Schema(description = "文件编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "原文件名", requiredMode = Schema.RequiredMode.REQUIRED, example = "rwa.jpg")
    private String name;

    @Schema(description = "文件 URL", requiredMode = Schema.RequiredMode.REQUIRED, example = "https://www.iocoder.cn/rwa.jpg")
    private String url;

}
