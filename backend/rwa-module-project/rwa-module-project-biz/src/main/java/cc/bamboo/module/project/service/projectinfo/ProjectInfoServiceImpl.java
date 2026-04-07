package cc.bamboo.module.project.service.projectinfo;

import cc.bamboo.framework.common.exception.ServiceException;
import cc.bamboo.module.chain.enums.ChainTaskStatusEnum;
import cc.bamboo.module.project.controller.admin.projectorder.vo.ProjectOrderAuditReqVO;
import cc.bamboo.module.project.dal.dataobject.projectoperation.ProjectOperationDO;
import cc.bamboo.module.project.dal.dataobject.projectorder.ProjectOrderDO;
import cc.bamboo.module.project.dal.dataobject.projectsnapshot.ProjectSnapshotDO;
import cc.bamboo.module.project.dal.mysql.projectoperation.ProjectOperationMapper;
import cc.bamboo.module.project.dal.mysql.projectorder.ProjectOrderMapper;
import cc.bamboo.module.project.enums.*;
import cc.bamboo.module.project.service.chainoperation.ChainOperationTaskService;
import cc.bamboo.module.project.service.projectinfo.dto.ProjectPoolConfigDTO;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.symmetric.AES;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import cc.bamboo.module.project.controller.admin.projectinfo.vo.*;
import cc.bamboo.module.project.dal.dataobject.projectinfo.ProjectInfoDO;
import cc.bamboo.module.project.dal.dataobject.projectminingconfig.ProjectMiningConfigDO;
import cc.bamboo.module.project.dal.mysql.projectminingconfig.ProjectMiningConfigMapper;
import cc.bamboo.module.project.dal.dataobject.projectfundconfig.ProjectFundConfigDO;
import cc.bamboo.module.project.dal.mysql.projectfundconfig.ProjectFundConfigMapper;
import cn.hutool.core.util.StrUtil;
import cc.bamboo.framework.common.pojo.PageResult;
import cc.bamboo.framework.common.util.object.BeanUtils;

import cc.bamboo.module.project.dal.mysql.projectinfo.ProjectInfoMapper;
import cc.bamboo.module.project.mq.producer.ProjectAuditPassProducer;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static cc.bamboo.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cc.bamboo.framework.tenant.core.context.TenantContextHolder.getTenantId;
import static cc.bamboo.module.project.enums.ApiConstants.*;
import static cc.bamboo.module.project.enums.ErrorCodeConstants.*;

/**
 * 项目核心表（基础+状态） Service 实现类
 *
 * @author Swolf
 */
@Service
@Validated
@Slf4j
public class ProjectInfoServiceImpl implements ProjectInfoService {

    @Resource
    private ProjectInfoMapper infoMapper;

    @Resource
    private ProjectAuditPassProducer projectAuditPassProducer;

    @Resource
    private ChainOperationTaskService chainOperationTaskService;

    @Resource
    private ProjectOperationMapper projectOperationMapper;

    @Resource
    private ProjectOrderMapper projectOrderMapper;

    @Resource
    private cc.bamboo.module.project.service.projectsnapshot.ProjectSnapshotService snapshotService;

    @Override
    public Long createInfo(ProjectInfoSaveReqVO createReqVO) {
        // 插入
        ProjectInfoDO info = BeanUtils.toBean(createReqVO, ProjectInfoDO.class);

        // 只能是待提交或者待审核状态
        if (!info.getAuditStatus().equals(AuditStatusEnum.PENDING.getStatus())
                && !info.getAuditStatus().equals(AuditStatusEnum.PENDING_SUBMISSION.getStatus())) {
            throw new ServiceException(999, "非法操作");
        }

        // 插入发行商
        Long tenantId = getTenantId();
        String tenantName = infoMapper.getPublisherNameByTenantId(tenantId);
        info.setPublisherUserId(tenantId);
        info.setPublisherCompanyName(tenantName);
        info.setTenantId(tenantId);
        infoMapper.insert(info);

        // 如果是待审核状态，创建快照并设置运行状态为未运行
        if (info.getAuditStatus().equals(AuditStatusEnum.PENDING.getStatus())) {
            info.setProjectStatus(ProjectStatusEnum.NOT_RUNNING.getStatus());
            infoMapper.updateById(info);

            // 创建待审核快照
            snapshotService.createPendingSnapshot(info);
            log.info("[createInfo] 创建待审核快照，项目ID: {}", info.getProjectId());
        }

        // 返回
        return info.getProjectId();
    }

