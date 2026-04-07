package cc.bamboo.module.chain.service.systeminitialization;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import cc.bamboo.module.chain.controller.admin.systeminitialization.vo.*;
import cc.bamboo.module.chain.dal.dataobject.systeminitialization.SystemInitializationDO;
import cc.bamboo.framework.common.pojo.PageResult;
import cc.bamboo.framework.common.pojo.PageParam;
import cc.bamboo.framework.common.util.object.BeanUtils;

import cc.bamboo.module.chain.dal.mysql.systeminitialization.SystemInitializationMapper;

import static cc.bamboo.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cc.bamboo.module.chain.enums.ErrorCodeConstants.*;

/**
 * 系统初始化 Service 实现类
 *
 * @author Swolf
 */
@Service
@Validated
public class SystemInitializationServiceImpl implements SystemInitializationService {

    @Resource
    private SystemInitializationMapper systemInitializationMapper;

    @Override
    public Long createSystemInitialization(SystemInitializationSaveReqVO createReqVO) {
        // 插入
        SystemInitializationDO systemInitialization = BeanUtils.toBean(createReqVO, SystemInitializationDO.class);
        systemInitializationMapper.insert(systemInitialization);
        // 返回
        return systemInitialization.getId();
    }

    @Override
    public void updateSystemInitialization(SystemInitializationSaveReqVO updateReqVO) {
        // 校验存在
        validateSystemInitializationExists(updateReqVO.getId());
        // 更新
        SystemInitializationDO updateObj = BeanUtils.toBean(updateReqVO, SystemInitializationDO.class);
        systemInitializationMapper.updateById(updateObj);
    }

    @Override
    public void deleteSystemInitialization(Long id) {
        // 校验存在
        validateSystemInitializationExists(id);
        // 删除
        systemInitializationMapper.deleteById(id);
    }

    private void validateSystemInitializationExists(Long id) {
        if (systemInitializationMapper.selectById(id) == null) {
            throw exception(SYSTEM_INITIALIZATION_NOT_EXISTS);
        }
    }

    @Override
    public SystemInitializationDO getSystemInitialization(Long id) {
        return systemInitializationMapper.selectById(id);
    }

    @Override
    public PageResult<SystemInitializationDO> getSystemInitializationPage(SystemInitializationPageReqVO pageReqVO) {
        return systemInitializationMapper.selectPage(pageReqVO);
    }

}