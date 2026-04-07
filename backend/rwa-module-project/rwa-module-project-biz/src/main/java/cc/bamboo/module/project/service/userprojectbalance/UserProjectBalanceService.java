package cc.bamboo.module.project.service.userprojectbalance;

import java.util.*;
import javax.validation.*;
import cc.bamboo.module.project.controller.admin.userprojectbalance.vo.*;
import cc.bamboo.module.project.dal.dataobject.userprojectbalance.UserProjectBalanceDO;
import cc.bamboo.framework.common.pojo.PageResult;
import cc.bamboo.framework.common.pojo.PageParam;

/**
 * 用户项目余额表（本金/收益汇总） Service 接口
 *
 * @author Swolf
 */
public interface UserProjectBalanceService {

    /**
     * 创建用户项目余额表（本金/收益汇总）
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createUserProjectBalance(@Valid UserProjectBalanceSaveReqVO createReqVO);

    /**
     * 更新用户项目余额表（本金/收益汇总）
     *
     * @param updateReqVO 更新信息
     */
    void updateUserProjectBalance(@Valid UserProjectBalanceSaveReqVO updateReqVO);

    /**
     * 删除用户项目余额表（本金/收益汇总）
     *
     * @param id 编号
     */
    void deleteUserProjectBalance(Long id);

    /**
     * 获得用户项目余额表（本金/收益汇总）
     *
     * @param id 编号
     * @return 用户项目余额表（本金/收益汇总）
     */
    UserProjectBalanceDO getUserProjectBalance(Long id);

    /**
     * 获得用户项目余额表（本金/收益汇总）分页
     *
     * @param pageReqVO 分页查询
     * @return 用户项目余额表（本金/收益汇总）分页
     */
    PageResult<UserProjectBalanceDO> getUserProjectBalancePage(UserProjectBalancePageReqVO pageReqVO);

}