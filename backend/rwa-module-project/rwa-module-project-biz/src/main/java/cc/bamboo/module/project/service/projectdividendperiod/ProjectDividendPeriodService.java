package cc.bamboo.module.project.service.projectdividendperiod;

import java.util.*;
import javax.validation.*;
import cc.bamboo.module.project.controller.admin.projectdividendperiod.vo.*;
import cc.bamboo.module.project.dal.dataobject.projectdividendperiod.ProjectDividendPeriodDO;
import cc.bamboo.framework.common.pojo.PageResult;
import cc.bamboo.framework.common.pojo.PageParam;

/**
 * 分红周期 Service 接口
 *
 * @author Swolf
 */
public interface ProjectDividendPeriodService {

    /**
     * 创建分红周期
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createDividendPeriod(@Valid ProjectDividendPeriodSaveReqVO createReqVO);

    /**
     * 更新分红周期
     *
     * @param updateReqVO 更新信息
     */
    void updateDividendPeriod(@Valid ProjectDividendPeriodSaveReqVO updateReqVO);

    /**
     * 删除分红周期
     *
     * @param id 编号
     */
    void deleteDividendPeriod(Long id);

    /**
     * 获得分红周期
     *
     * @param id 编号
     * @return 分红周期
     */
    ProjectDividendPeriodDO getDividendPeriod(Long id);

    /**
     * 获得分红周期分页
     *
     * @param pageReqVO 分页查询
     * @return 分红周期分页
     */
    PageResult<ProjectDividendPeriodDO> getDividendPeriodPage(ProjectDividendPeriodPageReqVO pageReqVO);

}