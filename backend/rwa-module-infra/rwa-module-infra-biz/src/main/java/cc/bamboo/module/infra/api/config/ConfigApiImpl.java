package cc.bamboo.module.infra.api.config;

import cc.bamboo.framework.common.pojo.CommonResult;
import cc.bamboo.module.infra.dal.dataobject.config.ConfigDO;
import cc.bamboo.module.infra.service.config.ConfigService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

import static cc.bamboo.framework.common.pojo.CommonResult.success;

@RestController // 提供 RESTful API 接口，给 Feign 调用
@Validated
public class ConfigApiImpl implements ConfigApi {

    @Resource
    private ConfigService configService;

    @Override
    public CommonResult<String> getConfigValueByKey(String key) {
        ConfigDO config = configService.getConfigByKey(key);
        return success(config != null ? config.getValue() : null);
    }

}
