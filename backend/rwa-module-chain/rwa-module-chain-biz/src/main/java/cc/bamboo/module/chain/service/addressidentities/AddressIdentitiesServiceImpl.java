package cc.bamboo.module.chain.service.addressidentities;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import cc.bamboo.module.chain.controller.admin.addressidentities.vo.*;
import cc.bamboo.module.chain.dal.dataobject.addressidentities.AddressIdentitiesDO;
import cc.bamboo.framework.common.pojo.PageResult;
import cc.bamboo.framework.common.pojo.PageParam;
import cc.bamboo.framework.common.util.object.BeanUtils;

import cc.bamboo.module.chain.dal.mysql.addressidentities.AddressIdentitiesMapper;

import static cc.bamboo.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cc.bamboo.module.chain.enums.ErrorCodeConstants.*;

/**
 * 地址身份关联 Service 实现类
 *
 * @author Swolf
 */
@Service
@Validated
public class AddressIdentitiesServiceImpl implements AddressIdentitiesService {

    @Resource
    private AddressIdentitiesMapper addressIdentitiesMapper;

    @Override
    public Long createAddressIdentities(AddressIdentitiesSaveReqVO createReqVO) {
        // 插入
        AddressIdentitiesDO addressIdentities = BeanUtils.toBean(createReqVO, AddressIdentitiesDO.class);
        addressIdentitiesMapper.insert(addressIdentities);
        // 返回
        return addressIdentities.getId();
    }

    @Override
    public void updateAddressIdentities(AddressIdentitiesSaveReqVO updateReqVO) {
        // 校验存在
        validateAddressIdentitiesExists(updateReqVO.getId());
        // 更新
        AddressIdentitiesDO updateObj = BeanUtils.toBean(updateReqVO, AddressIdentitiesDO.class);
        addressIdentitiesMapper.updateById(updateObj);
    }

    @Override
    public void deleteAddressIdentities(Long id) {
        // 校验存在
        validateAddressIdentitiesExists(id);
        // 删除
        addressIdentitiesMapper.deleteById(id);
    }

    private void validateAddressIdentitiesExists(Long id) {
        if (addressIdentitiesMapper.selectById(id) == null) {
            throw exception(ADDRESS_IDENTITIES_NOT_EXISTS);
        }
    }

    @Override
    public AddressIdentitiesDO getAddressIdentities(Long id) {
        return addressIdentitiesMapper.selectById(id);
    }

    @Override
    public PageResult<AddressIdentitiesDO> getAddressIdentitiesPage(AddressIdentitiesPageReqVO pageReqVO) {
        return addressIdentitiesMapper.selectPage(pageReqVO);
    }

}