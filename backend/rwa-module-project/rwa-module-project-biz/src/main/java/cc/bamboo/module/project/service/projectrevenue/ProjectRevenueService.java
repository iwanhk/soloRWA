package cc.bamboo.module.project.service.projectrevenue;

import javax.validation.*;
import cc.bamboo.module.project.controller.admin.projectrevenue.vo.*;
import cc.bamboo.module.project.dal.dataobject.projectrevenue.ProjectRevenueDO;
import cc.bamboo.module.project.mq.message.IncomeDistributeMessage;
import cc.bamboo.framework.common.pojo.PageResult;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 项目收益 Service 接口
 *
 * @author Swolf
 */
public interface ProjectRevenueService {

    /**
     * 创建项目收益
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createRevenue(@Valid ProjectRevenueSaveReqVO createReqVO);

    /**
     * 更新项目收益
     *
     * @param updateReqVO 更新信息
     */
    void updateRevenue(@Valid ProjectRevenueSaveReqVO updateReqVO);

    /**
     * 删除项目收益
     *
     * @param id 编号
     */
    void deleteRevenue(Long id);

    /**
     * 获得项目收益
     *
     * @param id 编号
     * @return 项目收益
     */
    ProjectRevenueDO getRevenue(Long id);

    /**
     * 获得项目收益分页
     *
     * @param pageReqVO 分页查询
     * @return 项目收益分页
     */
    PageResult<ProjectRevenueDO> getRevenuePage(ProjectRevenuePageReqVO pageReqVO);

    /**
     * 生成每日项目收益(Mock数据)
     * 
     * @param projectId   项目ID
     * @param revenueDate 收益日期
     * @return 项目收益ID
     */
    Long generateDailyRevenue(Long projectId, LocalDate revenueDate);

    /**
     * 计算订单收益
     * 为每笔订单计算并创建每日收益记录
     * 
     * @param projectRevenueId 项目收益ID
     */
    void calculateOrderIncome(Long projectRevenueId);

    /**
     * 发放收益
     * 更新订单余额的未提取分红,同时更新项目和订单统计
     * 
     * @param projectRevenueId 项目收益ID
     */
    void distributeIncome(Long projectRevenueId);

    /**
     * 发放收益
     *
     * @param incomeDate
     * @author: Hus
     * @date: 2026/1/23 11:43
     * @return: void
     * @description
     */
    void distributeIncome(LocalDate incomeDate);

    /**
     * 处理单个订单收益发放
     * 由RabbitMQ消息触发,异步处理
     * 
     * @param message 收益发放消息
     */
    void processOrderIncome(IncomeDistributeMessage message);

    /**
     * 修改可发放收益
     * 只有未发放状态的收益才能修改
     * 如果已有订单收益记录,会重新计算分配给每个订单的收益
     *
     * @param reqVO
     */
    void updateAvailableRevenue(UpdateAvailableRevenueReqVO reqVO);

    /**
     * 根据项目配置和收益自动计算成本
     *
     * @param projectId 项目ID
     * @param revenue   总收益
     * @return 计算结果(电力成本、人力成本、可发放收益)
     */
    CalculateCostsRespVO calculateCosts(Long projectId,
            BigDecimal revenue, LocalDate revenueDate);

    /**
     * 获取矿池收益并计算
     *
     * @param projectId   项目ID
     * @param revenueDate 收益日期
     * @return 矿池收益结果
     */
    PoolRevenueCalcRespVO calculatePoolRevenue(Long projectId, LocalDate revenueDate);

    /**
     * 生成每日项目收益(Mock数据，指定金额)
     * 
     * @param projectId    项目ID
     * @param revenueDate  收益日期
     * @param dailyRevenue 每日收益金额
     * @return 项目收益ID
     */
    Long generateDailyRevenueMork(Long projectId, LocalDate revenueDate, BigDecimal dailyRevenue);

    /**
     * 生成每日基金项目收益
     *
     * @param projectId   项目ID
     * @param revenueDate 收益日期
     * @return 项目收益ID
     */
    Long generateDailyFundRevenue(Long projectId, LocalDate revenueDate);

    /**
     * 获取指定币种对USD的汇率
     *
     * @param coinCode 币种代码（如 BTC）
     * @return 汇率
     */
    BigDecimal getCoinUsdRate(String coinCode);
}