package cc.bamboo.module.project.controller.admin.projectoperation.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.math.BigDecimal;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import com.alibaba.excel.annotation.*;

@Schema(description = "管理后台 - 项目运营统计表 Response VO")
@Data
@ExcelIgnoreUnannotated
public class ProjectOperationRespVO {

    @Schema(description = "关联project_core.id", requiredMode = Schema.RequiredMode.REQUIRED, example = "31725")
    @ExcelProperty("关联project_core.id")
    private Long projectId;

    @Schema(description = "参与投资人数", requiredMode = Schema.RequiredMode.REQUIRED, example = "25865")
    @ExcelProperty("参与投资人数")
    private Integer investorCount;

    @Schema(description = "申请分红人数", requiredMode = Schema.RequiredMode.REQUIRED, example = "27784")
    @ExcelProperty("申请分红人数")
    private Integer dividendApplyCount;

    @Schema(description = "申请分红金额", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("申请分红金额")
    private BigDecimal dividendApplyAmount;

    @Schema(description = "提前赎回人数", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("提前赎回人数")
    private Integer earlyRedemptionPeople;

    @Schema(description = "提前赎回金额", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("提前赎回金额")
    private BigDecimal earlyRedemptionAmount;

    @Schema(description = "提前赎回份额", example = "12165")
    @ExcelProperty("提前赎回份额")
    private Integer earlyRedemptionCount;

    @Schema(description = "到期赎回人数", requiredMode = Schema.RequiredMode.REQUIRED, example = "17994")
    @ExcelProperty("到期赎回人数")
    private Integer maturityRedemptionCount;

    @Schema(description = "到期赎回金额", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("到期赎回金额")
    private BigDecimal maturityRedemptionAmount;

    @Schema(description = "投资人总收益", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("投资人总收益")
    private BigDecimal totalInvestorIncome;

    @Schema(description = "投资人总收益率(%)", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("投资人总收益率(%)")
    private BigDecimal totalInvestorYield;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    /**
     * 收益币种
     */
    private String earningCurrency;
    /**
     * 投资币种
     */
    private String investmentCurrency;
    private BigDecimal projectIncome;
}