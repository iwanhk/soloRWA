package cc.bamboo.module.project.service.projectnotice;

import cc.bamboo.framework.mybatis.core.query.LambdaQueryWrapperX;
import cc.bamboo.framework.security.core.util.SecurityFrameworkUtils;
import cc.bamboo.framework.tenant.core.context.TenantContextHolder;
import cc.bamboo.module.project.controller.app.projectinfo.vo.AppProjectNoticePageReqVO;
import cc.bamboo.module.project.controller.app.projectinfo.vo.AppProjectNoticeRespVO;
import cc.bamboo.module.project.dal.dataobject.projectinfo.ProjectInfoDO;
import cc.bamboo.module.project.dal.dataobject.projectorder.ProjectOrderDO;
import cc.bamboo.module.project.dal.mysql.projectinfo.ProjectInfoMapper;
import cc.bamboo.module.project.dal.mysql.projectorder.ProjectOrderMapper;
import cc.bamboo.module.project.enums.NoticeStatusEnum;
import cc.bamboo.module.project.enums.OrderStatusEnum;
import cc.bamboo.module.project.service.projectinfo.ProjectInfoService;
import cc.bamboo.module.project.service.projectinfo.ProjectInfoServiceImpl;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.lang.generator.SnowflakeGenerator;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.aspectj.weaver.ast.Not;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import cc.bamboo.module.project.controller.admin.projectnotice.vo.*;
import cc.bamboo.module.project.dal.dataobject.projectnotice.ProjectNoticeDO;
import cc.bamboo.framework.common.pojo.PageResult;
import cc.bamboo.framework.common.pojo.PageParam;
import cc.bamboo.framework.common.util.object.BeanUtils;

import cc.bamboo.module.project.dal.mysql.projectnotice.ProjectNoticeMapper;

import static cc.bamboo.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cc.bamboo.framework.security.core.util.SecurityFrameworkUtils.getLoginUser;
import static cc.bamboo.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;
import static cc.bamboo.module.project.enums.ErrorCodeConstants.*;

import cc.bamboo.framework.common.util.json.JsonUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.lang3.StringUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * 项目通告表（含全局通告） Service 实现类
 *
 * @author Swolf
 */
@Service
@Validated
@Slf4j
public class ProjectNoticeServiceImpl implements ProjectNoticeService {

    @Resource
    private ProjectNoticeMapper noticeMapper;

    @Resource
    private ProjectInfoService projectInfoService;

    @Resource
    private ProjectOrderMapper projectOrderMapper;

    @Resource
    private ProjectInfoMapper projectInfoMapper;

    @Override
    public Long createNotice(ProjectNoticeSaveReqVO createReqVO) {
        // 插入
        ProjectNoticeDO notice = BeanUtils.toBean(createReqVO, ProjectNoticeDO.class);
        // 生成NoticeNo,No + 雪花树
        // 查询project
        ProjectInfoDO projectInfoDO = projectInfoMapper.selectById(notice.getProjectId());
        if (projectInfoDO == null) {
            throw exception(PROJECT_NOT_FOUND);
        }
        Long tenantId = TenantContextHolder.getTenantId();
        // 如果项目的租户id和当前租户id不一致，则抛出异常
        if (!Objects.equals(projectInfoDO.getTenantId(), tenantId) && tenantId != 1L) {
            throw exception(NOTICE_TENANT_NOT_MATCH);
        }
        notice.setProjectName(projectInfoDO.getProjectName());
        String noticeNo = "NO" + IdUtil.getSnowflakeNextId();
        notice.setNoticeNo(noticeNo);
        if (NoticeStatusEnum.RELEASED.getType().equals(notice.getNoticeStatus())) {
            // 如果是已发布，则设置发布人和发布时间
            Long publishUserId = getLoginUserId();
            String publishUserName = SecurityFrameworkUtils.getLoginUserNickname();
            notice.setPublishUserId(publishUserId);
            notice.setPublishUserName(publishUserName);
            notice.setPublishTime(LocalDateTime.now());
        }
        noticeMapper.insert(notice);
        // 返回
        return notice.getId();
    }

