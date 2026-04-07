package cc.bamboo.module.user.service.agreement;

import cc.bamboo.framework.redis.service.RedisService;
import cc.bamboo.module.user.controller.app.userinfo.vo.AppAgreementRespVO;
import cc.bamboo.module.user.dal.redis.RedisKeyConstants;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;

import cc.bamboo.module.user.controller.app.userinfo.vo.AppAgreementListRespVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;

import cc.bamboo.module.user.controller.admin.agreement.vo.*;
import cc.bamboo.module.user.dal.dataobject.agreement.AgreementDO;
import cc.bamboo.framework.common.pojo.PageResult;
import cc.bamboo.framework.common.pojo.PageParam;
import cc.bamboo.framework.common.util.object.BeanUtils;

import cc.bamboo.module.user.dal.mysql.agreement.AgreementMapper;

import static cc.bamboo.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cc.bamboo.module.user.enums.ErrorCodeConstants.*;

/**
 * 系统协议表 Service 实现类
 *
 * @author Swolf
 */
@Service
@Validated
public class AgreementServiceImpl implements AgreementService {

    @Resource
    private AgreementMapper agreementMapper;

    @Resource
    private RedisService redisService;

    @Override
    public Long createAgreement(AgreementSaveReqVO createReqVO) {
        // 插入
        AgreementDO agreement = BeanUtils.toBean(createReqVO, AgreementDO.class);
        agreementMapper.insert(agreement);
        // 返回
        redisService.deleteObject(RedisKeyConstants.APP_AGREEMENT_LIST);
        return agreement.getId();
    }

    @Override
    public void updateAgreement(AgreementSaveReqVO updateReqVO) {
        // 校验存在
        validateAgreementExists(updateReqVO.getId());
        // 更新
        AgreementDO updateObj = BeanUtils.toBean(updateReqVO, AgreementDO.class);
        agreementMapper.updateById(updateObj);

        redisService.deleteObject(RedisKeyConstants.APP_AGREEMENT_LIST);
        redisService.deleteObject(RedisKeyConstants.APP_AGREEMENT_DETAIL + updateReqVO.getAgreementKey());
    }

    @Override
    public void deleteAgreement(Long id) {
        // 校验存在
        validateAgreementExists(id);
        // 删除
        agreementMapper.deleteById(id);

        redisService.deleteObject(RedisKeyConstants.APP_AGREEMENT_LIST);
        redisService.deleteObject(RedisKeyConstants.APP_AGREEMENT_DETAIL + id);

    }

    private void validateAgreementExists(Long id) {
        if (agreementMapper.selectById(id) == null) {
            throw exception(AGREEMENT_NOT_EXISTS);
        }
    }

    @Override
    public AgreementDO getAgreement(Long id) {
        return agreementMapper.selectById(id);
    }

    @Override
    public PageResult<AgreementDO> getAgreementPage(AgreementPageReqVO pageReqVO) {
        return agreementMapper.selectPage(pageReqVO);
    }

    @Override
    public List<AgreementDO> getAgreementSimple() {

        LambdaQueryWrapper<AgreementDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AgreementDO::getIsCurrent, Boolean.TRUE);
        queryWrapper.select(AgreementDO::getId, AgreementDO::getAgreementTitle, AgreementDO::getAgreementContent);
        return agreementMapper.selectList(queryWrapper);
    }

    @Override
    public List<AppAgreementListRespVO> getAgreementList() {
        //先从缓存查询
        List<AppAgreementListRespVO> agreementList = redisService.getCacheObject(RedisKeyConstants.APP_AGREEMENT_LIST);
        if (agreementList != null) {
            return agreementList;
        }
        LambdaQueryWrapper<AgreementDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AgreementDO::getIsCurrent, Boolean.TRUE);
        queryWrapper.select(AgreementDO::getId, AgreementDO::getAgreementTitle, AgreementDO::getAgreementContent, AgreementDO::getAgreementKey);
        List<AgreementDO> agreementDOS = agreementMapper.selectList(queryWrapper);
        agreementList = BeanUtils.toBean(agreementDOS, AppAgreementListRespVO.class);
        // 缓存
        redisService.setCacheObject(RedisKeyConstants.APP_AGREEMENT_LIST, agreementList, 24L, TimeUnit.HOURS);
        return agreementList;
    }

    @Override
    public AppAgreementRespVO getAgreementDetail(Long id) {
        AgreementDO agreementDO = agreementMapper.selectById(id);
        return BeanUtils.toBean(agreementDO, AppAgreementRespVO.class);
    }

    @Override
    public AppAgreementRespVO getAgreementDetailByKey(String agreementKey) {
        //redis缓存
        AppAgreementRespVO agreementRespVO = redisService.getCacheObject(RedisKeyConstants.APP_AGREEMENT_DETAIL + agreementKey);
        if (agreementRespVO != null) {
            return agreementRespVO;
        }
        LambdaQueryWrapper<AgreementDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AgreementDO::getAgreementKey, agreementKey);
        queryWrapper.eq(AgreementDO::getIsCurrent, Boolean.TRUE);
        queryWrapper.select(AgreementDO::getId, AgreementDO::getAgreementTitle, AgreementDO::getAgreementContent, AgreementDO::getAgreementKey);
        AgreementDO agreementDO = agreementMapper.selectOne(queryWrapper);
        agreementRespVO  = BeanUtils.toBean(agreementDO, AppAgreementRespVO.class);
        // 缓存
        redisService.setCacheObject(RedisKeyConstants.APP_AGREEMENT_DETAIL + agreementKey, agreementRespVO, 24L, TimeUnit.HOURS);
        return agreementRespVO;
    }

}