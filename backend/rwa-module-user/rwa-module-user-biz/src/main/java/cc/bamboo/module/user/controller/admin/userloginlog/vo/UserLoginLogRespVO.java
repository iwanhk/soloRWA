package cc.bamboo.module.user.controller.admin.userloginlog.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import com.alibaba.excel.annotation.*;
import cc.bamboo.framework.excel.core.annotations.DictFormat;
import cc.bamboo.framework.excel.core.convert.DictConvert;

@Schema(description = "管理后台 - 用户登录日志 Response VO")
@Data
@ExcelIgnoreUnannotated
public class UserLoginLogRespVO {

    @Schema(description = "日志ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "19716")
    @ExcelProperty("日志ID")
    private Long id;

    @Schema(description = "登录用户ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "4175")
    @ExcelProperty("登录用户ID")
    private Long userId;

    @Schema(description = "登录方式：1-密码登录 2-验证码登录", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty("登录方式：1-密码登录 2-验证码登录")
    private Integer loginType;

    @Schema(description = "登录时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("登录时间")
    private LocalDateTime loginTime;

    @Schema(description = "登录IP地址", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("登录IP地址")
    private String loginIp;

    @Schema(description = "登录城市（如“中国 香港”）")
    @ExcelProperty("登录城市（如“中国 香港”）")
    private String loginCity;

    @Schema(description = "设备信息（可选：如手机型号/浏览器标识）")
    @ExcelProperty("设备信息（可选：如手机型号/浏览器标识）")
    private String deviceInfo;

    @Schema(description = "登录状态：1-成功 2-失败（失败时可记录原因）", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty(value = "登录状态：1-成功 2-失败（失败时可记录原因）", converter = DictConvert.class)
    @DictFormat("login_status") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private Integer loginStatus;

    @Schema(description = "登录失败原因（如“密码错误”，登录成功时为空）", example = "不香")
    @ExcelProperty("登录失败原因（如“密码错误”，登录成功时为空）")
    private String failReason;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}