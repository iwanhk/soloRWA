package cc.bamboo.module.user.controller.admin.agreement.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import javax.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 系统协议表新增/修改 Request VO")
@Data
public class AgreementSaveReqVO {

    @Schema(description = "协议ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "15539")
    private Long id;

    @Schema(description = "协议类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    private Integer agreementType;

    private String agreementKey;

    @Schema(description = "协议标题", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "协议标题不能为空")
    private String agreementTitle;

    @Schema(description = "协议内容", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "协议内容不能为空")
    private String agreementContent;

    @Schema(description = "协议版本号", requiredMode = Schema.RequiredMode.REQUIRED)
    private String version;

    @Schema(description = "是否当前生效版本：1-是 0-否（同一类型仅1个生效版本）", requiredMode = Schema.RequiredMode.REQUIRED)
    private Boolean isCurrent;

    @Schema(description = "生效时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime effectiveTime;

    @Schema(description = "过期时间")
    private LocalDateTime expireTime;

}