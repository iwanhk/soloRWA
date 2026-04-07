package cc.bamboo.module.project.controller.app.projectorder.vo;

import cc.bamboo.module.project.controller.app.orderbalance.vo.AppProjectOrderBalanceRespVO;
import cc.bamboo.module.project.controller.app.projectinfo.vo.AppProjectPaymentInfoRespVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户 APP - 订单列表 Response VO
 *
 * @author Swolf
 */
@Schema(description = "用户 APP - 订单列表 Response VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppOrderListRespVO {

    @Schema(description = "订单ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "订单号", requiredMode = Schema.RequiredMode.REQUIRED, example = "ORD20231201123456")
    private String orderNo;

    @Schema(description = "申请日期", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime applyDate;

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

    @Schema(description = "订单过期时间")
    private LocalDateTime expireTime;

    @Schema(description = "审核备注")
    private String auditRemark;

    /**
     * 链地址
     */
    @Schema(description = "链地址")
    private String chainAddress;
    /**
     * 合同号
     */
    @Schema(description = "合同号")
    private String contractNo;
    /**
     * 支付凭证
     */
    @Schema(description = "支付凭证")
    private String payVoucherUrl;

    /**
     * 支付方式
     */
    @Schema(description = "支付方式")
    private String payType;
    /**
     * 购买确认时间
     */
    @Schema(description = "购买确认时间")
    private LocalDateTime confirmPurchaseTime;

    @Schema(description = "累计总收益")
    private BigDecimal totalIncome;

    @Schema(description = "开户名", example = "北京XX科技有限公司")
    private String bankAccountName;

    @Schema(description = "银行账号", example = "6222021234567890123")
    private String bankAccount;

    @Schema(description = "开户行", example = "中国工商银行北京分行")
    private String bankName;

    @Schema(description = "投资币种")
    private String investmentCurrency;

    @Schema(description = "分红币种")
    private String earningCurrency;

    private LocalDateTime createTime;

}