    @Override
    public void updateInfo(ProjectInfoUpdateReqVO updateReqVO) {
        // 校验存在
        ProjectInfoDO project = validateInfoExists(updateReqVO.getProjectId());
        // 结束的项目无法编辑、
        if (ProjectStatusEnum.ENDED.getStatus().equals(project.getProjectStatus())) {
            throw new ServiceException(999, "已结束的项目无法编辑");
        }
        // 只能是待提交或者待审核状态
        if (!updateReqVO.getAuditStatus().equals(AuditStatusEnum.PENDING.getStatus())
                && !updateReqVO.getAuditStatus().equals(AuditStatusEnum.PENDING_SUBMISSION.getStatus())) {
            throw new ServiceException(999, "非法操作");
        }
        // 判断项目是否已发布过（auditStatus == APPROVED 表示已发布）
        boolean isReleased = AuditStatusEnum.APPROVED.getStatus().equals(project.getAuditStatus());
        boolean isSubmittingAudit = AuditStatusEnum.PENDING.getStatus().equals(updateReqVO.getAuditStatus());

        if (isReleased) {
            // ========== 已发布项目：使用 editStatus 管理编辑状态 ==========

            // 禁止修改核心字段：配置类型、项目类型、资产类型
            if (updateReqVO.getProjectConfigType() != null
                    && !updateReqVO.getProjectConfigType().equals(project.getProjectConfigType())) {
                throw new ServiceException(999, "项目已审核通过，禁止修改配置类型");
            }
            if (updateReqVO.getProjectType() != null
                    && !updateReqVO.getProjectType().equals(project.getProjectType())) {
                throw new ServiceException(999, "项目已审核通过，禁止修改项目类型");
            }
            if (updateReqVO.getAssetType() != null && !updateReqVO.getAssetType().equals(project.getAssetType())) {
                throw new ServiceException(999, "项目已审核通过，禁止修改资产类型");
            }

            // 检查是否有待审核快照
            if (snapshotService.hasPendingSnapshot(project.getProjectId())) {
                throw exception(SNAPSHOT_PENDING_CANNOT_EDIT);
            }

            // 创建快照
            ProjectInfoDO snapshotData = BeanUtils.toBean(updateReqVO, ProjectInfoDO.class);
            Long snapshotId = snapshotService.createPendingSnapshot(snapshotData);

            // 获取快照版本号
            ProjectSnapshotDO snapshot = snapshotService.getSnapshot(snapshotId);
            Integer snapshotVersion = snapshot.getSnapshotVersion();

            // 更新项目的编辑状态和版本
            ProjectInfoDO statusUpdate = new ProjectInfoDO();
            statusUpdate.setProjectId(project.getProjectId());
            statusUpdate.setAuditStatus(AuditStatusEnum.APPROVED.getStatus()); // 保持已通过状态
            statusUpdate.setEditVersion(snapshotVersion); // 设置快照版本号

            if (isSubmittingAudit) {
                // 提交审核
                statusUpdate.setEditStatus(AuditStatusEnum.PENDING.getStatus()); // 1-待审核
                log.info("[updateInfo] 已发布项目提交审核，editStatus=待审核，editVersion={}，项目ID: {}",
                        snapshotVersion, project.getProjectId());
            } else {
                // 保存草稿
                statusUpdate.setEditStatus(AuditStatusEnum.PENDING_SUBMISSION.getStatus()); // 0-待提交
                log.info("[updateInfo] 已发布项目保存草稿，editStatus=待提交，editVersion={}，项目ID: {}",
                        snapshotVersion, project.getProjectId());
            }

            infoMapper.updateById(statusUpdate);

        } else {
            // ========== 未发布项目：使用 auditStatus 管理审核状态 ==========

            if (isSubmittingAudit) {
                // 首次提交审核：创建快照
                if (snapshotService.hasPendingSnapshot(project.getProjectId())) {
                    throw exception(SNAPSHOT_PENDING_CANNOT_EDIT);
                }

                ProjectInfoDO snapshotData = BeanUtils.toBean(updateReqVO, ProjectInfoDO.class);
                Long snapshotId = snapshotService.createPendingSnapshot(snapshotData);

                // 获取快照版本号
                ProjectSnapshotDO snapshot = snapshotService.getSnapshot(snapshotId);
                Integer snapshotVersion = snapshot.getSnapshotVersion();

                // 更新项目状态为待审核，并设置版本号
                ProjectInfoDO statusUpdate = new ProjectInfoDO();
                statusUpdate.setProjectId(project.getProjectId());
                statusUpdate.setAuditStatus(AuditStatusEnum.PENDING.getStatus());
                statusUpdate.setEditVersion(snapshotVersion); // 设置快照版本号
                infoMapper.updateById(statusUpdate);

                log.info("[updateInfo] 未发布项目提交审核，auditStatus=待审核，editVersion={}，项目ID: {}",
                        snapshotVersion, project.getProjectId());
            } else {
                // 保存草稿：直接更新项目表
                ProjectInfoDO updateObj = BeanUtils.toBean(updateReqVO, ProjectInfoDO.class);
                updateObj.setProjectStatus(ProjectStatusEnum.NOT_RUNNING.getStatus());
                infoMapper.updateById(updateObj);

                log.info("[updateInfo] 未发布项目保存草稿，直接更新项目表，项目ID: {}", project.getProjectId());
            }
        }
    }

