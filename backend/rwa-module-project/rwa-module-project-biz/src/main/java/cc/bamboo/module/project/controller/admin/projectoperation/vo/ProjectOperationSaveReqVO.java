package cc.bamboo.module.project.controller.admin.projectoperation.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import javax.validation.constraints.*;
import java.math.BigDecimal;

@Schema(description = "管理后台 - 项目运营统计表新增/修改 Request VO")
@Data
public class ProjectOperationSaveReqVO {

    @Schema(description = "关联project_core.id", requiredMode = Schema.RequiredMode.REQUIRED, example = "31725")
    private Long projectId;

    @Schema(description = "参与投资人数", requiredMode = Schema.RequiredMode.REQUIRED, example = "25865")
    @NotNull(message = "参与投资人数不能为空")
    private Integer investorCount;

    @Schema(description = "申请分红人数", requiredMode = Schema.RequiredMode.REQUIRED, example = "27784")
    @NotNull(message = "申请分红人数不能为空")
    private Integer dividendApplyCount;

    @Schema(description = "申请分红金额(元)", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "申请分红金额(元)不能为空")
    private BigDecimal dividendApplyAmount;

    @Schema(description = "提前赎回人数", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "提前赎回人数不能为空")
    private Integer earlyRedemptionPeople;

    @Schema(description = "提前赎回金额(元)", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "提前赎回金额(元)不能为空")
    private BigDecimal earlyRedemptionAmount;

    @Schema(description = "提前赎回份额", example = "12165")
    private Integer earlyRedemptionCount;

    @Schema(description = "到期赎回人数", requiredMode = Schema.RequiredMode.REQUIRED, example = "17994")
    @NotNull(message = "到期赎回人数不能为空")
    private Integer maturityRedemptionCount;

    @Schema(description = "到期赎回金额(元)", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "到期赎回金额(元)不能为空")
    private BigDecimal maturityRedemptionAmount;

    @Schema(description = "投资人总收益(元)", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "投资人总收益(元)不能为空")
    private BigDecimal totalInvestorIncome;

    @Schema(description = "投资人总收益率(%)", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "投资人总收益率(%)不能为空")
    private BigDecimal totalInvestorYield;

}