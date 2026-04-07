package cc.bamboo.module.chain.controller.admin.addressidentities.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import javax.validation.constraints.*;

@Schema(description = "管理后台 - 地址身份关联新增/修改 Request VO")
@Data
public class AddressIdentitiesSaveReqVO {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1536")
    private Long id;

    @Schema(description = "区块链地址", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "区块链地址不能为空")
    private String address;

    @Schema(description = "身份ID，关联到对应的身份表", requiredMode = Schema.RequiredMode.REQUIRED, example = "13618")
    @NotNull(message = "身份ID，关联到对应的身份表不能为空")
    private Long identityId;

    @Schema(description = "身份类型，如ClaimIssuer、User等", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotEmpty(message = "身份类型，如ClaimIssuer、User等不能为空")
    private String type;

    @Schema(description = "合约地址")
    private String contractAddress;

}