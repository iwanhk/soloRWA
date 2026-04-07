package cc.bamboo.module.chain.mq.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 分红记录上链检查消息
 *
 * @author Swolf
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DividendRecordMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 操作日志ID
     */
    private Long logId;

    /**
     * 交易哈希
     */
    private String transactionHash;

    /**
     * 重试次数
     */
    private Integer retryCount;

}
