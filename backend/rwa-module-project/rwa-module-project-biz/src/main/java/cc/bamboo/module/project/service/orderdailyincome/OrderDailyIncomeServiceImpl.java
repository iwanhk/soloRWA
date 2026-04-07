package cc.bamboo.module.project.service.orderdailyincome;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import cc.bamboo.module.project.controller.admin.orderdailyincome.vo.*;
import cc.bamboo.module.project.dal.dataobject.orderdailyincome.OrderDailyIncomeDO;
import cc.bamboo.framework.common.pojo.PageResult;
import cc.bamboo.framework.common.pojo.PageParam;
import cc.bamboo.framework.common.util.object.BeanUtils;

import cc.bamboo.module.project.dal.mysql.orderdailyincome.OrderDailyIncomeMapper;

import static cc.bamboo.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cc.bamboo.module.project.enums.ErrorCodeConstants.*;

/**
 * 订单每日收益统计 Service 实现类
 *
 * @author Swolf
 */
@Service
@Validated
public class OrderDailyIncomeServiceImpl implements OrderDailyIncomeService {

    @Resource
    private OrderDailyIncomeMapper orderDailyIncomeMapper;

    @Override
    public Long createOrderDailyIncome(OrderDailyIncomeSaveReqVO createReqVO) {
        // 插入
        OrderDailyIncomeDO orderDailyIncome = BeanUtils.toBean(createReqVO, OrderDailyIncomeDO.class);
        orderDailyIncomeMapper.insert(orderDailyIncome);
        // 返回
        return orderDailyIncome.getId();
    }

    @Override
    public void updateOrderDailyIncome(OrderDailyIncomeSaveReqVO updateReqVO) {
        // 校验存在
        validateOrderDailyIncomeExists(updateReqVO.getId());
        // 更新
        OrderDailyIncomeDO updateObj = BeanUtils.toBean(updateReqVO, OrderDailyIncomeDO.class);
        orderDailyIncomeMapper.updateById(updateObj);
    }

    @Override
    public void deleteOrderDailyIncome(Long id) {
        // 校验存在
        validateOrderDailyIncomeExists(id);
        // 删除
        orderDailyIncomeMapper.deleteById(id);
    }

    private void validateOrderDailyIncomeExists(Long id) {
        if (orderDailyIncomeMapper.selectById(id) == null) {
            throw exception(ORDER_DAILY_INCOME_NOT_EXISTS);
        }
    }

    @Override
    public OrderDailyIncomeDO getOrderDailyIncome(Long id) {
        return orderDailyIncomeMapper.selectById(id);
    }

    @Override
    public PageResult<OrderDailyIncomeDO> getOrderDailyIncomePage(OrderDailyIncomePageReqVO pageReqVO) {
        return orderDailyIncomeMapper.selectPage(pageReqVO);
    }

}