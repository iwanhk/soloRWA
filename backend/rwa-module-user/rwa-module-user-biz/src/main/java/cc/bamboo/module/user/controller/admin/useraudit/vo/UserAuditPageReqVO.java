package cc.bamboo.module.user.controller.admin.useraudit.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cc.bamboo.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cc.bamboo.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 用户投资者认证审核分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class UserAuditPageReqVO extends PageParam {

    @Schema(description = "关联用户ID", example = "8299")
    private Long userId;

    @Schema(description = "用户姓名（提交审核时的姓名）", example = "王五")
    private String realName;

    @Schema(description = "证件号（脱敏存储）")
    private String idCard;

    @Schema(description = "证件号有效期")
    private String idCardExpire;

    @Schema(description = "证件号人像面图片地址", example = "https://www.iocoder.cn")
    private String idCardFrontUrl;

    @Schema(description = "证件号国徽面图片地址", example = "https://www.iocoder.cn")
    private String idCardBackUrl;

    @Schema(description = "投资资质图片地址", example = "https://www.iocoder.cn")
    private String investmentQualificationUrl;

    @Schema(description = "银行流水单图片地址", example = "https://www.iocoder.cn")
    private String bankFlowUrl;

    @Schema(description = "住址证明图片地址", example = "https://www.iocoder.cn")
    private String residenceProofUrl;

    @Schema(description = "关联用户银行卡ID（user_bank_card.id）", example = "6043")
    private Long bankCardId;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "联系电话")
    private String contactPhone;

    @Schema(description = "审核状态：0-待提交 1-待审核 2-审核通过 3-审核驳回", example = "1")
    private Integer auditStatus;

    @Schema(description = "提交版本（用户第N次提交认证）")
    private Integer submitVersion;

    @Schema(description = "审核备注（驳回原因）", example = "你猜")
    private String auditRemark;

    @Schema(description = "是否为最新提交记录（1-是 0-否）")
    private Boolean isLatest;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}