package cc.bamboo.module.project.service.spiderpool.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * SpiderPool 日收益响应 DTO
 *
 * @author Swolf
 */
@Data
public class SpiderPoolDayProfitRespDTO {

    /**
     * 日期 (秒级时间戳)
     */
    private Long day;

    /**
     * 币种
     */
    private String coin;

    /**
     * 子账号名称
     */
    private String userName;

    /**
     * 日均算力 H/s
     */
    private BigDecimal avgShareAccept;

    /**
     * 总收益 (币种单位)
     */
    private BigDecimal dayProfit;

    /**
     * PPS收益 (币种单位)
     */
    private BigDecimal ppsDayProfit;

    /**
     * 手续费收益 (币种单位)
     */
    private BigDecimal pplnsDayProfit;

    /**
     * 难度
     */
    private String difficult;

}
