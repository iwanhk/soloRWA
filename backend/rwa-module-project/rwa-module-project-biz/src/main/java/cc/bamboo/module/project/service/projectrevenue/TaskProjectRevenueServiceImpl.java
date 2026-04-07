package cc.bamboo.module.project.service.projectrevenue;

import cc.bamboo.framework.tenant.core.aop.TenantIgnore;
import cc.bamboo.module.chain.api.dividend.DividendRecordApi;
import cc.bamboo.module.chain.api.dividend.dto.DividendRecordReqDTO;
import cc.bamboo.module.project.dal.dataobject.projectinfo.ProjectInfoDO;
import cc.bamboo.module.project.dal.dataobject.projectrevenue.ProjectRevenueDO;
import cc.bamboo.module.project.dal.mysql.projectinfo.ProjectInfoMapper;
import cc.bamboo.module.project.dal.mysql.projectrevenue.ProjectRevenueMapper;
import cc.bamboo.module.project.enums.ProjectStatusEnum;
import cc.bamboo.module.project.enums.RunStatusEnum;
import cc.bamboo.module.project.enums.SendStatusEnum;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import cc.bamboo.module.project.dal.dataobject.orderdailyincome.OrderDailyIncomeDO;
import cc.bamboo.module.project.dal.mysql.orderdailyincome.OrderDailyIncomeMapper;
import cc.bamboo.module.project.enums.ChainStatusEnum;
import java.util.ArrayList;
import java.math.BigInteger;
import java.util.List;

/**
 * 项目收益 Service 接口
 *
 * @author Swolf
 */
@Component
@Slf4j
public class TaskProjectRevenueServiceImpl {

    @Resource
    private ProjectInfoMapper projectInfoMapper;

    @Resource
    private ProjectRevenueService projectRevenueService;

    @Resource
    private ProjectRevenueMapper projectRevenueMapper;

    @Resource
    private OrderDailyIncomeMapper orderDailyIncomeMapper;

    @Resource
    private DividendRecordApi dividendRecordApi;

    @XxlJob("GenerateDailyRevenueJob")
    // @TenantJob
    @TenantIgnore
    public void TaskGenerateDailyRevenue() {
        log.info("[TaskGenerateDailyRevenue][开始处理项目收益]");
        LocalDate yesterday = LocalDate.now().minusDays(1);
        LambdaQueryWrapper<ProjectInfoDO> queryWrapper = new LambdaQueryWrapper<>();
        //queryWrapper.gt(ProjectInfoDO::getLockEndTime, yesterday);
        queryWrapper.le(ProjectInfoDO::getLockStartTime, yesterday);
        queryWrapper.ne(ProjectInfoDO::getProjectStatus, RunStatusEnum.ENDED.getStatus());
        // 查找锁定期内的项目
        List<ProjectInfoDO> projectList = projectInfoMapper.selectList(queryWrapper);
        for (ProjectInfoDO project : projectList) {
            if(project.getLockEndTime() != null && project.getLockEndTime().isBefore(yesterday)){
                continue;
            }

            // 计算项目收益
            if (project.getProjectConfigType() != null && project.getProjectConfigType() == 1) {
                // 基金型项目
                projectRevenueService.generateDailyFundRevenue(project.getProjectId(), yesterday);
            } else {
                // 默认/挖矿型项目
                projectRevenueService.generateDailyRevenue(project.getProjectId(), yesterday);
            }
        }
        log.info("[TaskGenerateDailyRevenue][处理项目收益完成]");
    }

    @XxlJob("GenerateDailyIncomeJob")
    @TenantIgnore
    public void TaskGenerateDailyIncome() {
        log.info("[TaskGenerateDailyIncome][开始处理项目订单收益]");
        LocalDate yesterday = LocalDate.now().minusDays(1);
        List<ProjectRevenueDO> projectRevenueList = projectRevenueMapper
                .selectList(new LambdaQueryWrapper<ProjectRevenueDO>()
                        .eq(ProjectRevenueDO::getRevenueDate, yesterday));
        for (ProjectRevenueDO projectRevenue : projectRevenueList) {
            projectRevenueService.calculateOrderIncome(projectRevenue.getId());
        }
        log.info("[TaskGenerateDailyIncome][处理项目订单收益完成]");
    }

