package cc.bamboo.module.project.service.projectfundconfig;

import java.util.*;
import javax.validation.*;
import cc.bamboo.module.project.controller.admin.projectfundconfig.vo.*;
import cc.bamboo.module.project.dal.dataobject.projectfundconfig.ProjectFundConfigDO;
import cc.bamboo.framework.common.pojo.PageResult;
import cc.bamboo.framework.common.pojo.PageParam;

/**
 * 基金项目配置 Service 接口
 *
 * @author Swolf
 */
public interface ProjectFundConfigService {

    /**
     * 创建基金项目配置
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createFundConfig(@Valid ProjectFundConfigSaveReqVO createReqVO);

    /**
     * 更新基金项目配置
     *
     * @param updateReqVO 更新信息
     */
    void updateFundConfig(@Valid ProjectFundConfigSaveReqVO updateReqVO);

    /**
     * 删除基金项目配置
     *
     * @param id 编号
     */
    void deleteFundConfig(Long id);

    /**
     * 获得基金项目配置
     *
     * @param id 编号
     * @return 基金项目配置
     */
    ProjectFundConfigDO getFundConfig(Long id);

    /**
     * 获得基金项目配置分页
     *
     * @param pageReqVO 分页查询
     * @return 基金项目配置分页
     */
    PageResult<ProjectFundConfigDO> getFundConfigPage(ProjectFundConfigPageReqVO pageReqVO);

}