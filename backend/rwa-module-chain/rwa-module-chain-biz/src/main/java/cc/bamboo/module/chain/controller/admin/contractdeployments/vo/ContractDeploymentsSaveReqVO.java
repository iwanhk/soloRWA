package cc.bamboo.module.chain.controller.admin.contractdeployments.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import javax.validation.constraints.*;

@Schema(description = "管理后台 - 合约部署新增/修改 Request VO")
@Data
public class ContractDeploymentsSaveReqVO {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "11213")
    private Long id;

    @Schema(description = "合约类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @NotEmpty(message = "合约类型不能为空")
    private String contractType;

    @Schema(description = "部署地址", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "部署地址不能为空")
    private String deploymentAddress;

    @Schema(description = "部署者地址", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "部署者地址不能为空")
    private String deployerAddress;

    @Schema(description = "部署信息，JSON格式")
    private String deploymentInfo;

    @Schema(description = "交易哈希")
    private String transactionHash;

    @Schema(description = "区块号")
    private Long blockNumber;

    @Schema(description = "状态，如deployed-已部署", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @NotEmpty(message = "状态，如deployed-已部署不能为空")
    private String status;

}