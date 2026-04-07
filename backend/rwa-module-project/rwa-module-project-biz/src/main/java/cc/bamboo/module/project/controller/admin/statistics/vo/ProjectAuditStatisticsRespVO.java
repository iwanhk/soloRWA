package cc.bamboo.module.project.controller.admin.statistics.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 项目审核统计 Response VO
 *
 * @author Swolf
 */
@Schema(description = "管理后台 - 项目审核统计 Response VO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectAuditStatisticsRespVO {

    @Schema(description = "项目上线审核数", requiredMode = Schema.RequiredMode.REQUIRED, example = "5")
    private Long projectOnlineAuditCount;

    @Schema(description = "项目运行审核数", requiredMode = Schema.RequiredMode.REQUIRED, example = "3")
    private Long projectRunningAuditCount;

}
