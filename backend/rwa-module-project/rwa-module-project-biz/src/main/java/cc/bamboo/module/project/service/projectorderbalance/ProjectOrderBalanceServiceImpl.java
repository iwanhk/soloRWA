package cc.bamboo.module.project.service.projectorderbalance;

import cc.bamboo.framework.redis.service.RedisService;
import cc.bamboo.module.project.controller.app.orderbalance.vo.*;
import cc.bamboo.module.project.dal.dataobject.projectinfo.ProjectInfoDO;
import cc.bamboo.module.project.dal.dataobject.projectorder.ProjectOrderDO;
import cc.bamboo.module.project.dal.dataobject.projectorderbalancelog.ProjectOrderBalanceLogDO;
import cc.bamboo.module.project.dal.mysql.projectinfo.ProjectInfoMapper;
import cc.bamboo.module.project.dal.mysql.projectorder.ProjectOrderMapper;
import cc.bamboo.module.project.enums.AssetStatusEnum;
import cc.bamboo.module.project.enums.OrderStatusEnum;
import cc.bamboo.module.project.enums.RunStatusEnum;
import cc.bamboo.module.project.service.projectorder.AppProjectOrderService;
import cc.bamboo.module.project.service.projectorder.ProjectOrderService;
import cc.bamboo.module.project.service.spiderpool.SpiderPoolService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.math.BigDecimal;
import java.time.LocalDate;

import cc.bamboo.module.project.controller.admin.projectorderbalance.vo.*;
import cc.bamboo.module.project.dal.dataobject.projectorderbalance.ProjectOrderBalanceDO;
import cc.bamboo.framework.common.pojo.PageResult;
import cc.bamboo.framework.common.pojo.PageParam;
import cc.bamboo.framework.common.util.object.BeanUtils;

import cc.bamboo.module.project.dal.mysql.projectorderbalance.ProjectOrderBalanceMapper;
import cc.bamboo.module.project.dal.mysql.orderdailyincome.OrderDailyIncomeMapper;
import cc.bamboo.module.project.dal.dataobject.orderdailyincome.OrderDailyIncomeDO;
import cc.bamboo.module.project.dal.redis.RedisKeyConstants;
import org.springframework.cache.annotation.Cacheable;

import static cc.bamboo.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cc.bamboo.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;
import static cc.bamboo.module.project.enums.ApiConstants.*;
import static cc.bamboo.module.project.enums.ErrorCodeConstants.*;

/**
 * 用户项目余额表 Service 实现类
 *
 * @author Swolf
 */
@Service
@Validated
public class ProjectOrderBalanceServiceImpl implements ProjectOrderBalanceService {

    @Resource
    private ProjectOrderBalanceMapper orderBalanceMapper;

    @Resource
    private ProjectInfoMapper projectInfoMapper;

    @Resource
    private OrderDailyIncomeMapper orderDailyIncomeMapper;

    @Resource
    private AppProjectOrderService appProjectOrderService;

    @Resource
    private RedisService redisService;

    @Resource
    private ProjectOrderMapper projectOrderMapper;

    @Resource
    private SpiderPoolService spiderPoolService;

    @Override
    public AppAssetDetailRespVO getAppAssetDetail(Long userId, AppAssetDetailReqVO reqVO) {

        String key = reqVO.getOrderId() == null ? String.format(ASSET_DETAIL_KEY_ALL, userId)
                : String.format(ASSET_DETAIL_KEY, userId, reqVO.getOrderId());
        AppAssetDetailRespVO appAssetDetailRespVO = redisService.getCacheObject(key);
        if (appAssetDetailRespVO != null) {
            return appAssetDetailRespVO;
        }

        // 2. Get today's income from daily income table
        List<AppAssetByCurrencyRespVO> todayIncome = orderDailyIncomeMapper.selectSumIncomeByDateAndUserId(
                LocalDate.now(), userId, reqVO.getProjectId(), reqVO.getOrderId());

        // 3. Construct response
        appAssetDetailRespVO = new AppAssetDetailRespVO();

        // 持有金额
        List<AppAssetByCurrencyRespVO> list = orderBalanceMapper.selectTotalAssetsByUserIdGroupByCurrency(userId,
                reqVO.getProjectId(), reqVO.getOrderId());
        appAssetDetailRespVO.setTotalAssets(list);
        // 收益金额
        List<AppAssetByCurrencyRespVO> totalIncomeList = orderBalanceMapper.selectTotalIncomeByUserId(userId,
                reqVO.getProjectId(), reqVO.getOrderId());
        appAssetDetailRespVO.setTotalIncome(totalIncomeList);
        // 今日收益
        appAssetDetailRespVO.setTodayIncome(todayIncome);
        // 缓存
        redisService.setCacheObject(key, appAssetDetailRespVO, 10L, TimeUnit.MINUTES);
        return appAssetDetailRespVO;
    }

