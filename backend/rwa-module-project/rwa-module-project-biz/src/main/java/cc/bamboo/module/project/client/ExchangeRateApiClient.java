package cc.bamboo.module.project.client;

import com.dtflys.forest.annotation.Get;

import java.util.Map;

/**
 * ExchangeRate API 客户端
 * 
 * 使用 exchangerate-api.com 提供的免费API
 * 文档: https://www.exchangerate-api.com/docs/free
 *
 * @author Swolf
 */
public interface ExchangeRateApiClient {

    /**
     * 获取最新汇率
     * 
     * 免费API限制:
     * - 每月1500次请求
     * - 每天更新一次汇率
     * - 支持161种货币
     * 
     * 响应格式:
     * {
     * "result": "success",
     * "base_code": "USD",
     * "conversion_rates": {
     * "CNY": 7.2345,
     * "HKD": 7.8234,
     * ...
     * }
     * }
     *
     * @param baseCurrency 基础货币代码 (如 USD)
     * @return 汇率数据
     */
    @Get("https://open.er-api.com/v6/latest/USD")
    Map<String, Object> getLatestRates();

}
