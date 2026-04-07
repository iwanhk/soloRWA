package cc.bamboo.module.user.dal.dataobject.userinfo;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cc.bamboo.framework.mybatis.core.dataobject.BaseDO;

/**
 * 用户基础信息 DO
 *
 * @author Swolf
 */
@TableName("biz_user_info")
@KeySequence("biz_user_info_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoDO extends BaseDO {

    /**
     * 用户ID
     */
    @TableId
    private Long id;
    /**
     * 手机号
     */
    private String mobile;
    /**
     * 密码
     */
    private String password;
    /**
     * 用户姓名
     */
    private String realName;
    /**
     * 身份证号
     */
    private String idCard;
    /**
     * 身份证有效期
     */
    private String idCardExpire;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 联系电话
     */
    private String phone;
    /**
     * 审核状态
     */
    private Integer auditStatus;
    /**
     * 状态：1启动 2禁用
     */
    private Integer status;
    /**
     * 币种
     */
    private String defaultCurrency;
    /**
     * 2FA验证状态：0-未开启 1-已开启 2-待验证
     */
    private Integer twoFactorAuthStatus;
    /**
     * 2FA验证密钥（Base32格式，开启2FA时生成）
     */
    private String twoFactorAuthSecret;
    /**
     * 2FA验证绑定时间
     */
    private LocalDateTime twoFactorAuthBindTime;
    /**
     * 2FA验证最后验证时间
     */
    private LocalDateTime twoFactorAuthLastVerifyTime;

    private String nickName;

    private String avatar;

}