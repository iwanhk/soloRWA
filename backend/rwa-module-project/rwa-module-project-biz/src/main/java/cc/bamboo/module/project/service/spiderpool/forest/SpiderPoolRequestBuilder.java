package cc.bamboo.module.project.service.spiderpool.forest;

import cc.bamboo.module.project.service.spiderpool.dto.SpiderPoolBaseReqDTO;
import cc.bamboo.module.project.service.spiderpool.dto.SpiderPoolDayProfitReqDTO;
import cc.bamboo.module.project.service.spiderpool.dto.SpiderPoolRealHashRateReqDTO;
import cc.bamboo.module.project.util.RSACoder;
import cn.hutool.json.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * SpiderPool 请求签名工具
 * 负责构建带签名的请求体
 *
 * @author Swolf
 */
@Component("spiderPoolRequestBuilder")
@Slf4j
public class SpiderPoolRequestBuilder {

    /**
     * 构建带签名的请求
     *
     * @param dataJsonObj 业务数据JSON对象
     * @param baseReq     基础请求参数 (包含accessKey, privateKey)
     * @return 完整的请求体
     */
    public SpiderPoolForestRequest buildRequest(JSONObject dataJsonObj, SpiderPoolBaseReqDTO baseReq) {
        try {
            String dataJson = dataJsonObj.toString();
            long timestamp = System.currentTimeMillis();

            // 签名格式: dataJson + "|" + timestamp
            String signData = dataJson + "|" + timestamp;
            String sign = RSACoder.sign(signData.getBytes("UTF-8"), baseReq.getPrivateKey());

            log.debug("[SpiderPool] 构建请求，dataJson: {}, timestamp: {}", dataJson, timestamp);

            return SpiderPoolForestRequest.builder()
                    .dataJson(dataJson)
                    .accessKey(baseReq.getAccessKey())
                    .timestamp(timestamp)
                    .sign(sign)
                    .build();

        } catch (Exception e) {
            log.error("[SpiderPool] 构建请求失败: {}", e.getMessage(), e);
            throw new RuntimeException("SpiderPool请求签名失败: " + e.getMessage(), e);
        }
    }

    /**
     * 构建日收益查询请求
     *
     * @param reqDTO 日收益请求参数
     * @return 请求体
     */
    public SpiderPoolForestRequest buildDayProfitRequest(SpiderPoolDayProfitReqDTO reqDTO) {
        JSONObject dataJsonObj = new JSONObject();
        dataJsonObj.set("coin", reqDTO.getCoin());
        dataJsonObj.set("subaccount", reqDTO.getSubaccount());
        dataJsonObj.set("timeStamp", reqDTO.getTimeStamp());
        return buildRequest(dataJsonObj, reqDTO);
    }

    /**
     * 构建子账号实时算力查询请求
     *
     * @param reqDTO 实时算力请求参数
     * @return 请求体
     */
    public SpiderPoolForestRequest buildRealHashRateRequest(SpiderPoolRealHashRateReqDTO reqDTO) {
        JSONObject dataJsonObj = new JSONObject();
        dataJsonObj.set("coin", reqDTO.getCoin());
        dataJsonObj.set("subaccount", reqDTO.getSubaccount());
        return buildRequest(dataJsonObj, reqDTO);
    }

}
