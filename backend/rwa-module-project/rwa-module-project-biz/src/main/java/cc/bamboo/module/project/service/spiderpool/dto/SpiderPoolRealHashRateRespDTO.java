package cc.bamboo.module.project.service.spiderpool.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * SpiderPool 子账号实时算力 响应DTO
 *
 * @author Swolf
 */
@Data
public class SpiderPoolRealHashRateRespDTO {

    /**
     * 子账号名称
     */
    private String subaccount;

    /**
     * 算力 (H/s)
     */
    private BigDecimal hashRate;

    /**
     * 延迟率
     */
    private BigDecimal staleRate;

    /**
     * 拒绝率
     */
    private BigDecimal rejectRate;

    /**
     * 秒级时间戳
     */
    private Long secondTimestamp;

    /**
     * 最后提交份额时间
     */
    private Long lastShareTime;

}
