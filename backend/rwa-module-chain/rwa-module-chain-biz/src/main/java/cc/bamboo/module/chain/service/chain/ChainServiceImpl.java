package cc.bamboo.module.chain.service.chain;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import cc.bamboo.module.chain.controller.admin.chain.vo.*;
import cc.bamboo.module.chain.dal.dataobject.chain.ChainDO;
import cc.bamboo.framework.common.pojo.PageResult;
import cc.bamboo.framework.common.util.object.BeanUtils;

import cc.bamboo.module.chain.dal.mysql.chain.ChainMapper;

import static cc.bamboo.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cc.bamboo.module.chain.enums.ErrorCodeConstants.*;

/**
 * 区块链信息 Service 实现类
 *
 * @author Trae
 */
@Service
@Validated
public class ChainServiceImpl implements ChainService {

    @Resource
    private ChainMapper chainMapper;

    @Override
    public Long createChain(ChainSaveReqVO createReqVO) {
        // 插入
        ChainDO chain = BeanUtils.toBean(createReqVO, ChainDO.class);
        chainMapper.insert(chain);
        // 返回
        return chain.getId();
    }

    @Override
    public void updateChain(ChainSaveReqVO updateReqVO) {
        // 校验存在
        validateChainExists(updateReqVO.getId());
        // 更新
        ChainDO updateObj = BeanUtils.toBean(updateReqVO, ChainDO.class);
        chainMapper.updateById(updateObj);
    }

    @Override
    public void deleteChain(Long id) {
        // 校验存在
        validateChainExists(id);
        // 删除
        chainMapper.deleteById(id);
    }

    private void validateChainExists(Long id) {
        if (chainMapper.selectById(id) == null) {
            throw exception(CHAIN_NOT_EXISTS);
        }
    }

    @Override
    public ChainDO getChain(Long id) {
        return chainMapper.selectById(id);
    }

    @Override
    public PageResult<ChainDO> getChainPage(ChainPageReqVO pageReqVO) {
        return chainMapper.selectPage(pageReqVO);
    }

    @Override
    public List<ChainDO> getActiveChainList() {
        return chainMapper.selectList(ChainDO::getStatus, 0); // 0-开启
    }

}
