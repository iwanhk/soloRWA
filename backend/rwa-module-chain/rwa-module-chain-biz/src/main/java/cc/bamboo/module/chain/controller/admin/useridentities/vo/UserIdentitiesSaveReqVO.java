package cc.bamboo.module.chain.controller.admin.useridentities.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import javax.validation.constraints.*;

@Schema(description = "管理后台 - 用户身份新增/修改 Request VO")
@Data
public class UserIdentitiesSaveReqVO {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "6150")
    private Long id;

    @Schema(description = "用户地址", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "用户地址不能为空")
    private String address;

    @Schema(description = "用户合约地址")
    private String contractAddress;

    @Schema(description = "管理密钥地址", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "管理密钥地址不能为空")
    private String managementKey;

    @Schema(description = "关联的区块链地址ID", example = "22391")
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

    @Schema(description = "国家代码")
    private Long countryCode;

    @Schema(description = "关联的代币ID，JSON格式")
    private String associatedTokenIds;

    @Schema(description = "待处理的代币ID，JSON格式")
    private String pendingTokenIds;

    @Schema(description = "状态，如active-活跃", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @NotEmpty(message = "状态，如active-活跃不能为空")
    private String status;

}