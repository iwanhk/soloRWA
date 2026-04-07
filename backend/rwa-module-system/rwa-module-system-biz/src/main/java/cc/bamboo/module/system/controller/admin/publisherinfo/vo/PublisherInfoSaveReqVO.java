package cc.bamboo.module.system.controller.admin.publisherinfo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import javax.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 发行商新增/修改 Request VO")
@Data
public class PublisherInfoSaveReqVO {

    @Schema(description = "发行商ID （sys_user.id，发行商对应的用户账号）", requiredMode = Schema.RequiredMode.REQUIRED, example = "1805")
    private Long id;

    @Schema(description = "用户手机号（冗余sys_user.phonenumber）", requiredMode = Schema.RequiredMode.REQUIRED)
    private String userPhone;

    @Schema(description = "注册时间（冗余sys_user.create_time）", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime registerTime;

    @Schema(description = "身份认证状态：0-未认证 1-认证中 2-已认证 3-认证失败", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "身份认证状态：0-未认证 1-认证中 2-已认证 3-认证失败不能为空")
    private Integer identityAuthStatus;

    @Schema(description = "认证身份（如“企业法人”“经办人”）")
    private String authIdentity;

    @Schema(description = "公司名称（企业全称，与营业执照一致）", requiredMode = Schema.RequiredMode.REQUIRED, example = "赵六")
    private String companyName;

    @Schema(description = "公司统一社会信用代码（唯一，18位）", requiredMode = Schema.RequiredMode.REQUIRED)
    private String companyCreditCode;

    @Schema(description = "营业执照URL（图片/文件）", example = "https://www.iocoder.cn")
    private String businessLicenseUrl;

    @Schema(description = "资质文件URL（多个用,分隔）")
    private String qualificationFileUrls;

    @Schema(description = "授权文件URL（多个用,分隔）")
    private String authorizationFileUrls;

    @Schema(description = "身份证姓名", example = "李四")
    private String idCardName;

    @Schema(description = "身份证号")
    private String idCardNo;

    @Schema(description = "身份证有效期")
    private LocalDate idCardExpireTime;

    @Schema(description = "身份证正面URL", example = "https://www.iocoder.cn")
    private String idCardFrontUrl;

    @Schema(description = "身份证背面URL", example = "https://www.iocoder.cn")
    private String idCardBackUrl;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "开户名（与公司名称/法人姓名一致）", example = "张三")
    private String bankAccountName;

    @Schema(description = "银行账户（卡号）", example = "19008")
    private String bankAccount;

    @Schema(description = "开户行", example = "赵六")
    private String bankName;

    private Integer companyType;
}