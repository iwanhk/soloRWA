package cc.bamboo.module.chain.service.claimissueridentities;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import cc.bamboo.module.chain.controller.admin.claimissueridentities.vo.*;
import cc.bamboo.module.chain.dal.dataobject.claimissueridentities.ClaimIssuerIdentitiesDO;
import cc.bamboo.framework.common.pojo.PageResult;
import cc.bamboo.framework.common.pojo.PageParam;
import cc.bamboo.framework.common.util.object.BeanUtils;

import cc.bamboo.module.chain.dal.mysql.claimissueridentities.ClaimIssuerIdentitiesMapper;

import static cc.bamboo.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cc.bamboo.module.chain.enums.ErrorCodeConstants.*;

/**
 * 声明发行者身份 Service 实现类
 *
 * @author Swolf
 */
@Service
@Validated
public class ClaimIssuerIdentitiesServiceImpl implements ClaimIssuerIdentitiesService {

    @Resource
    private ClaimIssuerIdentitiesMapper claimIssuerIdentitiesMapper;

    @Override
    public Long createClaimIssuerIdentities(ClaimIssuerIdentitiesSaveReqVO createReqVO) {
        // 插入
        ClaimIssuerIdentitiesDO claimIssuerIdentities = BeanUtils.toBean(createReqVO, ClaimIssuerIdentitiesDO.class);
        claimIssuerIdentitiesMapper.insert(claimIssuerIdentities);
        // 返回
        return claimIssuerIdentities.getId();
    }

    @Override
    public void updateClaimIssuerIdentities(ClaimIssuerIdentitiesSaveReqVO updateReqVO) {
        // 校验存在
        validateClaimIssuerIdentitiesExists(updateReqVO.getId());
        // 更新
        ClaimIssuerIdentitiesDO updateObj = BeanUtils.toBean(updateReqVO, ClaimIssuerIdentitiesDO.class);
        claimIssuerIdentitiesMapper.updateById(updateObj);
    }

    @Override
    public void deleteClaimIssuerIdentities(Long id) {
        // 校验存在
        validateClaimIssuerIdentitiesExists(id);
        // 删除
        claimIssuerIdentitiesMapper.deleteById(id);
    }

    private void validateClaimIssuerIdentitiesExists(Long id) {
        if (claimIssuerIdentitiesMapper.selectById(id) == null) {
            throw exception(CLAIM_ISSUER_IDENTITIES_NOT_EXISTS);
        }
    }

    @Override
    public ClaimIssuerIdentitiesDO getClaimIssuerIdentities(Long id) {
        return claimIssuerIdentitiesMapper.selectById(id);
    }

    @Override
    public PageResult<ClaimIssuerIdentitiesDO> getClaimIssuerIdentitiesPage(ClaimIssuerIdentitiesPageReqVO pageReqVO) {
        return claimIssuerIdentitiesMapper.selectPage(pageReqVO);
    }

}