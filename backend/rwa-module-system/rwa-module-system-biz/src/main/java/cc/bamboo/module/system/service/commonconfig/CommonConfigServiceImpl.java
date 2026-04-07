package cc.bamboo.module.system.service.commonconfig;

import cc.bamboo.framework.redis.service.RedisService;
import cc.bamboo.module.system.controller.app.dict.vo.AppCommonConfigRespVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;

import cc.bamboo.module.system.controller.admin.commonconfig.vo.*;
import cc.bamboo.module.system.dal.dataobject.commonconfig.CommonConfigDO;
import cc.bamboo.framework.common.pojo.PageResult;
import cc.bamboo.framework.common.pojo.PageParam;
import cc.bamboo.framework.common.util.object.BeanUtils;

import cc.bamboo.module.system.dal.mysql.commonconfig.CommonConfigMapper;

import static cc.bamboo.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cc.bamboo.module.system.enums.ApiConstants.*;
import static cc.bamboo.module.system.enums.ErrorCodeConstants.*;

/**
 * 参数配置 Service 实现类
 *
 * @author swolf
 */
@Service
@Validated
public class CommonConfigServiceImpl implements CommonConfigService {

    @Resource
    private CommonConfigMapper commonConfigMapper;

    @Resource
    private RedisService redisService;

    @Override
    public Long createCommonConfig(CommonConfigSaveReqVO createReqVO) {
        // 插入
        CommonConfigDO commonConfig = BeanUtils.toBean(createReqVO, CommonConfigDO.class);
        commonConfigMapper.insert(commonConfig);
        redisService.deleteObject(COMMON_CONFIG_LIST);
        // 返回
        return commonConfig.getId();
    }

    @Override
    public void updateCommonConfig(CommonConfigSaveReqVO updateReqVO) {
        // 校验存在
        validateCommonConfigExists(updateReqVO.getId());
        // 更新
        CommonConfigDO updateObj = BeanUtils.toBean(updateReqVO, CommonConfigDO.class);
        commonConfigMapper.updateById(updateObj);
        redisService.deleteObject(COMMON_CONFIG_LIST);
        redisService.deleteObject(String.format(COMMON_CONFIG_KEY, updateReqVO.getConfigKey()));
    }

    @Override
    public void deleteCommonConfig(Long id) {
        // 校验存在
        validateCommonConfigExists(id);
        // 删除
        commonConfigMapper.deleteById(id);
        redisService.deleteObject(COMMON_CONFIG_LIST);
    }

    private void validateCommonConfigExists(Long id) {
        if (commonConfigMapper.selectById(id) == null) {
            throw exception(COMMON_CONFIG_NOT_EXISTS);
        }
    }

    @Override
    public CommonConfigDO getCommonConfig(Long id) {
        return commonConfigMapper.selectById(id);
    }

    @Override
    public PageResult<CommonConfigDO> getCommonConfigPage(CommonConfigPageReqVO pageReqVO) {
        return commonConfigMapper.selectPage(pageReqVO);
    }

    @Override
    public List<AppCommonConfigRespVO> getCommonConfigList() {
        // 从 Redis 中获取参数配置列表
        List<AppCommonConfigRespVO> configList = redisService.getCacheObject(COMMON_CONFIG_LIST);
        if (configList != null) {
            return configList;
        }
        LambdaQueryWrapper<CommonConfigDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CommonConfigDO::getIsApp, 1);
        // 从数据库中查询参数配置列表
        List<CommonConfigDO> configDOList = commonConfigMapper.selectList(queryWrapper);
        // 转换为 Response VO 列表
        configList = BeanUtils.toBean(configDOList, AppCommonConfigRespVO.class);

        // 缓存参数配置列表到 Redis
        redisService.setCacheObject(COMMON_CONFIG_LIST, configList,24L, TimeUnit.HOURS);
        return configList;
    }

    @Override
    public AppCommonConfigRespVO getCommonConfigByKey(String key) {
        // 从 Redis 中获取参数配置
        AppCommonConfigRespVO config = redisService.getCacheObject(String.format(COMMON_CONFIG_KEY, key));
        if (config != null) {
            return config;
        }
        // 从数据库中查询参数配置
        CommonConfigDO configDO = commonConfigMapper.selectOne(new LambdaQueryWrapper<CommonConfigDO>()
                .eq(CommonConfigDO::getConfigKey, key)
                .eq(CommonConfigDO::getIsApp, 1));
        if (configDO == null) {
            throw exception(COMMON_CONFIG_NOT_EXISTS);
        }
        // 转换为 Response VO
        config = BeanUtils.toBean(configDO, AppCommonConfigRespVO.class);
        // 缓存参数配置到 Redis
        redisService.setCacheObject(String.format(COMMON_CONFIG_KEY, key), config,24L, TimeUnit.HOURS);
        return config;
    }

}