package cc.bamboo.module.project.controller.admin.projectorder.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 项目认购订单新增/修改 Request VO")
@Data
public class ProjectOrderSaveReqVO {

    @Schema(description = "订单ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "15943")
    private Long id;

    @Schema(description = "订单号（唯一，如：ORD202512160001）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "订单号（唯一，如：ORD202512160001）不能为空")
    private String orderNo;

    @Schema(description = "申请日期", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "申请日期不能为空")
    private LocalDateTime applyDate;

    @Schema(description = "用户ID（关联用户表）", requiredMode = Schema.RequiredMode.REQUIRED, example = "15701")
    @NotNull(message = "用户ID（关联用户表）不能为空")
    private Long userId;

    @Schema(description = "订单状态：0-待支付 1-审核中 2-审核通过 3-审核未通过 4-已取消", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "订单状态：0-待支付 1-审核中 2-审核通过 3-审核未通过 4-已取消不能为空")
    private Integer orderStatus;

    @Schema(description = "项目ID（关联project_core.id）", requiredMode = Schema.RequiredMode.REQUIRED, example = "14723")
    @NotNull(message = "项目ID（关联project_core.id）不能为空")
    private Long projectId;

    @Schema(description = "项目名称（冗余）", requiredMode = Schema.RequiredMode.REQUIRED, example = "芋艿")
    @NotEmpty(message = "项目名称（冗余）不能为空")
    private String projectName;

    @Schema(description = "申购份额(份)", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "申购份额(份)不能为空")
    private Integer subscribeQuantity;

    @Schema(description = "单价", example = "7252")
    private BigDecimal price;

    @Schema(description = "金额(元)（=申购份额×项目发行单价）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "金额(元)（=申购份额×项目发行单价）不能为空")
    private BigDecimal totalAmount;

    @Schema(description = "购买确认时间")
    private LocalDateTime confirmPurchaseTime;

    @Schema(description = "支付方式", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @NotEmpty(message = "支付方式不能为空")
    private String payType;

    @Schema(description = "链地址")
    private String chainAddress;

    @Schema(description = "合同号")
    private String contractNo;

    @Schema(description = "支付凭证图片URL", example = "https://www.iocoder.cn")
    private String payVoucherUrl;

    @Schema(description = "审核时间（未审核为空）")
    private LocalDateTime auditTime;

    @Schema(description = "审核人ID（关联用户表）", example = "32566")
    private Long auditUserId;

    @Schema(description = "审核人名称", example = "芋艿")
    private String auditUserName;

    @Schema(description = "链验证状态", example = "2")
    private Integer chainStatus;

    @Schema(description = "审核备注（如审核未通过原因）", example = "你说的对")
    private String auditRemark;

    @Schema(description = "取消时间（已取消订单填充）")
    private LocalDateTime cancelTime;

    @Schema(description = "取消原因（如超时未支付）", example = "不好")
    private String cancelReason;

    @Schema(description = "订单过期时间（待支付订单超时时间，如创建后24小时）")
    private LocalDateTime expireTime;

}