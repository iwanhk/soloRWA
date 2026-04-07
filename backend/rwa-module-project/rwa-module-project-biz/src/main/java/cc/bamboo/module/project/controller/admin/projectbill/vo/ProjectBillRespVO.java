package cc.bamboo.module.project.controller.admin.projectbill.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.math.BigDecimal;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import com.alibaba.excel.annotation.*;
import cc.bamboo.framework.excel.core.annotations.DictFormat;
import cc.bamboo.framework.excel.core.convert.DictConvert;

@Schema(description = "管理后台 - 项目账单管理表 Response VO")
@Data
@ExcelIgnoreUnannotated
public class ProjectBillRespVO {

    @Schema(description = "账单ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "31848")
    @ExcelProperty("账单ID")
    private Long id;

    @Schema(description = "流水号（唯一，如：BILL202512160001）", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("流水号（唯一，如：BILL202512160001）")
    private String billNo;

    @Schema(description = "账单类型：1-分红 2-到期赎回 3-提前赎回", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty("账单类型：1-分红 2-到期赎回 3-提前赎回")
    private Integer billType;

    @Schema(description = "申请时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("申请时间")
    private LocalDateTime applyTime;

    @Schema(description = "申请用户ID（关联用户表）", requiredMode = Schema.RequiredMode.REQUIRED, example = "7975")
    @ExcelProperty("申请用户ID（关联用户表）")
    private Long userId;

    @Schema(description = "项目ID（关联project_core.id）", requiredMode = Schema.RequiredMode.REQUIRED, example = "27271")
    @ExcelProperty("项目ID（关联project_core.id）")
    private Long projectId;

    @Schema(description = "所属项目名称（冗余）", requiredMode = Schema.RequiredMode.REQUIRED, example = "张三")
    @ExcelProperty("所属项目名称（冗余）")
    private String projectName;

    @Schema(description = "收款方（用户/企业名称）", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("收款方（用户/企业名称）")
    private String bankAccountName;

    @Schema(description = "收款账户（银行卡号/链地址）", requiredMode = Schema.RequiredMode.REQUIRED, example = "5942")
    @ExcelProperty("收款账户（银行卡号/链地址）")
    private String bankAccount;

    @Schema(description = "开户行（如“中国工商银行XX支行”）", requiredMode = Schema.RequiredMode.REQUIRED, example = "芋艿")
    @ExcelProperty("开户行（如“中国工商银行XX支行”）")
    private String bankName;

    @Schema(description = "账单金额（元）", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("账单金额（元）")
    private BigDecimal billAmount;

    @Schema(description = "实际到账金额（元）", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("实际到账金额（元）")
    private BigDecimal actualAmount;

    @Schema(description = "审核状态：1-待审核 2-审核通过 3-审核不通过", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @ExcelProperty(value = "审核状态：1-待审核 2-审核通过 3-审核不通过", converter = DictConvert.class)
    @DictFormat("audit_status") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private Integer auditStatus;

    @Schema(description = "审核人ID", example = "19683")
    @ExcelProperty("审核人ID")
    private Long auditUserId;

    @Schema(description = "审核人名称", example = "王五")
    @ExcelProperty("审核人名称")
    private String auditUserName;

    @Schema(description = "审核时间")
    @ExcelProperty("审核时间")
    private LocalDateTime auditTime;

    @Schema(description = "审核备注（审核不通过原因）", example = "你说的对")
    @ExcelProperty("审核备注（审核不通过原因）")
    private String auditRemark;

    @Schema(description = "支付凭证URL（审核通过后上传）", example = "https://www.iocoder.cn")
    @ExcelProperty("支付凭证URL（审核通过后上传）")
    private String payVoucherUrl;

    @Schema(description = "支付时间（凭证上传时记录）")
    @ExcelProperty("支付时间（凭证上传时记录）")
    private LocalDateTime payTime;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @Schema(description = "账单类型名称")
    @ExcelProperty("账单类型名称")
    private String billTypeName;

    @Schema(description = "审核状态名称")
    @ExcelProperty("审核状态名称")
    private String auditStatusName;

    @Schema(description = "用户名称")
    @ExcelProperty("用户名称")
    private String userName;

    private String orderNo;

    private Long orderId;

    private Integer quantity;

    @Schema(description = "是否已支付")
    @ExcelProperty("是否已支付")
    private Boolean isPaid;

    private String commissionRate;

    /**
     * 到账币种
     */
    private String actualCoin;
    /**
     * 到账汇率
     */
    private BigDecimal actualExchangeRate;

    /**
     * 申请币种
     */
    private String billCoin;

    private Integer arrivalDay;

}