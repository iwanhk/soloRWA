package cc.bamboo.module.user.controller.admin.userchain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import javax.validation.constraints.*;

@Schema(description = "管理后台 - 用户链地址表=新增/修改 Request VO")
@Data
public class UserChainSaveReqVO {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "17334")
    private Long id;

    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "8226")
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @Schema(description = "关联链ID（chain_manage.id）", requiredMode = Schema.RequiredMode.REQUIRED, example = "130")
    @NotNull(message = "关联链ID（chain_manage.id）不能为空")
    private Long chainId;

    @Schema(description = "用户在该链上的地址（如ETH地址：0x...）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "用户在该链上的地址（如ETH地址：0x...）不能为空")
    private String chainAddress;

    @Schema(description = "identity_id", example = "24161")
    private Long identityId;

    @Schema(description = "链上状态", example = "1")
    private Long chainStatus;

    @Schema(description = "是否默认地址：1-是 0-否（同一链下仅1个默认）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "是否默认地址：1-是 0-否（同一链下仅1个默认）不能为空")
    private Boolean isDefault;

    @Schema(description = "地址备注（如“常用钱包”）", example = "随便")
    private String addressRemark;

}