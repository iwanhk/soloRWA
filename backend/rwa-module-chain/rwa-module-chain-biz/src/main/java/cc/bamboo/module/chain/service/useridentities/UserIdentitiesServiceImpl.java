package cc.bamboo.module.chain.service.useridentities;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import cc.bamboo.module.chain.controller.admin.useridentities.vo.*;
import cc.bamboo.module.chain.dal.dataobject.useridentities.UserIdentitiesDO;
import cc.bamboo.framework.common.pojo.PageResult;
import cc.bamboo.framework.common.pojo.PageParam;
import cc.bamboo.framework.common.util.object.BeanUtils;

import cc.bamboo.module.chain.dal.mysql.useridentities.UserIdentitiesMapper;

import static cc.bamboo.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cc.bamboo.module.chain.enums.ErrorCodeConstants.*;

/**
 * 用户身份 Service 实现类
 *
 * @author Swolf
 */
@Service
@Validated
public class UserIdentitiesServiceImpl implements UserIdentitiesService {

    @Resource
    private UserIdentitiesMapper userIdentitiesMapper;

    @Override
    public Long createUserIdentities(UserIdentitiesSaveReqVO createReqVO) {
        // 插入
        UserIdentitiesDO userIdentities = BeanUtils.toBean(createReqVO, UserIdentitiesDO.class);
        userIdentitiesMapper.insert(userIdentities);
        // 返回
        return userIdentities.getId();
    }

    @Override
    public void updateUserIdentities(UserIdentitiesSaveReqVO updateReqVO) {
        // 校验存在
        validateUserIdentitiesExists(updateReqVO.getId());
        // 更新
        UserIdentitiesDO updateObj = BeanUtils.toBean(updateReqVO, UserIdentitiesDO.class);
        userIdentitiesMapper.updateById(updateObj);
    }

    @Override
    public void deleteUserIdentities(Long id) {
        // 校验存在
        validateUserIdentitiesExists(id);
        // 删除
        userIdentitiesMapper.deleteById(id);
    }

    private void validateUserIdentitiesExists(Long id) {
        if (userIdentitiesMapper.selectById(id) == null) {
            throw exception(USER_IDENTITIES_NOT_EXISTS);
        }
    }

    @Override
    public UserIdentitiesDO getUserIdentities(Long id) {
        return userIdentitiesMapper.selectById(id);
    }

    @Override
    public PageResult<UserIdentitiesDO> getUserIdentitiesPage(UserIdentitiesPageReqVO pageReqVO) {
        return userIdentitiesMapper.selectPage(pageReqVO);
    }

}