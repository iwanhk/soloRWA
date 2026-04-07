package cc.bamboo.module.user.controller.admin.statistics.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户审核统计 Response VO
 *
 * @author Swolf
 */
@Schema(description = "管理后台 - 用户审核统计 Response VO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAuditStatisticsRespVO {

    @Schema(description = "用户审核数", requiredMode = Schema.RequiredMode.REQUIRED, example = "12")
    private Long userAuditCount;

    @Schema(description = "银行卡审核数(仅已通过用户审核的)", requiredMode = Schema.RequiredMode.REQUIRED, example = "8")
    private Long bankCardAuditCount;

}
