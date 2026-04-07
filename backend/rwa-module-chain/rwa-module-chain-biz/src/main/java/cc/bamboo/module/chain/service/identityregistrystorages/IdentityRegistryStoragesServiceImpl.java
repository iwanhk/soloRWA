package cc.bamboo.module.chain.service.identityregistrystorages;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import cc.bamboo.module.chain.controller.admin.identityregistrystorages.vo.*;
import cc.bamboo.module.chain.dal.dataobject.identityregistrystorages.IdentityRegistryStoragesDO;
import cc.bamboo.framework.common.pojo.PageResult;
import cc.bamboo.framework.common.pojo.PageParam;
import cc.bamboo.framework.common.util.object.BeanUtils;

import cc.bamboo.module.chain.dal.mysql.identityregistrystorages.IdentityRegistryStoragesMapper;

import static cc.bamboo.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cc.bamboo.module.chain.enums.ErrorCodeConstants.*;

/**
 * 身份注册表存储 Service 实现类
 *
 * @author Swolf
 */
@Service
@Validated
public class IdentityRegistryStoragesServiceImpl implements IdentityRegistryStoragesService {

    @Resource
    private IdentityRegistryStoragesMapper identityRegistryStoragesMapper;

    @Override
    public Long createIdentityRegistryStorages(IdentityRegistryStoragesSaveReqVO createReqVO) {
        // 插入
        IdentityRegistryStoragesDO identityRegistryStorages = BeanUtils.toBean(createReqVO, IdentityRegistryStoragesDO.class);
        identityRegistryStoragesMapper.insert(identityRegistryStorages);
        // 返回
        return identityRegistryStorages.getId();
    }

    @Override
    public void updateIdentityRegistryStorages(IdentityRegistryStoragesSaveReqVO updateReqVO) {
        // 校验存在
        validateIdentityRegistryStoragesExists(updateReqVO.getId());
        // 更新
        IdentityRegistryStoragesDO updateObj = BeanUtils.toBean(updateReqVO, IdentityRegistryStoragesDO.class);
        identityRegistryStoragesMapper.updateById(updateObj);
    }

    @Override
    public void deleteIdentityRegistryStorages(Long id) {
        // 校验存在
        validateIdentityRegistryStoragesExists(id);
        // 删除
        identityRegistryStoragesMapper.deleteById(id);
    }

    private void validateIdentityRegistryStoragesExists(Long id) {
        if (identityRegistryStoragesMapper.selectById(id) == null) {
            throw exception(IDENTITY_REGISTRY_STORAGES_NOT_EXISTS);
        }
    }

    @Override
    public IdentityRegistryStoragesDO getIdentityRegistryStorages(Long id) {
        return identityRegistryStoragesMapper.selectById(id);
    }

    @Override
    public PageResult<IdentityRegistryStoragesDO> getIdentityRegistryStoragesPage(IdentityRegistryStoragesPageReqVO pageReqVO) {
        return identityRegistryStoragesMapper.selectPage(pageReqVO);
    }

}