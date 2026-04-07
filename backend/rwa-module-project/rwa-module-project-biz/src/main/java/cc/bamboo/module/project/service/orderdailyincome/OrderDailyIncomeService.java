package cc.bamboo.module.project.service.orderdailyincome;

import java.util.*;
import javax.validation.*;
import cc.bamboo.module.project.controller.admin.orderdailyincome.vo.*;
import cc.bamboo.module.project.dal.dataobject.orderdailyincome.OrderDailyIncomeDO;
import cc.bamboo.framework.common.pojo.PageResult;
import cc.bamboo.framework.common.pojo.PageParam;

/**
 * 订单每日收益统计 Service 接口
 *
 * @author Swolf
 */
public interface OrderDailyIncomeService {

    /**
     * 创建订单每日收益统计
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createOrderDailyIncome(@Valid OrderDailyIncomeSaveReqVO createReqVO);

    /**
     * 更新订单每日收益统计
     *
     * @param updateReqVO 更新信息
     */
    void updateOrderDailyIncome(@Valid OrderDailyIncomeSaveReqVO updateReqVO);

    /**
     * 删除订单每日收益统计
     *
     * @param id 编号
     */
    void deleteOrderDailyIncome(Long id);

    /**
     * 获得订单每日收益统计
     *
     * @param id 编号
     * @return 订单每日收益统计
     */
    OrderDailyIncomeDO getOrderDailyIncome(Long id);

    /**
     * 获得订单每日收益统计分页
     *
     * @param pageReqVO 分页查询
     * @return 订单每日收益统计分页
     */
    PageResult<OrderDailyIncomeDO> getOrderDailyIncomePage(OrderDailyIncomePageReqVO pageReqVO);

}