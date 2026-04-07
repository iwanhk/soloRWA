package cc.bamboo.module.user.controller.admin.userbank.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cc.bamboo.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cc.bamboo.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 用户银行卡信息分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class UserBankPageReqVO extends PageParam {

    @Schema(description = "关联用户ID", example = "18779")
    private Long userId;

    @Schema(description = "银行卡开户名（需与实名一致）", example = "赵六")
    private String bankAccountName;

    @Schema(description = "银行卡号", example = "30915")
    private String bankAccount;

    @Schema(description = "开户行", example = "王五")
    private String bankName;

    @Schema(description = "开户行支行")
    private String bankBranch;

    @Schema(description = "是否默认银行卡")
    private Boolean isDefault;

    @Schema(description = "审核状态", example = "1")
    private Integer auditStatus;

    @Schema(description = "审核备注", example = "你说的对")
    private String auditRemark;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}