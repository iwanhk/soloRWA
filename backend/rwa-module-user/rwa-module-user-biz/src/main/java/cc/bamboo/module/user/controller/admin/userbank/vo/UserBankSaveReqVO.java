package cc.bamboo.module.user.controller.admin.userbank.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import javax.validation.constraints.*;

@Schema(description = "管理后台 - 用户银行卡信息新增/修改 Request VO")
@Data
public class UserBankSaveReqVO {

    @Schema(description = "银行卡记录ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "27765")
    private Long id;

    @Schema(description = "关联用户ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "18779")
    @NotNull(message = "关联用户ID不能为空")
    private Long userId;

    @Schema(description = "银行卡开户名（需与实名一致）", requiredMode = Schema.RequiredMode.REQUIRED, example = "赵六")
    @NotEmpty(message = "银行卡开户名（需与实名一致）不能为空")
    private String bankAccountName;

    @Schema(description = "银行卡号", requiredMode = Schema.RequiredMode.REQUIRED, example = "30915")
    @NotEmpty(message = "银行卡号不能为空")
    private String bankAccount;

    @Schema(description = "开户行", requiredMode = Schema.RequiredMode.REQUIRED, example = "王五")
    @NotEmpty(message = "开户行不能为空")
    private String bankName;

    @Schema(description = "开户行支行")
    private String bankBranch;

    @Schema(description = "是否默认银行卡", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "是否默认银行卡不能为空")
    private Boolean isDefault;

    @Schema(description = "审核状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "审核状态不能为空")
    private Integer auditStatus;

    @Schema(description = "审核备注", example = "你说的对")
    private String auditRemark;

}