package cc.bamboo.module.project.service.spiderpool.forest;

import cc.bamboo.module.project.service.spiderpool.dto.BinanceTickerPriceRespDTO;
import com.dtflys.forest.annotation.Get;
import com.dtflys.forest.annotation.HTTPProxy;
import com.dtflys.forest.annotation.Query;

/**
 * Binance API 客户端
 *
 * @author Swolf
 */
//@HTTPProxy(host = "127.0.0.1", port = "7890")
public interface BinanceForestClient {

    /**
     * 获取最新价格
     * GET https://api.binance.com/api/v3/ticker/price
     *
     * @param symbol 交易对，如 BTCUSDT
     * @return 价格信息
     */
    @Get(url = "https://api.binance.com/api/v3/ticker/price")
    BinanceTickerPriceRespDTO getTickerPrice(@Query("symbol") String symbol);

}
