package cc.bamboo.module.project.controller.admin.projectoperation.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cc.bamboo.framework.common.pojo.PageParam;
import java.math.BigDecimal;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cc.bamboo.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 项目运营统计表分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ProjectOperationPageReqVO extends PageParam {

    @Schema(description = "参与投资人数", example = "25865")
    private Integer investorCount;

    @Schema(description = "申请分红人数", example = "27784")
    private Integer dividendApplyCount;

    @Schema(description = "申请分红金额(元)")
    private BigDecimal dividendApplyAmount;

    @Schema(description = "提前赎回人数")
    private Integer earlyRedemptionPeople;

    @Schema(description = "提前赎回金额(元)")
    private BigDecimal earlyRedemptionAmount;

    @Schema(description = "提前赎回份额", example = "12165")
    private Integer earlyRedemptionCount;

    @Schema(description = "到期赎回人数", example = "17994")
    private Integer maturityRedemptionCount;

    @Schema(description = "到期赎回金额(元)")
    private BigDecimal maturityRedemptionAmount;

    @Schema(description = "投资人总收益(元)")
    private BigDecimal totalInvestorIncome;

    @Schema(description = "投资人总收益率(%)")
    private BigDecimal totalInvestorYield;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}