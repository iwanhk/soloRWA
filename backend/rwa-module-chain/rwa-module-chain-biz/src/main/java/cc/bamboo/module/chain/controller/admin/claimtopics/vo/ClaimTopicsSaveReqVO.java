package cc.bamboo.module.chain.controller.admin.claimtopics.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import javax.validation.constraints.*;

@Schema(description = "管理后台 - 声明主题新增/修改 Request VO")
@Data
public class ClaimTopicsSaveReqVO {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "6890")
    private Long id;

    @Schema(description = "主题名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "李四")
    @NotEmpty(message = "主题名称不能为空")
    private String name;

    @Schema(description = "主题值", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "主题值不能为空")
    private String value;

    @Schema(description = "主题哈希", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "主题哈希不能为空")
    private String topic;

}