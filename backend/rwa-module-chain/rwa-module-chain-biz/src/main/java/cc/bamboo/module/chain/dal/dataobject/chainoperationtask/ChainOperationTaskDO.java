package cc.bamboo.module.chain.dal.dataobject.chainoperationtask;

import cc.bamboo.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 链操作任务 DO
 *
 * @author Swolf
 */
@TableName("biz_chain_operation_task")
@KeySequence("biz_chain_operation_task_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChainOperationTaskDO extends BaseDO {

    /**
     * 主键ID
     */
    @TableId
    private Long id;

    /**
     * 任务编号（幂等性标识）
     */
    private String taskNo;

    /**
     * 操作类型：1-部署Token，2-添加用户并签发Claim，3-铸造Token
     */
    private Integer operationType;

    /**
     * 项目ID
     */
    private Long projectId;

    /**
     * 项目名称
     */
    private String projectName;

    /**
     * 项目符号
     */
    private String projectSymbol;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户钱包地址
     */
    private String userAddress;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * Token合约地址
     */
    private String tokenAddress;

    /**
     * 铸造数量
     */
    private String mintAmount;

    /**
     * 国家代码
     */
    private Integer countryCode;

    /**
     * Claim Topic
     */
    private String claimTopic;

    /**
     * Claim Data
     */
    private String claimData;

    /**
     * 任务状态：1-待处理，2-处理中，3-成功，4-失败，5-已取消
     */
    private Integer status;

    /**
     * 重试次数
     */
    private Integer retryCount;

    /**
     * 最大重试次数
     */
    private Integer maxRetryCount;

    /**
     * 下次重试时间
     */
    private LocalDateTime nextRetryTime;

    /**
     * 执行步骤详情（JSON格式）
     */
    private String executionSteps;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 完成时间
     */
    private LocalDateTime completedTime;

    private String txHash;
}
