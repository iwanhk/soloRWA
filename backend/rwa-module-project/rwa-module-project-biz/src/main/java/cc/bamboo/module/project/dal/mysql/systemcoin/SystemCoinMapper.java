package cc.bamboo.module.project.dal.mysql.systemcoin;

import java.util.*;

import cc.bamboo.framework.common.pojo.PageResult;
import cc.bamboo.framework.mybatis.core.query.LambdaQueryWrapperX;
import cc.bamboo.framework.mybatis.core.mapper.BaseMapperX;
import cc.bamboo.module.project.dal.dataobject.systemcoin.SystemCoinDO;
import org.apache.ibatis.annotations.Mapper;
import cc.bamboo.module.project.controller.admin.systemcoin.vo.*;

/**
 * 币种管理 Mapper
 *
 * @author swolf
 */
@Mapper
public interface SystemCoinMapper extends BaseMapperX<SystemCoinDO> {

    default PageResult<SystemCoinDO> selectPage(SystemCoinPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<SystemCoinDO>()
                .eqIfPresent(SystemCoinDO::getCoinCode, reqVO.getCoinCode())
                .likeIfPresent(SystemCoinDO::getCoinName, reqVO.getCoinName())
                .eqIfPresent(SystemCoinDO::getCoinNameEn, reqVO.getCoinNameEn())
                .eqIfPresent(SystemCoinDO::getCoinType, reqVO.getCoinType())
                .eqIfPresent(SystemCoinDO::getSymbol, reqVO.getSymbol())
                .eqIfPresent(SystemCoinDO::getBinanceSymbol, reqVO.getBinanceSymbol())
                .eqIfPresent(SystemCoinDO::getPoolSymbol, reqVO.getPoolSymbol())
                .eqIfPresent(SystemCoinDO::getIconUrl, reqVO.getIconUrl())
                .eqIfPresent(SystemCoinDO::getDecimals, reqVO.getDecimals())
                .eqIfPresent(SystemCoinDO::getSort, reqVO.getSort())
                .eqIfPresent(SystemCoinDO::getStatus, reqVO.getStatus())
                .eqIfPresent(SystemCoinDO::getRemark, reqVO.getRemark())
                .betweenIfPresent(SystemCoinDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(SystemCoinDO::getId));
    }

    default SystemCoinDO getCoinByCode(String coinCode){
        return selectOne(SystemCoinDO::getCoinCode,coinCode);
    }
}