package cc.bamboo.module.chain.controller.admin.blockchainaddresses.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import javax.validation.constraints.*;

@Schema(description = "管理后台 - 区块链地址新增/修改 Request VO")
@Data
public class BlockchainAddressesSaveReqVO {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1999")
    private Long id;

    @Schema(description = "地址名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "芋艿")
    @NotEmpty(message = "地址名称不能为空")
    private String name;

    @Schema(description = "区块链地址", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "区块链地址不能为空")
    private String address;

    @Schema(description = "地址私钥", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "地址私钥不能为空")
    private String privateKey;

    @Schema(description = "地址描述", example = "你猜")
    private String description;

}