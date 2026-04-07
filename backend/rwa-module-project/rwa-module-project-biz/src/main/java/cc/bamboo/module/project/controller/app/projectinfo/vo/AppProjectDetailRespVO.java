package cc.bamboo.module.project.controller.app.projectinfo.vo;

import cc.bamboo.framework.excel.core.annotations.DictFormat;
import cc.bamboo.framework.excel.core.convert.DictConvert;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户 APP - 项目详情 Response VO
 *
 * @author Swolf
 */
@Schema(description = "用户 APP - 项目详情 Response VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppProjectDetailRespVO {

    @Schema(description = "项目ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "18333")
    @ExcelProperty("项目ID")
    private Long projectId;

    @Schema(description = "项目名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "赵六")
    @ExcelProperty("项目名称")
    private String projectName;

    @Schema(description = "项目类", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty(value = "项目类", converter = DictConvert.class)
    @DictFormat("biz_project_type") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private Integer projectType;

    @Schema(description = "资产类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty(value = "资产类型", converter = DictConvert.class)
    @DictFormat("biz_asset_type") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private Integer assetType;

    @Schema(description = "发行商", requiredMode = Schema.RequiredMode.REQUIRED, example = "24693")
    @ExcelProperty("发行商")
    private Long publisherUserId;

    @Schema(description = "发行商", requiredMode = Schema.RequiredMode.REQUIRED, example = "芋艿")
    @ExcelProperty("发行商")
    private String publisherCompanyName;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @Schema(description = "发行数量(份)", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("发行数量(份)")
    private Integer issueQuantity;

    @Schema(description = "发行单价(U)", requiredMode = Schema.RequiredMode.REQUIRED, example = "5038")
    @ExcelProperty("发行单价(U)")
    private BigDecimal issueUnitPrice;

    @Schema(description = "剩余数量", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("剩余数量")
    private Integer remainingQuantity;

    @Schema(description = "预期年化收益", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("预期年化收益")
    private BigDecimal expectedAnnualReturn;

    @Schema(description = "起购量", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("起购量")
    private Integer minimumPurchase;

    @Schema(description = "发行链ID（关联chain_manage）", requiredMode = Schema.RequiredMode.REQUIRED, example = "20023")
    @ExcelProperty("发行链ID（关联chain_manage）")
    private Long issueChainId;

    @Schema(description = "锁定期-开始", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("锁定期-开始")
    private LocalDate lockStartTime;

    @Schema(description = "锁定期-结束", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("锁定期-结束")
    private LocalDate lockEndTime;

    @Schema(description = "状态：0-未运行 1-待运行审核 2-运行中 3-运行审核不通过 4 已结束", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    private Integer projectStatus;

    @Schema(description = "项目介绍（图文）")
    @ExcelProperty("项目介绍（图文）")
    private String projectIntro;


    @Schema(description = "提前赎回手续费配置")
    @ExcelProperty("提前赎回手续费配置")
    private String earlyRedemptionFeeJson;

    @Schema(description = "提前赎回手续费")
    private BigDecimal earlyRedemptionFee;

    @Schema(description = "项目图片URL", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("项目图片URL")
    private String projectImageUrls;

    @Schema(description = "项目视频URL", example = "https://www.iocoder.cn")
    @ExcelProperty("项目视频URL")
    private String projectVideoUrl;

    @Schema(description = "最早分红日期")
    private LocalDate firstDividendDate;

    private String redemptionRules;

    /**
     * 基金时长（月），以30天为一月计算
     */
    @Schema(description = "基金时长（月），以30天为一月计算")
    private Integer duration;

    @Schema(description = "分红币种")
    private String earningCurrency;

    @Schema(description = "投资币种")
    private String investmentCurrency;

    private String cover;

    private String purchaseInstructions;

    private String dividendInstructions;

}
