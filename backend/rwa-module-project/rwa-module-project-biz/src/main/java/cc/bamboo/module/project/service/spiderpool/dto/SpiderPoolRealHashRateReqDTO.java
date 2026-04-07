package cc.bamboo.module.project.service.spiderpool.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 获取子账号实时算力 请求参数
 * POST /v2/sp/hashrate/subaccount/realHashRate
 *
 * @author Swolf
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SpiderPoolRealHashRateReqDTO extends SpiderPoolBaseReqDTO {

    /**
     * 子账号名称
     */
    private String subaccount;

}
