package cc.bamboo.module.chain.controller.admin.identityregistrystorages.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import javax.validation.constraints.*;

@Schema(description = "管理后台 - 身份注册表存储新增/修改 Request VO")
@Data
public class IdentityRegistryStoragesSaveReqVO {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "19084")
    private Long id;

    @Schema(description = "存储合约地址", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "存储合约地址不能为空")
    private String address;

    @Schema(description = "部署者地址", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "部署者地址不能为空")
    private String deployerAddress;

    @Schema(description = "交易哈希")
    private String transactionHash;

    @Schema(description = "区块号")
    private Long blockNumber;

    @Schema(description = "绑定的代币数量", requiredMode = Schema.RequiredMode.REQUIRED, example = "25752")
    @NotNull(message = "绑定的代币数量不能为空")
    private Long boundTokenCount;

    @Schema(description = "状态，如deployed-已部署，active-活跃", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @NotEmpty(message = "状态，如deployed-已部署，active-活跃不能为空")
    private String status;

}