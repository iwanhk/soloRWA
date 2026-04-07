package cc.bamboo.module.project.service.statistics;

import cc.bamboo.module.project.controller.admin.statistics.vo.OrderAuditStatisticsRespVO;
import cc.bamboo.module.project.dal.mysql.projectorder.ProjectOrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 订单审核统计 Service 实现类
 *
 * @author Swolf
 */
@Service
@Slf4j
public class OrderAuditStatisticsServiceImpl implements OrderAuditStatisticsService {

    @Resource
    private ProjectOrderMapper projectOrderMapper;

    @Override
    public OrderAuditStatisticsRespVO getOrderAuditStatistics() {
        log.info("[getOrderAuditStatistics] 开始获取订单审核统计");

        // 统计待审核订单数
        Long orderAuditCount = projectOrderMapper.countOrderAudit();

        OrderAuditStatisticsRespVO result = OrderAuditStatisticsRespVO.builder()
                .orderAuditCount(orderAuditCount)
                .build();

        log.info("[getOrderAuditStatistics] 订单审核统计: {}", result);
        return result;
    }

}
