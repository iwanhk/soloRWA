package cc.bamboo.module.user.controller.admin.userinfo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import javax.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 用户基础信息新增/修改 Request VO")
@Data
public class UserInfoSaveReqVO {

    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "18004")
    private Long id;

    @Schema(description = "手机号", requiredMode = Schema.RequiredMode.REQUIRED)
    private String mobile;

    @Schema(description = "密码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "密码不能为空")
    private String password;

    @Schema(description = "用户姓名", example = "芋艿")
    private String realName;

    @Schema(description = "身份证号")
    private String idCard;

    @Schema(description = "身份证有效期")
    private String idCardExpire;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "联系电话")
    private String phone;

    @Schema(description = "审核状态", example = "1")
    private Integer auditStatus;

    @Schema(description = "状态：1启动 2禁用", example = "2")
    private Integer status;

    @Schema(description = "币种", example = "USD")
    private String defaultCurrency;

    @Schema(description = "2FA验证状态：0-未开启 1-已开启 2-待验证", example = "0")
    private Integer twoFactorAuthStatus;

    @Schema(description = "2FA验证密钥（Base32格式，开启2FA时生成）")
    private String twoFactorAuthSecret;

    @Schema(description = "2FA验证绑定时间")
    private LocalDateTime twoFactorAuthBindTime;

    @Schema(description = "2FA验证最后验证时间")
    private LocalDateTime twoFactorAuthLastVerifyTime;

}