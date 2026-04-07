package cc.bamboo.module.project.controller.app.projectorder.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户 APP - 订单详情 Response VO
 *
 * @author Swolf
 */
@Schema(description = "用户 APP - 订单详情 Response VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppOrderDetailRespVO {

    @Schema(description = "订单ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "订单号", requiredMode = Schema.RequiredMode.REQUIRED, example = "ORD20231201123456")
    private String orderNo;

    @Schema(description = "申请日期", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime applyDate;

    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long userId;

    @Schema(description = "订单状态：0-待支付 1-审核中 2-审核通过 3-审核未通过 4-已取消", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer orderStatus;

    @Schema(description = "项目ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long projectId;

    @Schema(description = "项目名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "某某RWA项目")
    private String projectName;

    @Schema(description = "申购份额(份)", requiredMode = Schema.RequiredMode.REQUIRED, example = "10")
    private Integer subscribeQuantity;

    @Schema(description = "单价", requiredMode = Schema.RequiredMode.REQUIRED, example = "1000.00")
    private BigDecimal price;

    @Schema(description = "金额(元)", requiredMode = Schema.RequiredMode.REQUIRED, example = "10000.00")
    private BigDecimal totalAmount;

    @Schema(description = "确认时间")
    private LocalDateTime confirmPurchaseTime;

    @Schema(description = "支付方式", example = "银行转账")
    private String payType;

    @Schema(description = "链地址", example = "0x1234567890abcdef1234567890abcdef12345678")
    private String chainAddress;

    @Schema(description = "合同号", example = "CONTRACT20231201123456")
    private String contractNo;

    @Schema(description = "支付凭证URL", example = "https://example.com/voucher.jpg")
    private String payVoucherUrl;

    @Schema(description = "审核时间")
    private LocalDateTime auditTime;

    @Schema(description = "审核人ID", example = "100")
    private Long auditUserId;

    @Schema(description = "审核人名称", example = "管理员")
    private String auditUserName;

    @Schema(description = "审核备注", example = "审核通过")
    private String auditRemark;

    @Schema(description = "取消时间")
    private LocalDateTime cancelTime;

    @Schema(description = "取消原因", example = "用户主动取消")
    private String cancelReason;

    @Schema(description = "订单过期时间")
    private LocalDateTime expireTime;

    @Schema(description = "当前可提现余额（仅支付成功订单）")
    private BigDecimal withdrawableBalance;

    @Schema(description = "开户名", example = "北京XX科技有限公司")
    private String bankAccountName;

    @Schema(description = "银行账号", example = "6222021234567890123")
    private String bankAccount;

    @Schema(description = "开户行", example = "中国工商银行北京分行")
    private String bankName;

    /**
     * 持有金额
     */
    private BigDecimal holdAmount;

    /**
     * 当前持有份额(份)（赎回后扣减）
     */
    private Integer holdQuantity;

    private String auditFiles;

    @Schema(description = "投资币种")
    private String investmentCurrency;

    @Schema(description = "分红币种")
    private String earningCurrency;

    private LocalDateTime createTime;

    private Integer status;
}
