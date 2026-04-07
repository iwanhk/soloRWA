package cc.bamboo.module.project.client;

import com.dtflys.forest.annotation.Get;
import com.dtflys.forest.annotation.HTTPProxy;
import com.dtflys.forest.annotation.Query;

import java.util.Map;

/**
 * CoinGecko API 客户端
 *
 * @author Swolf
 */
//@HTTPProxy(host = "127.0.0.1", port = "7890")
public interface CoinGeckoClient {

    /**
     * 获取USDT汇率
     * 
     * 配置说明:
     * - 超时时间通过全局配置设置
     * - 重试机制通过拦截器实现
     *
     * @param ids 币种ID (tether)
     * @param vsCurrencies 目标货币 (usd,cny,hkd)
     * @return 汇率数据
     */
    @Get("https://api.coingecko.com/api/v3/simple/price")
    Map<String, Object> getPrice(@Query("ids") String ids, 
                                  @Query("vs_currencies") String vsCurrencies);

}
