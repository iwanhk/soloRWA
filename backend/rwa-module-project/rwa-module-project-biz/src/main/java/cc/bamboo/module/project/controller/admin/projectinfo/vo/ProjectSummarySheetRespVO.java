package cc.bamboo.module.project.controller.admin.projectinfo.vo;

import cc.bamboo.framework.excel.core.annotations.DictFormat;
import cc.bamboo.framework.excel.core.convert.DictConvert;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 项目汇总导出 Response VO")
@Data
@ExcelIgnoreUnannotated
public class ProjectSummarySheetRespVO {

    @Schema(description = "项目名称")
    @ExcelProperty("项目名称")
    private String projectName;

    @Schema(description = "状态")
    @ExcelProperty(value = "状态", converter = DictConvert.class)
    @DictFormat("biz_project_status")
    private Integer projectStatus;

    @Schema(description = "发行商")
    @ExcelProperty("发行商")
    private String publisherCompanyName;

    @Schema(description = "项目周期（月）")
    @ExcelProperty("项目周期")
    private Integer projectDuration;

    @Schema(description = "分红周期（天）")
    @ExcelProperty("分红周期")
    private Integer dividendPeriod;

    @Schema(description = "上线时间")
    @ExcelProperty("上线时间")
    @ColumnWidth(20)
    private LocalDateTime onlineTime;

    @Schema(description = "运行时间")
    @ExcelProperty("运行时间")
    private String runTime;

    @Schema(description = "发行数量")
    @ExcelProperty("发行数量")
    private Integer issueQuantity;

    @Schema(description = "单价")
    @ExcelProperty("单价")
    private String issueUnitPrice;

    @Schema(description = "最低认购金额")
    @ExcelProperty("最低认购金额")
    private String minimumSubscribeAmount;

    @Schema(description = "购买用户数")
    @ExcelProperty("购买用户数")
    private Long purchaseUserCount;

    @Schema(description = "总分红收益")
    @ExcelProperty("总分红收益")
    private String totalDividendIncome;

    @Schema(description = "已提取用户收益")
    @ExcelProperty("已提取用户收益")
    private String withdrawnUserIncome;
}
