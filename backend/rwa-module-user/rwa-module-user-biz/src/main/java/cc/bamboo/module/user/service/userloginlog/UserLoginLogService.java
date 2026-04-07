package cc.bamboo.module.user.service.userloginlog;

import java.util.*;
import javax.validation.*;
import cc.bamboo.module.user.controller.admin.userloginlog.vo.*;
import cc.bamboo.module.user.dal.dataobject.userloginlog.UserLoginLogDO;
import cc.bamboo.framework.common.pojo.PageResult;
import cc.bamboo.framework.common.pojo.PageParam;

/**
 * 用户登录日志 Service 接口
 *
 * @author Swolf
 */
public interface UserLoginLogService {

    /**
     * 创建用户登录日志
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createLoginLog(@Valid UserLoginLogSaveReqVO createReqVO);

    /**
     * 创建用户登录日志（异步）
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createLoginLogAsync( UserLoginLogSaveReqVO createReqVO);

    /**
     * 更新用户登录日志
     *
     * @param updateReqVO 更新信息
     */
    void updateLoginLog(@Valid UserLoginLogSaveReqVO updateReqVO);

    /**
     * 删除用户登录日志
     *
     * @param id 编号
     */
    void deleteLoginLog(Long id);

    /**
     * 获得用户登录日志
     *
     * @param id 编号
     * @return 用户登录日志
     */
    UserLoginLogDO getLoginLog(Long id);

    /**
     * 获得用户登录日志分页
     *
     * @param pageReqVO 分页查询
     * @return 用户登录日志分页
     */
    PageResult<UserLoginLogDO> getLoginLogPage(UserLoginLogPageReqVO pageReqVO);


}