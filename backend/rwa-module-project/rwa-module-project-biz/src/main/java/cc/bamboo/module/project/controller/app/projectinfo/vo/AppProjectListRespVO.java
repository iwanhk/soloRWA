package cc.bamboo.module.project.controller.app.projectinfo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户 APP - 项目列表 Response VO
 *
 * @author Swolf
 */
@Schema(description = "用户 APP - 项目列表 Response VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppProjectListRespVO {

    @Schema(description = "项目ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long projectId;

    @Schema(description = "项目名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "优质资产项目A")
    private String projectName;

    @Schema(description = "项目类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer projectType;

    @Schema(description = "资产类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer assetType;

    @Schema(description = "发行商公司名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "XX资产管理公司")
    private String publisherCompanyName;

    @Schema(description = "发行数量(份)", requiredMode = Schema.RequiredMode.REQUIRED, example = "10000")
    private Integer issueQuantity;

    @Schema(description = "发行单价(元)", requiredMode = Schema.RequiredMode.REQUIRED, example = "1000.00")
    private BigDecimal issueUnitPrice;

    @Schema(description = "剩余数量(份)", requiredMode = Schema.RequiredMode.REQUIRED, example = "5000")
    private Integer remainingQuantity;

    @Schema(description = "预期年化收益", requiredMode = Schema.RequiredMode.REQUIRED, example = "0.08")
    private BigDecimal expectedAnnualReturn;

    @Schema(description = "起购量(份)", requiredMode = Schema.RequiredMode.REQUIRED, example = "10")
    private Integer minimumPurchase;

    @Schema(description = "项目状态：0-未运行 1-运行待审核 2-运行中 3-盈利中", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    private Integer projectStatus;

    @Schema(description = "项目图片URL（第一张）", example = "https://example.com/img1.jpg")
    private String projectImageUrls;

    @Schema(description = "基金时长（月），以30天为一月计算")
    private Integer duration;

    @Schema(description = "分红币种")
    private String earningCurrency;

    @Schema(description = "投资币种")
    private String investmentCurrency;

}
