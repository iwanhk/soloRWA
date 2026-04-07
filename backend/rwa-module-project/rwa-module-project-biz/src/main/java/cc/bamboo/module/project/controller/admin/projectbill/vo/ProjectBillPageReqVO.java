package cc.bamboo.module.project.controller.admin.projectbill.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cc.bamboo.framework.common.pojo.PageParam;
import java.math.BigDecimal;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cc.bamboo.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 项目账单管理表分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ProjectBillPageReqVO extends PageParam {

    @Schema(description = "流水号（唯一，如：BILL202512160001）")
    private String billNo;

    @Schema(description = "账单类型：1-分红 2-到期赎回 3-提前赎回", example = "1")
    private Integer billType;

    @Schema(description = "申请时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] applyTime;

    @Schema(description = "申请用户ID（关联用户表）", example = "7975")
    private Long userId;

    @Schema(description = "项目ID（关联project_core.id）", example = "27271")
    private Long projectId;

    @Schema(description = "所属项目名称（冗余）", example = "张三")
    private String projectName;

    @Schema(description = "收款方（用户/企业名称）")
    private String payee;

    @Schema(description = "收款账户（银行卡号/链地址）", example = "5942")
    private String bankAccount;

    @Schema(description = "开户行（如“中国工商银行XX支行”）", example = "芋艿")
    private String bankName;

    @Schema(description = "账单金额（元）")
    private BigDecimal billAmount;

    @Schema(description = "审核状态：1-待审核 2-审核通过 3-审核不通过", example = "2")
    private Integer auditStatus;

    @Schema(description = "审核人ID", example = "19683")
    private Long auditUserId;

    @Schema(description = "审核人名称", example = "王五")
    private String auditUserName;

    @Schema(description = "审核时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] auditTime;

    @Schema(description = "审核备注（审核不通过原因）", example = "你说的对")
    private String auditRemark;

    @Schema(description = "支付凭证URL（审核通过后上传）", example = "https://www.iocoder.cn")
    private String payVoucherUrl;

    @Schema(description = "支付时间（凭证上传时记录）")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] payTime;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}