package cc.bamboo.module.user.controller.admin.userloginlog.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cc.bamboo.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cc.bamboo.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 用户登录日志分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class UserLoginLogPageReqVO extends PageParam {

    @Schema(description = "登录用户ID", example = "4175")
    private Long userId;

    @Schema(description = "登录方式：1-密码登录 2-验证码登录", example = "1")
    private Integer loginType;

    @Schema(description = "登录时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] loginTime;

    @Schema(description = "登录IP地址")
    private String loginIp;

    @Schema(description = "登录城市（如“中国 香港”）")
    private String loginCity;

    @Schema(description = "设备信息（可选：如手机型号/浏览器标识）")
    private String deviceInfo;

    @Schema(description = "登录状态：1-成功 2-失败（失败时可记录原因）", example = "1")
    private Integer loginStatus;

    @Schema(description = "登录失败原因（如“密码错误”，登录成功时为空）", example = "不香")
    private String failReason;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}