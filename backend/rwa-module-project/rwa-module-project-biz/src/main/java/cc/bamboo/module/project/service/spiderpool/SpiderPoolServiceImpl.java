package cc.bamboo.module.project.service.spiderpool;

import cc.bamboo.framework.redis.service.RedisService;
import cc.bamboo.module.project.service.spiderpool.dto.*;
import cc.bamboo.module.project.service.spiderpool.forest.*;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

import static cc.bamboo.module.project.enums.ApiConstants.COIN_RATE_KEY;

/**
 * SpiderPool 矿池API服务实现类
 * 使用Forest声明式HTTP客户端
 *
 * @author Swolf
 */
@Service
@Slf4j
public class SpiderPoolServiceImpl implements SpiderPoolService {

    @Resource
    private SpiderPoolForestClient spiderPoolClient;

    @Resource(name = "spiderPoolRequestBuilder")
    private SpiderPoolRequestBuilder requestBuilder;

    @Resource
    private BinanceForestClient binanceClient;

    @Resource
    private RedisService redisService;

    @Override
    public SpiderPoolDayProfitRespDTO getDayProfitDetailInfo(SpiderPoolDayProfitReqDTO reqDTO) {
        log.info("[SpiderPool] 查询子账号日收益，币种: {}, 子账号: {}, 时间戳: {}",
                reqDTO.getCoin(), reqDTO.getSubaccount(), reqDTO.getTimeStamp());

        try {
            // 1. 构建请求
            SpiderPoolForestRequest request = requestBuilder.buildDayProfitRequest(reqDTO);

            // 2. 调用API
            SpiderPoolForestResponse response = spiderPoolClient.getDayProfitDetailInfo(request);

            // 3. 检查响应 (code="SUCCESS" 才是真正成功)
            if (!response.isSuccess()) {
                log.error("[SpiderPool] API请求失败，code: {}, msg: {}", response.getCode(), response.getMsg());
                throw new RuntimeException("SpiderPool API请求失败: " + response.getCode() + " - " + response.getMsg());
            }

            // 4. 解析data
            JSONObject data = parseData(response.getData());
            if (data == null) {
                log.warn("[SpiderPool] 响应data为空");
                return null;
            }

            // 5. 转换为DTO
            SpiderPoolDayProfitRespDTO result = new SpiderPoolDayProfitRespDTO();
            result.setDay(data.getLong("day"));
            result.setCoin(data.getStr("coin"));
            result.setUserName(data.getStr("userName"));
            result.setAvgShareAccept(data.getBigDecimal("avgShareAccept"));
            result.setDayProfit(data.getBigDecimal("dayProfit"));
            result.setPpsDayProfit(data.getBigDecimal("ppsDayProfit"));
            result.setPplnsDayProfit(data.getBigDecimal("pplnsDayProfit"));
            result.setDifficult(data.getStr("difficult"));

            log.info("[SpiderPool] 查询成功，子账号: {}, 日收益: {}", reqDTO.getSubaccount(), result.getDayProfit());

            return result;

        } catch (Exception e) {
            log.error("[SpiderPool] 查询子账号日收益失败，币种: {}, 子账号: {}, 错误: {}",
                    reqDTO.getCoin(), reqDTO.getSubaccount(), e.getMessage(), e);
            throw new RuntimeException("SpiderPool API调用失败: " + e.getMessage(), e);
        }
    }

    @Override
    public SpiderPoolRealHashRateRespDTO getSubaccountRealHashRate(SpiderPoolRealHashRateReqDTO reqDTO) {
        log.info("[SpiderPool] 查询子账号实时算力，币种: {}, 子账号: {}", reqDTO.getCoin(), reqDTO.getSubaccount());

        try {
            // 1. 构建请求
            SpiderPoolForestRequest request = requestBuilder.buildRealHashRateRequest(reqDTO);

            // 2. 调用API
            SpiderPoolForestResponse response = spiderPoolClient.getSubaccountRealHashRate(request);

            // 3. 检查响应
            if (!response.isSuccess()) {
                log.error("[SpiderPool] API请求失败，code: {}, msg: {}", response.getCode(), response.getMsg());
                throw new RuntimeException("SpiderPool API请求失败: " + response.getCode() + " - " + response.getMsg());
            }

            // 4. 解析data
            JSONObject data = parseData(response.getData());
            if (data == null) {
                log.warn("[SpiderPool] 响应data为空");
                return null;
            }

            // 5. 转换为DTO
            SpiderPoolRealHashRateRespDTO result = new SpiderPoolRealHashRateRespDTO();
            result.setSubaccount(data.getStr("subaccount"));
            result.setHashRate(data.getBigDecimal("hashRate"));
            result.setStaleRate(data.getBigDecimal("staleRate"));
            result.setRejectRate(data.getBigDecimal("rejectRate"));
            result.setSecondTimestamp(data.getLong("secondTimestamp"));
            result.setLastShareTime(data.getLong("lastShareTime"));

            log.info("[SpiderPool] 查询成功，子账号: {}, 算力: {} H/s", reqDTO.getSubaccount(), result.getHashRate());

            return result;

        } catch (Exception e) {
            log.error("[SpiderPool] 查询子账号实时算力失败，币种: {}, 子账号: {}, 错误: {}",
                    reqDTO.getCoin(), reqDTO.getSubaccount(), e.getMessage(), e);
            throw new RuntimeException("SpiderPool API调用失败: " + e.getMessage(), e);
        }
    }

