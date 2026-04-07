package cc.bamboo.module.user.controller.app.useraudit.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

@Schema(description = "用户 APP - 提交用户认证 Request VO")
@Data
public class AppUserAuditSubmitReqVO {

    @Schema(description = "手机验证码", requiredMode = Schema.RequiredMode.REQUIRED, example = "1234")
    private String code;

    @Schema(description = "邮箱验证码", requiredMode = Schema.RequiredMode.REQUIRED, example = "1234")
    private String emailCode;

    @Schema(description = "用户姓名", requiredMode = Schema.RequiredMode.REQUIRED, example = "张三")
    @NotEmpty(message = "用户姓名不能为空")
    private String realName;

    @Schema(description = "证件号", requiredMode = Schema.RequiredMode.REQUIRED, example = "110101199001011234")
    @NotEmpty(message = "证件号不能为空")
    private String idCard;

    @Schema(description = "证件号有效期", requiredMode = Schema.RequiredMode.REQUIRED, example = "2030-12-31")
    @NotEmpty(message = "证件号有效期不能为空")
    private String idCardExpire;

    @Schema(description = "证件号人像面图片文件", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "证件号人像面图片不能为空")
    private String idCardFrontFile;

    @Schema(description = "证件号国徽面图片文件", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "证件号国徽面图片不能为空")
    private String idCardBackFile;

    @Schema(description = "投资资质图片文件列表(最多6张)")
    @Size(max = 6, message = "投资资质图片最多上传6张")
    private List<String> investmentQualificationFiles;

    @Schema(description = "银行流水图片文件列表(最多6张)")
    @Size(max = 6, message = "银行流水图片最多上传6张")
    private List<String> bankFlowFiles;

    @Schema(description = "住址证明图片文件列表(最多6张)")
    @Size(max = 6, message = "住址证明图片最多上传6张")
    private List<String> residenceProofFiles;

    @Schema(description = "银行卡号", requiredMode = Schema.RequiredMode.REQUIRED, example = "6222021234567890123")
    @NotEmpty(message = "银行卡号不能为空")
    private String bankAccount;

    @Schema(description = "开户银行", requiredMode = Schema.RequiredMode.REQUIRED, example = "中国工商银行")
    @NotEmpty(message = "开户银行不能为空")
    private String bankName;

    private String bankAccountName;

/*    @Schema(description = "邮箱", requiredMode = Schema.RequiredMode.REQUIRED, example = "user@example.com")
    @NotEmpty(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;*/

    @Schema(description = "联系电话", requiredMode = Schema.RequiredMode.REQUIRED, example = "13800138000")
    @NotEmpty(message = "联系电话不能为空")
    private String contactPhone;

}
