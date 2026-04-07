package cc.bamboo.module.user.controller.app.userchain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 可用链信息 Response VO
 */
@Schema(description = "用户 APP - 可用链信息 Response VO")
@Data
public class AppChainRespVO {

    @Schema(description = "主键ID", example = "1")
    private Long id;

    @Schema(description = "链名称", example = "Ethereum")
    private String name;

    @Schema(description = "链ID", example = "1")
    private Integer chainId;

    @Schema(description = "RPC URL", example = "https://mainnet.infura.io")
    private String rpcUrl;

    @Schema(description = "浏览器 URL", example = "https://etherscan.io")
    private String browserUrl;

    @Schema(description = "状态：0-开启，1-关闭", example = "0")
    private Integer status;

    @Schema(description = "排序", example = "1")
    private Integer sort;

}
