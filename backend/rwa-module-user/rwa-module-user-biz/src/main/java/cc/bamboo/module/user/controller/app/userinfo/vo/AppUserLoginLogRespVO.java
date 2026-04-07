package cc.bamboo.module.user.controller.app.userinfo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Schema(description = "用户 APP - 用户登录日志 Response VO")
@Data
public class AppUserLoginLogRespVO {

    @Schema(description = "日志ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "登录时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime loginTime;

    @Schema(description = "登录IP", requiredMode = Schema.RequiredMode.REQUIRED, example = "127.0.0.1")
    private String loginIp;

    @Schema(description = "登录地点", example = "中国 上海")
    private String loginCity;

    @Schema(description = "设备信息", example = "iPhone 13")
    private String deviceInfo;

    @Schema(description = "登录状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer loginStatus;

    /**
     * 登录方式：1-密码登录 2-验证码登录
     */
    private Integer loginType;

}
