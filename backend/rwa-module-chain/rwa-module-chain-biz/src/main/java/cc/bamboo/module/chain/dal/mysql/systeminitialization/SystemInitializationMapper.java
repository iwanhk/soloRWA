package cc.bamboo.module.chain.dal.mysql.systeminitialization;

import java.util.*;

import cc.bamboo.framework.common.pojo.PageResult;
import cc.bamboo.framework.mybatis.core.query.LambdaQueryWrapperX;
import cc.bamboo.framework.mybatis.core.mapper.BaseMapperX;
import cc.bamboo.module.chain.dal.dataobject.systeminitialization.SystemInitializationDO;
import org.apache.ibatis.annotations.Mapper;
import cc.bamboo.module.chain.controller.admin.systeminitialization.vo.*;

/**
 * 系统初始化 Mapper
 *
 * @author Swolf
 */
@Mapper
public interface SystemInitializationMapper extends BaseMapperX<SystemInitializationDO> {

    default PageResult<SystemInitializationDO> selectPage(SystemInitializationPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<SystemInitializationDO>()
                .eqIfPresent(SystemInitializationDO::getStep, reqVO.getStep())
                .eqIfPresent(SystemInitializationDO::getStatus, reqVO.getStatus())
                .eqIfPresent(SystemInitializationDO::getContractAddress, reqVO.getContractAddress())
                .eqIfPresent(SystemInitializationDO::getTransactionHash, reqVO.getTransactionHash())
                .eqIfPresent(SystemInitializationDO::getBlockNumber, reqVO.getBlockNumber())
                .eqIfPresent(SystemInitializationDO::getErrorMessage, reqVO.getErrorMessage())
                .eqIfPresent(SystemInitializationDO::getMetadata, reqVO.getMetadata())
                .betweenIfPresent(SystemInitializationDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(SystemInitializationDO::getId));
    }

}