package cc.bamboo.module.chain.controller.admin.chain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import javax.validation.constraints.*;

@Schema(description = "管理后台 - 区块链信息新增/修改 Request VO")
@Data
public class ChainSaveReqVO {

    @Schema(description = "主键ID", example = "1024")
    private Long id;

    @Schema(description = "链名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "Ethereum")
    @NotEmpty(message = "链名称不能为空")
    private String name;

    @Schema(description = "链ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "链ID不能为空")
    private Integer chainId;

    @Schema(description = "RPC URL", requiredMode = Schema.RequiredMode.REQUIRED, example = "https://mainnet.infura.io/v3/...")
    @NotEmpty(message = "RPC URL不能为空")
    private String rpcUrl;

    @Schema(description = "浏览器 URL", example = "https://etherscan.io")
    private String browserUrl;

    @Schema(description = "状态：0-开启，1-关闭", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    @NotNull(message = "状态不能为空")
    private Integer status;

    @Schema(description = "排序", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    @NotNull(message = "排序不能为空")
    private Integer sort;

}