    /**
     * 将响应data转换为JSONObject
     */
    private JSONObject parseData(Object dataObj) {
        if (dataObj == null) {
            return null;
        }
        if (dataObj instanceof JSONObject) {
            return (JSONObject) dataObj;
        }
        // 其他类型尝试转换
        return JSONUtil.parseObj(JSONUtil.toJsonStr(dataObj));
    }

    @Override
    public BigDecimal getBtcRate() {
        String btcRate = redisService.getCacheObject("BtcRate");
        if(StringUtils.isNotBlank(btcRate)){
            return new BigDecimal(btcRate);
        }
        log.info("[Binance] 查询BTC/USDT实时汇率");
        try {
            BinanceTickerPriceRespDTO tickerPrice = binanceClient
                    .getTickerPrice("BTCUSDT");
            if (tickerPrice == null || tickerPrice.getPrice() == null) {
                log.warn("[Binance] 查询汇率返回为空");
                return null;
            }
            log.info("[Binance] 查询成功，BTC/USDT: {}", tickerPrice.getPrice());
            //缓存
            redisService.setCacheObject("BtcRate", tickerPrice.getPrice().toString(),5L, TimeUnit.MINUTES);
            return tickerPrice.getPrice();
        } catch (Exception e) {
            log.error("[Binance] 查询汇率失败: {}", e.getMessage(), e);
            //throw new RuntimeException("Binance API调用失败: " + e.getMessage(), e);
            return BigDecimal.ZERO;
        }
    }

    @Override
    public BigDecimal getCoinRate(String coin) {
        String key = String.format(COIN_RATE_KEY, coin);
        String rate = redisService.getCacheObject(key);
        if(StringUtils.isNotBlank(rate)){
            return new BigDecimal(rate);
        }
        log.info("[Binance] 查询{}实时汇率", coin);
        try {
            BinanceTickerPriceRespDTO tickerPrice = binanceClient
                    .getTickerPrice(coin);
            if (tickerPrice == null || tickerPrice.getPrice() == null) {
                log.warn("[Binance] 查询{}汇率返回为空", coin);
                return null;
            }
            log.info("[Binance] 查询成功，{}: {}",coin, tickerPrice.getPrice());
            //缓存
            redisService.setCacheObject(key, tickerPrice.getPrice().toString(),5L, TimeUnit.MINUTES);
            return tickerPrice.getPrice();
        } catch (Exception e) {
            log.error("[Binance] 查询汇率失败{}: {}",coin, e.getMessage(), e);
            //throw new RuntimeException("Binance API调用失败: " + e.getMessage(), e);
            return BigDecimal.ZERO;
        }
    }

    @Override
    public BigDecimal getUSDTUSDRate() {
        String coin = "USDTUSD";
        String key = String.format(COIN_RATE_KEY, coin);
        String rate = redisService.getCacheObject(key);
        if(StringUtils.isNotBlank(rate)){
            return new BigDecimal(rate);
        }
        log.info("[Binance] 查询{}实时汇率", coin);
        try {
            BinanceTickerPriceRespDTO tickerPrice = binanceClient
                    .getTickerPrice(coin);
            if (tickerPrice == null || tickerPrice.getPrice() == null) {
                log.warn("[Binance] 查询{}汇率返回为空", coin);
                return null;
            }
            log.info("[Binance] 查询成功，{}: {}",coin, tickerPrice.getPrice());
            //缓存
            redisService.setCacheObject(key, tickerPrice.getPrice().toString(),5L, TimeUnit.MINUTES);
            return tickerPrice.getPrice();
        } catch (Exception e) {
            log.error("[Binance] 查询汇率失败{}: {}",coin, e.getMessage(), e);
            //失败就默认为1
            return BigDecimal.ONE;
        }
    }

}
