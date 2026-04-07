package cc.bamboo.module.user.controller.admin.userinfo.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cc.bamboo.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cc.bamboo.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 用户基础信息分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class UserInfoPageReqVO extends PageParam {

    @Schema(description = "手机号")
    private String mobile;

    @Schema(description = "密码")
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

    @Schema(description = "币种")
    private String defaultCurrency;

    @Schema(description = "2FA验证状态：0-未开启 1-已开启 2-待验证", example = "1")
    private Integer twoFactorAuthStatus;

    @Schema(description = "2FA验证密钥（Base32格式，开启2FA时生成）")
    private String twoFactorAuthSecret;

    @Schema(description = "2FA验证绑定时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] twoFactorAuthBindTime;

    @Schema(description = "2FA验证最后验证时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] twoFactorAuthLastVerifyTime;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}