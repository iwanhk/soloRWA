package cc.bamboo.module.chain.service.identityregistrystorages;

import java.util.*;
import javax.validation.*;
import cc.bamboo.module.chain.controller.admin.identityregistrystorages.vo.*;
import cc.bamboo.module.chain.dal.dataobject.identityregistrystorages.IdentityRegistryStoragesDO;
import cc.bamboo.framework.common.pojo.PageResult;
import cc.bamboo.framework.common.pojo.PageParam;

/**
 * 身份注册表存储 Service 接口
 *
 * @author Swolf
 */
public interface IdentityRegistryStoragesService {

    /**
     * 创建身份注册表存储
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createIdentityRegistryStorages(@Valid IdentityRegistryStoragesSaveReqVO createReqVO);

    /**
     * 更新身份注册表存储
     *
     * @param updateReqVO 更新信息
     */
    void updateIdentityRegistryStorages(@Valid IdentityRegistryStoragesSaveReqVO updateReqVO);

    /**
     * 删除身份注册表存储
     *
     * @param id 编号
     */
    void deleteIdentityRegistryStorages(Long id);

    /**
     * 获得身份注册表存储
     *
     * @param id 编号
     * @return 身份注册表存储
     */
    IdentityRegistryStoragesDO getIdentityRegistryStorages(Long id);

    /**
     * 获得身份注册表存储分页
     *
     * @param pageReqVO 分页查询
     * @return 身份注册表存储分页
     */
    PageResult<IdentityRegistryStoragesDO> getIdentityRegistryStoragesPage(IdentityRegistryStoragesPageReqVO pageReqVO);

}