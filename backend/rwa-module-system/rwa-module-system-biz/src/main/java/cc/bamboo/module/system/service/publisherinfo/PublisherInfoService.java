package cc.bamboo.module.system.service.publisherinfo;

import java.util.*;
import javax.validation.*;
import cc.bamboo.module.system.controller.admin.publisherinfo.vo.*;
import cc.bamboo.module.system.dal.dataobject.publisherinfo.PublisherInfoDO;
import cc.bamboo.framework.common.pojo.PageResult;
import cc.bamboo.framework.common.pojo.PageParam;

/**
 * 发行商 Service 接口
 *
 * @author Swolf
 */
public interface PublisherInfoService {

    /**
     * 创建发行商
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createPublisherInfo(@Valid PublisherInfoSaveReqVO createReqVO);

    /**
     * 更新发行商
     *
     * @param updateReqVO 更新信息
     */
    void updatePublisherInfo(@Valid PublisherInfoSaveReqVO updateReqVO);

    /**
     * 删除发行商
     *
     * @param id 编号
     */
    void deletePublisherInfo(Long id);

    /**
     * 获得发行商
     *
     * @param id 编号
     * @return 发行商
     */
    PublisherInfoDO getPublisherInfo(Long id);

    /**
     * 获得发行商分页
     *
     * @param pageReqVO 分页查询
     * @return 发行商分页
     */
    PageResult<PublisherInfoDO> getPublisherInfoPage(PublisherInfoPageReqVO pageReqVO);

    /**
     * 根据用户ID获得发行商
     *
     * @param userId 用户ID
     * @return 发行商
     */
    PublisherInfoDO getPublisherInfoByUserId(Long userId);

    /**
     * 根据租户ID获得发行商
     *
     * @param tenantId 租户ID
     * @return 发行商
     */
    PublisherInfoDO getPublisherInfoByTenantId(Long tenantId);

}