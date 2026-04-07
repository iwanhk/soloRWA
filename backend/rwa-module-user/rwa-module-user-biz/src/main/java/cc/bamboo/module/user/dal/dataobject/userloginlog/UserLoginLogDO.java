package cc.bamboo.module.user.dal.dataobject.userloginlog;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cc.bamboo.framework.mybatis.core.dataobject.BaseDO;

/**
 * 用户登录日志 DO
 *
 * @author Swolf
 */
@TableName("biz_user_login_log")
@KeySequence("biz_user_login_log_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginLogDO extends BaseDO {

    /**
     * 日志ID
     */
    @TableId
    private Long id;
    /**
     * 登录用户ID
     */
    private Long userId;
    /**
     * 登录方式：1-密码登录 2-验证码登录
     */
    private Integer loginType;
    /**
     * 登录时间
     */
    private LocalDateTime loginTime;
    /**
     * 登录IP地址
     */
    private String loginIp;
    /**
     * 登录城市（如“中国 香港”）
     */
    private String loginCity;
    /**
     * 设备信息（可选：如手机型号/浏览器标识）
     */
    private String deviceInfo;
    /**
     * 登录状态：1-成功 2-失败（失败时可记录原因）
     *
     * 枚举 {@link TODO login_status 对应的类}
     */
    private Integer loginStatus;
    /**
     * 登录失败原因（如“密码错误”，登录成功时为空）
     */
    private String failReason;

}