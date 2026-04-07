package cc.bamboo.module.chain.service.claimissueridentities;

import java.util.*;
import javax.validation.*;
import cc.bamboo.module.chain.controller.admin.claimissueridentities.vo.*;
import cc.bamboo.module.chain.dal.dataobject.claimissueridentities.ClaimIssuerIdentitiesDO;
import cc.bamboo.framework.common.pojo.PageResult;
import cc.bamboo.framework.common.pojo.PageParam;

/**
 * 声明发行者身份 Service 接口
 *
 * @author Swolf
 */
public interface ClaimIssuerIdentitiesService {

    /**
     * 创建声明发行者身份
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createClaimIssuerIdentities(@Valid ClaimIssuerIdentitiesSaveReqVO createReqVO);

    /**
     * 更新声明发行者身份
     *
     * @param updateReqVO 更新信息
     */
    void updateClaimIssuerIdentities(@Valid ClaimIssuerIdentitiesSaveReqVO updateReqVO);

    /**
     * 删除声明发行者身份
     *
     * @param id 编号
     */
    void deleteClaimIssuerIdentities(Long id);

    /**
     * 获得声明发行者身份
     *
     * @param id 编号
     * @return 声明发行者身份
     */
    ClaimIssuerIdentitiesDO getClaimIssuerIdentities(Long id);

    /**
     * 获得声明发行者身份分页
     *
     * @param pageReqVO 分页查询
     * @return 声明发行者身份分页
     */
    PageResult<ClaimIssuerIdentitiesDO> getClaimIssuerIdentitiesPage(ClaimIssuerIdentitiesPageReqVO pageReqVO);

}