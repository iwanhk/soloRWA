package cc.bamboo.module.project.service.exchange;

import cc.bamboo.module.project.controller.app.exchange.vo.ExchangeRateRespVO;
import cc.bamboo.module.project.controller.app.exchange.vo.FiatExchangeRateRespVO;

/**
 * 汇率服务接口
 *
 * @author Swolf
 */
public interface ExchangeRateService {

    /**
     * 获取USDT汇率
     *
     * @return USDT汇率(USD, CNY, HKD)
     */
    ExchangeRateRespVO getUsdtExchangeRate();

    /**
     * 获取法币汇率(USD/CNY/HKD)
     *
     * @return 法币之间的相互汇率
     */
    FiatExchangeRateRespVO getFiatExchangeRates();

}
