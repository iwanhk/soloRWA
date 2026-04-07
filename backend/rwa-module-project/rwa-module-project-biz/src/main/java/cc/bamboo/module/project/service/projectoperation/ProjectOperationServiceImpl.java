package cc.bamboo.module.project.service.projectoperation;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import cc.bamboo.module.project.controller.admin.projectoperation.vo.*;
import cc.bamboo.module.project.dal.dataobject.projectoperation.ProjectOperationDO;
import cc.bamboo.framework.common.pojo.PageResult;
import cc.bamboo.framework.common.pojo.PageParam;
import cc.bamboo.framework.common.util.object.BeanUtils;

import cc.bamboo.module.project.dal.mysql.projectoperation.ProjectOperationMapper;

import static cc.bamboo.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cc.bamboo.module.project.enums.ErrorCodeConstants.*;

/**
 * 项目运营统计表 Service 实现类
 *
 * @author Swolf
 */
@Service
@Validated
public class ProjectOperationServiceImpl implements ProjectOperationService {

    @Resource
    private ProjectOperationMapper operationMapper;

    @Override
    public Long createOperation(ProjectOperationSaveReqVO createReqVO) {
        // 插入
        ProjectOperationDO operation = BeanUtils.toBean(createReqVO, ProjectOperationDO.class);
        operationMapper.insert(operation);
        // 返回
        return operation.getProjectId();
    }

    @Override
    public void updateOperation(ProjectOperationSaveReqVO updateReqVO) {
        // 校验存在
        validateOperationExists(updateReqVO.getProjectId());
        // 更新
        ProjectOperationDO updateObj = BeanUtils.toBean(updateReqVO, ProjectOperationDO.class);
        operationMapper.updateById(updateObj);
    }

    @Override
    public void deleteOperation(Long id) {
        // 校验存在
        validateOperationExists(id);
        // 删除
        operationMapper.deleteById(id);
    }

    private void validateOperationExists(Long id) {
        if (operationMapper.selectById(id) == null) {
            throw exception(OPERATION_NOT_EXISTS);
        }
    }

    @Override
    public ProjectOperationDO getOperation(Long id) {
        return operationMapper.selectById(id);
    }

    @Override
    public PageResult<ProjectOperationDO> getOperationPage(ProjectOperationPageReqVO pageReqVO) {
        return operationMapper.selectPage(pageReqVO);
    }

}