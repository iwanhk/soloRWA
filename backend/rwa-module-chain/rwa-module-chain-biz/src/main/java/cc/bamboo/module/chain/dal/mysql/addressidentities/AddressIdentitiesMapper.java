package cc.bamboo.module.chain.dal.mysql.addressidentities;

import java.util.*;

import cc.bamboo.framework.common.pojo.PageResult;
import cc.bamboo.framework.mybatis.core.query.LambdaQueryWrapperX;
import cc.bamboo.framework.mybatis.core.mapper.BaseMapperX;
import cc.bamboo.module.chain.dal.dataobject.addressidentities.AddressIdentitiesDO;
import org.apache.ibatis.annotations.Mapper;
import cc.bamboo.module.chain.controller.admin.addressidentities.vo.*;

/**
 * 地址身份关联 Mapper
 *
 * @author Swolf
 */
@Mapper
public interface AddressIdentitiesMapper extends BaseMapperX<AddressIdentitiesDO> {

    default PageResult<AddressIdentitiesDO> selectPage(AddressIdentitiesPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<AddressIdentitiesDO>()
                .eqIfPresent(AddressIdentitiesDO::getAddress, reqVO.getAddress())
                .eqIfPresent(AddressIdentitiesDO::getIdentityId, reqVO.getIdentityId())
                .eqIfPresent(AddressIdentitiesDO::getType, reqVO.getType())
                .eqIfPresent(AddressIdentitiesDO::getContractAddress, reqVO.getContractAddress())
                .betweenIfPresent(AddressIdentitiesDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(AddressIdentitiesDO::getId));
    }

}