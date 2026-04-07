package cc.bamboo.module.project.service.projectinfo;

import cc.bamboo.framework.mybatis.core.query.LambdaQueryWrapperX;
import cc.bamboo.framework.tenant.core.aop.TenantIgnore;
import cc.bamboo.module.project.dal.dataobject.projectinfo.ProjectInfoDO;
import cc.bamboo.module.project.dal.dataobject.projectorder.ProjectOrderDO;
import cc.bamboo.module.project.dal.dataobject.projectdividendperiod.ProjectDividendPeriodDO;
import cc.bamboo.module.project.dal.mysql.projectinfo.ProjectInfoMapper;
import cc.bamboo.module.project.dal.mysql.projectorder.ProjectOrderMapper;
import cc.bamboo.module.project.dal.mysql.projectdividendperiod.ProjectDividendPeriodMapper;
import cc.bamboo.module.project.enums.AuditStatusEnum;
import cc.bamboo.module.project.enums.RunStatusEnum;
import cc.bamboo.module.project.service.message.ProjectMessageService;
import cc.bamboo.module.user.enums.notice.NoticeTemplateEnum;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cc.bamboo.module.project.enums.ApiConstants.*;
import static cc.bamboo.module.user.enums.ApiConstants.PARAM_PROJECT;

/**
 * 项目定时任务 Service 实现类
 * 用于处理项目分红到期、赎回到期等定时通知任务
 *
 * @author Kiro
 */
@Component
@Slf4j
public class ProjectScheduleServiceImpl {



    @Resource
    private ProjectInfoMapper projectInfoMapper;

    @Resource
    private ProjectOrderMapper projectOrderMapper;

    @Resource
    private ProjectMessageService projectMessageService;

    @Resource
    private ProjectDividendPeriodMapper projectDividendPeriodMapper;

    /**
     * 处理当天分红开始的项目通知
     * 查询当天到期unlock_date的项目分红周期，给购买了该项目的用户发送PROJECT_DIVIDEND_ARRIVE消息
     */
    @XxlJob("DividendArriveNotificationJob")
    @TenantIgnore
    public void processDividendArriveNotification() {
        log.info("[processDividendArriveNotification][开始处理分红到期通知]");

        // 1. 查询今日解锁的分红周期
        LocalDate today = LocalDate.now();
        List<ProjectDividendPeriodDO> periods = projectDividendPeriodMapper.selectList(
                new LambdaQueryWrapperX<ProjectDividendPeriodDO>()
                        .eq(ProjectDividendPeriodDO::getUnlockDate, today));

        if (periods.isEmpty()) {
            log.info("[processDividendArriveNotification][今日无分红到期的周期]");
            return;
        }

        log.info("[processDividendArriveNotification][今日分红到期的周期数量: {}]", periods.size());

        // 2. 遍历周期，查询对应的项目信息，然后发送通知
        for (ProjectDividendPeriodDO period : periods) {
            try {
                ProjectInfoDO project = projectInfoMapper.selectById(period.getProjectId());
                if (project == null) {
                    log.error("[processDividendArriveNotification][项目不存在，projectId={}]", period.getProjectId());
                    continue;
                }

                // 可以在通知中带上期数信息，如果有需要的话
                sendNotificationToProjectUsers(project, NoticeTemplateEnum.PROJECT_DIVIDEND_ARRIVE.getCode());

            } catch (Exception e) {
                log.error("[processDividendArriveNotification][处理项目分红通知失败，projectId={}, periodId={}]",
                        period.getProjectId(), period.getId(), e);
            }
        }

        log.info("[processDividendArriveNotification][处理分红到期通知完成]");
    }

    /**
     * 处理昨日锁定期结束的项目通知
     * 查询昨天结束lock_end_time的项目，给购买了该项目的用户发送PROJECT_REDEMPTION_ARRIVE消息
     */
    @XxlJob("RedemptionArriveNotificationJob")
    @TenantIgnore
    public void processRedemptionArriveNotification() {
        log.info("[processRedemptionArriveNotification][开始处理赎回到期通知]");

        // 1. 查询昨日锁定期结束的项目
        LocalDate yesterday = LocalDate.now().minusDays(1);


        List<ProjectInfoDO> projects = projectInfoMapper.selectList(
                new LambdaQueryWrapperX<ProjectInfoDO>().eq(ProjectInfoDO::getLockEndTime, yesterday));


        if (projects.isEmpty()) {
            log.info("[processRedemptionArriveNotification][昨日无锁定期结束的项目]");
            return;
        }

        log.info("[processRedemptionArriveNotification][昨日锁定期结束的项目数量: {}]", projects.size());

        // 2. 遍历项目，查询购买该项目的用户并发送通知
        for (ProjectInfoDO project : projects) {
            try {
                sendNotificationToProjectUsers(project, NoticeTemplateEnum.PROJECT_REDEMPTION_ARRIVE.getCode());
            } catch (Exception e) {
                log.error("[processRedemptionArriveNotification][处理项目赎回通知失败，projectId={}]",
                        project.getProjectId(), e);
            }
        }

        log.info("[processRedemptionArriveNotification][处理赎回到期通知完成]");
    }

