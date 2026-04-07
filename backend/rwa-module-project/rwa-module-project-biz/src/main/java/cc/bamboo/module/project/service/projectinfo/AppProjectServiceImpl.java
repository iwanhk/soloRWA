package cc.bamboo.module.project.service.projectinfo;

import cc.bamboo.framework.common.pojo.PageResult;
import cc.bamboo.framework.common.util.json.JsonUtils;
import cc.bamboo.framework.common.util.object.BeanUtils;
import cc.bamboo.framework.mybatis.core.query.LambdaQueryWrapperX;
import cc.bamboo.module.project.controller.app.projectinfo.vo.*;
import cc.bamboo.module.project.dal.dataobject.projectinfo.ProjectInfoDO;
import cc.bamboo.module.project.dal.mysql.projectdividendperiod.ProjectDividendPeriodMapper;
import cc.bamboo.module.project.dal.mysql.projectinfo.ProjectInfoMapper;
import cc.bamboo.module.project.dal.mysql.projectorder.ProjectOrderMapper;
import cc.bamboo.module.project.enums.AuditStatusEnum;
import cc.bamboo.module.project.enums.ProjectStatusEnum;
import cc.bamboo.module.project.service.projectorder.ProjectOrderService;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static cc.bamboo.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cc.bamboo.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;
import static cc.bamboo.module.project.enums.ApiConstants.ASSET_TYPE_OPEN_FUND;
import static cc.bamboo.module.project.enums.ApiConstants.SELL_STATUS_ON_SALE;
import static cc.bamboo.module.project.enums.ErrorCodeConstants.PROJECT_NOT_FOUND;

/**
 * 用户端 - 项目信息 Service 实现类
 *
 * @author Swolf
 */
@Service
@Validated
@Slf4j
public class AppProjectServiceImpl implements AppProjectService {

    @Resource
    private ProjectInfoMapper projectInfoMapper;

    @Resource
    private ProjectDividendPeriodMapper dividendPeriodMapper;

    @Resource
    private ProjectOrderService projectOrderService;

    @Resource
    private ProjectOrderMapper projectOrderMapper;

    @Override
    public ProjectInfoDO getProjectDetail(Long id) {
        // 查询项目
        ProjectInfoDO project = projectInfoMapper.selectById(id);

        // 验证项目存在性
        if (project == null) {
            log.warn("[getProjectDetail] 项目不存在，项目ID: {}", id);
            throw exception(PROJECT_NOT_FOUND);
        }

        log.info("[getProjectDetail] 查询项目详情成功，项目ID: {}, 项目名称: {}", id, project.getProjectName());
        return project;
    }

    @Override
    public AppProjectDetailRespVO getAppProjectDetail(Long id) {
        // 查询项目详情
        ProjectInfoDO project = getProjectDetail(id);

        if (!AuditStatusEnum.APPROVED.getStatus().equals(project.getAuditStatus())) {
            throw exception(PROJECT_NOT_FOUND);
        }

        // 转换为 VO
        AppProjectDetailRespVO respVO = BeanUtils.toBean(project, AppProjectDetailRespVO.class);

        // 多语言处理
        fillMultiLang(respVO, project);
        if(Objects.equals(project.getProjectType(), ASSET_TYPE_OPEN_FUND)){
            // 查询最早分红日期
            respVO.setFirstDividendDate(project.getLockStartTime());
        }else{
            // 查询最早分红日期
            respVO.setFirstDividendDate(getFirstDividendDate(id));
        }


        // 计算提前赎回手续费率（如果在锁定期内）
   /*     LocalDate now = LocalDate.now();
        BigDecimal feeRate = BigDecimal.ZERO;
        long holdDays = 0;
        if (project.getLockStartTime() != null && project.getLockEndTime() != null) {
            // 在锁定期之间
            if (now.isAfter(project.getLockStartTime()) && now.isBefore(project.getLockEndTime())) {
                holdDays = ChronoUnit.DAYS.between(project.getLockStartTime(), now);
            } else if (now.isAfter(project.getLockEndTime())) {
                // 在锁定期之后
                respVO.setEarlyRedemptionFee(BigDecimal.ZERO);
                return respVO;
            }

        }
        // 如果项目还没有锁定期
        if (project.getLockStartTime() == null) {
            holdDays = 0;
        }

        feeRate = projectOrderService
                .calculateEarlyRedemptionFeeRate(project.getEarlyRedemptionFeeJson(), holdDays);
        respVO.setEarlyRedemptionFee(feeRate.multiply(BigDecimal.valueOf(100)));*/

        return respVO;
    }

