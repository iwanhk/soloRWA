package cc.bamboo.module.project.controller.admin.projectinfo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 项目核心表（基础+状态）新增/修改 Request VO")
@Data
public class ProjectInfoUpdateReqVO {


    @Schema(description = "项目ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "18333")
    private Long projectId;

    @Schema(description = "项目名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "赵六")
    @NotEmpty(message = "项目名称不能为空")
    private String projectName;

    @Schema(description = "项目类", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "项目类不能为空")
    private Integer projectType;

    @Schema(description = "资产类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "资产类型不能为空")
    private Integer assetType;

    @Schema(description = "发行数量(份)", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "发行数量(份)不能为空")
    private Integer issueQuantity;

    @Schema(description = "发行单价(元)", requiredMode = Schema.RequiredMode.REQUIRED, example = "5038")
    @NotNull(message = "发行单价(元)不能为空")
    private BigDecimal issueUnitPrice;

    @Schema(description = "剩余数量(份)【高频更新】", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "剩余数量(份)【高频更新】不能为空")
    private Integer remainingQuantity;

    @Schema(description = "预期年化收益", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "预期年化收益不能为空")
    private BigDecimal expectedAnnualReturn;

    @Schema(description = "起购量", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "起购量不能为空")
    private Integer minimumPurchase;

    @Schema(description = "发行链ID（关联chain_manage）", requiredMode = Schema.RequiredMode.REQUIRED, example = "20023")
    @NotNull(message = "发行链ID（关联chain_manage）不能为空")
    private Long issueChainId;

    //private Integer projectStatus;

    private Integer auditStatus;

    @Schema(description = "项目介绍（图文）")
    private String projectIntro;

    @Schema(description = "项目资料URL")
    private String projectFileUrls;

    @Schema(description = "提前赎回手续费配置")
    private String earlyRedemptionFeeJson;

    @Schema(description = "项目图片URL", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "项目图片URL不能为空")
    private String projectImageUrls;

    @Schema(description = "项目视频URL", example = "https://www.iocoder.cn")
    private String projectVideoUrl;

    @Schema(description = "视频封面URL", example = "https://www.iocoder.cn/cover.jpg")
    private String cover;

    private String redemptionRules;

    @Schema(description = "基金时长", example = "月")
    private Integer duration;

    @Schema(description = "预期年化收益货币", example = "USDT")
    private String earningCurrency;

    @Schema(description = "投资货币", example = "CNY")
    private String investmentCurrency;

    private String purchaseInstructions;

    private String dividendInstructions;

    private String language;

    private String projectJson;

    private Integer projectConfigType;

}