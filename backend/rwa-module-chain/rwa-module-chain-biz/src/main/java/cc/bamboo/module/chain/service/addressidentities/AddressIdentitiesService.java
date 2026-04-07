package cc.bamboo.module.chain.service.addressidentities;

import java.util.*;
import javax.validation.*;
import cc.bamboo.module.chain.controller.admin.addressidentities.vo.*;
import cc.bamboo.module.chain.dal.dataobject.addressidentities.AddressIdentitiesDO;
import cc.bamboo.framework.common.pojo.PageResult;
import cc.bamboo.framework.common.pojo.PageParam;

/**
 * 地址身份关联 Service 接口
 *
 * @author Swolf
 */
public interface AddressIdentitiesService {

    /**
     * 创建地址身份关联
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createAddressIdentities(@Valid AddressIdentitiesSaveReqVO createReqVO);

    /**
     * 更新地址身份关联
     *
     * @param updateReqVO 更新信息
     */
    void updateAddressIdentities(@Valid AddressIdentitiesSaveReqVO updateReqVO);

    /**
     * 删除地址身份关联
     *
     * @param id 编号
     */
    void deleteAddressIdentities(Long id);

    /**
     * 获得地址身份关联
     *
     * @param id 编号
     * @return 地址身份关联
     */
    AddressIdentitiesDO getAddressIdentities(Long id);

    /**
     * 获得地址身份关联分页
     *
     * @param pageReqVO 分页查询
     * @return 地址身份关联分页
     */
    PageResult<AddressIdentitiesDO> getAddressIdentitiesPage(AddressIdentitiesPageReqVO pageReqVO);

}