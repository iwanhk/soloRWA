package cc.bamboo.module.user.controller.app.userinfo.vo;

import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author Hus
 * @version 1.0
 * @date 2026/1/9 10:46
 * @description
 */
@Schema(description = "用户 APP - 用户基础信息")
@Data
public class AppUserInfoRespVO {

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 用户姓名
     */
    private String realName;

    /**
     * 审核状态: 0未提交 1待审核 2通过 3不通过
     */
    @Schema(description = "审核状态", example = "0未提交 1待审核 2通过 3不通过")
    private Integer auditStatus;

    /**
     * 币种
     */

    private String defaultCurrency;
    /**
     * 2FA验证状态：0-未开启 1-已开启 2-待验证
     */
    @Schema(description = "2FA验证状态", example = "0-未开启 1-已开启 2-待验证")
    private Integer twoFactorAuthStatus;


    private Integer chainCount;

    private Integer orderCount;

    private Long id;

    private String nickName;

    private String avatar;

    private String email;
}
