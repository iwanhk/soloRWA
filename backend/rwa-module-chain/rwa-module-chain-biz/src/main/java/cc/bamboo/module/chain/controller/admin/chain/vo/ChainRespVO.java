package cc.bamboo.module.chain.controller.admin.chain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 区块链信息 Response VO")
@Data
public class ChainRespVO {

    @Schema(description = "主键ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "链名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "Ethereum")
    private String name;

    @Schema(description = "链ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer chainId;

    @Schema(description = "RPC URL", requiredMode = Schema.RequiredMode.REQUIRED, example = "https://mainnet.infura.io/v3/...")
    private String rpcUrl;

    @Schema(description = "浏览器 URL", example = "https://etherscan.io")
    private String browserUrl;

    @Schema(description = "状态：0-开启，1-关闭", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    private Integer status;

    @Schema(description = "排序", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    private Integer sort;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}
