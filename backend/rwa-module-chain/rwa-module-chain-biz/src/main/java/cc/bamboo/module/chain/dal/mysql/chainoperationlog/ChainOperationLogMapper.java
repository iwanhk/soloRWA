package cc.bamboo.module.chain.dal.mysql.chainoperationlog;

import cc.bamboo.framework.mybatis.core.mapper.BaseMapperX;
import cc.bamboo.framework.mybatis.core.query.LambdaQueryWrapperX;
import cc.bamboo.module.chain.dal.dataobject.chainoperationlog.ChainOperationLogDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 链上操作日志 Mapper
 *
 * @author Swolf
 */
@Mapper
public interface ChainOperationLogMapper extends BaseMapperX<ChainOperationLogDO> {

    /**
     * 根据业务ID和操作类型查询日志
     */
    default List<ChainOperationLogDO> selectByBusinessIdAndType(Long businessId, String operationType) {
        return selectList(new LambdaQueryWrapperX<ChainOperationLogDO>()
                .eq(ChainOperationLogDO::getBusinessId, businessId)
                .eq(ChainOperationLogDO::getOperationType, operationType)
                .orderByDesc(ChainOperationLogDO::getCreateTime));
    }

    /**
     * 根据交易哈希查询日志
     */
    default ChainOperationLogDO selectByTransactionHash(String transactionHash) {
        return selectOne(ChainOperationLogDO::getTransactionHash, transactionHash);
    }
}
