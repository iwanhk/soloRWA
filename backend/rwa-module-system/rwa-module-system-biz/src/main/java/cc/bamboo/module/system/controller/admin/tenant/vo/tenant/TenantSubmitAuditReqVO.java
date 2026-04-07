package cc.bamboo.module.system.controller.admin.tenant.vo.tenant;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 租户提交审核请求 VO
 * 开发商登录后提交发行商信息进行审核
 */
@Schema(description = "管理后台 - 租户提交审核 Request VO")
@Data
public class TenantSubmitAuditReqVO {

    // ========== 公司信息 ==========

    @Schema(description = "公司统一社会信用代码（唯一，18位）", requiredMode = Schema.RequiredMode.REQUIRED, example = "91110000000000000X")
    @NotBlank(message = "企业信用代码不能为空")
    private String companyCreditCode;

    @Schema(description = "营业执照图片URL", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "营业执照不能为空")
    private String businessLicenseUrl;

    @Schema(description = "资质文件URL（多个逗号分隔）")
    private String qualificationFileUrls;

    @Schema(description = "授权文件URL（多个逗号分隔）")
    private String authorizationFileUrls;

    // ========== 法人/经办人信息 ==========

    @Schema(description = "认证身份", requiredMode = Schema.RequiredMode.REQUIRED, example = "企业法人")
    @NotBlank(message = "认证身份不能为空")
    private String authIdentity;

    @Schema(description = "身份证姓名", requiredMode = Schema.RequiredMode.REQUIRED, example = "张三")
    @NotBlank(message = "身份证姓名不能为空")
    private String idCardName;

    @Schema(description = "身份证号", requiredMode = Schema.RequiredMode.REQUIRED, example = "110101199001011234")
    @NotBlank(message = "身份证号不能为空")
    private String idCardNo;

    @Schema(description = "身份证有效期", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "身份证有效期不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate idCardExpireTime;

    @Schema(description = "身份证正面图片URL", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "身份证正面不能为空")
    private String idCardFrontUrl;

    @Schema(description = "身份证背面图片URL", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "身份证背面不能为空")
    private String idCardBackUrl;

    // ========== 联系信息 ==========

    @Schema(description = "邮箱", example = "admin@example.com")
    private String email;

    // ========== 银行信息 ==========

    @Schema(description = "账户名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "张三")
    @NotBlank(message = "开户名不能为空")
    private String bankAccountName;

    @Schema(description = "银行账户", requiredMode = Schema.RequiredMode.REQUIRED, example = "6222000000000000000")
    @NotBlank(message = "银行账户不能为空")
    private String bankAccount;

    @Schema(description = "开户行", requiredMode = Schema.RequiredMode.REQUIRED, example = "中国银行北京分行")
    @NotBlank(message = "开户行不能为空")
    private String bankName;

}
