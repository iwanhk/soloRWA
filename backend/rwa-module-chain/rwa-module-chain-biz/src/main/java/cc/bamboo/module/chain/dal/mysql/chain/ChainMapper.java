package cc.bamboo.module.chain.dal.mysql.chain;

import java.util.*;

import cc.bamboo.framework.common.pojo.PageResult;
import cc.bamboo.framework.mybatis.core.query.LambdaQueryWrapperX;
import cc.bamboo.framework.mybatis.core.mapper.BaseMapperX;
import cc.bamboo.module.chain.dal.dataobject.chain.ChainDO;
import org.apache.ibatis.annotations.Mapper;
import cc.bamboo.module.chain.controller.admin.chain.vo.*;

/**
 * 区块链信息 Mapper
 *
 * @author Trae
 */
@Mapper
public interface ChainMapper extends BaseMapperX<ChainDO> {

    default PageResult<ChainDO> selectPage(ChainPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ChainDO>()
                .likeIfPresent(ChainDO::getName, reqVO.getName())
                .eqIfPresent(ChainDO::getChainId, reqVO.getChainId())
                .eqIfPresent(ChainDO::getStatus, reqVO.getStatus())
                .orderByAsc(ChainDO::getSort));
    }

    default ChainDO selectByChainId(Integer chainId) {
        return selectOne(ChainDO::getChainId, chainId);
    }

}
