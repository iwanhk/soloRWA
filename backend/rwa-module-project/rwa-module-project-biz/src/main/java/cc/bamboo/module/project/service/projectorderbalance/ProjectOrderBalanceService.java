package cc.bamboo.module.project.service.projectorderbalance;

import java.util.*;
import javax.validation.*;
import cc.bamboo.module.project.controller.admin.projectorderbalance.vo.*;
import cc.bamboo.module.project.controller.app.orderbalance.vo.*;
import cc.bamboo.module.project.dal.dataobject.projectorderbalance.ProjectOrderBalanceDO;
import cc.bamboo.framework.common.pojo.PageResult;
import cc.bamboo.framework.common.pojo.PageParam;

/**
 * 用户项目余额表 Service 接口
 *
 * @author Swolf
 */
public interface ProjectOrderBalanceService {

    /**
     * 获得用户资产详情
     *
     * @param userId 用户ID
     * @param reqVO  查询参数
     * @return 资产详情
     */
    AppAssetDetailRespVO getAppAssetDetail(Long userId, AppAssetDetailReqVO reqVO);

    /**
     * 获得用户每日收益明细
     *
     * @param userId 用户ID
     * @param reqVO  查询参数
     * @return 每日收益明细列表
     */
    List<AppDailyIncomeRespVO> getAppDailyIncomeDetail(Long userId, AppDailyIncomeDetailReqVO reqVO);

    /**
     * 获得用户收益日历
     *
     * @param userId 用户ID
     * @param reqVO  查询参数
     * @return 收益日历列表
     */
    List<AppIncomeCalendarRespVO> getAppIncomeCalendar(Long userId, AppIncomeCalendarReqVO reqVO);

    /**
     * 创建用户项目余额表
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createOrderBalance(@Valid ProjectOrderBalanceSaveReqVO createReqVO);

    /**
     * 更新用户项目余额表
     *
     * @param updateReqVO 更新信息
     */
    void updateOrderBalance(@Valid ProjectOrderBalanceSaveReqVO updateReqVO);

    /**
     * 删除用户项目余额表
     *
     * @param id 编号
     */
    void deleteOrderBalance(Long id);

    /**
     * 获得用户项目余额表
     *
     * @param id 编号
     * @return 用户项目余额表
     */
    ProjectOrderBalanceDO getOrderBalance(Long id);

    /**
     * 获得用户项目余额表分页
     *
     * @param pageReqVO 分页查询
     * @return 用户项目余额表分页
     */
    PageResult<ProjectOrderBalanceDO> getOrderBalancePage(ProjectOrderBalancePageReqVO pageReqVO);

    /**
     * 获得用户项目余额表分页
     *
     * @param pageReqVO 分页查询
     * @return 用户项目余额表分页
     */
    PageResult<AppProjectOrderBalanceRespVO> getAppOrderBalancePage(AppProjectOrderBalancePageReqVO pageReqVO);
}