package cc.bamboo.module.user.service.userinfo;

import java.util.*;
import javax.validation.*;
import cc.bamboo.module.user.controller.admin.userinfo.vo.*;
import cc.bamboo.module.user.dal.dataobject.userinfo.UserInfoDO;
import cc.bamboo.framework.common.pojo.PageResult;
import cc.bamboo.framework.common.pojo.PageParam;

/**
 * 用户基础信息 Service 接口
 *
 * @author Swolf
 */
public interface UserInfoService {

    /**
     * 创建用户基础信息
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createInfo(@Valid UserInfoSaveReqVO createReqVO);

    /**
     * 更新用户基础信息
     *
     * @param updateReqVO 更新信息
     */
    void updateInfo(@Valid UserInfoSaveReqVO updateReqVO);

    /**
     * 更新用户基础信息
     *
     * @param userInfoDO 更新信息
     */
    void updateInfo(UserInfoDO userInfoDO);

    /**
     * 删除用户基础信息
     *
     * @param id 编号
     */
    void deleteInfo(Long id);

    /**
     * 获得用户基础信息
     *
     * @param id 编号
     * @return 用户基础信息
     */
    UserInfoDO getInfo(Long id);

    /**
     * 获得用户基础信息分页
     *
     * @param pageReqVO 分页查询
     * @return 用户基础信息分页
     */
    PageResult<UserInfoDO> getInfoPage(UserInfoPageReqVO pageReqVO);

    /**
     * 根据手机号获得用户基础信息
     *
     * @param mobile 手机号
     * @return 用户基础信息
     */
    UserInfoDO getUserInfoByMobile(String mobile);

    /**
     * 根据邮箱获得用户基础信息
     *
     * @param email 邮箱
     * @return 用户基础信息
     */
    UserInfoDO getUserInfoByEmail(String email);

    /**
     * 判断密码是否匹配
     *
     * @param rawPassword     未加密的密码
     * @param encodedPassword 加密后的密码
     * @return 是否匹配
     */
    boolean isPasswordMatch(String rawPassword, String encodedPassword);

    /**
     * 审核2FA解绑请求
     * 
     */
    void approve2FAUnbind(AuditF2AReqVO reqVO);
}