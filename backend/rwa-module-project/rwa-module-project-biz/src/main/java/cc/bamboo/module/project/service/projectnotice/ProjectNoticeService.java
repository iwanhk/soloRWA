package cc.bamboo.module.project.service.projectnotice;

import java.util.*;
import javax.validation.*;
import cc.bamboo.module.project.controller.admin.projectnotice.vo.*;
import cc.bamboo.module.project.controller.app.projectinfo.vo.AppProjectNoticePageReqVO;
import cc.bamboo.module.project.controller.app.projectinfo.vo.AppProjectNoticeRespVO;
import cc.bamboo.module.project.dal.dataobject.projectnotice.ProjectNoticeDO;
import cc.bamboo.framework.common.pojo.PageResult;
import cc.bamboo.framework.common.pojo.PageParam;

/**
 * 项目通告表（含全局通告） Service 接口
 *
 * @author Swolf
 */
public interface ProjectNoticeService {

    /**
     * 创建项目通告表（含全局通告）
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createNotice(@Valid ProjectNoticeSaveReqVO createReqVO);

    /**
     * 更新项目通告表（含全局通告）
     *
     * @param updateReqVO 更新信息
     */
    void updateNotice(@Valid ProjectNoticeUpdateReqVO updateReqVO);

    /**
     * 删除项目通告表（含全局通告）
     *
     * @param id 编号
     */
    void deleteNotice(Long id);

    /**
     * 获得项目通告表（含全局通告）
     *
     * @param id 编号
     * @return 项目通告表（含全局通告）
     */
    ProjectNoticeDO getNotice(Long id);

    /**
     * 获得项目通告表（含全局通告）分页
     *
     * @param pageReqVO 分页查询
     * @return 项目通告表（含全局通告）分页
     */
    PageResult<ProjectNoticeDO> getNoticePage(ProjectNoticePageReqVO pageReqVO);


    /**
     * 获得项目通告表（含全局通告）分页
     *
     * @param pageReqVO 分页查询
     * @return 项目通告表（含全局通告）分页
     */
    PageResult<AppProjectNoticeRespVO> getAppNoticePage(AppProjectNoticePageReqVO pageReqVO);
}