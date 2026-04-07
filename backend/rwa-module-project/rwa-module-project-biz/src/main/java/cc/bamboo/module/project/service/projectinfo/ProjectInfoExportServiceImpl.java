package cc.bamboo.module.project.service.projectinfo;

import cc.bamboo.framework.excel.core.util.ExcelUtils;
import cc.bamboo.framework.mybatis.core.query.LambdaQueryWrapperX;
import cc.bamboo.module.project.controller.admin.projectinfo.vo.ProjectSummarySheetRespVO;
import cc.bamboo.module.project.dal.dataobject.projectdividendperiod.ProjectDividendPeriodDO;
import cc.bamboo.module.project.dal.dataobject.projectinfo.ProjectInfoDO;
import cc.bamboo.module.project.dal.dataobject.projectoperation.ProjectOperationDO;
import cc.bamboo.module.project.dal.mysql.projectdividendperiod.ProjectDividendPeriodMapper;
import cc.bamboo.module.project.dal.mysql.projectinfo.ProjectInfoMapper;
import cc.bamboo.module.project.dal.mysql.projectoperation.ProjectOperationMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class ProjectInfoExportServiceImpl implements ProjectInfoExportService {

    @Resource
    private ProjectInfoMapper projectInfoMapper;

    @Resource
    private ProjectOperationMapper projectOperationMapper;

    @Resource
    private ProjectDividendPeriodMapper dividendPeriodMapper;

    @Override
    public void exportProjectSummaryExcel(List<Long> projectIds, HttpServletResponse response) throws IOException {
        List<ProjectInfoDO> projects = getProjects(projectIds);
        if (projects.isEmpty()) {
            ExcelUtils.write(response, "项目汇总.xls", "数据", ProjectSummarySheetRespVO.class, Collections.emptyList());
            return;
        }

        List<Long> actualProjectIds = new ArrayList<>(projects.size());
        for (ProjectInfoDO project : projects) {
            if (project.getProjectId() != null) {
                actualProjectIds.add(project.getProjectId());
            }
        }

        Map<Long, ProjectOperationDO> projectOperationMap = getProjectOperationMap(actualProjectIds);
        Map<Long, Integer> dividendPeriodMap = getDividendPeriodDurationMap(actualProjectIds);

        List<ProjectSummarySheetRespVO> rows = new ArrayList<>(projects.size());
        for (ProjectInfoDO project : projects) {
            ProjectSummarySheetRespVO row = buildRow(project,
                    projectOperationMap.get(project.getProjectId()),
                    dividendPeriodMap.get(project.getProjectId()));
            rows.add(row);
        }

        ExcelUtils.write(response, "项目汇总.xls", "数据", ProjectSummarySheetRespVO.class, rows);
    }

    private List<ProjectInfoDO> getProjects(List<Long> projectIds) {
        if (CollectionUtils.isEmpty(projectIds)) {
            return projectInfoMapper.selectList(new LambdaQueryWrapperX<ProjectInfoDO>()
                    .orderByDesc(ProjectInfoDO::getProjectId));
        }
        return projectInfoMapper.selectList(new LambdaQueryWrapperX<ProjectInfoDO>()
                .in(ProjectInfoDO::getProjectId, projectIds)
                .orderByDesc(ProjectInfoDO::getProjectId));
    }

    private Map<Long, ProjectOperationDO> getProjectOperationMap(List<Long> projectIds) {
        if (CollectionUtils.isEmpty(projectIds)) {
            return Collections.emptyMap();
        }
        List<ProjectOperationDO> list = projectOperationMapper.selectList(new LambdaQueryWrapperX<ProjectOperationDO>()
                .in(ProjectOperationDO::getProjectId, projectIds));
        Map<Long, ProjectOperationDO> map = new HashMap<>();
        for (ProjectOperationDO item : list) {
            if (item == null || item.getProjectId() == null) {
                continue;
            }
            map.put(item.getProjectId(), item);
        }
        return map;
    }

    private Map<Long, Integer> getDividendPeriodDurationMap(List<Long> projectIds) {
        if (CollectionUtils.isEmpty(projectIds)) {
            return Collections.emptyMap();
        }
        List<ProjectDividendPeriodDO> periods = dividendPeriodMapper.selectList(
                new LambdaQueryWrapperX<ProjectDividendPeriodDO>()
                        .in(ProjectDividendPeriodDO::getProjectId, projectIds)
                        .orderByAsc(ProjectDividendPeriodDO::getProjectId)
                        .orderByAsc(ProjectDividendPeriodDO::getPeriodSeq));
        Map<Long, Integer> map = new HashMap<>();
        for (ProjectDividendPeriodDO period : periods) {
            if (period.getProjectId() == null) {
                continue;
            }
            if (!map.containsKey(period.getProjectId())) {
                map.put(period.getProjectId(), period.getPeriodDuration());
            }
        }
        return map;
    }

    private ProjectSummarySheetRespVO buildRow(ProjectInfoDO project, ProjectOperationDO operation,
            Integer dividendPeriodDuration) {
        ProjectSummarySheetRespVO row = new ProjectSummarySheetRespVO();
        row.setProjectName(project.getProjectName());
        row.setProjectStatus(project.getProjectStatus());
        row.setPublisherCompanyName(project.getPublisherCompanyName());
        row.setProjectDuration(project.getDuration());
        row.setDividendPeriod(dividendPeriodDuration);
        row.setOnlineTime(project.getCreateTime());

        row.setRunTime(formatRunTime(project.getLockStartTime(), project.getLockEndTime()));
        row.setIssueQuantity(project.getIssueQuantity());
        row.setIssueUnitPrice(formatAmountWithCurrency(project.getIssueUnitPrice(), project.getInvestmentCurrency()));

        BigDecimal minimumSubscribeAmount = null;
        if (project.getIssueUnitPrice() != null && project.getMinimumPurchase() != null) {
            minimumSubscribeAmount = project.getIssueUnitPrice()
                    .multiply(BigDecimal.valueOf(project.getMinimumPurchase()));
        }
        row.setMinimumSubscribeAmount(
                formatAmountWithCurrency(minimumSubscribeAmount, project.getInvestmentCurrency()));

        row.setPurchaseUserCount(
                operation != null && operation.getInvestorCount() != null ? operation.getInvestorCount().longValue()
                        : 0L);
        row.setTotalDividendIncome(formatAmountWithCurrency(
                operation != null && operation.getTotalInvestorIncome() != null ? operation.getTotalInvestorIncome()
                        : BigDecimal.ZERO,
                project.getEarningCurrency()));
        row.setWithdrawnUserIncome(formatAmountWithCurrency(
                operation != null && operation.getDividendApplyAmount() != null ? operation.getDividendApplyAmount()
                        : BigDecimal.ZERO,
                project.getEarningCurrency()));
        return row;
    }

    private String formatAmountWithCurrency(BigDecimal amount, String currency) {
        if (amount == null) {
            return "";
        }
        if (currency == null || currency.trim().isEmpty()) {
            return amount.toPlainString();
        }
        return amount.toPlainString() + " " + currency.trim();
    }

    private String formatRunTime(LocalDate start, LocalDate end) {
        if (start == null && end == null) {
            return "";
        }
        DateTimeFormatter fmt = DateTimeFormatter.ISO_LOCAL_DATE;
        String startStr = start == null ? "" : fmt.format(start);
        String endStr = end == null ? "" : fmt.format(end);
        if (startStr.isEmpty()) {
            return endStr;
        }
        if (endStr.isEmpty()) {
            return startStr;
        }
        return startStr + " ~ " + endStr;
    }
}
