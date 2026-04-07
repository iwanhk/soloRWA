package cc.bamboo.module.project.service.spiderpool.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 获取子账号日收益 请求参数
 * POST /v2/sp/subaccount/getDayProfitDetailInfo
 *
 * @author Swolf
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SpiderPoolDayProfitReqDTO extends SpiderPoolBaseReqDTO {

    /**
     * 子账号名称
     */
    private String subaccount;

    /**
     * 秒级时间戳 (UTC+0)
     */
    private Long timeStamp;

}
