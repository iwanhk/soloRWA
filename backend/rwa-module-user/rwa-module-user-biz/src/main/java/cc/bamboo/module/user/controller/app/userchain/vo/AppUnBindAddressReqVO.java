package cc.bamboo.module.user.controller.app.userchain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author Hus
 * @version 1.0
 * @date 2026/1/9 11:19
 * @description
 */
@Schema(description = "用户 APP - 解绑链地址")
@Data
public class AppUnBindAddressReqVO {

    private Long addressId;

}
