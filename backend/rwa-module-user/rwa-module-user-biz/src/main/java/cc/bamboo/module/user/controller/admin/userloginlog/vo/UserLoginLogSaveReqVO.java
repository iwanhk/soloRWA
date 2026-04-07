package cc.bamboo.module.user.controller.admin.userloginlog.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import javax.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 用户登录日志新增/修改 Request VO")
@Data
public class UserLoginLogSaveReqVO {

    @Schema(description = "日志ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "19716")
    private Long id;

    @Schema(description = "登录用户ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "4175")
    @NotNull(message = "登录用户ID不能为空")
    private Long userId;

    @Schema(description = "登录方式：1-密码登录 2-验证码登录", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "登录方式：1-密码登录 2-验证码登录不能为空")
    private Integer loginType;

    @Schema(description = "登录时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "登录时间不能为空")
    private LocalDateTime loginTime;

    @Schema(description = "登录IP地址", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "登录IP地址不能为空")
    private String loginIp;

    @Schema(description = "登录城市（如“中国 香港”）")
    private String loginCity;

    @Schema(description = "设备信息（可选：如手机型号/浏览器标识）")
    private String deviceInfo;

    @Schema(description = "登录状态：1-成功 2-失败（失败时可记录原因）", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "登录状态：1-成功 2-失败（失败时可记录原因）不能为空")
    private Integer loginStatus;

    @Schema(description = "登录失败原因（如“密码错误”，登录成功时为空）", example = "不香")
    private String failReason;

}