    @XxlJob("DistributeIncomeJob")
    @TenantIgnore
    public void TaskDistributeIncome() {
        log.info("[TaskDistributeIncome][开始处理项目订单收益分配]");
        LocalDate yesterday = LocalDate.now().minusDays(1);
        List<ProjectRevenueDO> projectRevenueList = projectRevenueMapper
                .selectList(new LambdaQueryWrapper<ProjectRevenueDO>()
                        .eq(ProjectRevenueDO::getRevenueDate, yesterday)
                        .eq(ProjectRevenueDO::getIsSend, SendStatusEnum.NOT_SEND.getStatus()));
        for (ProjectRevenueDO projectRevenue : projectRevenueList) {
            projectRevenueService.distributeIncome(projectRevenue.getId());
        }
        log.info("[TaskDistributeIncome][处理项目订单收益分配完成]");
    }

    @XxlJob("PushRevenueChainJob")
    @TenantIgnore
    public void TaskPushRevenueChain() {
        log.info("[TaskPushRevenueChain][开始更新项目收益链上状态]");
        LocalDate yesterday = LocalDate.now().minusDays(1);

        // 查询昨天 chainStatus = -1 (UNPROCESSED) 的 ProjectRevenue
        List<ProjectRevenueDO> projectRevenueList = projectRevenueMapper
                .selectList(new LambdaQueryWrapper<ProjectRevenueDO>()
                        .eq(ProjectRevenueDO::getRevenueDate, yesterday)
                        .eq(ProjectRevenueDO::getIsSend, SendStatusEnum.SEND.getStatus())
                        .eq(ProjectRevenueDO::getChainStatus, ChainStatusEnum.UNPROCESSED.getStatus()));

        for (ProjectRevenueDO projectRevenue : projectRevenueList) {
            try {

                // 2. 查询该 ProjectRevenue 的所有 OrderDailyIncome
                List<OrderDailyIncomeDO> incomeList = orderDailyIncomeMapper
                        .selectList(new LambdaQueryWrapper<OrderDailyIncomeDO>()
                                .eq(OrderDailyIncomeDO::getProjectRevenueId, projectRevenue.getId()));

                if (incomeList.isEmpty()) {
                    log.warn("[TaskPushRevenueChain] 项目收益无订单明细，跳过。projectId: {}, revenueId: {}",
                            projectRevenue.getProjectId(), projectRevenue.getId());
                    continue;
                }

                // 3. 检查所有 OrderDailyIncome 是否都已发放 (status = 1)
                boolean allIssued = incomeList.stream()
                        .allMatch(income -> SendStatusEnum.SEND.getStatus().equals(income.getStatus()));

                if (allIssued) {
                    // 4. 调用 ChainOperationLogApi
                    // 构建精简的业务数据，只包含链上所需的 addresses 和 shares

                    List<String> addresses = new ArrayList<>();
                    List<BigInteger> shares = new ArrayList<>();

                    for (OrderDailyIncomeDO income : incomeList) {
                        if (income.getAddress() != null && income.getHoldQuantity() != null) {
                            addresses.add(income.getAddress());
                            shares.add(BigInteger.valueOf(income.getHoldQuantity()));
                        }
                    }

                    DividendRecordReqDTO reqDTO = new DividendRecordReqDTO();
                    reqDTO.setDate(projectRevenue.getRevenueDate());
                    reqDTO.setProjectId(projectRevenue.getProjectId());

                    // 金额转换: BigDecimal -> BigInteger (精度 18位)
                    if (projectRevenue.getAvailableRevenue() != null) {
                        reqDTO.setAmount(projectRevenue.getAvailableRevenue().multiply(java.math.BigDecimal.TEN.pow(18))
                                .toBigInteger());
                    } else {
                        reqDTO.setAmount(BigInteger.ZERO);
                    }

                    reqDTO.setCurrency(projectRevenue.getCoinCode());
                    reqDTO.setAddresses(addresses);
                    reqDTO.setShares(shares);

                    dividendRecordApi.recordDividend(reqDTO);

                    // 5. 更新 ProjectRevenue chainStatus 为 0 (PENDING)
                    projectRevenue.setChainStatus(ChainStatusEnum.PENDING.getStatus());
                    projectRevenueMapper.updateById(projectRevenue);
                    log.info("[TaskPushRevenueChain] 项目收益状态已更新为待处理，revenueId: {}", projectRevenue.getId());
                }
            } catch (Exception e) {
                log.error("[TaskPushRevenueChain] 处理项目收益异常，revenueId: {}, 错误: {}",
                        projectRevenue.getId(), e.getMessage(), e);
            }
        }
        log.info("[TaskPushRevenueChain][更新项目收益链上状态完成]");
    }
}