    @Override

    public List<AppDailyIncomeRespVO> getAppDailyIncomeDetail(Long userId, AppDailyIncomeDetailReqVO reqVO) {
        String key = String.format(RedisKeyConstants.PROJECT_ORDER_BALANCE_DAILY_INCOME,userId,reqVO.getDate(),reqVO.getOrderId());
        List<AppDailyIncomeRespVO> respVOList = redisService.getCacheObject(key);
        if (respVOList != null) {
            return respVOList;
        }
        List<OrderDailyIncomeDO> list = orderDailyIncomeMapper.selectListByDateAndUserId(
                reqVO.getDate(), userId, reqVO.getProjectId(), reqVO.getOrderId());

        if (list.isEmpty()) {
            return Collections.emptyList();
        }

        // Fetch project names
        Set<Long> projectIds = list.stream().map(OrderDailyIncomeDO::getProjectId).collect(Collectors.toSet());
        Map<Long, ProjectInfoDO> projectMap = projectInfoMapper.selectBatchIds(projectIds).stream()
                .collect(Collectors.toMap(ProjectInfoDO::getProjectId, p -> p));

        Set<Long> orderIds = list.stream().map(p -> p.getOrderId()).collect(Collectors.toSet());
        LambdaQueryWrapper<ProjectOrderBalanceDO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(ProjectOrderBalanceDO::getOrderId, orderIds);
        List<ProjectOrderBalanceDO> orderBalances = orderBalanceMapper.selectList(lambdaQueryWrapper);
        Map<Long, ProjectOrderBalanceDO>  orderMap =  orderBalances.stream().collect(Collectors.toMap(ProjectOrderBalanceDO::getOrderId, p -> p));

        respVOList = new ArrayList<>();
        for (OrderDailyIncomeDO item : list) {
            AppDailyIncomeRespVO resp = new AppDailyIncomeRespVO();
            resp.setProjectId(item.getProjectId());
            resp.setOrderId(item.getOrderId());
            resp.setOrderDailyIncome(item.getDailyIncome());
            resp.setIncomeDate(item.getIncomeDate());
            ProjectInfoDO project = projectMap.get(item.getProjectId());
            resp.setProjectName(project != null ? project.getProjectName() : "");
            resp.setProjectId(item.getProjectId());
            ProjectOrderBalanceDO  order = orderMap.get(item.getOrderId());
            if (order != null) {
                resp.setInvestmentCurrency(order.getInvestmentCurrency());
                resp.setEarningCurrency(order.getEarningCurrency());
                resp.setHoldAmount(order.getHoldAmount());
            }
            respVOList.add(resp);
        }
        redisService.setCacheObject(key, respVOList, 10L, TimeUnit.MINUTES);
        return respVOList;
    }

    @Override
    public List<AppIncomeCalendarRespVO> getAppIncomeCalendar(Long userId, AppIncomeCalendarReqVO reqVO) {
        String key = String.format(RedisKeyConstants.PROJECT_ORDER_BALANCE_INCOME_CALENDAR,userId,reqVO.getStartDate(),reqVO.getEndDate(),reqVO.getOrderId());
        List<AppIncomeCalendarRespVO> result = redisService.getCacheObject(key);
        if (result != null) {
            return result;
        }

        List<AppIncomeByCoinRespVO> list = orderDailyIncomeMapper.selectDailySumListByDateRangeAndUserId(
                reqVO.getStartDate(), reqVO.getEndDate(), userId, reqVO.getProjectId(), reqVO.getOrderId());
        if (list.isEmpty()) {
            return Collections.emptyList();
        }
        Map<LocalDate, List<AppAssetByCurrencyRespVO>> map = new HashMap<>();
        for (AppIncomeByCoinRespVO item : list) {
            map.computeIfAbsent(item.getIncomeDate(), k -> new ArrayList<>())
                    .add(new AppAssetByCurrencyRespVO(item.getCoinCode(), item.getTotal()));
        }

        result = map.entrySet().stream()
                .map(e -> {
                    AppIncomeCalendarRespVO vo = new AppIncomeCalendarRespVO();
                    vo.setDate(e.getKey());
                    vo.setDailyTotalIncome(e.getValue());
                    return vo;
                })
                .sorted(Comparator.comparing(AppIncomeCalendarRespVO::getDate))
                .collect(Collectors.toList());
        redisService.setCacheObject(key, result, 10L, TimeUnit.MINUTES);
        return result;
    }

