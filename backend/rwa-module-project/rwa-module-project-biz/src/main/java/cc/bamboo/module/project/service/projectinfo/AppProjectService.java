package cc.bamboo.module.project.service.projectinfo;

import cc.bamboo.framework.common.pojo.PageResult;
import cc.bamboo.module.project.controller.app.projectinfo.vo.AppProjectDetailRespVO;
import cc.bamboo.module.project.controller.app.projectinfo.vo.AppProjectListReqVO;
import cc.bamboo.module.project.controller.app.projectinfo.vo.AppProjectListRespVO;
import cc.bamboo.module.project.controller.app.projectinfo.vo.AssetTypeCountRespVO;
import cc.bamboo.module.project.dal.dataobject.projectinfo.ProjectInfoDO;

import java.time.LocalDate;
import java.util.List;

/**
 * 用户端 - 项目信息 Service 接口
 *
 * @author Swolf
 */
public interface AppProjectService {

    /**
     * 获取项目详情
     *
     * @param id 项目ID
     * @return 项目详情
     */
    ProjectInfoDO getProjectDetail(Long id);

    AppProjectDetailRespVO getAppProjectDetail(Long id);

    /**
     * 获取项目文件URL列表
     *
     * @param id
     * @author: Hus
     * @date: 2026/2/2 19:12
     * @return: String
     * @description
     */
    String getProjectFileUrls(Long id);

    /**
     * 查询正在出售的项目列表
     *
     * @return 项目列表
     */
    PageResult<AppProjectListRespVO> getProjectList(AppProjectListReqVO reqVO);

    /**
     * 获取项目最早的分红日期
     *
     * @param projectId 项目ID
     * @return 最早分红日期，如果没有配置则返回null
     */
    LocalDate getFirstDividendDate(Long projectId);

    /**
     * 获取项目资产类型统计
     *
     * @author: Hus
     * @date: 2026/2/3 10:22
     * @return: List<AssetTypeCountRespVO>
     * @description
     */
    List<AssetTypeCountRespVO> countProjectByAssetType();

}
