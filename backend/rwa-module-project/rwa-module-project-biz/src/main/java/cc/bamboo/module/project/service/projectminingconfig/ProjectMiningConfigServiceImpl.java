package cc.bamboo.module.project.service.projectminingconfig;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import cc.bamboo.module.project.controller.admin.projectminingconfig.vo.*;
import cc.bamboo.module.project.dal.dataobject.projectminingconfig.ProjectMiningConfigDO;
import cc.bamboo.framework.common.pojo.PageResult;
import cc.bamboo.framework.common.pojo.PageParam;
import cc.bamboo.framework.common.util.object.BeanUtils;

import cc.bamboo.module.project.dal.mysql.projectminingconfig.ProjectMiningConfigMapper;

import static cc.bamboo.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cc.bamboo.module.project.enums.ErrorCodeConstants.*;

/**
 * 挖矿项目配置 Service 实现类
 *
 * @author swolf
 */
@Service
@Validated
public class ProjectMiningConfigServiceImpl implements ProjectMiningConfigService {

    @Resource
    private ProjectMiningConfigMapper miningConfigMapper;

    @Override
    public Long createMiningConfig(ProjectMiningConfigSaveReqVO createReqVO) {
        // 插入
        ProjectMiningConfigDO miningConfig = BeanUtils.toBean(createReqVO, ProjectMiningConfigDO.class);
        miningConfigMapper.insert(miningConfig);
        // 返回
        return miningConfig.getProjectId();
    }

    @Override
    public void updateMiningConfig(ProjectMiningConfigSaveReqVO updateReqVO) {
        // 校验存在
        validateMiningConfigExists(updateReqVO.getProjectId());
        // 更新
        ProjectMiningConfigDO updateObj = BeanUtils.toBean(updateReqVO, ProjectMiningConfigDO.class);
        miningConfigMapper.updateById(updateObj);
    }

    @Override
    public void deleteMiningConfig(Long id) {
        // 校验存在
        validateMiningConfigExists(id);
        // 删除
        miningConfigMapper.deleteById(id);
    }

    private void validateMiningConfigExists(Long id) {
        if (miningConfigMapper.selectById(id) == null) {
            throw exception(MINING_CONFIG_NOT_EXISTS);
        }
    }

    @Override
    public ProjectMiningConfigDO getMiningConfig(Long id) {
        return miningConfigMapper.selectById(id);
    }

    @Override
    public PageResult<ProjectMiningConfigDO> getMiningConfigPage(ProjectMiningConfigPageReqVO pageReqVO) {
        return miningConfigMapper.selectPage(pageReqVO);
    }

}