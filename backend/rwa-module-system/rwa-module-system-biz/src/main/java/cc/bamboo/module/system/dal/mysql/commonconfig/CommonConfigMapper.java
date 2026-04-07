package cc.bamboo.module.system.dal.mysql.commonconfig;

import java.util.*;

import cc.bamboo.framework.common.pojo.PageResult;
import cc.bamboo.framework.mybatis.core.query.LambdaQueryWrapperX;
import cc.bamboo.framework.mybatis.core.mapper.BaseMapperX;
import cc.bamboo.module.system.dal.dataobject.commonconfig.CommonConfigDO;
import org.apache.ibatis.annotations.Mapper;
import cc.bamboo.module.system.controller.admin.commonconfig.vo.*;

/**
 * 参数配置 Mapper
 *
 * @author swolf
 */
@Mapper
public interface CommonConfigMapper extends BaseMapperX<CommonConfigDO> {

    default PageResult<CommonConfigDO> selectPage(CommonConfigPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<CommonConfigDO>()
                .likeIfPresent(CommonConfigDO::getName, reqVO.getName())
                .eqIfPresent(CommonConfigDO::getConfigKey, reqVO.getConfigKey())
                .eqIfPresent(CommonConfigDO::getValue, reqVO.getValue())
                .eqIfPresent(CommonConfigDO::getIsApp, reqVO.getIsApp())
                .eqIfPresent(CommonConfigDO::getRemark, reqVO.getRemark())
                .betweenIfPresent(CommonConfigDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(CommonConfigDO::getId));
    }

}