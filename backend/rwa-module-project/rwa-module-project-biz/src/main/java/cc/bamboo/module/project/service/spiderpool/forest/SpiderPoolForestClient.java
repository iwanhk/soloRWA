package cc.bamboo.module.project.service.spiderpool.forest;

import com.dtflys.forest.annotation.BaseRequest;
import com.dtflys.forest.annotation.Body;
import com.dtflys.forest.annotation.HTTPProxy;
import com.dtflys.forest.annotation.Post;

/**
 * SpiderPool Forest HTTP客户端
 * 使用声明式接口调用矿池API
 *
 * @author Swolf
 */
@BaseRequest(baseURL = "${spiderpoolApiBaseUrl}", contentType = "application/json")
//@HTTPProxy(host = "127.0.0.1", port = "7890")
public interface SpiderPoolForestClient {

    /**
     * 获取子账号日收益信息
     * POST /v2/sp/subaccount/getDayProfitDetailInfo
     *
     * @param request 请求体 (包含dataJson, accessKey, timestamp, sign)
     * @return API响应
     */
    @Post("/v2/sp/subaccount/getDayProfitDetailInfo")
    SpiderPoolForestResponse getDayProfitDetailInfo(@Body SpiderPoolForestRequest request);

    /**
     * 获取子账号实时算力
     * POST /v2/sp/hashrate/subaccount/realHashRate
     *
     * @param request 请求体
     * @return API响应
     */
    @Post("/v2/sp/hashrate/subaccount/realHashRate")
    SpiderPoolForestResponse getSubaccountRealHashRate(@Body SpiderPoolForestRequest request);

}
