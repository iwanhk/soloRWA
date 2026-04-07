package cc.bamboo.module.project.controller.admin.projectinfo.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cc.bamboo.framework.common.pojo.PageParam;
import java.math.BigDecimal;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cc.bamboo.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 项目核心表（基础+状态）分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ProjectInfoPageReqVO extends PageParam {

    @Schema(description = "项目名称", example = "赵六")
    private String projectName;

    @Schema(description = "项目类", example = "1")
    private Integer projectType;

    @Schema(description = "资产类型", example = "1")
    private Integer assetType;

    @Schema(description = "发行商", example = "24693")
    private Long publisherUserId;

    @Schema(description = "发行商", example = "芋艿")
    private String publisherCompanyName;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Schema(description = "发行数量(份)")
    private Integer issueQuantity;

    @Schema(description = "发行单价(元)", example = "5038")
    private BigDecimal issueUnitPrice;

    @Schema(description = "剩余数量(份)【高频更新】")
    private Integer remainingQuantity;

    @Schema(description = "预期年化收益")
    private BigDecimal expectedAnnualReturn;

    @Schema(description = "起购量")
    private Integer minimumPurchase;

    @Schema(description = "发行链ID（关联chain_manage）", example = "20023")
    private Long issueChainId;

    @Schema(description = "认购期-开始")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] subscriptionStartTime;

    @Schema(description = "认购期-结束")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] subscriptionEndTime;

    @Schema(description = "锁定期-开始")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] lockStartTime;

    @Schema(description = "锁定期-结束")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] lockEndTime;

    @Schema(description = "状态：1-未开售 2-出售中 3-已售罄 4-盈利中", example = "2")
    private Integer projectStatus;

    @Schema(description = "项目介绍（图文）")
    private String projectIntro;

    @Schema(description = "项目资料URL")
    private String projectFileUrls;

    @Schema(description = "认购合同ID")
    private String subscriptionContractIds;

    @Schema(description = "分红合同ID")
    private String dividendContractIds;

    @Schema(description = "到期赎回合同ID")
    private String maturityRedemptionContractIds;

    @Schema(description = "提前赎回合同ID")
    private String earlyRedemptionContractIds;

    @Schema(description = "提前赎回手续费配置")
    private String earlyRedemptionFeeJson;

    @Schema(description = "项目图片URL")
    private String projectImageUrls;

    @Schema(description = "项目视频URL", example = "https://www.iocoder.cn")
    private String projectVideoUrl;

    private Integer auditStatus;

}