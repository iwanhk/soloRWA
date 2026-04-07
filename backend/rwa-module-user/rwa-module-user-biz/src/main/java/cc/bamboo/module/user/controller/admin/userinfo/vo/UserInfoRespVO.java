package cc.bamboo.module.user.controller.admin.userinfo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import com.alibaba.excel.annotation.*;

@Schema(description = "管理后台 - 用户基础信息 Response VO")
@Data
@ExcelIgnoreUnannotated
public class UserInfoRespVO {

    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "18004")
    @ExcelProperty("用户ID")
    private Long id;

    @Schema(description = "手机号", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("手机号")
    private String mobile;

    @Schema(description = "密码", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("密码")
    private String password;

    @Schema(description = "用户姓名", example = "芋艿")
    @ExcelProperty("用户姓名")
    private String realName;

    @Schema(description = "身份证号")
    @ExcelProperty("身份证号")
    private String idCard;

    @Schema(description = "身份证有效期")
    @ExcelProperty("身份证有效期")
    private String idCardExpire;

    @Schema(description = "邮箱")
    @ExcelProperty("邮箱")
    private String email;

    @Schema(description = "联系电话")
    @ExcelProperty("联系电话")
    private String phone;

    @Schema(description = "审核状态", example = "1")
    @ExcelProperty("审核状态")
    private Integer auditStatus;

    @Schema(description = "状态：1启动 2禁用", example = "2")
    @ExcelProperty("状态：1启动 2禁用")
    private Integer status;

    @Schema(description = "币种", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("币种")
    private String defaultCurrency;

    @Schema(description = "2FA验证状态：0-未开启 1-已开启 2-待验证", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty("2FA验证状态：0-未开启 1-已开启 2-待验证")
    private Integer twoFactorAuthStatus;

    @Schema(description = "2FA验证密钥（Base32格式，开启2FA时生成）")
    @ExcelProperty("2FA验证密钥（Base32格式，开启2FA时生成）")
    private String twoFactorAuthSecret;

    @Schema(description = "2FA验证绑定时间")
    @ExcelProperty("2FA验证绑定时间")
    private LocalDateTime twoFactorAuthBindTime;

    @Schema(description = "2FA验证最后验证时间")
    @ExcelProperty("2FA验证最后验证时间")
    private LocalDateTime twoFactorAuthLastVerifyTime;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @Schema(description = "待审核的银行卡申请ID")
    private Long pendingBankApplyId;

    private String nickName;

    private String avatar;

}