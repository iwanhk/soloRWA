package cc.bamboo.module.project.controller.admin.projectinfo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.math.BigDecimal;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import com.alibaba.excel.annotation.*;
import cc.bamboo.framework.excel.core.annotations.DictFormat;
import cc.bamboo.framework.excel.core.convert.DictConvert;

@Schema(description = "管理后台 - 项目核心表（基础+状态） Response VO")
@Data
@ExcelIgnoreUnannotated
public class ProjectInfoRespVO {

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

    @Schema(description = "发行单价(元)", requiredMode = Schema.RequiredMode.REQUIRED, example = "5038")
    @ExcelProperty("发行单价(元)")
    private BigDecimal issueUnitPrice;

    @Schema(description = "剩余数量(份)【高频更新】", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("剩余数量(份)【高频更新】")
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

    @Schema(description = "认购期-开始", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("认购期-开始")
    private LocalDateTime subscriptionStartTime;

    @Schema(description = "认购期-结束", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("认购期-结束")
    private LocalDateTime subscriptionEndTime;

    @Schema(description = "锁定期-开始", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("锁定期-开始")
    private LocalDateTime lockStartTime;

    @Schema(description = "锁定期-结束", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("锁定期-结束")
    private LocalDateTime lockEndTime;

    /**
     * 分红期-开始
     */
    private LocalDateTime dividendsStartTime;
    /**
     * 分红期-结束
     */
    private LocalDateTime dividendsEndTime;

    @Schema(description = "状态：1-未开售 2-出售中 3-已售罄 4-盈利中", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @ExcelProperty(value = "状态：1-未开售 2-出售中 3-已售罄 4-盈利中", converter = DictConvert.class)
    @DictFormat("biz_project_status") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private Integer projectStatus;

    @Schema(description = "项目介绍（图文）")
    @ExcelProperty("项目介绍（图文）")
    private String projectIntro;

    @Schema(description = "项目资料URL")
    @ExcelProperty("项目资料URL")
    private String projectFileUrls;



    @Schema(description = "提前赎回手续费配置")
    @ExcelProperty("提前赎回手续费配置")
    private String earlyRedemptionFeeJson;

    @Schema(description = "项目图片URL", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("项目图片URL")
    private String projectImageUrls;

    @Schema(description = "项目视频URL", example = "https://www.iocoder.cn")
    @ExcelProperty("项目视频URL")
    private String projectVideoUrl;

    /**
     * 链合约状态
     */
    private Integer chainStatus;

    /**
     * 出售状态：0下架 1上架
     */
    private Integer sellStatus;

    private Integer auditStatus;

    private String redemptionRules;

    @Schema(description = "基金时长", example = "月")
    private Integer duration;

    @Schema(description = "预期年化收益货币", example = "USDT")
    private String earningCurrency;

    @Schema(description = "投资货币", example = "CNY")
    private String investmentCurrency;

    private String purchaseInstructions;

    private String dividendInstructions;

    private String auditRemark;

    private String language;

    private String projectJson;

    private Integer version;

    /**
     * 最新编辑的审核状态：0-待提交 1-待审核 2-审核通过 3-审核不通过
     */
    private Integer editStatus;

    /**
     * 最新快照版本号
     */
    private Integer editVersion;

    private Integer projectConfigType;

}