package cc.bamboo.module.project.controller.admin.projectbill.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 项目账单管理表新增/修改 Request VO")
@Data
public class ProjectBillSaveReqVO {

    @Schema(description = "账单ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "31848")
    private Long id;

    @Schema(description = "流水号（唯一，如：BILL202512160001）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "流水号（唯一，如：BILL202512160001）不能为空")
    private String billNo;

    @Schema(description = "账单类型：1-分红 2-到期赎回 3-提前赎回", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "账单类型：1-分红 2-到期赎回 3-提前赎回不能为空")
    private Integer billType;

    @Schema(description = "申请时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "申请时间不能为空")
    private LocalDateTime applyTime;

    @Schema(description = "申请用户ID（关联用户表）", requiredMode = Schema.RequiredMode.REQUIRED, example = "7975")
    @NotNull(message = "申请用户ID（关联用户表）不能为空")
    private Long userId;

    @Schema(description = "项目ID（关联project_core.id）", requiredMode = Schema.RequiredMode.REQUIRED, example = "27271")
    @NotNull(message = "项目ID（关联project_core.id）不能为空")
    private Long projectId;

    @Schema(description = "所属项目名称（冗余）", requiredMode = Schema.RequiredMode.REQUIRED, example = "张三")
    @NotEmpty(message = "所属项目名称（冗余）不能为空")
    private String projectName;

    @Schema(description = "收款方（用户/企业名称）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "收款方（用户/企业名称）不能为空")
    private String payee;

    @Schema(description = "收款账户（银行卡号/链地址）", requiredMode = Schema.RequiredMode.REQUIRED, example = "5942")
    @NotEmpty(message = "收款账户（银行卡号/链地址）不能为空")
    private String bankAccount;

    @Schema(description = "开户行（如“中国工商银行XX支行”）", requiredMode = Schema.RequiredMode.REQUIRED, example = "芋艿")
    @NotEmpty(message = "开户行（如“中国工商银行XX支行”）不能为空")
    private String bankName;

    @Schema(description = "账单金额（元）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "账单金额（元）不能为空")
    private BigDecimal billAmount;

    @Schema(description = "审核状态：1-待审核 2-审核通过 3-审核不通过", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @NotNull(message = "审核状态：1-待审核 2-审核通过 3-审核不通过不能为空")
    private Integer auditStatus;

    @Schema(description = "审核人ID", example = "19683")
    private Long auditUserId;

    @Schema(description = "审核人名称", example = "王五")
    private String auditUserName;

    @Schema(description = "审核时间")
    private LocalDateTime auditTime;

    @Schema(description = "审核备注（审核不通过原因）", example = "你说的对")
    private String auditRemark;

    @Schema(description = "支付凭证URL（审核通过后上传）", example = "https://www.iocoder.cn")
    private String payVoucherUrl;

    @Schema(description = "支付时间（凭证上传时记录）")
    private LocalDateTime payTime;

}