    @Override
    public void submitAudit(SubmitAuditReqVO submitReqVO) {
        // 校验存在
        ProjectInfoDO old = validateInfoExists(submitReqVO.getProjectId());
        // 校验状态,只能从待审核或者拒绝状态提交审核
        if (!old.getAuditStatus().equals(AuditStatusEnum.PENDING_SUBMISSION.getStatus())
                && !old.getAuditStatus().equals(AuditStatusEnum.REJECTED.getStatus())) {
            throw new ServiceException(999, "只能从待提交或者审核不通过可提交审核");
        }
        // 更新
        ProjectInfoDO updateObj = new ProjectInfoDO();
        updateObj.setProjectId(submitReqVO.getProjectId());
        updateObj.setAuditStatus(AuditStatusEnum.PENDING.getStatus());
        updateObj.setProjectStatus(ProjectStatusEnum.NOT_RUNNING.getStatus());
        infoMapper.updateById(updateObj);
    }

    @Override
    public void deleteInfo(Long id) {
        // 校验存在
        validateInfoExists(id);
        // 删除
        infoMapper.deleteById(id);
    }

    private ProjectInfoDO validateInfoExists(Long id) {
        ProjectInfoDO info = infoMapper.selectById(id);
        if (info == null) {
            throw exception(INFO_NOT_EXISTS);
        }
        return info;
    }

    @Override
    public ProjectInfoDO getInfo(Long id) {
        return infoMapper.selectById(id);
    }

    @Override
    public PageResult<ProjectInfoDO> getInfoPage(ProjectInfoPageReqVO pageReqVO) {
        return infoMapper.selectPage(pageReqVO);
    }

    @Override
    public List<ProjectInfoDO> getInfoList() {
        List<ProjectInfoDO> list = infoMapper.selectList();
        return list;
    }

