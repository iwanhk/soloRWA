package cc.bamboo.module.project.service.projectdividendperiod;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import cc.bamboo.module.project.controller.admin.projectdividendperiod.vo.*;
import cc.bamboo.module.project.dal.dataobject.projectdividendperiod.ProjectDividendPeriodDO;
import cc.bamboo.framework.common.pojo.PageResult;
import cc.bamboo.framework.common.pojo.PageParam;
import cc.bamboo.framework.common.util.object.BeanUtils;

import cc.bamboo.module.project.dal.mysql.projectdividendperiod.ProjectDividendPeriodMapper;

import static cc.bamboo.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cc.bamboo.module.project.enums.ErrorCodeConstants.*;

/**
 * 分红周期 Service 实现类
 *
 * @author Swolf
 */
@Service
@Validated
public class ProjectDividendPeriodServiceImpl implements ProjectDividendPeriodService {

    @Resource
    private ProjectDividendPeriodMapper dividendPeriodMapper;

    @Override
    public Long createDividendPeriod(ProjectDividendPeriodSaveReqVO createReqVO) {
        // 插入
        ProjectDividendPeriodDO dividendPeriod = BeanUtils.toBean(createReqVO, ProjectDividendPeriodDO.class);
        dividendPeriodMapper.insert(dividendPeriod);
        // 返回
        return dividendPeriod.getId();
    }

    @Override
    public void updateDividendPeriod(ProjectDividendPeriodSaveReqVO updateReqVO) {
        // 校验存在
        validateDividendPeriodExists(updateReqVO.getId());
        // 更新
        ProjectDividendPeriodDO updateObj = BeanUtils.toBean(updateReqVO, ProjectDividendPeriodDO.class);
        dividendPeriodMapper.updateById(updateObj);
    }

    @Override
    public void deleteDividendPeriod(Long id) {
        // 校验存在
        validateDividendPeriodExists(id);
        // 删除
        dividendPeriodMapper.deleteById(id);
    }

    private void validateDividendPeriodExists(Long id) {
        if (dividendPeriodMapper.selectById(id) == null) {
            throw exception(DIVIDEND_PERIOD_NOT_EXISTS);
        }
    }

    @Override
    public ProjectDividendPeriodDO getDividendPeriod(Long id) {
        return dividendPeriodMapper.selectById(id);
    }

    @Override
    public PageResult<ProjectDividendPeriodDO> getDividendPeriodPage(ProjectDividendPeriodPageReqVO pageReqVO) {
        return dividendPeriodMapper.selectPage(pageReqVO);
    }

}