package cc.bamboo.module.project.service.projectrevenue;

import cc.bamboo.framework.common.exception.ServiceException;
import cc.bamboo.framework.tenant.core.aop.TenantIgnore;
import cc.bamboo.module.project.dal.dataobject.projectminingconfig.ProjectMiningConfigDO;
import cc.bamboo.module.project.dal.dataobject.projectfundconfig.ProjectFundConfigDO;
import cc.bamboo.module.project.dal.dataobject.projectorder.ProjectOrderDO;
import cc.bamboo.module.project.dal.dataobject.systemcoin.SystemCoinDO;
import cc.bamboo.module.project.dal.mysql.projectminingconfig.ProjectMiningConfigMapper;
import cc.bamboo.module.project.dal.mysql.projectfundconfig.ProjectFundConfigMapper;
import cc.bamboo.module.project.dal.mysql.projectorder.ProjectOrderMapper;
import cc.bamboo.module.project.dal.mysql.systemcoin.SystemCoinMapper;
import cc.bamboo.module.project.enums.SendStatusEnum;
import cc.bamboo.module.project.service.projectinfo.ProjectInfoService;
import cc.bamboo.module.project.service.projectinfo.dto.ProjectPoolConfigDTO;
import cc.bamboo.module.project.service.spiderpool.SpiderPoolService;
import cc.bamboo.module.project.service.spiderpool.dto.SpiderPoolDayProfitReqDTO;
import cc.bamboo.module.project.service.spiderpool.dto.SpiderPoolDayProfitRespDTO;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import cc.bamboo.module.project.controller.admin.projectrevenue.vo.*;
import cc.bamboo.module.project.dal.dataobject.projectrevenue.ProjectRevenueDO;
import cc.bamboo.module.project.dal.dataobject.projectinfo.ProjectInfoDO;
import cc.bamboo.module.project.dal.dataobject.projectorderbalance.ProjectOrderBalanceDO;
import cc.bamboo.module.project.dal.dataobject.orderdailyincome.OrderDailyIncomeDO;
import cc.bamboo.module.project.dal.dataobject.projectorderbalancelog.ProjectOrderBalanceLogDO;
import cc.bamboo.module.project.enums.BalanceLogTypeEnum;
import cc.bamboo.framework.common.pojo.PageResult;
import cc.bamboo.framework.common.util.object.BeanUtils;

import cc.bamboo.module.project.dal.mysql.projectrevenue.ProjectRevenueMapper;
import cc.bamboo.module.project.dal.mysql.projectinfo.ProjectInfoMapper;
import cc.bamboo.module.project.dal.mysql.projectorderbalance.ProjectOrderBalanceMapper;
import cc.bamboo.module.project.dal.mysql.orderdailyincome.OrderDailyIncomeMapper;
import cc.bamboo.module.project.dal.mysql.projectorderbalancelog.ProjectOrderBalanceLogMapper;
import cc.bamboo.module.project.dal.mysql.projectoperation.ProjectOperationMapper;
import cc.bamboo.module.project.mq.message.IncomeDistributeMessage;
import cc.bamboo.module.project.mq.producer.IncomeDistributeProducer;

import static cc.bamboo.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cc.bamboo.module.project.enums.ApiConstants.ASSET_TYPE_CLOSED_FUND;
import static cc.bamboo.module.project.enums.ErrorCodeConstants.*;

/**
 * 项目收益 Service 实现类
 *
 * @author Swolf
 */
@Service
@Validated
@Slf4j
public class ProjectRevenueServiceImpl implements ProjectRevenueService {

    @Resource
    private ProjectRevenueMapper revenueMapper;

    @Resource
    private ProjectInfoMapper projectInfoMapper;

    @Resource
    private ProjectOrderBalanceMapper orderBalanceMapper;

    @Resource
    private OrderDailyIncomeMapper orderDailyIncomeMapper;

    @Resource
    private ProjectOrderBalanceLogMapper balanceLogMapper;

    @Resource
    private ProjectOperationMapper projectOperationMapper;

    @Resource
    private ProjectOrderMapper projectOrderMapper;

    @Resource
    private IncomeDistributeProducer incomeDistributeProducer;

    @Resource
    private SpiderPoolService spiderPoolService;

    @Resource
    private ProjectInfoService projectInfoService;

    @Resource
    private SystemCoinMapper systemCoinMapper;

    @Resource
    private ProjectMiningConfigMapper projectMiningConfigMapper;

    @Resource
    private ProjectFundConfigMapper projectFundConfigMapper;

    private static final BigDecimal H_TO_T = new BigDecimal("1000000000000");

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createRevenue(ProjectRevenueSaveReqVO createReqVO) {
        log.info("[createRevenue] 开始创建项目收益，项目ID: {}, 日期: {}", createReqVO.getProjectId(), createReqVO.getRevenueDate());

        // 1. 检查该项目该日期是否已有收益记录
        LambdaQueryWrapper<ProjectRevenueDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ProjectRevenueDO::getProjectId, createReqVO.getProjectId());
        queryWrapper.eq(ProjectRevenueDO::getRevenueDate, createReqVO.getRevenueDate());
        Long count = revenueMapper.selectCount(queryWrapper);
        if (count > 0) {
            log.warn("[createRevenue] 该项目当日已有收益记录，项目ID: {}, 日期: {}", createReqVO.getProjectId(),
                    createReqVO.getRevenueDate());
            throw new cc.bamboo.framework.common.exception.ServiceException(999, "该项目当日已有收益记录,不能重复创建");
        }

        // 2. 验证项目存在
        ProjectInfoDO project = projectInfoMapper.selectById(createReqVO.getProjectId());
        if (project == null) {
            log.warn("[createRevenue] 项目不存在，项目ID: {}", createReqVO.getProjectId());
            throw exception(PROJECT_NOT_FOUND);
        }

        // 3. 创建收益记录
        ProjectRevenueDO revenue = BeanUtils.toBean(createReqVO, ProjectRevenueDO.class);
        revenue.setProjectName(project.getProjectName());
        revenue.setIsSend(SendStatusEnum.NOT_SEND.getStatus()); // 默认未发放
        revenue.setIsSync(SendStatusEnum.SEND.getStatus()); // 默认已同步
        revenueMapper.insert(revenue);

        log.info("[createRevenue] 收益记录已创建，收益ID: {}, 项目ID: {}", revenue.getId(), createReqVO.getProjectId());

        // 4. 同步创建订单收益记录(biz_order_daily_income)
        calculateOrderIncome(revenue.getId());

