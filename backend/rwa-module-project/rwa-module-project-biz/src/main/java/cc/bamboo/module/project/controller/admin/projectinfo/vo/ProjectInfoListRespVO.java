package cc.bamboo.module.project.controller.admin.projectinfo.vo;

import cc.bamboo.framework.excel.core.annotations.DictFormat;
import cc.bamboo.framework.excel.core.convert.DictConvert;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 项目核心表（基础+状态） Response VO")
@Data
@ExcelIgnoreUnannotated
public class ProjectInfoListRespVO {

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

    @Schema(description = "状态：1-未开售 2-出售中 3-已售罄 4-盈利中", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @ExcelProperty(value = "状态：1-未开售 2-出售中 3-已售罄 4-盈利中", converter = DictConvert.class)
    @DictFormat("biz_project_status") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private Integer projectStatus;

    /**
     * 链合约状态
     */
    private Integer chainStatus;

    /**
     * 出售状态：0下架 1上架
     */
    private Integer sellStatus;

    private Integer auditStatus;

    @Schema(description = "基金时长", example = "月")
    private Integer duration;

    @Schema(description = "预期年化收益货币", example = "USDT")
    private String earningCurrency;

    @Schema(description = "投资货币", example = "CNY")
    private String investmentCurrency;

    private String auditRemark;

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