    @Override
    public Long createOrderBalance(ProjectOrderBalanceSaveReqVO createReqVO) {
        // 插入
        ProjectOrderBalanceDO orderBalance = BeanUtils.toBean(createReqVO, ProjectOrderBalanceDO.class);
        orderBalanceMapper.insert(orderBalance);
        // 返回
        return orderBalance.getId();
    }

    @Override
    public void updateOrderBalance(ProjectOrderBalanceSaveReqVO updateReqVO) {
        // 校验存在
        validateOrderBalanceExists(updateReqVO.getId());
        // 更新
        ProjectOrderBalanceDO updateObj = BeanUtils.toBean(updateReqVO, ProjectOrderBalanceDO.class);
        orderBalanceMapper.updateById(updateObj);
    }

    @Override
    public void deleteOrderBalance(Long id) {
        // 校验存在
        validateOrderBalanceExists(id);
        // 删除
        orderBalanceMapper.deleteById(id);
    }

    private void validateOrderBalanceExists(Long id) {
        if (orderBalanceMapper.selectById(id) == null) {
            throw exception(ORDER_BALANCE_NOT_EXISTS);
        }
    }

    @Override
    public ProjectOrderBalanceDO getOrderBalance(Long id) {
        return orderBalanceMapper.selectById(id);
    }

    @Override
    public PageResult<ProjectOrderBalanceDO> getOrderBalancePage(ProjectOrderBalancePageReqVO pageReqVO) {
        return orderBalanceMapper.selectPage(pageReqVO);
    }

    @Override
    public PageResult<AppProjectOrderBalanceRespVO> getAppOrderBalancePage(AppProjectOrderBalancePageReqVO pageReqVO) {
        Long userId = getLoginUserId();
        pageReqVO.setUserId(userId);
        // 先查询订单
        LambdaQueryWrapper<ProjectOrderDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ProjectOrderDO::getUserId, userId);
        queryWrapper.eq(pageReqVO.getProjectId() != null, ProjectOrderDO::getProjectId, pageReqVO.getProjectId());
        queryWrapper.in(ProjectOrderDO::getOrderStatus, Arrays.asList(OrderStatusEnum.UNDER_REVIEW.getStatus(),
                OrderStatusEnum.APPROVED.getStatus()));
        PageResult<ProjectOrderDO> orderPageResult = projectOrderMapper.selectPage(pageReqVO, queryWrapper);