    @Override
    public String getProjectFileUrls(Long id) {
        // 查询项目详情
        ProjectInfoDO project = getProjectDetail(id);
        Long userId = getLoginUserId();
        return project.getProjectFileUrls();
        /*// 如果userId 为空，则直接返回空
        long count = projectOrderMapper.getProjectOrderCountByProjectId(id, userId);
        if (count > 0) {
            return project.getProjectFileUrls();
        }
        // 如果不为空则判断用户是否购买了该项目，只有购买了该项目的用户才可以查看该项目的通告
        try {
            String files = project.getProjectFileUrls();
            if (StringUtils.isNotBlank(files)) {
                // 解析json的array
                List<ProjectFileRespVO> fileList = JsonUtils.parseArray(files, ProjectFileRespVO.class);
                // 将文件路径置空
                fileList.forEach(item -> item.setPath(null));
                // 重新转为String
                files = JsonUtils.toJsonString(fileList);

                return files;
            }
        } catch (Exception e) {
            log.error("[getAppProjectDetail] 解析项目文件URL失败，项目ID: {}", id, e);
        }

        return null;*/
    }

    @Override
    public PageResult<AppProjectListRespVO> getProjectList(AppProjectListReqVO reqVO) {

        // 只有审核通过的项目才能被APP端查看
        PageResult<ProjectInfoDO> pageResult = projectInfoMapper.selectPage(reqVO,
                new LambdaQueryWrapperX<ProjectInfoDO>()
                        .eq(ProjectInfoDO::getAuditStatus, AuditStatusEnum.APPROVED.getStatus())
                        .eq(ProjectInfoDO::getSellStatus, SELL_STATUS_ON_SALE) // 上架状态
                        .eqIfPresent(ProjectInfoDO::getAssetType, reqVO.getAssetType())
                        .orderByDesc(ProjectInfoDO::getCreateTime));

        PageResult<AppProjectListRespVO> result = BeanUtils.toBean(pageResult, AppProjectListRespVO.class);

        // 多语言处理
        if (result.getList() != null && !result.getList().isEmpty()) {
            for (int i = 0; i < result.getList().size(); i++) {
                fillMultiLang(result.getList().get(i), pageResult.getList().get(i));
            }
        }

        return result;
    }

    @Override
    public LocalDate getFirstDividendDate(Long projectId) {
        return dividendPeriodMapper.selectFirstUnlockDateByProjectId(projectId);
    }

    @Override
    public List<AssetTypeCountRespVO> countProjectByAssetType() {
        return projectInfoMapper.countProjectByAssetType();
    }

    /**
     * 填充多语言字段
     */
    private void fillMultiLang(Object respVO, ProjectInfoDO project) {
        String locale = cc.bamboo.framework.web.core.util.WebFrameworkUtils.getCurrentLocale();
        if (StringUtils.isBlank(locale) || StringUtils.isBlank(project.getProjectJson())) {
            return;
        }

        try {
            // ProjectJson 结构: { "en-US": { "project_name": "...", ... } }
            //需要忽略大小写
            Map<String, Map<String, String>> multiLangMap = JsonUtils.parseObject(
                    project.getProjectJson(),
                    new TypeReference<Map<String, Map<String, String>>>() {
                    });

            if (multiLangMap == null) {
                return;
            }

            // 2. 忽略大小写查找匹配的键
            String matchedLocaleKey = null;
            String targetLocaleLower = locale.toLowerCase(Locale.ROOT); // 统一转小写，指定Locale.ROOT避免本地化问题
            for (String key : multiLangMap.keySet()) {
                if (key != null && key.toLowerCase(Locale.ROOT).equals(targetLocaleLower)) {
                    matchedLocaleKey = key;
                    break; // 找到第一个匹配的就退出
                }
            }

            // 3. 未找到匹配的键则返回
            if (matchedLocaleKey == null) {
                return;
            }

            // 4. 使用匹配到的真实键获取对应的值
            Map<String, String> langData = multiLangMap.get(matchedLocaleKey);

            if (langData == null) {
                return;
            }

            if (respVO instanceof AppProjectDetailRespVO) {
                AppProjectDetailRespVO detailVO = (AppProjectDetailRespVO) respVO;
                if (langData.containsKey("project_name"))
                    detailVO.setProjectName(langData.get("project_name"));
                if (langData.containsKey("project_intro"))
                    detailVO.setProjectIntro(langData.get("project_intro"));
                if (langData.containsKey("redemption_rules"))
                    detailVO.setRedemptionRules(langData.get("redemption_rules"));
                if (langData.containsKey("purchase_instructions"))
                    detailVO.setPurchaseInstructions(langData.get("purchase_instructions"));
                if (langData.containsKey("dividend_instructions"))
                    detailVO.setDividendInstructions(langData.get("dividend_instructions"));
            } else if (respVO instanceof AppProjectListRespVO) {
                AppProjectListRespVO listVO = (AppProjectListRespVO) respVO;
                if (langData.containsKey("project_name"))
                    listVO.setProjectName(langData.get("project_name"));
            }
        } catch (Exception e) {
            log.warn("[fillMultiLang] 解析多语言失败, projectId: {}", project.getProjectId(), e);
        }
    }

}
