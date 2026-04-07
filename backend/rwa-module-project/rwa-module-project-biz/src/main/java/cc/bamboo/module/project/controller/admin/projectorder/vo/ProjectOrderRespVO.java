package cc.bamboo.module.project.controller.admin.projectorder.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.math.BigDecimal;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import com.alibaba.excel.annotation.*;

@Schema(description = "管理后台 - 项目认购订单 Response VO")
@Data
@ExcelIgnoreUnannotated
public class ProjectOrderRespVO {

    @Schema(description = "订单ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "15943")
    @ExcelProperty("订单ID")
    private Long id;

    @Schema(description = "订单号（唯一，如：ORD202512160001）", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("订单号（唯一，如：ORD202512160001）")
    private String orderNo;

    @Schema(description = "申请日期", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("申请日期")
    private LocalDateTime applyDate;

    @Schema(description = "用户ID（关联用户表）", requiredMode = Schema.RequiredMode.REQUIRED, example = "15701")
    @ExcelProperty("用户ID（关联用户表）")
    private Long userId;

    private String userName;

    @Schema(description = "订单状态：0-待支付 1-审核中 2-审核通过 3-审核未通过 4-已取消", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty("订单状态：0-待支付 1-审核中 2-审核通过 3-审核未通过 4-已取消")
    private Integer orderStatus;

    @Schema(description = "项目ID（关联project_core.id）", requiredMode = Schema.RequiredMode.REQUIRED, example = "14723")
    @ExcelProperty("项目ID（关联project_core.id）")
    private Long projectId;

    @Schema(description = "项目名称（冗余）", requiredMode = Schema.RequiredMode.REQUIRED, example = "芋艿")
    @ExcelProperty("项目名称（冗余）")
    private String projectName;

    @Schema(description = "申购份额(份)", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("申购份额(份)")
    private Integer subscribeQuantity;

    @Schema(description = "单价", example = "7252")
    @ExcelProperty("单价")
    private BigDecimal price;

    @Schema(description = "金额(元)（=申购份额×项目发行单价）", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("金额(元)（=申购份额×项目发行单价）")
    private BigDecimal totalAmount;

    @Schema(description = "购买确认时间")
    @ExcelProperty("购买确认时间")
    private LocalDateTime confirmPurchaseTime;

    @Schema(description = "支付方式", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @ExcelProperty("支付方式")
    private String payType;

    @Schema(description = "链地址")
    @ExcelProperty("链地址")
    private String chainAddress;

    @Schema(description = "合同号")
    @ExcelProperty("合同号")
    private String contractNo;

    @Schema(description = "支付凭证图片URL", example = "https://www.iocoder.cn")
    @ExcelProperty("支付凭证图片URL")
    private String payVoucherUrl;

    @Schema(description = "审核时间（未审核为空）")
    @ExcelProperty("审核时间（未审核为空）")
    private LocalDateTime auditTime;

    @Schema(description = "审核人ID（关联用户表）", example = "32566")
    @ExcelProperty("审核人ID（关联用户表）")
    private Long auditUserId;

    @Schema(description = "审核人名称", example = "芋艿")
    @ExcelProperty("审核人名称")
    private String auditUserName;

    @Schema(description = "链验证状态", example = "2")
    @ExcelProperty("链验证状态")
    private Integer chainStatus;

    @Schema(description = "审核备注（如审核未通过原因）", example = "你说的对")
    @ExcelProperty("审核备注（如审核未通过原因）")
    private String auditRemark;

    @Schema(description = "取消时间（已取消订单填充）")
    @ExcelProperty("取消时间（已取消订单填充）")
    private LocalDateTime cancelTime;

    @Schema(description = "取消原因（如超时未支付）", example = "不好")
    @ExcelProperty("取消原因（如超时未支付）")
    private String cancelReason;

    @Schema(description = "订单过期时间（待支付订单超时时间，如创建后24小时）")
    @ExcelProperty("订单过期时间（待支付订单超时时间，如创建后24小时）")
    private LocalDateTime expireTime;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    private String earningCurrency;

    private String investmentCurrency;

}