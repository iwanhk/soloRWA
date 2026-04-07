package cc.bamboo.module.user.service.userloginlog;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import cc.bamboo.module.user.controller.admin.userloginlog.vo.*;
import cc.bamboo.module.user.dal.dataobject.userloginlog.UserLoginLogDO;
import cc.bamboo.framework.common.pojo.PageResult;
import cc.bamboo.framework.common.pojo.PageParam;
import cc.bamboo.framework.common.util.object.BeanUtils;

import cc.bamboo.module.user.dal.mysql.userloginlog.UserLoginLogMapper;

import static cc.bamboo.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cc.bamboo.module.user.enums.ErrorCodeConstants.*;

/**
 * 用户登录日志 Service 实现类
 *
 * @author Swolf
 */
@Service
@Validated
public class UserLoginLogServiceImpl implements UserLoginLogService {

    @Resource
    private UserLoginLogMapper loginLogMapper;

    @Override
    public Long createLoginLog(UserLoginLogSaveReqVO createReqVO) {
        // 插入
        UserLoginLogDO loginLog = BeanUtils.toBean(createReqVO, UserLoginLogDO.class);
        loginLogMapper.insert(loginLog);
        // 返回
        return loginLog.getId();
    }

    @Override
    @Async
    public Long createLoginLogAsync(UserLoginLogSaveReqVO createReqVO) {

        return createLoginLog(createReqVO);
    }

    @Override
    public void updateLoginLog(UserLoginLogSaveReqVO updateReqVO) {
        // 校验存在
        validateLoginLogExists(updateReqVO.getId());
        // 更新
        UserLoginLogDO updateObj = BeanUtils.toBean(updateReqVO, UserLoginLogDO.class);
        loginLogMapper.updateById(updateObj);
    }

    @Override
    public void deleteLoginLog(Long id) {
        // 校验存在
        validateLoginLogExists(id);
        // 删除
        loginLogMapper.deleteById(id);
    }

    private void validateLoginLogExists(Long id) {
        if (loginLogMapper.selectById(id) == null) {
            throw exception(LOGIN_LOG_NOT_EXISTS);
        }
    }

    @Override
    public UserLoginLogDO getLoginLog(Long id) {
        return loginLogMapper.selectById(id);
    }

    @Override
    public PageResult<UserLoginLogDO> getLoginLogPage(UserLoginLogPageReqVO pageReqVO) {
        return loginLogMapper.selectPage(pageReqVO);
    }

}