package cc.bamboo.module.chain.controller.admin.claimissueridentities.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import javax.validation.constraints.*;

@Schema(description = "管理后台 - 声明发行者身份新增/修改 Request VO")
@Data
public class ClaimIssuerIdentitiesSaveReqVO {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "19561")
    private Long id;

    @Schema(description = "发行者地址", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "发行者地址不能为空")
    private String address;

    @Schema(description = "发行者合约地址")
    private String contractAddress;

    @Schema(description = "管理密钥地址", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "管理密钥地址不能为空")
    private String managementKey;

    @Schema(description = "关联的区块链地址ID", example = "16071")
    private Long blockchainAddressId;

    @Schema(description = "盐值，用于加密或哈希计算")
    private String salt;

    @Schema(description = "交易哈希")
    private String transactionHash;

    @Schema(description = "区块号")
    private Long blockNumber;

    @Schema(description = "合约是否已部署，0-未部署，1-已部署", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "合约是否已部署，0-未部署，1-已部署不能为空")
    private Boolean contractDeployed;

    @Schema(description = "声明密钥是否已设置，0-未设置，1-已设置", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "声明密钥是否已设置，0-未设置，1-已设置不能为空")
    private Boolean claimKeySetup;

    @Schema(description = "状态，如active-活跃", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotEmpty(message = "状态，如active-活跃不能为空")
    private String status;

}