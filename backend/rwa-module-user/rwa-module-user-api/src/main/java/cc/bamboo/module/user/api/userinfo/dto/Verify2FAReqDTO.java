package cc.bamboo.module.user.api.userinfo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Hus
 * @version 1.0
 * @date 2026/1/30 11:20
 * @description
 */
@Data
public class Verify2FAReqDTO implements Serializable {

    private Long userId;

    private String code;
}
