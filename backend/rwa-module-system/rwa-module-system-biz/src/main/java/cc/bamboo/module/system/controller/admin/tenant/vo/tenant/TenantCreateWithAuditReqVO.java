package cc.bamboo.module.system.controller.admin.tenant.vo.tenant;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 租户创建（带审核状态）Request VO")
@Data
public class TenantCreateWithAuditReqVO {

    @Schema(description = "租户名", requiredMode = Schema.RequiredMode.REQUIRED, example = "芋道")
    @NotNull(message = "租户名不能为空")
    private String name;

    @Schema(description = "联系人", requiredMode = Schema.RequiredMode.REQUIRED, example = "芋艿")
    @NotNull(message = "联系人不能为空")
    private String contactName;

    @Schema(description = "联系手机", example = "15601691300")
    private String contactMobile;

    @Schema(description = "租户状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "租户状态不能为空")
    private Integer status;

    @Schema(description = "绑定域名", example = "https://www.iocoder.cn")
    private String website;

    @Schema(description = "租户套餐编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "租户套餐编号不能为空")
    private Long packageId;

    @Schema(description = "过期时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "过期时间不能为空")
    private LocalDateTime expireTime;

    @Schema(description = "账号数量", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "账号数量不能为空")
    private Integer accountCount;

    @Schema(description = "审核状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    @NotNull(message = "审核状态不能为空")
    private Integer auditStatus;

    @Schema(description = "用户账号", requiredMode = Schema.RequiredMode.REQUIRED, example = "rwa")
    @NotNull(message = "用户账号不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9]{4,30}$", message = "用户账号由 数字、字母 组成")
    @Size(min = 4, max = 30, message = "用户账号长度为 4-30 个字符")
    private String username;

    @Schema(description = "密码", requiredMode = Schema.RequiredMode.REQUIRED, example = "123456")
    @NotNull(message = "密码不能为空")
    @Length(min = 4, max = 16, message = "密码长度为 4-16 位")
    private String password;

    // ==========
    // 发行商信息字段（公司名和用户手机号从租户信息自动填充，identityAuthStatus根据auditStatus自动设置）==========

    @Schema(description = "注册时间")
    private LocalDateTime registerTime;

    @Schema(description = "认证身份", example = "企业法人")
    private String authIdentity;

    @Schema(description = "公司统一社会信用代码（唯一，18位）", example = "91110000000000000X")
    private String companyCreditCode;

    @Schema(description = "营业执照URL（图片/文件）", example = "https://www.iocoder.cn/license.jpg")
    private String businessLicenseUrl;

    @Schema(description = "资质文件URL（多个用,分隔）")
    private String qualificationFileUrls;

    @Schema(description = "授权文件URL（多个用,分隔）")
    private String authorizationFileUrls;

    @Schema(description = "身份证姓名", example = "张三")
    private String idCardName;

    @Schema(description = "身份证号", example = "110101199001011234")
    private String idCardNo;

    @Schema(description = "身份证有效期")
    private LocalDate idCardExpireTime;

    @Schema(description = "身份证正面URL", example = "https://www.iocoder.cn/idcard_front.jpg")
    private String idCardFrontUrl;

    @Schema(description = "身份证背面URL", example = "https://www.iocoder.cn/idcard_back.jpg")
    private String idCardBackUrl;

    @Schema(description = "邮箱", example = "test@iocoder.cn")
    private String email;

    @Schema(description = "开户名（与公司名称/法人姓名一致）", example = "芋道科技有限公司")
    private String bankAccountName;

    @Schema(description = "银行账户（卡号）", example = "6222000000000000000")
    private String bankAccount;

    @Schema(description = "开户行", example = "中国工商银行北京分行")
    private String bankName;

    private Integer companyType;

}
