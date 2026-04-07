package cc.bamboo.module.chain.controller.admin.identity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 添加用户请求 VO
 * 
 * @author Swolf
 */
@Schema(description = "管理后台 - 添加用户请求")
@Data
public class AddUserReqVO {
    
    @Schema(description = "用户钱包地址", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "钱包地址不能为空")
    private String address;
    
    @Schema(description = "要关联的 Token ID")
    private Long tokenId;
    
    @Schema(description = "国家代码（ISO 3166-1 numeric）")
    private Integer countryCode;

    private Long userId;
}
