package cc.bamboo.module.project.service.projectminingconfig;

import java.util.*;
import javax.validation.*;
import cc.bamboo.module.project.controller.admin.projectminingconfig.vo.*;
import cc.bamboo.module.project.dal.dataobject.projectminingconfig.ProjectMiningConfigDO;
import cc.bamboo.framework.common.pojo.PageResult;
import cc.bamboo.framework.common.pojo.PageParam;

/**
 * 挖矿项目配置 Service 接口
 *
 * @author swolf
 */
public interface ProjectMiningConfigService {

    /**
     * 创建挖矿项目配置
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createMiningConfig(@Valid ProjectMiningConfigSaveReqVO createReqVO);

    /**
     * 更新挖矿项目配置
     *
     * @param updateReqVO 更新信息
     */
    void updateMiningConfig(@Valid ProjectMiningConfigSaveReqVO updateReqVO);

    /**
     * 删除挖矿项目配置
     *
     * @param id 编号
     */
    void deleteMiningConfig(Long id);

    /**
     * 获得挖矿项目配置
     *
     * @param id 编号
     * @return 挖矿项目配置
     */
    ProjectMiningConfigDO getMiningConfig(Long id);

    /**
     * 获得挖矿项目配置分页
     *
     * @param pageReqVO 分页查询
     * @return 挖矿项目配置分页
     */
    PageResult<ProjectMiningConfigDO> getMiningConfigPage(ProjectMiningConfigPageReqVO pageReqVO);

}