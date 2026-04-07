package cc.bamboo.module.project.service.spiderpool.dto;

import lombok.Data;

/**
 * SpiderPool API 基础请求参数
 * 包含所有接口都需要的认证信息
 *
 * @author Swolf
 */
@Data
public class SpiderPoolBaseReqDTO {

    /**
     * SpiderPool AccessKey
     */
    private String accessKey;

    /**
     * RSA私钥 (用于签名)
     */
    private String privateKey;

    /**
     * 币种
     */
    private String coin;

}
