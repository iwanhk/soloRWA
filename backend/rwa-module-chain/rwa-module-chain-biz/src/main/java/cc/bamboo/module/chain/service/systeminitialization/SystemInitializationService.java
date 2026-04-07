package cc.bamboo.module.chain.service.systeminitialization;

import java.util.*;
import javax.validation.*;
import cc.bamboo.module.chain.controller.admin.systeminitialization.vo.*;
import cc.bamboo.module.chain.dal.dataobject.systeminitialization.SystemInitializationDO;
import cc.bamboo.framework.common.pojo.PageResult;
import cc.bamboo.framework.common.pojo.PageParam;

/**
 * 系统初始化 Service 接口
 *
 * @author Swolf
 */
public interface SystemInitializationService {

    /**
     * 创建系统初始化
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createSystemInitialization(@Valid SystemInitializationSaveReqVO createReqVO);

    /**
     * 更新系统初始化
     *
     * @param updateReqVO 更新信息
     */
    void updateSystemInitialization(@Valid SystemInitializationSaveReqVO updateReqVO);

    /**
     * 删除系统初始化
     *
     * @param id 编号
     */
    void deleteSystemInitialization(Long id);

    /**
     * 获得系统初始化
     *
     * @param id 编号
     * @return 系统初始化
     */
    SystemInitializationDO getSystemInitialization(Long id);

    /**
     * 获得系统初始化分页
     *
     * @param pageReqVO 分页查询
     * @return 系统初始化分页
     */
    PageResult<SystemInitializationDO> getSystemInitializationPage(SystemInitializationPageReqVO pageReqVO);

}