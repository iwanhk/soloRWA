package cc.bamboo.module.project.controller.admin.projectinfo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 提交审核")
@Data
public class SubmitAuditReqVO {

    @Schema(description = "项目ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "18333")
    private Long projectId;

}