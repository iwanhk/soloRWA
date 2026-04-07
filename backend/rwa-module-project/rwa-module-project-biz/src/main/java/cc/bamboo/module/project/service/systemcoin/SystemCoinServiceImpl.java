package cc.bamboo.module.project.service.systemcoin;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import cc.bamboo.module.project.controller.admin.systemcoin.vo.*;
import cc.bamboo.module.project.dal.dataobject.systemcoin.SystemCoinDO;
import cc.bamboo.framework.common.pojo.PageResult;
import cc.bamboo.framework.common.pojo.PageParam;
import cc.bamboo.framework.common.util.object.BeanUtils;

import cc.bamboo.module.project.dal.mysql.systemcoin.SystemCoinMapper;

import static cc.bamboo.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cc.bamboo.module.project.enums.ErrorCodeConstants.*;

/**
 * 币种管理 Service 实现类
 *
 * @author swolf
 */
@Service
@Validated
public class SystemCoinServiceImpl implements SystemCoinService {

    @Resource
    private SystemCoinMapper systemCoinMapper;

    @Override
    public Long createSystemCoin(SystemCoinSaveReqVO createReqVO) {
        // 插入
        SystemCoinDO systemCoin = BeanUtils.toBean(createReqVO, SystemCoinDO.class);
        systemCoinMapper.insert(systemCoin);
        // 返回
        return systemCoin.getId();
    }

    @Override
    public void updateSystemCoin(SystemCoinSaveReqVO updateReqVO) {
        // 校验存在
        validateSystemCoinExists(updateReqVO.getId());
        // 更新
        SystemCoinDO updateObj = BeanUtils.toBean(updateReqVO, SystemCoinDO.class);
        systemCoinMapper.updateById(updateObj);
    }

    @Override
    public void deleteSystemCoin(Long id) {
        // 校验存在
        validateSystemCoinExists(id);
        // 删除
        systemCoinMapper.deleteById(id);
    }

    private void validateSystemCoinExists(Long id) {
        if (systemCoinMapper.selectById(id) == null) {
            throw exception(SYSTEM_COIN_NOT_EXISTS);
        }
    }

    @Override
    public SystemCoinDO getSystemCoin(Long id) {
        return systemCoinMapper.selectById(id);
    }

    @Override
    public PageResult<SystemCoinDO> getSystemCoinPage(SystemCoinPageReqVO pageReqVO) {
        return systemCoinMapper.selectPage(pageReqVO);
    }

    @Override
    public List<SystemCoinDO> getSystemCoinListByType(Integer coinType) {
        return systemCoinMapper
                .selectList(new cc.bamboo.framework.mybatis.core.query.LambdaQueryWrapperX<SystemCoinDO>()
                        .eqIfPresent(SystemCoinDO::getCoinType, coinType)
                        .eq(SystemCoinDO::getStatus, 1) // 只返回启用的
                        .orderByAsc(SystemCoinDO::getSort));
    }

}