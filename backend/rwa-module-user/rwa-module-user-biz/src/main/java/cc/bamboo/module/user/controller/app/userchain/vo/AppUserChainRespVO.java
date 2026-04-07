package cc.bamboo.module.user.controller.app.userchain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author Hus
 * @version 1.0
 * @date 2026/1/9 11:19
 * @description
 */
@Schema(description = "用户 APP - 用户链")
@Data
public class AppUserChainRespVO {


    private String chainAddress;

    @Schema(description = "链状态", example = "0绑定中 1正常 2解绑中")
    private Integer chainStatus;

    private Long id;
}
