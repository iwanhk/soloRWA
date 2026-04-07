package cc.bamboo.module.system.controller.admin.commonconfig.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import javax.validation.constraints.*;

@Schema(description = "管理后台 - 参数配置新增/修改 Request VO")
@Data
public class CommonConfigSaveReqVO {

    @Schema(description = "参数主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "21437")
    private Long id;

    @Schema(description = "参数名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "芋艿")
    @NotEmpty(message = "参数名称不能为空")
    private String name;

    @Schema(description = "参数键名", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "参数键名不能为空")
    private String configKey;

    @Schema(description = "参数键值", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "参数键值不能为空")
    private String value;

    @Schema(description = "前端可用", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "前端可用不能为空")
    private Integer isApp;

    @Schema(description = "备注", example = "你猜")
    private String remark;

}