    @Override
    public void submitRunAudit(SubmitAuditReqVO submitReqVO) {
        // 校验存在
        ProjectInfoDO project = validateInfoExists(submitReqVO.getProjectId());
        // 结束的项目无法编辑、
        if (ProjectStatusEnum.ENDED.getStatus().equals(project.getProjectStatus())) {
            throw new ServiceException(999, "已结束的项目无法上下架");
        }

        // 校验状态: 只有上线通过(2) 且 未运行(0) 才能提交运行审核
        if (!AuditStatusEnum.APPROVED.getStatus().equals(project.getAuditStatus())) {
            throw new ServiceException(999, "只有上线通过的项目才能申请运行");
        }
        if (!RunStatusEnum.NOT_RUNNING.getStatus().equals(project.getProjectStatus())
                && !RunStatusEnum.REJECTED.getStatus().equals(project.getProjectStatus())) {
            throw new ServiceException(999, "当前运行状态不允许提交审核");
        }

        // 校验项目配置是否存在
        if (PROJECT_CONFIG_TYPE_MINING.equals(project.getProjectConfigType())) {
            if (miningConfigMapper.selectById(project.getProjectId()) == null) {
                throw new ServiceException(999, "项目配置未完成，请先进行项目配置");
            }
        } else if (PROJECT_CONFIG_TYPE_FUND.equals(project.getProjectConfigType())) {
            if (fundConfigMapper.selectById(project.getProjectId()) == null) {
                throw new ServiceException(999, "项目配置未完成，请先进行项目配置");
            }
        }

        // 更新为待运行审核
        ProjectInfoDO updateObj = new ProjectInfoDO();
        updateObj.setProjectId(submitReqVO.getProjectId());
        updateObj.setProjectStatus(RunStatusEnum.PENDING_RUN.getStatus());
        infoMapper.updateById(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProjectInfoDO auditRun(ProjectRunAuditReqVO auditReqVO, Long auditUserId, String auditUserName) {
        // 校验存在
        ProjectInfoDO project = validateInfoExists(auditReqVO.getProjectId());

        // 校验状态: 只有待运行审核(1) 才能审核
        if (!RunStatusEnum.PENDING_RUN.getStatus().equals(project.getProjectStatus())) {
            throw new ServiceException(999, "当前状态不是待运行审核状态");
        }

        ProjectInfoDO updateObj = new ProjectInfoDO();
        updateObj.setProjectId(project.getProjectId());

        if (auditReqVO.getApproved()) {
            // 审核通过 -> 运行中
            updateObj.setProjectStatus(RunStatusEnum.RUNNING.getStatus());

            // 计算时间: 明天0点开始
            LocalDate lockStartTime = LocalDate.now().plusDays(1);
            updateObj.setLockStartTime(lockStartTime);

            // 结束时间 = 开始时间 + 基金时长(月)
            if (project.getDuration() != null) {
                updateObj.setLockEndTime(lockStartTime.plusMonths(project.getDuration()));
            }

            if (ASSET_TYPE_CLOSED_FUND.equals(project.getProjectType())) {
                updateObj.setSellStatus(SELL_STATUS_NOT_SALE); // 下架
            }
            // 更新项目订单的锁定期
            LambdaUpdateWrapper<ProjectOrderDO> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.set(ProjectOrderDO::getLockStartTime, lockStartTime);
            updateWrapper.set(ProjectOrderDO::getLockEndTime, updateObj.getLockEndTime());
            updateWrapper.eq(ProjectOrderDO::getProjectId, project.getProjectId());
            updateWrapper.eq(ProjectOrderDO::getOrderStatus, OrderStatusEnum.APPROVED.getStatus());
            projectOrderMapper.update(updateWrapper);
        } else {
            // 审核拒绝 -> 未运行
            updateObj.setProjectStatus(RunStatusEnum.REJECTED.getStatus());
        }

        // 更新审计备注 (复用auditRemark字段或新增字段，根据需求，这里复用可能覆盖上线审核备注，但当前DO无其他字段)
        // 建议：如果需要保留历史审核记录，应使用日志表。此处仅更新当前备注。
        updateObj.setAuditRemark(auditReqVO.getAuditRemark());

        infoMapper.updateById(updateObj);
        return updateObj;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProjectInfoDO auditProject(ProjectInfoAuditReqVO auditReqVO, Long auditUserId, String auditUserName) {
        log.info("[auditProject] 开始审核项目，项目ID: {}, 审核结果: {}, 审核人ID: {}",
                auditReqVO.getProjectId(), auditReqVO.getApproved(), auditUserId);

        // 1. 验证项目存在性
        ProjectInfoDO project = infoMapper.selectById(auditReqVO.getProjectId());
        if (project == null) {
            log.warn("[auditProject] 项目不存在，项目ID: {}", auditReqVO.getProjectId());
            throw exception(INFO_NOT_EXISTS);
        }

        // 2. 验证审核不通过时必须填写原因
        if (!auditReqVO.getApproved() && StrUtil.isBlank(auditReqVO.getAuditRemark())) {
            log.warn("[auditProject] 审核不通过时必须填写原因，项目ID: {}", auditReqVO.getProjectId());
            throw exception(AUDIT_REMARK_REQUIRED);
        }

        // 3. 查找待审核快照
        ProjectSnapshotDO pendingSnapshot = snapshotService
                .getPendingSnapshot(project.getProjectId());

        // 4. 验证快照存在（现在所有审核都应该有快照）
        if (pendingSnapshot == null) {
            log.error("[auditProject] 待审核快照不存在，项目ID: {}", project.getProjectId());
            throw new ServiceException(999, "待审核快照不存在，无法进行审核");
        }

        // 5. 审核处理
        if (auditReqVO.getApproved()) {
            // 审核通过：将快照数据同步到项目表
            log.info("[auditProject] 审核通过，同步快照数据到项目表，快照ID: {}", pendingSnapshot.getId());

            ProjectInfoDO syncData = JSON.parseObject(
                    pendingSnapshot.getProjectData(), ProjectInfoDO.class);
            syncData.setProjectId(project.getProjectId());
            syncData.setAuditStatus(AuditStatusEnum.APPROVED.getStatus());
            syncData.setAuditUserId(auditUserId);
            syncData.setAuditUserName(auditUserName);
            syncData.setAuditRemark(auditReqVO.getAuditRemark());
            syncData.setChainStatus(ChainTaskStatusEnum.PENDING.getStatus());
            syncData.setSellStatus(SELL_STATUS_ON_SALE); // 自动上架
            syncData.setVersion(pendingSnapshot.getSnapshotVersion());

            // 设置编辑状态和版本（无论首次还是后续审核）
            syncData.setEditStatus(AuditStatusEnum.APPROVED.getStatus()); // 2-审核通过
            syncData.setEditVersion(pendingSnapshot.getSnapshotVersion());

            boolean isFirstApproval = !AuditStatusEnum.APPROVED.getStatus().equals(project.getAuditStatus());
            if (isFirstApproval) {
                log.info("[auditProject] 首次审核通过，auditStatus=已通过, editStatus=审核通过, editVersion={}",
                        pendingSnapshot.getSnapshotVersion());
                // 如果是开放基金型的首次通过。直接为运行中
                if (PROJECT_CONFIG_TYPE_FUND.equals(project.getProjectConfigType())
                        && ASSET_TYPE_OPEN_FUND.equals(project.getProjectType())) {
                    syncData.setProjectStatus(RunStatusEnum.RUNNING.getStatus());
                    LocalDate startTime = LocalDate.now().plusDays(1L);
                    // 收益日期设置为第二天
                    syncData.setLockStartTime(startTime);
                    if (project.getDuration() != null) {
                        syncData.setLockEndTime(startTime.plusMonths(project.getDuration()));
                    }
                }
            } else {
                log.info("[auditProject] 编辑审核通过，auditStatus保持已通过, editStatus=审核通过, editVersion={}",
                        pendingSnapshot.getSnapshotVersion());
            }
            // 如果发行数量变化，重新计算剩余数量
            if (syncData.getIssueQuantity() != null
                    && !syncData.getIssueQuantity().equals(project.getIssueQuantity())) {
                Integer salesQuantity = project.getSalesQuantity() != null ? project.getSalesQuantity() : 0;
                Integer newRemainingQuantity = syncData.getIssueQuantity() - salesQuantity;
                syncData.setRemainingQuantity(newRemainingQuantity);
                log.info("[auditProject] 发行数量变化，重新计算剩余数量。项目ID: {}, 原发行数量: {}, 新发行数量: {}, 已售数量: {}, 新剩余数量: {}",
                        project.getProjectId(), project.getIssueQuantity(), syncData.getIssueQuantity(), salesQuantity,
                        newRemainingQuantity);
            }

            infoMapper.updateById(syncData);

            // 标记快照为已通过
            snapshotService.updateStatus(pendingSnapshot.getId(),
                    SnapshotStatusEnum.APPROVED.getStatus(),
                    auditUserId, auditUserName, auditReqVO.getAuditRemark());

            log.info("[auditProject] 快照数据同步完成，项目ID: {}, 版本: {}",
                    project.getProjectId(), pendingSnapshot.getSnapshotVersion());

            // 如果没有ProjectOperation，创建
            ProjectOperationDO operation = projectOperationMapper.selectById(project.getProjectId());
            if (operation == null) {
                operation = ProjectOperationDO.builder()
                        .projectId(auditReqVO.getProjectId())
                        .investorCount(0)
                        .dividendApplyCount(0)
                        .dividendApplyAmount(BigDecimal.ZERO)
                        .earlyRedemptionPeople(0)
                        .earlyRedemptionAmount(BigDecimal.ZERO)
                        .earlyRedemptionCount(0)
                        .maturityRedemptionCount(0)
                        .maturityRedemptionAmount(BigDecimal.ZERO)
                        .totalInvestorIncome(BigDecimal.ZERO)
                        .totalInvestorYield(BigDecimal.ZERO)
                        .earningCurrency(project.getEarningCurrency())
                        .investmentCurrency(project.getInvestmentCurrency())
                        .build();
                operation.setTenantId(project.getTenantId());
                projectOperationMapper.insert(operation);
                log.info("[auditProject] 项目运营统计记录已创建，项目ID: {}", operation.getProjectId());
            }
        } else {
            // 审核拒绝：标记快照为已拒绝
            log.info("[auditProject] 审核拒绝，快照ID: {}", pendingSnapshot.getId());

            snapshotService.updateStatus(pendingSnapshot.getId(),
                    SnapshotStatusEnum.REJECTED.getStatus(),
                    auditUserId, auditUserName, auditReqVO.getAuditRemark());

            // 判断项目当前状态，决定拒绝后的状态（通过 auditStatus 判断）
            boolean isReleased = AuditStatusEnum.APPROVED.getStatus().equals(project.getAuditStatus());

            ProjectInfoDO statusUpdate = new ProjectInfoDO();
            statusUpdate.setProjectId(project.getProjectId());
            statusUpdate.setAuditUserId(auditUserId);
            statusUpdate.setAuditUserName(auditUserName);
            statusUpdate.setAuditRemark(auditReqVO.getAuditRemark());

            if (isReleased) {
                // 已发布项目：恢复为已通过状态，editStatus 设为拒绝
                statusUpdate.setAuditStatus(AuditStatusEnum.APPROVED.getStatus()); // 保持已通过
                statusUpdate.setEditStatus(AuditStatusEnum.REJECTED.getStatus()); // 3-审核不通过
                log.info("[auditProject] 已发布项目编辑被拒绝，auditStatus保持已通过，editStatus=审核不通过");
            } else {
                // 未发布项目：设置为已拒绝状态
                statusUpdate.setAuditStatus(AuditStatusEnum.REJECTED.getStatus());
                log.info("[auditProject] 未发布项目首次审核被拒绝，auditStatus=已拒绝");
            }

            infoMapper.updateById(statusUpdate);
        }

        log.info("[auditProject] 项目审核完成，项目ID: {}, 审核结果: {}",
                auditReqVO.getProjectId(), auditReqVO.getApproved() ? "通过" : "不通过");

        return project;
    }

    @Override
    public void sendProjectAuditPassMessage(Long projectId) {
        ProjectInfoDO project = infoMapper.selectById(projectId);
        if (project != null && ChainTaskStatusEnum.PENDING.getStatus().equals(project.getChainStatus())) {
            // 更新为处理中
            LambdaUpdateWrapper<ProjectInfoDO> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(ProjectInfoDO::getProjectId, projectId);
            updateWrapper.eq(ProjectInfoDO::getChainStatus, ChainTaskStatusEnum.PENDING.getStatus());
            updateWrapper.set(ProjectInfoDO::getChainStatus, ChainTaskStatusEnum.PROCESSING.getStatus());
            int result = infoMapper.update(updateWrapper);
            if (result > 0) {
                // 生成项目符号：使用项目名称的前几个字符或简写
                String symbol = IdUtil.fastSimpleUUID();

                // 创建任务并发送消息
                String taskNo = chainOperationTaskService.createDeployTokenTask(
                        project.getProjectId(),
                        project.getProjectName(),
                        symbol);

                log.info("[auditProject] 已创建部署 Token 任务并发送消息，项目ID: {}, taskNo: {}",
                        project.getProjectId(), taskNo);
            }

        }

    }

    /**
     * 生成项目符号
     * 从项目名称生成一个简短的符号（最多10个字符）
     */
    private String generateProjectSymbol(String projectName) {
        if (projectName == null || projectName.isEmpty()) {
            return "TOKEN";
        }
        // 移除空格和特殊字符，转换为大写，最多取10个字符
        String symbol = projectName.replaceAll("[^a-zA-Z0-9]", "").toUpperCase();
        return symbol.length() > 10 ? symbol.substring(0, 10) : symbol;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSellStatus(ProjectInfoSellStatusReqVO sellStatusReqVO) {
        log.info("[updateSellStatus] 开始修改项目上下架状态，项目ID: {}, 目标状态: {}",
                sellStatusReqVO.getProjectId(), sellStatusReqVO.getSellStatus());

        // 1. 验证项目存在性
        ProjectInfoDO project = infoMapper.selectById(sellStatusReqVO.getProjectId());
        if (project == null) {
            log.warn("[updateSellStatus] 项目不存在，项目ID: {}", sellStatusReqVO.getProjectId());
            throw exception(INFO_NOT_EXISTS);
        }

        // 结束的项目无法编辑、
        if (ProjectStatusEnum.ENDED.getStatus().equals(project.getProjectStatus())) {
            throw new ServiceException(999, "已结束的项目上下架");
        }

        // 2. 只有上线通过的项目才能修改上下架状态
        if (!project.getAuditStatus().equals(AuditStatusEnum.APPROVED.getStatus())) {
            log.warn("[updateSellStatus] 项目未上线通过，不能修改上下架状态，项目ID: {}, 当前审核状态: {}",
                    sellStatusReqVO.getProjectId(), project.getAuditStatus());
            throw new ServiceException(999, "项目未上线通过，不能修改上下架状态");
        }
        // 3.封闭型项目如果已经运行中，则不能上架
        if (ASSET_TYPE_CLOSED_FUND.equals(project.getProjectType())
                && SELL_STATUS_ON_SALE.equals(sellStatusReqVO.getSellStatus())
                && RunStatusEnum.RUNNING.getStatus().equals(project.getProjectStatus())) {
            log.warn("[updateSellStatus] 项目未上线通过，不能修改上下架状态，项目ID: {}, 当前审核状态: {}",
                    sellStatusReqVO.getProjectId(), project.getAuditStatus());
            throw new ServiceException(999, "运行中的封闭型项目，不能修改上下架状态");
        }
        // 4.结束的项目不能上架
        if (SELL_STATUS_ON_SALE.equals(sellStatusReqVO.getSellStatus())
                && RunStatusEnum.ENDED.getStatus().equals(project.getProjectStatus())) {
            log.warn("[updateSellStatus] 项目未上线通过，不能修改上下架状态，项目ID: {}, 当前审核状态: {}",
                    sellStatusReqVO.getProjectId(), project.getAuditStatus());
            throw new ServiceException(999, "结束的项目，不能修改上下架状态");
        }

        // 3. 更新上下架状态
        ProjectInfoDO updateProject = ProjectInfoDO.builder()
                .projectId(sellStatusReqVO.getProjectId())
                .sellStatus(sellStatusReqVO.getSellStatus())
                .build();

        infoMapper.updateById(updateProject);

        log.info("[updateSellStatus] 项目上下架状态修改完成，项目ID: {}, 新状态: {}",
                sellStatusReqVO.getProjectId(), sellStatusReqVO.getSellStatus() == 1 ? "上架" : "下架");
    }

    @Resource
    private ProjectMiningConfigMapper miningConfigMapper;

    @Resource
    private ProjectFundConfigMapper fundConfigMapper;

    @Override
    public void updateConfig(ProjectConfigReqVO configReqVO) {
        log.info("[updateConfig] 开始更新项目配置，项目ID: {}", configReqVO.getProjectId());

        // 1. 验证项目存在性
        validateInfoExists(configReqVO.getProjectId());

        // 2. 查询配置是否存在
        ProjectMiningConfigDO config = miningConfigMapper.selectById(configReqVO.getProjectId());
        if (config == null) {
            config = ProjectMiningConfigDO.builder()
                    .projectId(configReqVO.getProjectId())
                    .build();
        }

        // 3. 更新字段
        config.setPowerConsumption(configReqVO.getPowerConsumption());
        config.setElectricityPrice(configReqVO.getElectricityPrice());
        config.setOperationCost(configReqVO.getOperationCost());
        config.setOperationCostType(configReqVO.getOperationCostType());
        config.setTeamShareRatio(configReqVO.getTeamShareRatio());
        config.setThresholdMin(configReqVO.getThresholdMin());
        config.setThresholdMax(configReqVO.getThresholdMax());
        config.setAlertEnabled(configReqVO.getAlertEnabled());

        // 更新矿池配置
        if (StrUtil.isNotBlank(configReqVO.getPoolAccessKey())) {
            config.setPoolAccessKey(configReqVO.getPoolAccessKey());
        }
        if (StrUtil.isNotBlank(configReqVO.getPoolName())) {
            config.setPoolName(configReqVO.getPoolName());
        }
        // 私钥加密存储 (如果有值则更新)
        if (StrUtil.isNotBlank(configReqVO.getPoolPrivateKey())) {
            // 简单处理，实际生产应加密
            config.setPoolPrivateKey(configReqVO.getPoolPrivateKey());
        }

        // 4. 保存
        if (miningConfigMapper.selectById(config.getProjectId()) == null) {
            miningConfigMapper.insert(config);
        } else {
            miningConfigMapper.updateById(config);
        }

        log.info("[updateConfig] 项目配置更新完成，项目ID: {}", configReqVO.getProjectId());
    }

    @Override
    public ProjectConfigRespVO getConfig(Long projectId) {
        ProjectMiningConfigDO config = miningConfigMapper.selectById(projectId);
        if (config == null) {
            return new ProjectConfigRespVO();
        }
        ProjectConfigRespVO resp = BeanUtils.toBean(config, ProjectConfigRespVO.class);
        resp.setProjectId(config.getProjectId());
        resp.setHasPoolPrivateKey(StrUtil.isNotBlank(config.getPoolPrivateKey()));
        return resp;
    }

    @Override
    public ProjectPoolConfigDTO getPoolConfig(Long projectId) {
        ProjectMiningConfigDO config = miningConfigMapper.selectById(projectId);
        if (config == null) {
            return null;
        }

        ProjectPoolConfigDTO dto = new ProjectPoolConfigDTO();
        dto.setPoolName(config.getPoolName());
        dto.setPoolAccessKey(config.getPoolAccessKey());
        // 解密私钥 (如果做了加密)
        dto.setPoolPrivateKey(config.getPoolPrivateKey());

        return dto;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void endRun(Long projectId) {
        log.info("[endRun] 结束项目运行，项目ID: {}", projectId);
        // 1. 验证项目存在性
        ProjectInfoDO project = validateInfoExists(projectId);

        // 2. 只有正在运行的项目可以结束运行
        if (RunStatusEnum.PENDING_RUN.getStatus().equals(project.getProjectStatus())) {
            log.warn("[endRun] 当前状态是运行审核中，项目ID: {}, 当前状态: {}", projectId, project.getProjectStatus());
            throw new ServiceException(999, "运行审核中项目不可以结束运行，请审核结束后再操作");
        }else if (RunStatusEnum.ENDED.getStatus().equals(project.getProjectStatus())) {
            log.warn("[endRun] 当前状态是运行审核中，项目ID: {}, 当前状态: {}", projectId, project.getProjectStatus());
            throw new ServiceException(999, "当前项目已结束运行，无需重复操作");
        }




        // 3. 计算前一天的日期
        LocalDate yesterday = LocalDate.now().minusDays(1);

        // 4. 更新项目状态和锁定结束时间
        ProjectInfoDO updateObj = new ProjectInfoDO();
        updateObj.setProjectId(projectId);
        updateObj.setProjectStatus(RunStatusEnum.ENDED.getStatus());
        updateObj.setLockEndTime(yesterday);
        updateObj.setSellStatus(SELL_STATUS_NOT_SALE);

        // 可选：如果是封闭型基金等，可能在此还要设置其它状态（如sellStatus=0 尽管之前已下架）。这里按照要求仅更新上述即可
        infoMapper.updateById(updateObj);

        // 5. 将所有买了该项目的订单的 lock_end_time 设置为前一天
        LambdaUpdateWrapper<ProjectOrderDO> orderUpdateWrapper = new LambdaUpdateWrapper<>();
        // 不仅要等于当前的项目ID，还要是已经审核通过或者成功的订单
        orderUpdateWrapper.eq(ProjectOrderDO::getProjectId, projectId);
        orderUpdateWrapper.eq(ProjectOrderDO::getOrderStatus, OrderStatusEnum.APPROVED.getStatus());
        orderUpdateWrapper.set(ProjectOrderDO::getLockEndTime, yesterday);

        projectOrderMapper.update(orderUpdateWrapper);

        log.info("[endRun] 项目及其订单锁定结束时间已更新为前一天，项目ID: {}", projectId);
    }

}