    @Override
    public void updateNotice(ProjectNoticeUpdateReqVO updateReqVO) {
        // 校验存在
        ProjectNoticeDO old = validateNoticeExists(updateReqVO.getId());
        Long tenantId = TenantContextHolder.getTenantId();
        // 如果项目的租户id和当前租户id不一致，则抛出异常
        if (!Objects.equals(old.getTenantId(), tenantId) && tenantId != 1L) {
            throw exception(NOTICE_TENANT_NOT_MATCH);
        }
        // 更新
        ProjectNoticeDO updateObj = BeanUtils.toBean(updateReqVO, ProjectNoticeDO.class);
        if (NoticeStatusEnum.RELEASED.getType().equals(updateReqVO.getNoticeStatus())) {
            // 如果是已发布，则设置发布人和发布时间
            Long publishUserId = getLoginUserId();
            String publishUserName = SecurityFrameworkUtils.getLoginUserNickname();
            updateObj.setPublishUserId(publishUserId);
            updateObj.setPublishUserName(publishUserName);
            updateObj.setPublishTime(LocalDateTime.now());
        }
        noticeMapper.updateById(updateObj);
    }

    @Override
    public void deleteNotice(Long id) {
        // 校验存在
        validateNoticeExists(id);
        // 删除
        noticeMapper.deleteById(id);
    }

    private ProjectNoticeDO validateNoticeExists(Long id) {
        ProjectNoticeDO notice = noticeMapper.selectById(id);
        if (notice == null) {
            throw exception(NOTICE_NOT_EXISTS);
        } else {
            return notice;
        }
    }

    @Override
    public ProjectNoticeDO getNotice(Long id) {
        return noticeMapper.selectById(id);
    }

    @Override
    public PageResult<ProjectNoticeDO> getNoticePage(ProjectNoticePageReqVO pageReqVO) {
        return noticeMapper.selectPage(pageReqVO);
    }

    @Override
    public PageResult<AppProjectNoticeRespVO> getAppNoticePage(AppProjectNoticePageReqVO pageReqVO) {
        Long userId = getLoginUserId();
        // 如果userId 为空，则直接返回空
        if (userId == null) {
            return new PageResult<>();
        }
        // 如果不为空则判断用户是否购买了该项目，只有购买了该项目的用户才可以查看该项目的通告

        long count = projectOrderMapper.getProjectOrderCountByProjectId(pageReqVO.getProjectId(), userId);
        // 如果用户没有购买该项目，则直接返回空
        if (count == 0) {
            return new PageResult<>();
        }
        // 查询该项目的所有已发布的通告
        PageResult<ProjectNoticeDO> pageResult = noticeMapper.selectPage(pageReqVO,
                new LambdaQueryWrapperX<ProjectNoticeDO>()
                        .eqIfPresent(ProjectNoticeDO::getProjectId, pageReqVO.getProjectId())
                        .eq(ProjectNoticeDO::getNoticeStatus, NoticeStatusEnum.RELEASED.getType())
                        .orderByDesc(ProjectNoticeDO::getPublishTime));
        PageResult<AppProjectNoticeRespVO> result = BeanUtils.toBean(pageResult, AppProjectNoticeRespVO.class);
        if (result.getList() != null && !result.getList().isEmpty()) {
            for (int i = 0; i < result.getList().size(); i++) {
                fillMultiLang(result.getList().get(i), pageResult.getList().get(i));
            }
        }
        return result;
    }

    /**
     * 填充多语言字段
     */
    private void fillMultiLang(AppProjectNoticeRespVO respVO, ProjectNoticeDO notice) {
        String locale = cc.bamboo.framework.web.core.util.WebFrameworkUtils.getCurrentLocale();
        if (StringUtils.isBlank(locale) || StringUtils.isBlank(notice.getNoticeJson())) {
            return;
        }

        try {
            // noticeJson 结构: { "en-US": { "notice_title": "...", "notice_content": "..." }
            // }
            Map<String, Map<String, String>> multiLangMap = JsonUtils.parseObject(
                    notice.getNoticeJson(),
                    new TypeReference<Map<String, Map<String, String>>>() {
                    });

            if (multiLangMap == null || !multiLangMap.containsKey(locale)) {
                return;
            }

            Map<String, String> langData = multiLangMap.get(locale);
            if (langData == null) {
                return;
            }

            if (langData.containsKey("notice_title")) {
                respVO.setNoticeTitle(langData.get("notice_title"));
            }
            if (langData.containsKey("notice_content")) {
                respVO.setNoticeContent(langData.get("notice_content"));
            }

        } catch (Exception e) {
            log.warn("[fillMultiLang] 解析多语言失败, noticeId: {}", notice.getId(), e);
        }
    }

}