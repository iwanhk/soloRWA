package cc.bamboo.module.project.service.spiderpool;

import cc.bamboo.module.project.service.spiderpool.dto.SpiderPoolDayProfitReqDTO;
import cc.bamboo.module.project.service.spiderpool.dto.SpiderPoolDayProfitRespDTO;
import cc.bamboo.module.project.service.spiderpool.dto.SpiderPoolRealHashRateReqDTO;
import cc.bamboo.module.project.service.spiderpool.dto.SpiderPoolRealHashRateRespDTO;

import java.math.BigDecimal;

/**
 * SpiderPool 矿池API服务接口
 * 用于调用SpiderPool矿池的API获取收益信息
 *
 * @author Swolf
 */
public interface SpiderPoolService {

    /**
     * 获取子账号日收益信息
     *
     * @param reqDTO 请求参数 (包含coin, subaccount, timeStamp, accessKey, privateKey)
     * @return 日收益信息
     */
    SpiderPoolDayProfitRespDTO getDayProfitDetailInfo(SpiderPoolDayProfitReqDTO reqDTO);

    /**
     * 获取子账号实时算力
     *
     * @param reqDTO 请求参数 (包含coin, subaccount, accessKey, privateKey)
     * @return 实时算力信息
     */
    SpiderPoolRealHashRateRespDTO getSubaccountRealHashRate(SpiderPoolRealHashRateReqDTO reqDTO);

    /**
     * 获取BTC/USDT实时汇率
     *
     * @return 汇率
     */
    BigDecimal getBtcRate();

    /**
     * 获取指定币种实时汇率
     *
     * @param coin 币种名称 (例如: "btc", "eth")
     * @author: Hus
     * @date: 2026/2/5 15:29
     * @return: BigDecimal 实时汇率
     * @description
     */
    BigDecimal getCoinRate(String coin);

    /**
     * 获取USDT -USD 汇率
     *
     * @author: Hus
     * @date: 2026/2/5 15:51
     * @return: BigDecimal
     * @description
     */
    BigDecimal getUSDTUSDRate();

}
