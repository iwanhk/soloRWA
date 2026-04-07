package cc.bamboo.module.chain.service.useridentities;

import java.util.*;
import javax.validation.*;
import cc.bamboo.module.chain.controller.admin.useridentities.vo.*;
import cc.bamboo.module.chain.dal.dataobject.useridentities.UserIdentitiesDO;
import cc.bamboo.framework.common.pojo.PageResult;
import cc.bamboo.framework.common.pojo.PageParam;

/**
 * 用户身份 Service 接口
 *
 * @author Swolf
 */
public interface UserIdentitiesService {

    /**
     * 创建用户身份
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createUserIdentities(@Valid UserIdentitiesSaveReqVO createReqVO);

    /**
     * 更新用户身份
     *
     * @param updateReqVO 更新信息
     */
    void updateUserIdentities(@Valid UserIdentitiesSaveReqVO updateReqVO);

    /**
     * 删除用户身份
     *
     * @param id 编号
     */
    void deleteUserIdentities(Long id);

    /**
     * 获得用户身份
     *
     * @param id 编号
     * @return 用户身份
     */
    UserIdentitiesDO getUserIdentities(Long id);

    /**
     * 获得用户身份分页
     *
     * @param pageReqVO 分页查询
     * @return 用户身份分页
     */
    PageResult<UserIdentitiesDO> getUserIdentitiesPage(UserIdentitiesPageReqVO pageReqVO);

}