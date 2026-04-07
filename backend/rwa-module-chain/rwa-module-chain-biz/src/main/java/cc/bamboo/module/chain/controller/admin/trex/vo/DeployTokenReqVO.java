package cc.bamboo.module.chain.controller.admin.trex.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 部署 Token 请求 VO
 * 
 * @author Swolf
 */
@Schema(description = "管理后台 - 部署 Token 请求")
@Data
public class DeployTokenReqVO {
    
    @Schema(description = "盐值，用于确定性部署", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "盐值不能为空")
    private String salt;
    
    @Schema(description = "Token 所有者地址", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "所有者地址不能为空")
    private String ownerAddress;
    
    @Schema(description = "Token 名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Token 名称不能为空")
    private String name;
    
    @Schema(description = "Token 符号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Token 符号不能为空")
    private String symbol;
    
    @Schema(description = "小数位数，默认 18")
    private Integer decimals = 18;
    
    @Schema(description = "Token 代理人列表")
    private List<String> tokenAgents;
    
    @Schema(description = "声明主题列表（bytes32 哈希）")
    private List<String> claimTopics;
    
    @Schema(description = "可信签发者列表（Identity 合约地址）")
    private List<String> issuers;
    
    @Schema(description = "签发者可签发的声明主题")
    private List<List<String>> issuerClaims;
}
