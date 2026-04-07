package cc.bamboo.module.project.service.statistics;

import cc.bamboo.framework.security.core.util.SecurityFrameworkUtils;
import cc.bamboo.module.project.controller.admin.statistics.vo.BillAuditStatisticsRespVO;
import cc.bamboo.module.project.dal.mysql.projectbill.ProjectBillMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 账单审核统计 Service 实现类
 *
 * @author Swolf
 */
@Service
@Slf4j
public class BillAuditStatisticsServiceImpl implements BillAuditStatisticsService {

    @Resource
    private ProjectBillMapper projectBillMapper;

    @Override
    public BillAuditStatisticsRespVO getBillAuditStatistics() {
        log.info("[getBillAuditStatistics] 开始获取账单审核统计");

        Long billAuditCount;

        // 获取当前用户的租户ID

        billAuditCount = projectBillMapper.countPendingAudit();
        log.info("[getBillAuditStatistics] 管理员查询,所有待审核账单数: {}", billAuditCount);
        /*if (tenantId == null || tenantId == 1L) {
            // 管理员: 统计所有待审核账单
            billAuditCount = projectBillMapper.countPendingAudit();
            log.info("[getBillAuditStatistics] 管理员查询,所有待审核账单数: {}", billAuditCount);
        } else {
            // 发行商: 仅统计自己租户的待审核账单
            billAuditCount = projectBillMapper.countPendingAuditByTenant();
            log.info("[getBillAuditStatistics] 发行商查询(tenantId={}),待审核账单数: {}", tenantId, billAuditCount);
        }*/

        BillAuditStatisticsRespVO result = BillAuditStatisticsRespVO.builder()
                .billAuditCount(billAuditCount)
                .build();

        log.info("[getBillAuditStatistics] 账单审核统计: {}", result);
        return result;
    }

}