    /**
     * 给购买了指定项目的用户发送通知
     *
     * @param project      项目信息
     * @param templateCode 消息模板编码
     */
    private void sendNotificationToProjectUsers(ProjectInfoDO project, String templateCode) {
        Long projectId = project.getProjectId();

        // 查询购买该项目且审核通过的订单
        List<ProjectOrderDO> orders = projectOrderMapper.selectList(
                new LambdaQueryWrapperX<ProjectOrderDO>()
                        .eq(ProjectOrderDO::getProjectId, projectId)
                        .eq(ProjectOrderDO::getOrderStatus, AuditStatusEnum.APPROVED.getStatus()));

        if (orders.isEmpty()) {
            log.info("[sendNotificationToProjectUsers][项目无有效订单，projectId={}]", projectId);
            return;
        }

        // 获取去重后的用户ID列表
        List<Long> userIds = orders.stream()
                .map(ProjectOrderDO::getUserId)
                .distinct()
                .collect(Collectors.toList());

        log.info("[sendNotificationToProjectUsers][项目{}需通知用户数量: {}]", projectId, userIds.size());

        // 准备模板参数
        Map<String, Object> templateParams = new HashMap<>();
        templateParams.put(PARAM_PROJECT, project.getProjectName());

        // 发送消息给每个用户
        for (ProjectOrderDO order : orders) {
            projectMessageService.sendOrderMessageAsync(order.getUserId(), order.getId(), templateCode, templateParams);
        }
    }

    @XxlJob("RedemptionArriveEedJob")
    @TenantIgnore
    public void processRedemptionArriveEnd() {
        log.info("[processRedemptionArriveEnd][开始处理到期项目]");

        // 1. 查询昨日锁定期结束的项目
        LocalDate yesterday = LocalDate.now().minusDays(1);

        //查找已到期并且未结束的项目
        List<ProjectInfoDO> projects = projectInfoMapper.selectList(
                new LambdaQueryWrapperX<ProjectInfoDO>().le(ProjectInfoDO::getLockEndTime, yesterday).ne(ProjectInfoDO::getProjectStatus, RunStatusEnum.ENDED.getStatus()));


        if (projects.isEmpty()) {
            log.info("[processRedemptionArriveEnd][昨日无锁定期结束的项目]");
            return;
        }

        log.info("[processRedemptionArriveEnd][昨日锁定期结束的项目数量: {}]", projects.size());

        // 2. 遍历项目，查询购买该项目的用户并发送通知
        for (ProjectInfoDO project : projects) {
            //更新项目状态为结束
            LambdaUpdateWrapper<ProjectInfoDO> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(ProjectInfoDO::getProjectId, project.getProjectId());
            updateWrapper.set(ProjectInfoDO::getProjectStatus, RunStatusEnum.ENDED.getStatus());
            projectInfoMapper.update(null, updateWrapper);
        }

        log.info("[processRedemptionArriveEnd][开始处理到期项目]");
    }

    //下架已经到锁定期开始的封闭型项目
     @XxlJob("ProjectLockStartJob")
     @TenantIgnore
    public void processProjectLockStart() {
        log.info("[processProjectLockStart][开始处理到期项目]");

        // 1. 查询已经到了锁定期开始时间，但是没有下架的项目
         LocalDate now = LocalDate.now();
        LambdaUpdateWrapper<ProjectInfoDO> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(ProjectInfoDO::getSellStatus, SELL_STATUS_NOT_SALE);
         updateWrapper.eq(ProjectInfoDO::getSellStatus, SELL_STATUS_ON_SALE);
         updateWrapper.eq(ProjectInfoDO::getProjectType, ASSET_TYPE_CLOSED_FUND);
         updateWrapper.le(ProjectInfoDO::getLockStartTime, now);
         updateWrapper.isNotNull(ProjectInfoDO::getLockStartTime);
         projectInfoMapper.update(null, updateWrapper);

     }
}
