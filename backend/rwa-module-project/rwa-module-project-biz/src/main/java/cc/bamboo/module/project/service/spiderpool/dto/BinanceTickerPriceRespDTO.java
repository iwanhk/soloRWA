package cc.bamboo.module.project.service.spiderpool.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Binance 价格查询响应
 *
 * @author Swolf
 */
@Data
public class BinanceTickerPriceRespDTO {

    /**
     * 交易对
     */
    private String symbol;

    /**
     * 价格
     */
    private BigDecimal price;

}