        PageResult<AppProjectOrderBalanceRespVO> pageResult = new PageResult<>();
        pageResult.setTotal(orderPageResult.getTotal());
        if (!orderPageResult.getList().isEmpty()) {

            // 订单信息
            Set<Long> orderIds = orderPageResult.getList().stream().map(ProjectOrderDO::getId)
                    .collect(Collectors.toSet());
            List<ProjectOrderBalanceDO> orderBalanceDOList = orderBalanceMapper.getProjectOrderBalanceList(orderIds);
            Map<Long, ProjectOrderBalanceDO> orderBalanceMap = orderBalanceDOList.stream()
                    .collect(Collectors.toMap(ProjectOrderBalanceDO::getOrderId, p -> p));
            // 项目信息
            Set<Long> projectIds = orderPageResult.getList().stream().map(ProjectOrderDO::getProjectId)
                    .collect(Collectors.toSet());
            List<ProjectInfoDO> projectInfoDOList = projectInfoMapper.selectBatchIds(projectIds);
            Map<Long, ProjectInfoDO> projectInfoMap = projectInfoDOList.stream()
                    .collect(Collectors.toMap(ProjectInfoDO::getProjectId, p -> p));
            // 订单
            List<AppProjectOrderBalanceRespVO> appProjectOrderBalanceRespVOList = new ArrayList<>();
            for (ProjectOrderDO resp : orderPageResult.getList()) {
                ProjectOrderBalanceDO orderBalance = orderBalanceMap.get(resp.getId());
                AppProjectOrderBalanceRespVO respVO = new AppProjectOrderBalanceRespVO();
                respVO.setUserId(resp.getUserId());
                respVO.setProjectId(resp.getProjectId());
                respVO.setProjectName(resp.getProjectName());
                respVO.setBuyQuantity(resp.getSubscribeQuantity());
                respVO.setPrincipalAmount(resp.getTotalAmount());
                respVO.setId(resp.getId());
                respVO.setInvestmentCurrency(resp.getInvestmentCurrency());
                respVO.setEarningCurrency(resp.getEarningCurrency());
                if (orderBalance != null) {
                    // 收入相关全部乘汇率
                    respVO.setTotalIncome(orderBalance.getTotalIncome());
                    respVO.setUnwithdrawnDividend(orderBalance.getUnwithdrawnDividend());
                    respVO.setWithdrawnDividend(orderBalance.getWithdrawnDividend());
                    respVO.setHoldAmount(orderBalance.getHoldAmount());
                    respVO.setHoldQuantity(orderBalance.getHoldQuantity());
                    respVO.setTotalRedemptionAmount(orderBalance.getTotalRedemptionAmount());
                } else {
                    respVO.setTotalIncome(BigDecimal.ZERO);
                    respVO.setUnwithdrawnDividend(BigDecimal.ZERO);
                    respVO.setWithdrawnDividend(BigDecimal.ZERO);
                    respVO.setHoldAmount(BigDecimal.ZERO);
                    respVO.setHoldQuantity(0);
                    respVO.setTotalRedemptionAmount(BigDecimal.ZERO);

                }
                // 判断状态
                if (resp.getOrderStatus().equals(OrderStatusEnum.UNDER_REVIEW.getStatus())) {
                    respVO.setStatus(AssetStatusEnum.CONFIRMING.getStatus());
                } else if (resp.getOrderStatus().equals(OrderStatusEnum.APPROVED.getStatus())) {
                    ProjectInfoDO projectInfoDO = projectInfoMap.get(resp.getProjectId());
                    LocalDate now = LocalDate.now();
                    if(Objects.equals(projectInfoDO.getProjectType(), ASSET_TYPE_OPEN_FUND)){
                        //如果有锁定时间，并且在锁定期内，则是收益中状态
                        if(projectInfoDO.getLockStartTime() != null){
                            if(now.isAfter(projectInfoDO.getLockStartTime())){
                                respVO.setStatus(AssetStatusEnum.PROFIT.getStatus());
                            }else if(projectInfoDO.getLockEndTime() != null && now.isAfter(projectInfoDO.getLockEndTime())){
                                //如果当前时间在锁定期之后则是可赎回
                                respVO.setStatus(AssetStatusEnum.REDEEMABLE.getStatus());
                            }else{
                                respVO.setStatus(AssetStatusEnum.WAIT.getStatus());
                            }

                        }else{
                            respVO.setStatus(AssetStatusEnum.WAIT.getStatus());
                        }
                        if (resp.getOrderStatus().equals(OrderStatusEnum.ENDED.getStatus())) {
                            respVO.setStatus(AssetStatusEnum.ENDED.getStatus());
                        }
                    }else{
                        //如果有锁定时间，并且在锁定期内，则是收益中状态
                        if(projectInfoDO.getLockStartTime() != null && projectInfoDO.getLockEndTime() != null){
                            if(now.isAfter(projectInfoDO.getLockStartTime())
                                    && now.isBefore(projectInfoDO.getLockEndTime())){
                                respVO.setStatus(AssetStatusEnum.PROFIT.getStatus());
                            }else if(now.isAfter(projectInfoDO.getLockEndTime())){
                                //如果当前时间在锁定期之后则是可赎回
                                respVO.setStatus(AssetStatusEnum.REDEEMABLE.getStatus());
                            }else{
                                respVO.setStatus(AssetStatusEnum.WAIT.getStatus());
                            }

                        }else{
                            respVO.setStatus(AssetStatusEnum.WAIT.getStatus());
                        }
                        if (resp.getOrderStatus().equals(OrderStatusEnum.ENDED.getStatus())) {
                            respVO.setStatus(AssetStatusEnum.ENDED.getStatus());
                        }
                    }

                }else if (resp.getOrderStatus().equals(OrderStatusEnum.ENDED.getStatus())) {
                    respVO.setStatus(AssetStatusEnum.ENDED.getStatus());
                }

                appProjectOrderBalanceRespVOList.add(respVO);
            }
            pageResult.setList(appProjectOrderBalanceRespVOList);
        }

        return pageResult;
    }

}