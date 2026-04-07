package cc.bamboo.module.system.controller.app.base;

import cc.bamboo.framework.common.enums.CommonStatusEnum;
import cc.bamboo.framework.common.pojo.CommonResult;
import cc.bamboo.framework.common.util.object.BeanUtils;
import cc.bamboo.module.system.controller.app.dict.vo.AppCommonConfigRespVO;
import cc.bamboo.module.system.controller.app.dict.vo.AppDictDataRespVO;
import cc.bamboo.module.system.controller.app.dict.vo.AppDictTypeWithDataRespVO;
import cc.bamboo.module.system.dal.dataobject.dict.DictDataDO;
import cc.bamboo.module.system.dal.dataobject.dict.DictTypeDO;
import cc.bamboo.module.system.dal.redis.RedisKeyConstants;
import cc.bamboo.module.system.service.commonconfig.CommonConfigService;
import cc.bamboo.module.system.service.dict.DictDataService;
import cc.bamboo.module.system.service.dict.DictTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static cc.bamboo.framework.common.pojo.CommonResult.success;

@Tag(name = "用户 App - 基础数据")
@RestController
@RequestMapping("/system/base")
@Validated
public class AppBaseController {



    @GetMapping("/time")
    @Operation(summary = "获取系统时间")
    @PermitAll
    public CommonResult<Long> getTime() {
        return success(System.currentTimeMillis());
    }


}
