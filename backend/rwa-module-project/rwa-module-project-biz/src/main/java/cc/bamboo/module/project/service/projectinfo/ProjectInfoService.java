package cc.bamboo.module.project.service.projectinfo;

import javax.validation.*;
import cc.bamboo.module.project.controller.admin.projectinfo.vo.*;
import cc.bamboo.module.project.dal.dataobject.projectinfo.ProjectInfoDO;
import cc.bamboo.framework.common.pojo.PageResult;
import cc.bamboo.module.project.service.projectinfo.dto.ProjectPoolConfigDTO;

import java.util.List;

/**
 * 项目核心表（基础+状态） Service 接口
 *
 * @author Swolf
 */
public interface ProjectInfoService {

    /**
     * 创建项目核心表（基础+状态）
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createInfo(@Valid ProjectInfoSaveReqVO createReqVO);

    /**
     * 更新项目核心表（基础+状态）
     *
     * @param updateReqVO 更新信息
     */
    void updateInfo(@Valid ProjectInfoUpdateReqVO updateReqVO);

    /**
     * 提交审核
     *
     * @param submitReqVO 提交审核信息
     */
    void submitAudit(@Valid SubmitAuditReqVO submitReqVO);

    /**
     * 删除项目核心表（基础+状态）
     *
     * @param id 编号
     */
    void deleteInfo(Long id);

    /**
     * 获得项目核心表（基础+状态）
     *
     * @param id 编号
     * @return 项目核心表（基础+状态）
     */
    ProjectInfoDO getInfo(Long id);

    /**
     * 获得项目核心表（基础+状态）分页
     *
     * @param pageReqVO 分页查询
     * @return 项目核心表（基础+状态）分页
     */
    PageResult<ProjectInfoDO> getInfoPage(ProjectInfoPageReqVO pageReqVO);

    /**
     * 获取项目简易列表
     *
     * @author: Hus
     * @date: 2026/1/4 16:48
     * @return: PageResult<ProjectInfoDO>
     * @description
     */
    List<ProjectInfoDO> getInfoList();

    /**
     * 审核项目
     *
     * @param auditReqVO    审核信息
     * @param auditUserId   审核人ID
     * @param auditUserName 审核人名称
     */
    ProjectInfoDO auditProject(@Valid ProjectInfoAuditReqVO auditReqVO, Long auditUserId, String auditUserName);

    void sendProjectAuditPassMessage(Long projectId);

    /**
     * 修改项目上下架状态
     *
     * @param sellStatusReqVO 上下架信息
     */
    void updateSellStatus(@Valid ProjectInfoSellStatusReqVO sellStatusReqVO);

    /**
     * 更新项目配置
     *
     * @param configReqVO 配置信息
     */
    void updateConfig(@Valid ProjectConfigReqVO configReqVO);

    /**
     * 获取项目配置
     *
     * @param projectId 项目ID
     * @return 项目配置
     */
    ProjectConfigRespVO getConfig(Long projectId);

    /**
     * 提交运行审核
     *
     * @param submitReqVO 提交信息
     */
    void submitRunAudit(@Valid SubmitAuditReqVO submitReqVO);

    /**
     * 审核运行
     *
     * @param auditReqVO    审核信息
     * @param auditUserId   审核人ID
     * @param auditUserName 审核人名称
     * @return 更新后的项目信息
     */
    ProjectInfoDO auditRun(@Valid ProjectRunAuditReqVO auditReqVO, Long auditUserId, String auditUserName);

    /**
     * 获取项目矿池配置 (包含解密后的私钥)
     *
     * @param projectId 项目ID
     * @return 矿池配置
     */
    ProjectPoolConfigDTO getPoolConfig(Long projectId);

    /**
     * 结束运行
     *
     * @param projectId 项目ID
     */
    void endRun(Long projectId);

}