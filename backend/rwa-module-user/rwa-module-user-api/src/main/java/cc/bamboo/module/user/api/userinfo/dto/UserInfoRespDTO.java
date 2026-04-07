package cc.bamboo.module.user.api.userinfo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * RPC 服务 - 用户信息 Response DTO
 *
 * @author Swolf
 */
@Schema(description = "RPC 服务 - 用户信息 Response DTO")
@Data
public class UserInfoRespDTO {

    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "手机号", requiredMode = Schema.RequiredMode.REQUIRED, example = "15601691300")
    private String mobile;

    @Schema(description = "用户姓名", example = "张三")
    private String realName;

    @Schema(description = "身份证号", example = "110101199001011234")
    private String idCard;

    @Schema(description = "邮箱", example = "zhangsan@example.com")
    private String email;

    @Schema(description = "联系电话", example = "010-12345678")
    private String phone;

    @Schema(description = "审核状态", example = "1")
    private Integer auditStatus;

    @Schema(description = "状态：1启动 2禁用", example = "1")
    private Integer status;

}
