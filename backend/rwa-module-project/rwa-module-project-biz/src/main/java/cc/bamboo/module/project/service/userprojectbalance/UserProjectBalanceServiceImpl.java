package cc.bamboo.module.project.service.userprojectbalance;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import cc.bamboo.module.project.controller.admin.userprojectbalance.vo.*;
import cc.bamboo.module.project.dal.dataobject.userprojectbalance.UserProjectBalanceDO;
import cc.bamboo.framework.common.pojo.PageResult;
import cc.bamboo.framework.common.pojo.PageParam;
import cc.bamboo.framework.common.util.object.BeanUtils;

import cc.bamboo.module.project.dal.mysql.userprojectbalance.UserProjectBalanceMapper;

import static cc.bamboo.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cc.bamboo.module.project.enums.ErrorCodeConstants.*;

/**
 * 用户项目余额表（本金/收益汇总） Service 实现类
 *
 * @author Swolf
 */
@Service
@Validated
public class UserProjectBalanceServiceImpl implements UserProjectBalanceService {

    @Resource
    private UserProjectBalanceMapper userProjectBalanceMapper;

    @Override
    public Long createUserProjectBalance(UserProjectBalanceSaveReqVO createReqVO) {
        // 插入
        UserProjectBalanceDO userProjectBalance = BeanUtils.toBean(createReqVO, UserProjectBalanceDO.class);
        userProjectBalanceMapper.insert(userProjectBalance);
        // 返回
        return userProjectBalance.getId();
    }

    @Override
    public void updateUserProjectBalance(UserProjectBalanceSaveReqVO updateReqVO) {
        // 校验存在
        validateUserProjectBalanceExists(updateReqVO.getId());
        // 更新
        UserProjectBalanceDO updateObj = BeanUtils.toBean(updateReqVO, UserProjectBalanceDO.class);
        userProjectBalanceMapper.updateById(updateObj);
    }

    @Override
    public void deleteUserProjectBalance(Long id) {
        // 校验存在
        validateUserProjectBalanceExists(id);
        // 删除
        userProjectBalanceMapper.deleteById(id);
    }

    private void validateUserProjectBalanceExists(Long id) {
        if (userProjectBalanceMapper.selectById(id) == null) {
            throw exception(USER_PROJECT_BALANCE_NOT_EXISTS);
        }
    }

    @Override
    public UserProjectBalanceDO getUserProjectBalance(Long id) {
        return userProjectBalanceMapper.selectById(id);
    }

    @Override
    public PageResult<UserProjectBalanceDO> getUserProjectBalancePage(UserProjectBalancePageReqVO pageReqVO) {
        return userProjectBalanceMapper.selectPage(pageReqVO);
    }

}