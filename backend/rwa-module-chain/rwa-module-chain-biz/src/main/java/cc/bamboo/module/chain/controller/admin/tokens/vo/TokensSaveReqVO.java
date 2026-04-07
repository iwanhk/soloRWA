package cc.bamboo.module.chain.controller.admin.tokens.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import javax.validation.constraints.*;

@Schema(description = "管理后台 - 代币新增/修改 Request VO")
@Data
public class TokensSaveReqVO {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "9548")
    private Long id;

    @Schema(description = "代币名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "赵六")
    @NotEmpty(message = "代币名称不能为空")
    private String name;

    @Schema(description = "代币符号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "代币符号不能为空")
    private String symbol;

    @Schema(description = "小数位数", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "小数位数不能为空")
    private Long decimals;

    @Schema(description = "代币合约地址")
    private String address;

    @Schema(description = "所有者地址", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "所有者地址不能为空")
    private String ownerAddress;

    @Schema(description = "部署者地址", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "部署者地址不能为空")
    private String deployerAddress;

    @Schema(description = "身份注册表存储ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "8976")
    @NotNull(message = "身份注册表存储ID不能为空")
    private Long identityRegistryStorageId;

    @Schema(description = "身份注册表地址")
    private String identityRegistryAddress;

    @Schema(description = "声明主题注册表地址")
    private String claimTopicsRegistryAddress;

    @Schema(description = "可信发行者注册表地址")
    private String trustedIssuersRegistryAddress;

    @Schema(description = "模块化合规合约地址")
    private String modularComplianceAddress;

    @Schema(description = "代币链上ID地址")
    private String tokenOnchainIdAddress;

    @Schema(description = "交易哈希")
    private String transactionHash;

    @Schema(description = "区块号")
    private Long blockNumber;

    @Schema(description = "状态，如pending-待处理，deployed-已部署", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @NotEmpty(message = "状态，如pending-待处理，deployed-已部署不能为空")
    private String status;

    @Schema(description = "盐值，用于加密或哈希计算", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "盐值，用于加密或哈希计算不能为空")
    private String salt;

    @Schema(description = "代币代理，JSON格式")
    private String tokenAgents;

    @Schema(description = "声明主题，JSON格式")
    private String claimTopics;

    @Schema(description = "发行者，JSON格式")
    private String issuers;

    @Schema(description = "发行者声明，JSON格式")
    private String issuerClaims;

    @Schema(description = "部署信息，JSON格式")
    private String deploymentInfo;

    @Schema(description = "错误信息，若有")
    private String errorMessage;

}