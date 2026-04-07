package cc.bamboo.module.project.service.spiderpool.forest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SpiderPool API 统一请求体
 *
 * @author Swolf
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpiderPoolForestRequest {

    /**
     * 业务数据JSON字符串
     */
    private String dataJson;

    /**
     * AccessKey
     */
    private String accessKey;

    /**
     * 时间戳 (毫秒)
     */
    private Long timestamp;

    /**
     * RSA签名
     */
    private String sign;

}
