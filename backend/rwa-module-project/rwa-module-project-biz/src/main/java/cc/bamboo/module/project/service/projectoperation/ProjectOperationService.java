package cc.bamboo.module.project.service.projectoperation;

import java.util.*;
import javax.validation.*;
import cc.bamboo.module.project.controller.admin.projectoperation.vo.*;
import cc.bamboo.module.project.dal.dataobject.projectoperation.ProjectOperationDO;
import cc.bamboo.framework.common.pojo.PageResult;
import cc.bamboo.framework.common.pojo.PageParam;

/**
 * 项目运营统计表 Service 接口
 *
 * @author Swolf
 */
public interface ProjectOperationService {

    /**
     * 创建项目运营统计表
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createOperation(@Valid ProjectOperationSaveReqVO createReqVO);

    /**
     * 更新项目运营统计表
     *
     * @param updateReqVO 更新信息
     */
    void updateOperation(@Valid ProjectOperationSaveReqVO updateReqVO);

    /**
     * 删除项目运营统计表
     *
     * @param id 编号
     */
    void deleteOperation(Long id);

    /**
     * 获得项目运营统计表
     *
     * @param id 编号
     * @return 项目运营统计表
     */
    ProjectOperationDO getOperation(Long id);

    /**
     * 获得项目运营统计表分页
     *
     * @param pageReqVO 分页查询
     * @return 项目运营统计表分页
     */
    PageResult<ProjectOperationDO> getOperationPage(ProjectOperationPageReqVO pageReqVO);

}