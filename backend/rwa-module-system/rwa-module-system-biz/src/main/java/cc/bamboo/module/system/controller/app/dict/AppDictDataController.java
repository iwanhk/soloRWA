package cc.bamboo.module.system.controller.app.dict;

import cc.bamboo.framework.common.enums.CommonStatusEnum;
import cc.bamboo.framework.common.pojo.CommonResult;
import cc.bamboo.framework.common.util.object.BeanUtils;
import cc.bamboo.module.system.controller.app.dict.vo.AppDictDataRespVO;
import cc.bamboo.module.system.controller.app.dict.vo.AppDictTypeWithDataRespVO;
import cc.bamboo.module.system.controller.app.dict.vo.AppCommonConfigRespVO;
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
import java.util.ArrayList;
import java.util.List;

import static cc.bamboo.framework.common.pojo.CommonResult.success;

@Tag(name = "用户 App - 字典数据")
@RestController
@RequestMapping("/system/dict-data")
@Validated
public class AppDictDataController {

    /**
     * 查询字典类型的最小id阈值
     */
    private static final Long DICT_TYPE_MIN_ID = 630L;

    @Resource
    private DictDataService dictDataService;

    @Resource
    private DictTypeService dictTypeService;

    @Resource
    private CommonConfigService  commonConfigService;

    @GetMapping("/type")
    @Operation(summary = "根据字典类型查询字典数据信息")
    @Parameter(name = "type", description = "字典类型", required = true, example = "common_status")
    @PermitAll
    public CommonResult<List<AppDictDataRespVO>> getDictDataListByType(@RequestParam("type") String type) {
        List<DictDataDO> list = dictDataService.getDictDataList(
                CommonStatusEnum.ENABLE.getStatus(), type);
        return success(BeanUtils.toBean(list, AppDictDataRespVO.class));
    }

    @GetMapping("/list-with-type")
    @Operation(summary = "查询字典类型及数据列表", description = "查询id>=630的字典类型及其关联的字典数据")
    @PermitAll
    @Cacheable(cacheNames = RedisKeyConstants.DICT_TYPE_WITH_DATA_LIST + "#10m", unless = "#result == null")
    public CommonResult<List<AppDictTypeWithDataRespVO>> getDictTypeWithDataList() {
        // 1. 查询 id >= 630 的字典类型列表
        List<DictTypeDO> dictTypeList = dictTypeService.getDictTypeListByMinId(DICT_TYPE_MIN_ID);

        // 2. 构建结果列表
        List<AppDictTypeWithDataRespVO> result = new ArrayList<>();
        for (DictTypeDO dictType : dictTypeList) {
            // 3. 查询每个字典类型对应的字典数据
            List<DictDataDO> dataList = dictDataService.getDictDataList(
                    CommonStatusEnum.ENABLE.getStatus(), dictType.getType());

            // 4. 构建字典类型及数据的响应VO
            AppDictTypeWithDataRespVO respVO = new AppDictTypeWithDataRespVO();
            respVO.setName(dictType.getName());
            respVO.setType(dictType.getType());

            // 5. 转换字典数据列表
            List<AppDictTypeWithDataRespVO.DictDataItem> dataItems = new ArrayList<>();
            for (DictDataDO data : dataList) {
                dataItems.add(new AppDictTypeWithDataRespVO.DictDataItem(
                        data.getLabel(),
                        data.getValue(),
                        data.getSort()));
            }
            respVO.setDataList(dataItems);
            result.add(respVO);
        }

        return success(result);
    }

    @GetMapping("/list-common-config")
    @Operation(summary = "查询公共配置列表")
    @PermitAll
    public CommonResult<List<AppCommonConfigRespVO>> getCommonConfigList() {
        return success(commonConfigService.getCommonConfigList());
    }

    @GetMapping("/common-config")
    @Operation(summary = "根据键查询公共配置")
    @Parameter(name = "key", description = "公共配置键", required = true, example = "common_status")
    @PermitAll
    public CommonResult<AppCommonConfigRespVO> getCommonConfigByKey(@RequestParam("key") String key) {
        return success(commonConfigService.getCommonConfigByKey(key));
    }
}
