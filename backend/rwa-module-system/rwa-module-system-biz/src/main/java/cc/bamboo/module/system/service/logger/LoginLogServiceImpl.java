package cc.bamboo.module.system.service.logger;

import cc.bamboo.framework.common.pojo.PageResult;
import cc.bamboo.framework.common.util.object.BeanUtils;
import cc.bamboo.module.system.api.logger.dto.LoginLogCreateReqDTO;
import cc.bamboo.module.system.controller.admin.logger.vo.loginlog.LoginLogPageReqVO;
import cc.bamboo.module.system.dal.dataobject.logger.LoginLogDO;
import cc.bamboo.module.system.dal.mysql.logger.LoginLogMapper;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;

/**
 * 登录日志 Service 实现
 */
@Service
@Validated
public class LoginLogServiceImpl implements LoginLogService {

    @Resource
    private LoginLogMapper loginLogMapper;

    @Override
    public PageResult<LoginLogDO> getLoginLogPage(LoginLogPageReqVO pageReqVO) {
        return loginLogMapper.selectPage(pageReqVO);
    }

    @Override
    public void createLoginLog(LoginLogCreateReqDTO reqDTO) {
        LoginLogDO loginLog = BeanUtils.toBean(reqDTO, LoginLogDO.class);
        loginLogMapper.insert(loginLog);
    }

}
