package cc.bamboo.module.system.service.commonconfig;

import java.util.*;
import javax.validation.*;
import cc.bamboo.module.system.controller.admin.commonconfig.vo.*;
import cc.bamboo.module.system.controller.app.dict.vo.AppCommonConfigRespVO;
import cc.bamboo.module.system.dal.dataobject.commonconfig.CommonConfigDO;
import cc.bamboo.framework.common.pojo.PageResult;
import cc.bamboo.framework.common.pojo.PageParam;

/**
 * 参数配置 Service 接口
 *
 * @author swolf
 */
public interface CommonConfigService {

    /**
     * 创建参数配置
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createCommonConfig(@Valid CommonConfigSaveReqVO createReqVO);

    /**
     * 更新参数配置
     *
     * @param updateReqVO 更新信息
     */
    void updateCommonConfig(@Valid CommonConfigSaveReqVO updateReqVO);

    /**
     * 删除参数配置
     *
     * @param id 编号
     */
    void deleteCommonConfig(Long id);

    /**
     * 获得参数配置
     *
     * @param id 编号
     * @return 参数配置
     */
    CommonConfigDO getCommonConfig(Long id);

    /**
     * 获得参数配置分页
     *
     * @param pageReqVO 分页查询
     * @return 参数配置分页
     */
    PageResult<CommonConfigDO> getCommonConfigPage(CommonConfigPageReqVO pageReqVO);

     /**
      * 获得参数配置列表
      *
      * @return 参数配置列表
      */
    List<AppCommonConfigRespVO>  getCommonConfigList();

     /**
      * 获得参数配置
      *
      * @param key 参数键
      * @return 参数配置
      */
     AppCommonConfigRespVO getCommonConfigByKey(String key);
}