package cc.bamboo.module.project.service.exchange;

import cc.bamboo.module.project.client.CoinGeckoClient;
import cc.bamboo.module.project.client.ExchangeRateApiClient;
import cc.bamboo.module.project.controller.app.exchange.vo.ExchangeRateRespVO;
import cc.bamboo.module.project.controller.app.exchange.vo.FiatExchangeRateRespVO;
import com.dtflys.forest.Forest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

/**
 * 汇率服务实现类
 *
 * @author Swolf
 */
@Service
@Validated
@Slf4j
public class ExchangeRateServiceImpl implements ExchangeRateService {

    /**
     * 获取USDT汇率
     * 
     * 使用Spring Cache缓存,缓存时间5分钟
     * 缓存key格式: exchangeRate#5m:usdt
     * - exchangeRate: 缓存名称
     * - 5m: 5分钟过期时间
     * - usdt: 具体的key
     */
    @Override
    @Cacheable(value = "exchangeRate#5m", key = "'usdt'")
    public ExchangeRateRespVO getUsdtExchangeRate() {
        log.info("[getUsdtExchangeRate] 开始获取USDT汇率(未命中缓存)");

        try {
            // 创建Forest客户端实例
            CoinGeckoClient client = Forest.client(CoinGeckoClient.class);
            // Map<String, Object> response = client.getPrice("tether", "usd,cny,hkd");
            // 调用API获取汇率(带超时和重试机制)
             Map<String, Object> response = client.getPrice("tether", "usd,cny,hkd,btc");


              log.info("[getUsdtExchangeRate] API响应: {}", response);

              // 解析响应数据
              // 响应格式: {"tether":{"usd":0.999322,"cny":7.02,"hkd":7.77}}

              @SuppressWarnings("unchecked")
              Map<String, Object> tetherData = (Map<String, Object>)
              response.get("tether");;

              if (tetherData == null) {
              log.error("[getUsdtExchangeRate] 响应数据中没有tether字段");
              throw new RuntimeException("获取汇率失败: 响应数据格式错误");
              }

              // 提取汇率数据
              BigDecimal usd = convertToBigDecimal(tetherData.get("usd"));
            //BigDecimal usd = BigDecimal.ONE;
              BigDecimal cny = convertToBigDecimal(tetherData.get("cny"));
              BigDecimal hkd = convertToBigDecimal(tetherData.get("hkd"));
              BigDecimal btc = convertToBigDecimal(tetherData.get("btc"));

              ExchangeRateRespVO result = ExchangeRateRespVO.builder()
              .usd(usd)
              .cny(cny)
              .hkd(hkd)
              .btc(btc)
              .build();
           /* ExchangeRateRespVO result = ExchangeRateRespVO.builder()
                    .usd(new BigDecimal("0.999322"))
                    .cny(new BigDecimal("6.999322"))
                    .hkd(new BigDecimal("7.999322"))
                    .build();*/

            return result;

        } catch (Exception e) {
            log.error("[getUsdtExchangeRate] 获取USDT汇率失败", e);
            throw new RuntimeException("获取USDT汇率失败: " + e.getMessage(), e);
        }
    }

    /**
     * 获取法币汇率(USD/CNY/HKD)
     * 
     * 使用Spring Cache缓存,缓存时间5分钟
     * 使用 exchangerate-api.com 获取专业的法币汇率
     */
    @Override
    @Cacheable(value = "exchangeRate#5m", key = "'fiat'")
    public FiatExchangeRateRespVO getFiatExchangeRates() {
        log.info("[getFiatExchangeRates] 开始获取法币汇率(未命中缓存)");

        try {
            // 创建Forest客户端实例
            ExchangeRateApiClient client = Forest.client(ExchangeRateApiClient.class);

            // 调用API获取以USD为基准的汇率
            Map<String, Object> response = client.getLatestRates();

            log.info("[getFiatExchangeRates] API响应: {}", response);

            // 检查响应状态
            String result = (String) response.get("result");
            if (!"success".equals(result)) {
                log.error("[getFiatExchangeRates] API返回失败状态: {}", result);
                throw new RuntimeException("获取汇率失败: API返回状态异常");
            }

            // 解析汇率数据
            // 响应格式:
            // {"result":"success","base_code":"USD","rates":{"CNY":7.23,"HKD":7.82,...}}
            @SuppressWarnings("unchecked")
            Map<String, Object> rates = (Map<String, Object>) response.get("rates");

            if (rates == null) {
                log.error("[getFiatExchangeRates] 响应数据中没有rates字段");
                throw new RuntimeException("获取汇率失败: 响应数据格式错误");
            }

            // 提取CNY和HKD汇率 (1 USD = ? CNY/HKD)
            BigDecimal cny = convertToBigDecimal(rates.get("CNY"));
            BigDecimal hkd = convertToBigDecimal(rates.get("HKD"));

            FiatExchangeRateRespVO fiatResult = FiatExchangeRateRespVO.builder()
                    .cny(cny)
                    .hkd(hkd)
                    .build();

            log.info("[getFiatExchangeRates] 法币汇率获取完成: CNY={}, HKD={}", cny, hkd);
            return fiatResult;

        } catch (Exception e) {
            log.error("[getFiatExchangeRates] 获取法币汇率失败", e);
            throw new RuntimeException("获取法币汇率失败: " + e.getMessage(), e);
        }
    }

    /**
     * 转换为BigDecimal
     */
    private BigDecimal convertToBigDecimal(Object value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        if (value instanceof Number) {
            return new BigDecimal(value.toString());
        }
        return new BigDecimal(value.toString());
    }

}
