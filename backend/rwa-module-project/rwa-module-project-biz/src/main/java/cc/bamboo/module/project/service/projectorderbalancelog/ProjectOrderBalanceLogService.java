package cc.bamboo.module.project.service.projectorderbalancelog;

import java.util.*;
import javax.validation.*;
import cc.bamboo.module.project.controller.admin.projectorderbalancelog.vo.*;
import cc.bamboo.module.project.dal.dataobject.projectorderbalancelog.ProjectOrderBalanceLogDO;
import cc.bamboo.framework.common.pojo.PageResult;
import cc.bamboo.framework.common.pojo.PageParam;

/**
 * 用户项目余额记录 Service 接口
 *
 * @author Swolf
 */
public interface ProjectOrderBalanceLogService {

    /**
     * 创建用户项目余额记录
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createOrderBalanceLog(@Valid ProjectOrderBalanceLogSaveReqVO createReqVO);

    /**
     * 更新用户项目余额记录
     *
     * @param updateReqVO 更新信息
     */
    void updateOrderBalanceLog(@Valid ProjectOrderBalanceLogSaveReqVO updateReqVO);

    /**
     * 删除用户项目余额记录
     *
     * @param id 编号
     */
    void deleteOrderBalanceLog(Long id);

    /**
     * 获得用户项目余额记录
     *
     * @param id 编号
     * @return 用户项目余额记录
     */
    ProjectOrderBalanceLogDO getOrderBalanceLog(Long id);

    /**
     * 获得用户项目余额记录分页
     *
     * @param pageReqVO 分页查询
     * @return 用户项目余额记录分页
     */
    PageResult<ProjectOrderBalanceLogDO> getOrderBalanceLogPage(ProjectOrderBalanceLogPageReqVO pageReqVO);

}