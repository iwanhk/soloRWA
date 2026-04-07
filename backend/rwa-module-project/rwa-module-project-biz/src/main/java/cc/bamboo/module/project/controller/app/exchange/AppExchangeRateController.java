package cc.bamboo.module.project.controller.app.exchange;

import cc.bamboo.framework.common.pojo.CommonResult;
import cc.bamboo.framework.tenant.core.aop.TenantIgnore;
import cc.bamboo.module.project.controller.app.exchange.vo.ExchangeRateRespVO;
import cc.bamboo.module.project.controller.app.exchange.vo.FiatExchangeRateRespVO;
import cc.bamboo.module.project.service.exchange.ExchangeRateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;

import static cc.bamboo.framework.common.pojo.CommonResult.success;

/**
 * APP - 汇率接口
 *
 * @author Swolf
 */
@Tag(name = "APP - 汇率")
@RestController
@RequestMapping("/project/exchange-rate")
@Validated
public class AppExchangeRateController {

    @Resource
    private ExchangeRateService exchangeRateService;

    @GetMapping("/usdt")
    @Operation(summary = "获取USDT汇率")
    @TenantIgnore
    @PermitAll
    public CommonResult<ExchangeRateRespVO> getUsdtExchangeRate() {
        ExchangeRateRespVO rate = exchangeRateService.getUsdtExchangeRate();
        return success(rate);
    }

    @GetMapping("/fiat")
    @Operation(summary = "获取法币汇率(USD/CNY/HKD)")
    @TenantIgnore
    @PermitAll
    public CommonResult<FiatExchangeRateRespVO> getFiatExchangeRates() {
        FiatExchangeRateRespVO rates = exchangeRateService.getFiatExchangeRates();
        return success(rates);
    }

}
