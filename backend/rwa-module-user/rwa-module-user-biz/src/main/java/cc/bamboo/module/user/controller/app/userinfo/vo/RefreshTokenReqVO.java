package cc.bamboo.module.user.controller.app.userinfo.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author Hus
 * @version 1.0
 * @date 2025/12/17 16:55
 * @description
 */
@Data
public class RefreshTokenReqVO {

    @NotBlank
    private String refreshToken;
}
