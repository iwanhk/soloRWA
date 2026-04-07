package cc.bamboo.module.project.service.projectfundconfig;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import cc.bamboo.module.project.controller.admin.projectfundconfig.vo.*;
import cc.bamboo.module.project.dal.dataobject.projectfundconfig.ProjectFundConfigDO;
import cc.bamboo.framework.common.pojo.PageResult;
import cc.bamboo.framework.common.pojo.PageParam;
import cc.bamboo.framework.common.util.object.BeanUtils;

import cc.bamboo.module.project.dal.mysql.projectfundconfig.ProjectFundConfigMapper;

import static cc.bamboo.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cc.bamboo.module.project.enums.ErrorCodeConstants.*;

/**
 * 基金项目配置 Service 实现类
 *
 * @author Swolf
 */
@Service
@Validated
@Slf4j
public class ProjectFundConfigServiceImpl implements ProjectFundConfigService {

    @Resource
    private ProjectFundConfigMapper fundConfigMapper;

    @Override
    public Long createFundConfig(ProjectFundConfigSaveReqVO createReqVO) {
        // 插入
        ProjectFundConfigDO fundConfig = BeanUtils.toBean(createReqVO, ProjectFundConfigDO.class);
        fundConfigMapper.insert(fundConfig);
        // 返回
        return fundConfig.getProjectId();
    }

    @Override
    public void updateFundConfig(ProjectFundConfigSaveReqVO updateReqVO) {
        log.info("[updateFundConfig] 开始更新基金项目配置，项目ID: {}", updateReqVO.getProjectId());

        // 1. 查询配置是否存在
        ProjectFundConfigDO config = fundConfigMapper.selectById(updateReqVO.getProjectId());
        if (config == null) {
            config = BeanUtils.toBean(updateReqVO, ProjectFundConfigDO.class);
            fundConfigMapper.insert(config);
            log.info("[updateFundConfig] 基金项目配置不存在，已创建新记录，项目ID: {}", updateReqVO.getProjectId());
        } else {
            ProjectFundConfigDO updateObj = BeanUtils.toBean(updateReqVO, ProjectFundConfigDO.class);
            fundConfigMapper.updateById(updateObj);
            log.info("[updateFundConfig] 基金项目配置已更新，项目ID: {}", updateReqVO.getProjectId());
        }
    }

    @Override
    public void deleteFundConfig(Long id) {
        // 校验存在
        validateFundConfigExists(id);
        // 删除
        fundConfigMapper.deleteById(id);
    }

    private void validateFundConfigExists(Long id) {
        if (fundConfigMapper.selectById(id) == null) {
            throw exception(FUND_CONFIG_NOT_EXISTS);
        }
    }

    @Override
    public ProjectFundConfigDO getFundConfig(Long id) {
        return fundConfigMapper.selectById(id);
    }

    @Override
    public PageResult<ProjectFundConfigDO> getFundConfigPage(ProjectFundConfigPageReqVO pageReqVO) {
        return fundConfigMapper.selectPage(pageReqVO);
    }

}