package cc.bamboo.module.project.controller.admin.projectinfo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 项目上下架请求VO
 *
 * @author Swolf
 */
@Schema(description = "管理后台 - 项目上下架 Request VO")
@Data
public class ProjectInfoSellStatusReqVO {

    @Schema(description = "项目ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "项目ID不能为空")
    private Long projectId;

    @Schema(description = "出售状态：0-下架 1-上架", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "出售状态不能为空")
    private Integer sellStatus;

}
