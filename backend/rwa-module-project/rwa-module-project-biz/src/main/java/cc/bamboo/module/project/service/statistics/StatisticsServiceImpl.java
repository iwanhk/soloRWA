package cc.bamboo.module.project.service.statistics;

import cc.bamboo.module.project.controller.admin.statistics.vo.*;
import cc.bamboo.module.project.dal.dataobject.projectinfo.ProjectInfoDO;
import cc.bamboo.module.project.dal.dataobject.projectorder.ProjectOrderDO;
import cc.bamboo.module.project.dal.mysql.projectinfo.ProjectInfoMapper;
import cc.bamboo.module.project.dal.mysql.projectorder.ProjectOrderMapper;
import cc.bamboo.module.project.enums.OrderStatusEnum;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 首页统计 Service 实现类
 */
@Service
@Validated
@Slf4j
public class StatisticsServiceImpl implements StatisticsService {

    @Resource
    private ProjectOrderMapper projectOrderMapper;

    @Resource
    private ProjectInfoMapper projectInfoMapper;

    @Override
    public StatsOverviewRespVO getOverview() {
        StatsOverviewRespVO respVO = new StatsOverviewRespVO();

        // 1. 统计项目总数
        Long projectCount = projectInfoMapper.selectCount(new LambdaQueryWrapper<>());
        respVO.setProjectCount(projectCount);

        // 2. 统计订单总量
        Long orderCount = projectOrderMapper.selectCount(new LambdaQueryWrapper<>());
        respVO.setOrderCount(orderCount);

        // 3. 统计交易总额（只统计已成功的订单：状态3-审核通过）
        List<ProjectOrderDO> successOrders = projectOrderMapper.selectList(
                new LambdaQueryWrapper<ProjectOrderDO>().eq(ProjectOrderDO::getOrderStatus, OrderStatusEnum.APPROVED.getStatus()));
        BigDecimal totalAmount = successOrders.stream()
                .map(order -> order.getTotalAmount() != null ? order.getTotalAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        respVO.setTotalAmount(totalAmount);

        return respVO;
    }

    @Override
    public List<OrderTrendRespVO> getOrderTrend() {
        List<OrderTrendRespVO> trend = new ArrayList<>();
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd");

        for (int i = 29; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

            // 查询当天订单
            List<ProjectOrderDO> dayOrders = projectOrderMapper.selectList(
                    new LambdaQueryWrapper<ProjectOrderDO>()
                            .ge(ProjectOrderDO::getCreateTime, startOfDay)
                            .le(ProjectOrderDO::getCreateTime, endOfDay));

            OrderTrendRespVO trendVO = new OrderTrendRespVO();
            trendVO.setDate(date.format(formatter));
            trendVO.setOrderCount((long) dayOrders.size());
            // 只统计审核通过的订单金额
            BigDecimal dayAmount = dayOrders.stream()
                    .filter(o -> o.getOrderStatus() != null && o.getOrderStatus().equals(OrderStatusEnum.APPROVED.getStatus()))
                    .map(o -> o.getTotalAmount() != null ? o.getTotalAmount() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            trendVO.setAmount(dayAmount);
            trend.add(trendVO);
        }
        return trend;
    }

    @Override
    public List<OrderStatusDistRespVO> getOrderStatusDist() {
        List<OrderStatusDistRespVO> distribution = new ArrayList<>();


        Map<Integer, String> statusMap = new LinkedHashMap<>();
        statusMap.put(0, "待支付");
        statusMap.put(1, "审核中");
        statusMap.put(2, "审核通过");
        statusMap.put(3, "审核未通过");
        statusMap.put(4, "已取消");

        for (Map.Entry<Integer, String> entry : statusMap.entrySet()) {
            Long count = projectOrderMapper.selectCount(
                    new LambdaQueryWrapper<ProjectOrderDO>().eq(ProjectOrderDO::getOrderStatus, entry.getKey()));
            OrderStatusDistRespVO distVO = new OrderStatusDistRespVO();
            distVO.setStatus(entry.getKey());
            distVO.setName(entry.getValue());
            distVO.setCount(count);
            distribution.add(distVO);
        }
        return distribution;
    }

    @Override
    public List<TopProjectRespVO> getTopProjects(Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 5;
        }

        // 获取所有审核通过的订单
        List<ProjectOrderDO> successOrders = projectOrderMapper.selectList(
                new LambdaQueryWrapper<ProjectOrderDO>().eq(ProjectOrderDO::getOrderStatus, OrderStatusEnum.APPROVED.getStatus()));

        // 按项目分组统计
        Map<Long, List<ProjectOrderDO>> projectOrdersMap = successOrders.stream()
                .filter(o -> o.getProjectId() != null)
                .collect(Collectors.groupingBy(ProjectOrderDO::getProjectId));

        final int finalLimit = limit;
        return projectOrdersMap.entrySet().stream()
                .map(entry -> {
                    TopProjectRespVO vo = new TopProjectRespVO();
                    vo.setProjectId(entry.getKey());
                    List<ProjectOrderDO> orders = entry.getValue();
                    vo.setOrderCount((long) orders.size());
                    vo.setAmount(orders.stream()
                            .map(o -> o.getTotalAmount() != null ? o.getTotalAmount() : BigDecimal.ZERO)
                            .reduce(BigDecimal.ZERO, BigDecimal::add));
                    // 使用订单中的项目名称
                    vo.setName(orders.get(0).getProjectName());
                    return vo;
                })
                .sorted((a, b) -> {
                    // 先按订单数排序，再按金额排序
                    int cmp = b.getOrderCount().compareTo(a.getOrderCount());
                    if (cmp == 0) {
                        return b.getAmount().compareTo(a.getAmount());
                    }
                    return cmp;
                })
                .limit(finalLimit)
                .collect(Collectors.toList());
    }

    @Override
    public List<RecentOrderRespVO> getRecentOrders(Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 10;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // 查询最新订单
        List<ProjectOrderDO> orders = projectOrderMapper.selectList(
                new LambdaQueryWrapper<ProjectOrderDO>()
                        .orderByDesc(ProjectOrderDO::getCreateTime)
                        .last("LIMIT " + limit));

        return orders.stream().map(order -> {
            RecentOrderRespVO vo = new RecentOrderRespVO();
            vo.setId(order.getId());
            vo.setOrderNo(order.getOrderNo());
            vo.setProjectName(order.getProjectName());
            vo.setAmount(order.getTotalAmount());
            vo.setStatus(order.getOrderStatus());
            vo.setCreateTime(order.getCreateTime() != null ? order.getCreateTime().format(formatter) : "");
            return vo;
        }).collect(Collectors.toList());
    }
}
