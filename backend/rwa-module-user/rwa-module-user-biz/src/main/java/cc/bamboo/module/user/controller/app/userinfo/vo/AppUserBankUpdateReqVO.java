package cc.bamboo.module.user.controller.app.userinfo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Schema(description = "用户 APP - 银行卡信息修改 Request VO")
@Data
public class AppUserBankUpdateReqVO {

    @Schema(description = "银行卡记录ID", example = "1")
    @NotNull
    private Long id;

    @Schema(description = "银行卡开户名（需与实名一致）", requiredMode = Schema.RequiredMode.REQUIRED, example = "赵六")
    @NotEmpty(message = "银行卡开户名不能为空")
    private String bankAccountName;

    @Schema(description = "银行卡号", requiredMode = Schema.RequiredMode.REQUIRED, example = "6222021001112222333")
    @NotEmpty(message = "银行卡号不能为空")
    private String bankAccount;

    @Schema(description = "开户行", requiredMode = Schema.RequiredMode.REQUIRED, example = "中国工商银行")
    private String bankName;

    @Schema(description = "开户行支行", example = "北京海淀支行")
    private String bankBranch;

}