        return revenue.getId();
    }

    @Override
    public void updateRevenue(ProjectRevenueSaveReqVO updateReqVO) {
        // 校验存在
        validateRevenueExists(updateReqVO.getId());
        // 更新
        ProjectRevenueDO updateObj = BeanUtils.toBean(updateReqVO, ProjectRevenueDO.class);
        revenueMapper.updateById(updateObj);
    }

    @Override
    public void deleteRevenue(Long id) {
        // 校验存在
        validateRevenueExists(id);
        // 删除
        revenueMapper.deleteById(id);
    }

    private void validateRevenueExists(Long id) {
        if (revenueMapper.selectById(id) == null) {
            throw exception(REVENUE_NOT_EXISTS);
        }
    }

    @Override
    public ProjectRevenueDO getRevenue(Long id) {
        return revenueMapper.selectById(id);
    }

    @Override
    public PageResult<ProjectRevenueDO> getRevenuePage(ProjectRevenuePageReqVO pageReqVO) {
        return revenueMapper.selectPage(pageReqVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long generateDailyRevenue(Long projectId, LocalDate revenueDate) {
        log.info("[generateDailyRevenue] 开始生成每日项目收益，项目ID: {}, 收益日期: {}", projectId, revenueDate);

        // 1. 查询项目信息
        ProjectInfoDO project = projectInfoMapper.selectById(projectId);
        if (project == null) {
            log.warn("[generateDailyRevenue] 项目不存在，项目ID: {}", projectId);
            throw exception(PROJECT_NOT_FOUND);
        }
        // 查看今天是否有收益数据
        LambdaQueryWrapper<ProjectRevenueDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ProjectRevenueDO::getProjectId, projectId);
        queryWrapper.eq(ProjectRevenueDO::getRevenueDate, revenueDate);
        Long count = revenueMapper.selectCount(queryWrapper);
        if (count > 0) {
            log.warn("[generateDailyRevenue] 项目当日已有收益记录，项目ID: {}，日期: {}", projectId, revenueDate);
            return null;
        }

        // 2. 获取实际收益数据 (从SpiderPool获取)
        BigDecimal coinRevenue = BigDecimal.ZERO;
        BigDecimal coinRate = BigDecimal.ZERO;
        BigDecimal dailyRevenue = BigDecimal.ZERO; // Fiat Revenue (Total)
        BigDecimal computingPower = BigDecimal.ZERO;
        Integer isSync = SendStatusEnum.SEND.getStatus();
        String remark = "系统自动生成";
        String coin = project.getEarningCurrency();
        BigDecimal USDRate = BigDecimal.ONE;

        SystemCoinDO coinDO = systemCoinMapper.getCoinByCode(coin);

        try {
            // 获取矿池配置
            ProjectPoolConfigDTO poolConfig = projectInfoService.getPoolConfig(projectId);
            if (poolConfig == null || StrUtil.hasBlank(poolConfig.getPoolAccessKey(),
                    poolConfig.getPoolPrivateKey(), poolConfig.getPoolName())) {
                throw new RuntimeException("项目矿池配置不完整");
            }

            // 获取矿池日收益
            long timestamp = revenueDate.atStartOfDay(ZoneId.of("UTC")).toEpochSecond();
            SpiderPoolDayProfitReqDTO reqDTO = new SpiderPoolDayProfitReqDTO();
            reqDTO.setAccessKey(poolConfig.getPoolAccessKey());
            reqDTO.setPrivateKey(poolConfig.getPoolPrivateKey());
            reqDTO.setCoin(coinDO.getPoolSymbol());
            reqDTO.setSubaccount(poolConfig.getPoolName());
            reqDTO.setTimeStamp(timestamp);

            SpiderPoolDayProfitRespDTO dayProfit = spiderPoolService.getDayProfitDetailInfo(reqDTO);
            if (dayProfit == null) {
                log.warn("[generateDailyRevenue] 获取矿池收益为空 (可能是当日无收益)，项目ID: {}, 日期: {}", projectId, revenueDate);
                // throw new RuntimeException("矿池收益为空");
                // 此时保持0
            } else {
                coinRevenue = dayProfit.getDayProfit();
                BigDecimal avg = Optional.ofNullable(dayProfit.getAvgShareAccept())
                        .orElse(BigDecimal.ZERO);
                // 算力转换 H/s -> T/s (1T = 10^12) .divide(H_TO_T...)
                computingPower = avg.divide(H_TO_T, 10, RoundingMode.HALF_UP);
            }
            // 获取USDT汇率
            coinRate = spiderPoolService.getCoinRate(coinDO.getBinanceSymbol());
            if (coinRate == null || coinRate.compareTo(BigDecimal.ZERO) == 0) {
                // 尝试兜底或报错
                log.warn("[generateDailyRevenue] 获取汇率失败或为0，项目ID: {}", projectId);
                throw new RuntimeException("获取汇率失败");
            }
            // 获取USDT-USD汇率
            USDRate = spiderPoolService.getUSDTUSDRate();

            // COIN - USD汇率
            coinRate = coinRate.multiply(USDRate).setScale(8, RoundingMode.HALF_UP);
            ;
            // 计算法币总收益
            dailyRevenue = coinRevenue.multiply(coinRate).setScale(8, RoundingMode.HALF_UP);

            log.info("[generateDailyRevenue] 收益获取完成，项目ID: {}, {}收益: {}, 汇率: {}, 法币收益: {}, 算力: {}",
                    projectId, coin, coinRevenue, coinRate, dailyRevenue, computingPower);

        } catch (Exception e) {
            log.error("[generateDailyRevenue] 获取收益数据失败，项目ID: {}, 错误: {}", projectId, e.getMessage());
            isSync = SendStatusEnum.NOT_SEND.getStatus(); // 标记为未同步
            remark = "获取收益失败: " + e.getMessage();
        }

        // 获取项目配置
        ProjectMiningConfigDO projectMiningConfigDO = projectMiningConfigMapper.selectById(projectId);

        // 3. 计算电力成本 = 总算力(T) * 单T功耗(W) * 电费(元/度) * 24小时 / 1000
        BigDecimal electricityCost = BigDecimal.ZERO;
        if (projectMiningConfigDO != null && projectMiningConfigDO.getPowerConsumption() != null
                && projectMiningConfigDO.getElectricityPrice() != null) {
            electricityCost = computingPower
                    .multiply(projectMiningConfigDO.getPowerConsumption())
                    .multiply(projectMiningConfigDO.getElectricityPrice())
                    .multiply(new BigDecimal(24))
                    .divide(new BigDecimal(1000), 4, RoundingMode.HALF_UP);
        }
        log.info("[generateDailyRevenue] 电力成本计算完成，项目ID: {}, 电力成本: {}", projectId, electricityCost);

        // 4. 获取运维成本 (固定值/30)
        BigDecimal peopleCost = BigDecimal.ZERO;
        if (projectMiningConfigDO != null && projectMiningConfigDO.getOperationCost() != null) {
            peopleCost = projectMiningConfigDO.getOperationCost().divide(new BigDecimal(30), 4, RoundingMode.HALF_UP);
        }
        log.info("[generateDailyRevenue] 运维成本计算完成，项目ID: {}, 运维成本: {}", projectId, peopleCost);

        // 5. 计算净收益 (Fiat)
        BigDecimal netRevenueFiat = dailyRevenue.subtract(electricityCost).subtract(peopleCost);

        // 6. 计算团队分成 (Project Revenue)
        BigDecimal projectRevenue = BigDecimal.ZERO; // 团队分成 (Fiat)
        if (netRevenueFiat.compareTo(BigDecimal.ZERO) > 0
                && projectMiningConfigDO != null && projectMiningConfigDO.getTeamShareRatio() != null) {
            BigDecimal ratio = projectMiningConfigDO.getTeamShareRatio().divide(new BigDecimal(100)); // 百分比转小数
            projectRevenue = netRevenueFiat.multiply(ratio).setScale(4, RoundingMode.HALF_UP);
        }

        // 7. 计算可发放收益 (Coin) = (NetFiat - ProjectFiat) / Rate
        BigDecimal availableRevenue = BigDecimal.ZERO;
        if (netRevenueFiat.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal availableFiat = netRevenueFiat.subtract(projectRevenue);
            if (availableFiat.compareTo(BigDecimal.ZERO) > 0 && coinRate.compareTo(BigDecimal.ZERO) > 0) {
                availableRevenue = availableFiat.divide(coinRate, 8, RoundingMode.HALF_DOWN);
            }
        }

        log.info("[generateDailyRevenue] 可发放收益计算完成，项目ID: {}, 法币净收益: {}, 团队分成: {}, 可发放收益(Coin): {}",
                projectId, netRevenueFiat, projectRevenue, availableRevenue);

        // 8. 创建项目收益记录
        ProjectRevenueDO revenue = ProjectRevenueDO.builder()
                .revenueDate(revenueDate)
                .projectId(projectId)
                .projectName(project.getProjectName())
                .electricityCost(electricityCost)
                .peopleCost(peopleCost)
                .projectRevenue(coinRate != null && coinRate.compareTo(BigDecimal.ZERO) != 0
                        ? projectRevenue.divide(coinRate, 8, RoundingMode.HALF_DOWN)
                        : BigDecimal.ZERO) // 团队分成(Fiat)
                .availableRevenue(availableRevenue) // 可发放(Coin)
                .coinRevenue(coinRevenue) // 币种收益(Coin)
                .exchangeRate(coinRate)
                .coinCode(coin)
                .quantity(project.getSalesQuantity())
                .computingPower(computingPower)
                .isSend(SendStatusEnum.NOT_SEND.getStatus()) // 未发放
                .isSync(isSync)
                .remark(remark)
                .build();

        revenueMapper.insert(revenue);

        log.info("[generateDailyRevenue] 项目收益记录已创建，收益ID: {}, 项目ID: {}", revenue.getId(), projectId);

        return revenue.getId();
    }

    @Transactional(rollbackFor = Exception.class)
    public Long generateDailyRevenueMork(Long projectId, LocalDate revenueDate, BigDecimal coinRevenue) {
        log.info("[generateDailyRevenueMork] 开始生成模拟每日项目收益，项目ID: {}, 收益日期: {}", projectId, revenueDate);

        // 1. 查询项目信息
        ProjectInfoDO project = projectInfoMapper.selectById(projectId);
        if (project == null) {
            log.warn("[generateDailyRevenueMork] 项目不存在，项目ID: {}", projectId);
            throw exception(PROJECT_NOT_FOUND);
        }
        // 查看今天是否有收益数据
        LambdaQueryWrapper<ProjectRevenueDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ProjectRevenueDO::getProjectId, projectId);
        queryWrapper.eq(ProjectRevenueDO::getRevenueDate, revenueDate);
        if (revenueMapper.selectCount(queryWrapper) > 0) {
            log.warn("[generateDailyRevenueMork] 项目当日已有收益记录，项目ID: {}，日期: {}", projectId, revenueDate);
            return null;
        }

        ProjectRevenueDO.ProjectRevenueDOBuilder revenueBuilder = ProjectRevenueDO.builder()
                .revenueDate(revenueDate)
                .projectId(projectId)
                .projectName(project.getProjectName())
                .coinRevenue(coinRevenue)
                .isSend(SendStatusEnum.NOT_SEND.getStatus())
                .isSync(SendStatusEnum.SEND.getStatus())
                .remark("手动模拟生成");

        if (Objects.equals(project.getProjectConfigType(), 0)) { // 挖矿型
            log.info("[generateDailyRevenueMork] 识别为挖矿型项目，开始模拟计算...");
            BigDecimal computingPower = new BigDecimal(500); // 默认模拟算力
            BigDecimal coinRate = BigDecimal.ZERO;
            String coin = project.getEarningCurrency();
            SystemCoinDO coinDO = systemCoinMapper.getCoinByCode(coin);

            try {
                // 获取实时汇率
                BigDecimal rate = spiderPoolService.getCoinRate(coinDO != null ? coinDO.getBinanceSymbol() : "BTCUSDT");
                BigDecimal usdRate = spiderPoolService.getUSDTUSDRate();
                if (rate != null && usdRate != null) {
                    coinRate = rate.multiply(usdRate).setScale(8, RoundingMode.HALF_UP);
                } else {
                    coinRate = new BigDecimal("60000"); // 兜底模拟汇率
                }
            } catch (Exception e) {
                log.warn("[generateDailyRevenueMork] 获取汇率失败，使用模拟常数: {}", e.getMessage());
                coinRate = new BigDecimal("60000");
            }

            // 获取项目配置
            ProjectMiningConfigDO miningConfig = projectMiningConfigMapper.selectById(projectId);
            BigDecimal electricityCost = BigDecimal.ZERO;
            if (miningConfig != null && miningConfig.getPowerConsumption() != null
                    && miningConfig.getElectricityPrice() != null) {
                electricityCost = computingPower
                        .multiply(miningConfig.getPowerConsumption())
                        .multiply(miningConfig.getElectricityPrice())
                        .multiply(new BigDecimal(24))
                        .divide(new BigDecimal(1000), 2, RoundingMode.HALF_UP);
            }

            BigDecimal peopleCost = BigDecimal.ZERO;
            if (miningConfig != null && miningConfig.getOperationCost() != null) {
                peopleCost = miningConfig.getOperationCost().divide(new BigDecimal(30), 2, RoundingMode.HALF_UP);
            }

            // 计算法币收益
            BigDecimal dailyRevenueFiat = coinRevenue.multiply(coinRate).setScale(2, RoundingMode.HALF_UP);

            // 计算分成
            BigDecimal projectRevenueFiat = BigDecimal.ZERO;
            BigDecimal netRevenueFiat = dailyRevenueFiat.subtract(electricityCost).subtract(peopleCost);
            if (netRevenueFiat.compareTo(BigDecimal.ZERO) > 0 && miningConfig != null
                    && miningConfig.getTeamShareRatio() != null) {
                BigDecimal ratio = miningConfig.getTeamShareRatio().divide(new BigDecimal(100));
                projectRevenueFiat = netRevenueFiat.multiply(ratio).setScale(2, RoundingMode.HALF_UP);
            }

            // 归并到 Builder
            BigDecimal availableFiat = netRevenueFiat.subtract(projectRevenueFiat);
            BigDecimal availableRevenue = availableFiat.compareTo(BigDecimal.ZERO) > 0
                    && coinRate.compareTo(BigDecimal.ZERO) > 0
                            ? availableFiat.divide(coinRate, 8, RoundingMode.HALF_DOWN)
                            : BigDecimal.ZERO;

            revenueBuilder.electricityCost(electricityCost)
                    .peopleCost(peopleCost)
                    .projectRevenue(coinRate.compareTo(BigDecimal.ZERO) > 0
                            ? projectRevenueFiat.divide(coinRate, 8, RoundingMode.HALF_DOWN)
                            : BigDecimal.ZERO)
                    .availableRevenue(availableRevenue)
                    .exchangeRate(coinRate)
                    .coinCode(coin)
                    .computingPower(computingPower);

        } else { // 基金型
            log.info("[generateDailyRevenueMork] 识别为基金型项目，开始模拟计算...");
            ProjectFundConfigDO fundConfig = projectFundConfigMapper.selectById(projectId);
            if (fundConfig == null) {
                throw new ServiceException(999, "基金项目配置不存在");
            }

            BigDecimal peopleCost = fundConfig.getOperationCost() != null
                    ? fundConfig.getOperationCost().divide(new BigDecimal(30), 8, RoundingMode.HALF_UP)
                    : BigDecimal.ZERO;

            BigDecimal projectRevenue = BigDecimal.ZERO;
            BigDecimal netRevenueFiat = coinRevenue.subtract(peopleCost);
            if (coinRevenue.compareTo(BigDecimal.ZERO) > 0 && fundConfig.getTeamShareRatio() != null) {
                BigDecimal ratio = fundConfig.getTeamShareRatio().divide(new BigDecimal(100), 8, RoundingMode.HALF_UP);
                projectRevenue = netRevenueFiat.multiply(ratio).setScale(8, RoundingMode.HALF_UP);
            }

            BigDecimal availableRevenue = coinRevenue.subtract(peopleCost).subtract(projectRevenue);
            if (availableRevenue.compareTo(BigDecimal.ZERO) < 0) {
                availableRevenue = BigDecimal.ZERO;
            }

            revenueBuilder.electricityCost(BigDecimal.ZERO)
                    .peopleCost(peopleCost)
                    .projectRevenue(projectRevenue)
                    .availableRevenue(availableRevenue)
                    .exchangeRate(BigDecimal.ONE)
                    .coinCode(project.getEarningCurrency())
                    .computingPower(BigDecimal.ZERO);
        }

        ProjectRevenueDO revenue = revenueBuilder.build();
        revenueMapper.insert(revenue);

        log.info("[generateDailyRevenueMork] 项目收益记录已创建，收益ID: {}, 项目ID: {}", revenue.getId(), projectId);

        // 9. 计算订单收益
        calculateOrderIncome(revenue.getId());

        return revenue.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void calculateOrderIncome(Long projectRevenueId) {
        log.info("[calculateOrderIncome] 开始计算订单收益，项目收益ID: {}", projectRevenueId);

        // 1. 查询项目收益记录
        ProjectRevenueDO projectRevenue = revenueMapper.selectById(projectRevenueId);
        if (projectRevenue == null || projectRevenue.getAvailableRevenue() == null
                || SendStatusEnum.NOT_SEND.getStatus().equals(projectRevenue.getIsSync())) {
            log.warn("[calculateOrderIncome] 项目收益不存在，收益ID: {}", projectRevenueId);
            return;
        }

        // 2. 查询项目信息
        ProjectInfoDO project = projectInfoMapper.selectById(projectRevenue.getProjectId());
        if (project == null) {
            log.warn("[calculateOrderIncome] 项目不存在，项目ID: {}", projectRevenue.getProjectId());
            return;
        }

        // 3. 查询该项目的所有订单余额(持有份额>0)
        List<ProjectOrderBalanceDO> orderBalances = orderBalanceMapper.selectList(
                ProjectOrderBalanceDO::getProjectId, projectRevenue.getProjectId());

        // 过滤出持有份额大于0的订单
        List<ProjectOrderBalanceDO> activeBalances = orderBalances.stream()
                .filter(balance -> balance.getHoldQuantity() != null && balance.getHoldQuantity() > 0)
                .collect(Collectors.toList());

        if (activeBalances.isEmpty()) {
            log.warn("[calculateOrderIncome] 该项目没有持有份额的订单，项目ID: {}", projectRevenue.getProjectId());
            return;
        }

        List<Long> orderIds = activeBalances.stream()
                .map(ProjectOrderBalanceDO::getOrderId)
                .collect(Collectors.toList());
        List<ProjectOrderDO> projectOrders = projectOrderMapper.selectList(
                new LambdaQueryWrapper<ProjectOrderDO>()
                        .in(ProjectOrderDO::getId, orderIds));
        Map<Long, ProjectOrderDO> orderMap = projectOrders.stream()
                .collect(Collectors.toMap(ProjectOrderDO::getId, order -> order));

        // 4. 计算项目总份额
        Integer projectTotalQuantity = project.getIssueQuantity();

        // 封闭型的总份额 = 已售出份额
        if (ASSET_TYPE_CLOSED_FUND.equals(project.getProjectType())) {
            projectTotalQuantity = project.getSalesQuantity();
        }

        log.info("[calculateOrderIncome] 项目总份额: {}, 活跃订单数: {}", projectTotalQuantity, activeBalances.size());

        // 5. 为每笔订单计算收益
        List<OrderDailyIncomeDO> orderIncomes = new ArrayList<>();
        for (ProjectOrderBalanceDO balance : activeBalances) {
            // 订单收益 = 订单份额 / 项目总份额 * 项目收益
            BigDecimal orderIncome = projectRevenue.getAvailableRevenue()
                    .multiply(new BigDecimal(balance.getHoldQuantity()))
                    .divide(new BigDecimal(projectTotalQuantity), 8, RoundingMode.HALF_UP);

            // 从订单映射中获取订单号
            ProjectOrderDO order = orderMap.get(balance.getOrderId());
            // 计算当日收益率=当前当日收益/订单价值
            // BigDecimal dailyReturn = orderIncome.divide(balance.getHoldAmount(), 2,
            // RoundingMode.HALF_UP);
            OrderDailyIncomeDO orderDailyIncome = OrderDailyIncomeDO.builder()
                    .userId(balance.getUserId())
                    .projectId(balance.getProjectId())
                    .orderId(balance.getOrderId())
                    .projectRevenueId(projectRevenueId)
                    .incomeDate(projectRevenue.getRevenueDate())
                    .holdQuantity(balance.getHoldQuantity())
                    .dailyIncome(orderIncome)
                    .status(SendStatusEnum.NOT_SEND.getStatus()) // 未发放
                    .coinCode(balance.getEarningCurrency())
                    .build();
            if (order != null) {
                orderDailyIncome.setOrderNo(order.getOrderNo());
            }
            orderDailyIncome.setTenantId(project.getTenantId());
            orderIncomes.add(orderDailyIncome);

            log.debug("[calculateOrderIncome] 订单收益计算完成，订单ID: {}, 持有份额: {}, 订单收益: {}",
                    balance.getOrderId(), balance.getHoldQuantity(), orderIncome);
        }

        // 6. 批量插入订单收益记录
        orderDailyIncomeMapper.insertBatch(orderIncomes);

        log.info("[calculateOrderIncome] 订单收益计算完成，项目收益ID: {}, 订单数: {}",
                projectRevenueId, orderIncomes.size());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void distributeIncome(Long projectRevenueId) {

        // 3. 查询未发放的订单收益
        List<OrderDailyIncomeDO> orderIncomes = orderDailyIncomeMapper.selectUnsentByProjectRevenueId(projectRevenueId);

        if (orderIncomes.isEmpty()) {
            log.warn("[distributeIncome] 没有待发放的订单收益，收益ID: {}", projectRevenueId);
            return;
        }

        log.info("[distributeIncome] 待发放订单数: {}", orderIncomes.size());

        // 4. 发送消息到队列,异步处理每笔订单收益
        for (OrderDailyIncomeDO orderIncome : orderIncomes) {
            IncomeDistributeMessage message = IncomeDistributeMessage.builder()
                    .projectRevenueId(projectRevenueId)
                    .orderDailyIncomeId(orderIncome.getId())
                    .orderId(orderIncome.getOrderId())
                    .userId(orderIncome.getUserId())
                    .projectId(orderIncome.getProjectId())
                    .orderDailyIncome(orderIncome.getDailyIncome())
                    .build();

            incomeDistributeProducer.sendIncomeDistributeMessage(message);
        }

        // 修改为已发放
        LambdaUpdateWrapper<ProjectRevenueDO> projectRevenueQueryWrapper = new LambdaUpdateWrapper<>();
        projectRevenueQueryWrapper.eq(ProjectRevenueDO::getId, projectRevenueId);
        projectRevenueQueryWrapper.eq(ProjectRevenueDO::getIsSend, SendStatusEnum.NOT_SEND.getStatus());
        projectRevenueQueryWrapper.set(ProjectRevenueDO::getIsSend, SendStatusEnum.SEND.getStatus());
        int result = revenueMapper.update(projectRevenueQueryWrapper);
        if (result > 0) {
            ProjectRevenueDO projectRevenueDO = revenueMapper.selectById(projectRevenueId);
            // 更新项目收益
            projectOperationMapper.incrementProjectIncome(projectRevenueDO.getProjectId(),
                    projectRevenueDO.getProjectRevenue());
        }

        log.info("[distributeIncome] 收益发放消息已全部发送，项目收益ID: {}, 消息数量: {}",
                projectRevenueId, orderIncomes.size());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void distributeIncome(LocalDate incomeDate) {
        List<ProjectRevenueDO> projectRevenueList = revenueMapper.selectList(new LambdaQueryWrapper<ProjectRevenueDO>()
                .eq(ProjectRevenueDO::getRevenueDate, incomeDate)
                .eq(ProjectRevenueDO::getIsSend, SendStatusEnum.NOT_SEND.getStatus()));
        for (ProjectRevenueDO projectRevenue : projectRevenueList) {
            distributeIncome(projectRevenue.getId());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @TenantIgnore
    public void processOrderIncome(IncomeDistributeMessage message) {
        log.debug("[processOrderIncome] 开始处理订单收益，订单ID: {}, 收益金额: {}",
                message.getOrderId(), message.getOrderDailyIncome());

        // 1. 查询订单余额
        ProjectOrderBalanceDO balance = orderBalanceMapper.selectOne(
                ProjectOrderBalanceDO::getOrderId, message.getOrderId());

        if (balance == null) {
            log.warn("[processOrderIncome] 订单余额不存在，订单ID: {}", message.getOrderId());
            return;
        }
        BigDecimal newTotalIncome = balance.getTotalIncome().add(message.getOrderDailyIncome());
        // 4. 更新订单收益为已发放
        LambdaUpdateWrapper<OrderDailyIncomeDO> orderDailyIncomeQueryWrapper = new LambdaUpdateWrapper<>();
        orderDailyIncomeQueryWrapper.eq(OrderDailyIncomeDO::getId, message.getOrderDailyIncomeId());
        orderDailyIncomeQueryWrapper.eq(OrderDailyIncomeDO::getStatus, SendStatusEnum.NOT_SEND.getStatus());
        orderDailyIncomeQueryWrapper.set(OrderDailyIncomeDO::getStatus, SendStatusEnum.SEND.getStatus());
        orderDailyIncomeQueryWrapper.set(OrderDailyIncomeDO::getIssueTime, LocalDateTime.now());
        orderDailyIncomeQueryWrapper.set(OrderDailyIncomeDO::getCumulativeIncome, newTotalIncome);
        int result = orderDailyIncomeMapper.update(orderDailyIncomeQueryWrapper);
        if (result > 0) {
            // 2. 更新订单余额

            BigDecimal newUnwithdrawnDividend = balance.getUnwithdrawnDividend().add(message.getOrderDailyIncome());

            orderBalanceMapper.updateBalanceAddIncome(balance.getId(), message.getOrderDailyIncome());

            // 3. 记录余额变动日志(分红发放，收益增加)
            ProjectOrderBalanceLogDO logDO = ProjectOrderBalanceLogDO.builder()
                    .userId(message.getUserId())
                    .projectId(message.getProjectId())
                    .orderId(message.getOrderId())
                    .amount(message.getOrderDailyIncome())
                    .coinCode(balance.getEarningCurrency())
                    .afterAmount(newUnwithdrawnDividend)
                    .type(BalanceLogTypeEnum.DIVIDEND_GRANT.getType())
                    .build();
            logDO.setTenantId(balance.getTenantId());
            balanceLogMapper.insert(logDO);

            // 5. 更新项目运营统计(累计投资人收益)
            projectOperationMapper.incrementTotalInvestorIncome(message.getProjectId(), message.getOrderDailyIncome());
            log.debug("[processOrderIncome] 订单收益处理完成，订单ID: {}, 发放金额: {}, 累计收益: {}",
                    message.getOrderId(), message.getOrderDailyIncome(), newTotalIncome);
        } else {
            log.warn("[processOrderIncome] 订单收益已发放，订单ID: {}", message.getOrderId());
        }

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAvailableRevenue(UpdateAvailableRevenueReqVO reqVO) {
        log.info("[updateAvailableRevenue] 开始修改可发放收益，收益ID: {}, 新可发放收益: {}", reqVO.getId(), reqVO.getAvailableRevenue());

        Long id = reqVO.getId();
        BigDecimal availableRevenue = reqVO.getAvailableRevenue();
        String remark = reqVO.getRemark();
        BigDecimal coinRevenue = reqVO.getCoinRevenue();

        // 1. 查询项目收益记录
        ProjectRevenueDO projectRevenue = revenueMapper.selectById(id);
        if (projectRevenue == null) {
            log.warn("[updateAvailableRevenue] 项目收益不存在，收益ID: {}", id);
            throw exception(REVENUE_NOT_EXISTS);
        }

        // 2. 检查是否未发放状态
        if (projectRevenue.getIsSend() != null && projectRevenue.getIsSend() == SendStatusEnum.SEND.getStatus()) {
            log.warn("[updateAvailableRevenue] 收益已发放,无法修改，收益ID: {}", id);
            throw new ServiceException(999, "收益已发放,无法修改");
        }

        // 3. 更新可发放收益
        ProjectRevenueDO updateRevenue = new ProjectRevenueDO();
        updateRevenue.setId(id);
        updateRevenue.setCoinRevenue(coinRevenue);
        updateRevenue.setAvailableRevenue(availableRevenue);
        updateRevenue.setElectricityCost(reqVO.getElectricityCost());
        updateRevenue.setPeopleCost(reqVO.getPeopleCost());
        updateRevenue.setProjectRevenue(reqVO.getProjectRevenue());
        if (remark != null && !remark.isEmpty()) {
            updateRevenue.setRemark(remark);
        }
        revenueMapper.updateById(updateRevenue);

        log.info("[updateAvailableRevenue] 可发放收益更新完成，收益ID: {}, 新可发放收益: {}", id, availableRevenue);

        // 4. 查询是否有订单收益记录
        LambdaQueryWrapper<OrderDailyIncomeDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderDailyIncomeDO::getProjectRevenueId, id);
        List<OrderDailyIncomeDO> orderIncomes = orderDailyIncomeMapper.selectList(queryWrapper);

        if (orderIncomes == null || orderIncomes.isEmpty()) {
            log.info("[updateAvailableRevenue] 无订单收益记录需要更新，收益ID: {}", id);
            return;
        }

        log.info("[updateAvailableRevenue] 发现订单收益记录,需要重新计算，收益ID: {}, 记录数: {}", id, orderIncomes.size());

        // 5. 查询项目信息获取总份额
        ProjectInfoDO project = projectInfoMapper.selectById(projectRevenue.getProjectId());
        if (project == null) {
            log.warn("[updateAvailableRevenue] 项目不存在，项目ID: {}", projectRevenue.getProjectId());
            throw exception(PROJECT_NOT_FOUND);
        }

        Integer projectTotalQuantity = project.getIssueQuantity();

        // 6. 重新计算每笔订单收益并更新
        for (OrderDailyIncomeDO orderIncome : orderIncomes) {
            // 检查是否未发放
            if (orderIncome.getStatus() != null && orderIncome.getStatus() == SendStatusEnum.SEND.getStatus()) {
                log.warn("[updateAvailableRevenue] 订单收益已发放,跳过更新，订单收益ID: {}", orderIncome.getId());
                continue;
            }

            // 重新计算订单收益 = 订单份额 / 项目总份额 * 可发放收益
            BigDecimal newOrderIncome = availableRevenue
                    .multiply(new BigDecimal(orderIncome.getHoldQuantity()))
                    .divide(new BigDecimal(projectTotalQuantity), 2, RoundingMode.HALF_UP);

            // 更新订单收益
            LambdaUpdateWrapper<OrderDailyIncomeDO> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(OrderDailyIncomeDO::getId, orderIncome.getId());
            updateWrapper.set(OrderDailyIncomeDO::getDailyIncome, newOrderIncome);
            orderDailyIncomeMapper.update(updateWrapper);

            log.debug("[updateAvailableRevenue] 订单收益更新完成，订单收益ID: {}, 原收益: {}, 新收益: {}",
                    orderIncome.getId(), orderIncome.getDailyIncome(), newOrderIncome);
        }

        log.info("[updateAvailableRevenue] 所有订单收益重新计算完成，收益ID: {}, 更新记录数: {}", id, orderIncomes.size());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long generateDailyFundRevenue(Long projectId, LocalDate revenueDate) {
        log.info("[generateDailyFundRevenue] 开始生成每日基金项目收益，项目ID: {}, 收益日期: {}", projectId, revenueDate);

        // 1. 查询项目信息
        ProjectInfoDO project = projectInfoMapper.selectById(projectId);
        if (project == null) {
            log.warn("[generateDailyFundRevenue] 项目不存在，项目ID: {}", projectId);
            throw exception(PROJECT_NOT_FOUND);
        }

        // 2. 检查当日是否已有收益记录
        LambdaQueryWrapper<ProjectRevenueDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ProjectRevenueDO::getProjectId, projectId);
        queryWrapper.eq(ProjectRevenueDO::getRevenueDate, revenueDate);
        if (revenueMapper.selectCount(queryWrapper) > 0) {
            log.warn("[generateDailyFundRevenue] 项目当日已有收益记录，项目ID: {}，日期: {}", projectId, revenueDate);
            return null;
        }
        ProjectRevenueDO revenue = new ProjectRevenueDO();
        // 3. 获取基金项目专用配置
        ProjectFundConfigDO fundConfig = projectFundConfigMapper.selectById(projectId);
        if (fundConfig == null) {
            log.warn("[generateDailyFundRevenue] 基金项目配置不存在，项目ID: {}", projectId);
            revenue = ProjectRevenueDO.builder()
                    .revenueDate(revenueDate)
                    .projectId(projectId)
                    .projectName(project.getProjectName())
                    .electricityCost(BigDecimal.ZERO)
                    .peopleCost(BigDecimal.ZERO)
                    .projectRevenue(BigDecimal.ZERO)
                    .availableRevenue(BigDecimal.ZERO)
                    .coinRevenue(BigDecimal.ZERO)
                    .exchangeRate(BigDecimal.ONE) // 基金型默认汇率 1
                    .coinCode(project.getEarningCurrency())
                    .quantity(project.getSalesQuantity())
                    .computingPower(BigDecimal.ZERO)
                    .isSend(SendStatusEnum.NOT_SEND.getStatus())
                    .isSync(SendStatusEnum.NOT_SEND.getStatus())
                    .remark("项目未配置收益方法")
                    .build();
            revenueMapper.insert(revenue);
        } else {

            BigDecimal coinRevenue = BigDecimal.ZERO;
            // 5. 计算运维成本 (固定值/30)
            BigDecimal peopleCost = BigDecimal.ZERO;
            if (fundConfig.getOperationCost() != null) {
                peopleCost = fundConfig.getOperationCost().divide(new BigDecimal(30), 8, RoundingMode.HALF_UP);
            }

            // 6. 计算团队分成
            BigDecimal projectRevenue = BigDecimal.ZERO;
            BigDecimal netRevenueFiat = coinRevenue.subtract(peopleCost);
            if (coinRevenue.compareTo(BigDecimal.ZERO) > 0 && fundConfig.getTeamShareRatio() != null) {
                BigDecimal ratio = fundConfig.getTeamShareRatio().divide(new BigDecimal(100), 8, RoundingMode.HALF_UP);
                projectRevenue = netRevenueFiat.multiply(ratio).setScale(8, RoundingMode.HALF_UP);
            }

            // 7. 计算可发放收益 (基金型币种一致，Rate = 1)
            BigDecimal availableRevenue = coinRevenue.subtract(peopleCost).subtract(projectRevenue);
            if (availableRevenue.compareTo(BigDecimal.ZERO) < 0) {
                availableRevenue = BigDecimal.ZERO;
            }

            // 8. 创建项目收益记录
            revenue = ProjectRevenueDO.builder()
                    .revenueDate(revenueDate)
                    .projectId(projectId)
                    .projectName(project.getProjectName())
                    .electricityCost(BigDecimal.ZERO)
                    .peopleCost(peopleCost)
                    .projectRevenue(projectRevenue)
                    .availableRevenue(availableRevenue)
                    .coinRevenue(coinRevenue)
                    .exchangeRate(BigDecimal.ONE) // 基金型默认汇率 1
                    .coinCode(project.getEarningCurrency())
                    .quantity(project.getSalesQuantity())
                    .computingPower(BigDecimal.ZERO)
                    .isSend(SendStatusEnum.NOT_SEND.getStatus())
                    .isSend(SendStatusEnum.NOT_SEND.getStatus())
                    .remark("系统自动生成")
                    .build();

            revenueMapper.insert(revenue);
        }

        log.info("[generateDailyFundRevenue] 基金项目收益记录已创建，收益ID: {}, 项目ID: {}", revenue.getId(), projectId);

        // 9. 计算订单收益
        // calculateOrderIncome(revenue.getId());

        return revenue.getId();
    }

    @Override
    public BigDecimal getCoinUsdRate(String coinCode) {
        // 获取USDT汇率
        BigDecimal coinRate = spiderPoolService.getCoinRate(coinCode);
        if (coinRate == null || coinRate.compareTo(BigDecimal.ZERO) == 0) {
            // 尝试兜底或报错
            throw new RuntimeException("获取汇率失败");
        }
        // 获取USDT-USD汇率
        BigDecimal USDRate  = spiderPoolService.getUSDTUSDRate();

        // COIN - USD汇率
        coinRate = coinRate.multiply(USDRate).setScale(8, RoundingMode.HALF_UP);
        return coinRate;
    }

    @Override
    public CalculateCostsRespVO calculateCosts(Long projectId, BigDecimal revenue, LocalDate revenueDate) {
        log.info("[calculateCosts] 开始计算成本，项目ID: {}, 收益: {}, 日期: {}", projectId, revenue, revenueDate);

        // 1. 查询项目信息
        ProjectInfoDO project = projectInfoMapper.selectById(projectId);
        if (project == null) {
            log.warn("[calculateCosts] 项目不存在，项目ID: {}", projectId);
            throw exception(PROJECT_NOT_FOUND);
        }

        CalculateCostsRespVO result = new CalculateCostsRespVO();
        BigDecimal electricityCost = BigDecimal.ZERO;
        BigDecimal peopleCost = BigDecimal.ZERO;
        BigDecimal projectRevenue = BigDecimal.ZERO;

        if (Objects.equals(project.getProjectConfigType(), 0)) { // 挖矿型
            log.info("[calculateCosts] 识别为挖矿型项目，开始获取矿池数据...");

            // 获取项目配置
            ProjectMiningConfigDO config = projectMiningConfigMapper.selectById(projectId);
            if (config == null) {
                throw new ServiceException(999, "未配置项目挖矿型参数，请手动输入成本");
            }

            BigDecimal coinRevenue = revenue;
            BigDecimal coinRate = BigDecimal.ZERO;
            BigDecimal dailyRevenue = BigDecimal.ZERO; // Fiat Revenue (Total)
            BigDecimal computingPower = BigDecimal.ZERO;
            String coin = project.getEarningCurrency();
            BigDecimal USDRate = BigDecimal.ONE;

            SystemCoinDO coinDO = systemCoinMapper.getCoinByCode(coin);

            try {
                // 获取矿池配置
                ProjectPoolConfigDTO poolConfig = projectInfoService.getPoolConfig(projectId);
                if (poolConfig == null || StrUtil.hasBlank(poolConfig.getPoolAccessKey(),
                        poolConfig.getPoolPrivateKey(), poolConfig.getPoolName())) {
                    throw new RuntimeException("项目矿池配置不完整");
                }

                // 获取矿池日收益
                long timestamp = revenueDate.atStartOfDay(ZoneId.of("UTC")).toEpochSecond();
                SpiderPoolDayProfitReqDTO reqDTO = new SpiderPoolDayProfitReqDTO();
                reqDTO.setAccessKey(poolConfig.getPoolAccessKey());
                reqDTO.setPrivateKey(poolConfig.getPoolPrivateKey());
                reqDTO.setCoin(coinDO.getPoolSymbol());
                reqDTO.setSubaccount(poolConfig.getPoolName());
                reqDTO.setTimeStamp(timestamp);

                SpiderPoolDayProfitRespDTO dayProfit = spiderPoolService.getDayProfitDetailInfo(reqDTO);
                if (dayProfit == null) {
                    log.warn("[generateDailyRevenue] 获取矿池收益为空 (可能是当日无收益)，项目ID: {}, 日期: {}", projectId, revenueDate);
                    // throw new RuntimeException("矿池收益为空");
                    // 此时保持0
                } else {
                    BigDecimal avg = Optional.ofNullable(dayProfit.getAvgShareAccept())
                            .orElse(BigDecimal.ZERO);
                    // 算力转换 H/s -> T/s (1T = 10^12) .divide(H_TO_T...)
                    computingPower = avg.divide(H_TO_T, 10, RoundingMode.HALF_UP);
                }
                // 获取USDT汇率
                coinRate = spiderPoolService.getCoinRate(coinDO.getBinanceSymbol());
                if (coinRate == null || coinRate.compareTo(BigDecimal.ZERO) == 0) {
                    // 尝试兜底或报错
                    log.warn("[generateDailyRevenue] 获取汇率失败或为0，项目ID: {}", projectId);
                    throw new RuntimeException("获取汇率失败");
                }
                // 获取USDT-USD汇率
                USDRate = spiderPoolService.getUSDTUSDRate();

                // COIN - USD汇率
                coinRate = coinRate.multiply(USDRate).setScale(8, RoundingMode.HALF_UP);
                ;
                // 计算法币总收益
                dailyRevenue = coinRevenue.multiply(coinRate).setScale(8, RoundingMode.HALF_UP);

                log.info("[generateDailyRevenue] 收益获取完成，项目ID: {}, {}收益: {}, 汇率: {}, 法币收益: {}, 算力: {}",
                        projectId, coin, coinRevenue, coinRate, dailyRevenue, computingPower);

            } catch (Exception e) {
                log.error("[generateDailyRevenue] 获取收益数据失败，项目ID: {}, 错误: {}", projectId, e.getMessage());
                throw new RuntimeException("获取算力信息失败，请手动输入电力成本");
            }

            // 获取项目配置
            ProjectMiningConfigDO projectMiningConfigDO = projectMiningConfigMapper.selectById(projectId);

            // 3. 计算电力成本 = 总算力(T) * 单T功耗(W) * 电费(元/度) * 24小时 / 1000
            electricityCost = BigDecimal.ZERO;
            if (projectMiningConfigDO != null && projectMiningConfigDO.getPowerConsumption() != null
                    && projectMiningConfigDO.getElectricityPrice() != null) {
                electricityCost = computingPower
                        .multiply(projectMiningConfigDO.getPowerConsumption())
                        .multiply(projectMiningConfigDO.getElectricityPrice())
                        .multiply(new BigDecimal(24))
                        .divide(new BigDecimal(1000), 4, RoundingMode.HALF_UP);
            }
            log.info("[generateDailyRevenue] 电力成本计算完成，项目ID: {}, 电力成本: {}", projectId, electricityCost);

            // 4. 获取运维成本 (固定值/30)
            peopleCost = BigDecimal.ZERO;
            if (projectMiningConfigDO != null && projectMiningConfigDO.getOperationCost() != null) {
                peopleCost = projectMiningConfigDO.getOperationCost().divide(new BigDecimal(30), 4,
                        RoundingMode.HALF_UP);
            }
            log.info("[generateDailyRevenue] 运维成本计算完成，项目ID: {}, 运维成本: {}", projectId, peopleCost);

            // 5. 计算净收益 (Fiat)
            BigDecimal netRevenueFiat = dailyRevenue.subtract(electricityCost).subtract(peopleCost);

            // 6. 计算团队分成 (Project Revenue)
            projectRevenue = BigDecimal.ZERO; // 团队分成 (Fiat)
            if (netRevenueFiat.compareTo(BigDecimal.ZERO) > 0
                    && projectMiningConfigDO != null && projectMiningConfigDO.getTeamShareRatio() != null) {
                BigDecimal ratio = projectMiningConfigDO.getTeamShareRatio().divide(new BigDecimal(100)); // 百分比转小数
                projectRevenue = netRevenueFiat.multiply(ratio).setScale(4, RoundingMode.HALF_UP);
            }

            // 7. 计算可发放收益 (Coin) = (NetFiat - ProjectFiat) / Rate
            BigDecimal availableRevenue = BigDecimal.ZERO;
            if (netRevenueFiat.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal availableFiat = netRevenueFiat.subtract(projectRevenue);
                if (availableFiat.compareTo(BigDecimal.ZERO) > 0 && coinRate.compareTo(BigDecimal.ZERO) > 0) {
                    availableRevenue = availableFiat.divide(coinRate, 8, RoundingMode.HALF_DOWN);
                }
            }
            result.setElectricityCost(electricityCost);
            result.setPeopleCost(peopleCost);
            result.setAvailableRevenue(availableRevenue);
            result.setProjectRevenue(projectRevenue.divide(coinRate, 8, RoundingMode.HALF_DOWN));
            log.info("[calculateCosts] 挖矿型计算完成: 电力={}, 运维={}, 算力={}", electricityCost, peopleCost, computingPower);

        } else { // 基金型
            log.info("[calculateCosts] 识别为基金型项目...");
            ProjectFundConfigDO config = projectFundConfigMapper.selectById(projectId);
            if (config == null) {
                throw new ServiceException(999, "未配置基金参数，请手动输入成本");
            }
            // 基金型不计算电力成本
            electricityCost = BigDecimal.ZERO;

            // 计算运维成本
            if (config != null && config.getOperationCost() != null) {
                peopleCost = config.getOperationCost().divide(new BigDecimal(30), 2, RoundingMode.HALF_UP);
            }

            // 计算团队分成
            if (revenue.compareTo(BigDecimal.ZERO) > 0 && config != null && config.getTeamShareRatio() != null) {
                BigDecimal ratio = config.getTeamShareRatio().divide(new BigDecimal(100));
                projectRevenue = revenue.multiply(ratio).setScale(2, RoundingMode.HALF_UP);
            }

            log.info("[calculateCosts] 基金型计算完成: 运维={}", peopleCost);
            // 统一计算可发放收益 (availableRevenue = revenue - electricity - people -
            // projectRevenue)
            BigDecimal availableRevenue = revenue.subtract(electricityCost).subtract(peopleCost)
                    .subtract(projectRevenue);
            if (availableRevenue.compareTo(BigDecimal.ZERO) < 0) {
                availableRevenue = BigDecimal.ZERO;
            }

            result.setElectricityCost(electricityCost);
            result.setPeopleCost(peopleCost);
            result.setAvailableRevenue(availableRevenue);
            result.setProjectRevenue(projectRevenue);
        }

        return result;
    }

    @Override
    public PoolRevenueCalcRespVO calculatePoolRevenue(Long projectId, LocalDate revenueDate) {
        log.info("[calculatePoolRevenue] 开始计算矿池收益，项目ID: {}, 日期: {}", projectId, revenueDate);
        PoolRevenueCalcRespVO result = new PoolRevenueCalcRespVO();
        ProjectInfoDO project = projectInfoMapper.selectById(projectId);
        // 1. 获取矿池配置
        ProjectPoolConfigDTO poolConfig = projectInfoService.getPoolConfig(projectId);
        if (poolConfig == null || StrUtil.hasBlank(poolConfig.getPoolAccessKey(),
                poolConfig.getPoolPrivateKey(), poolConfig.getPoolName())) {
            throw new ServiceException(999, "项目矿池配置不完整");
        }
        result.setCoin("BTC");

        SystemCoinDO coinDO = systemCoinMapper.getCoinByCode(project.getEarningCurrency());
        // 3. 获取矿池日收益
        long timestamp = revenueDate.atStartOfDay(ZoneId.of("UTC")).toEpochSecond();
        SpiderPoolDayProfitReqDTO reqDTO = new SpiderPoolDayProfitReqDTO();
        reqDTO.setAccessKey(poolConfig.getPoolAccessKey());
        reqDTO.setPrivateKey(poolConfig.getPoolPrivateKey());
        reqDTO.setCoin(coinDO.getPoolSymbol());
        reqDTO.setSubaccount(poolConfig.getPoolName());
        reqDTO.setTimeStamp(timestamp);

        SpiderPoolDayProfitRespDTO dayProfit = spiderPoolService.getDayProfitDetailInfo(reqDTO);
        if (dayProfit != null) {
            BigDecimal coinRevenue = dayProfit.getDayProfit();
            result.setCoinRevenue(coinRevenue);
        } else {
            // 如果没查到，抛出异常或返回0? 用户需要填充表单，如果没数据应该提示。
            throw new ServiceException(999, "该日期无矿池收益数据 (Day: " + revenueDate + ")");
        }

        return result;
    }

}