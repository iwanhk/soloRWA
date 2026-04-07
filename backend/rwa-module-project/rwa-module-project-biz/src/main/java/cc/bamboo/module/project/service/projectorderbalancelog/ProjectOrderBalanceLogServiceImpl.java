package cc.bamboo.module.project.service.projectorderbalancelog;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import cc.bamboo.module.project.controller.admin.projectorderbalancelog.vo.*;
import cc.bamboo.module.project.dal.dataobject.projectorderbalancelog.ProjectOrderBalanceLogDO;
import cc.bamboo.framework.common.pojo.PageResult;
import cc.bamboo.framework.common.pojo.PageParam;
import cc.bamboo.framework.common.util.object.BeanUtils;

import cc.bamboo.module.project.dal.mysql.projectorderbalancelog.ProjectOrderBalanceLogMapper;

import static cc.bamboo.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cc.bamboo.module.project.enums.ErrorCodeConstants.*;

/**
 * 用户项目余额记录 Service 实现类
 *
 * @author Swolf
 */
@Service
@Validated
public class ProjectOrderBalanceLogServiceImpl implements ProjectOrderBalanceLogService {

    @Resource
    private ProjectOrderBalanceLogMapper orderBalanceLogMapper;

    @Override
    public Long createOrderBalanceLog(ProjectOrderBalanceLogSaveReqVO createReqVO) {
        // 插入
        ProjectOrderBalanceLogDO orderBalanceLog = BeanUtils.toBean(createReqVO, ProjectOrderBalanceLogDO.class);
        orderBalanceLogMapper.insert(orderBalanceLog);
        // 返回
        return orderBalanceLog.getId();
    }

    @Override
    public void updateOrderBalanceLog(ProjectOrderBalanceLogSaveReqVO updateReqVO) {
        // 校验存在
        validateOrderBalanceLogExists(updateReqVO.getId());
        // 更新
        ProjectOrderBalanceLogDO updateObj = BeanUtils.toBean(updateReqVO, ProjectOrderBalanceLogDO.class);
        orderBalanceLogMapper.updateById(updateObj);
    }

    @Override
    public void deleteOrderBalanceLog(Long id) {
        // 校验存在
        validateOrderBalanceLogExists(id);
        // 删除
        orderBalanceLogMapper.deleteById(id);
    }

    private void validateOrderBalanceLogExists(Long id) {
        if (orderBalanceLogMapper.selectById(id) == null) {
            throw exception(ORDER_BALANCE_LOG_NOT_EXISTS);
        }
    }

    @Override
    public ProjectOrderBalanceLogDO getOrderBalanceLog(Long id) {
        return orderBalanceLogMapper.selectById(id);
    }

    @Override
    public PageResult<ProjectOrderBalanceLogDO> getOrderBalanceLogPage(ProjectOrderBalanceLogPageReqVO pageReqVO) {
        return orderBalanceLogMapper.selectPage(pageReqVO);
    }

}