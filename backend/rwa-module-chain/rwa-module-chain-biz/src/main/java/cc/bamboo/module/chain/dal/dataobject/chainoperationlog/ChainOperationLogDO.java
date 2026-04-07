package cc.bamboo.module.chain.dal.dataobject.chainoperationlog;

import cc.bamboo.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 链上操作日志 DO
 *
 * @author Swolf
 */
@TableName("biz_chain_operation_log")
@KeySequence("biz_chain_operation_log_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChainOperationLogDO extends BaseDO {

    /**
     * ID
     */
    @TableId
    private Long id;

    /**
     * 操作类型 (如: DIVIDEND_RECORD, TOKEN_TRANSFER, etc.)
     */
    private String operationType;

    /**
     * 业务ID (如: 项目ID, 订单ID等)
     */
    private Long businessId;

    /**
     * 业务数据 (JSON格式)
     */
    private String businessData;

    /**
     * 收益日期
     */
    private java.time.LocalDate revenueDate;

    /**
     * 合约地址
     */
    private String contractAddress;

    /**
     * 交易哈希
     */
    private String transactionHash;

    /**
     * 区块号
     */
    private Long blockNumber;

    /**
     * 操作状态: 0-待处理, 1-成功, 2-失败
     */
    private Integer status;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 操作人地址
     */
    private String operatorAddress;

    /**
     * Gas 使用量
     */
    private Long gasUsed;

    /**
     * 备注
     */
    private String remark;
}
