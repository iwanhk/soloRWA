package cc.bamboo.module.project.dal.mysql.userchain;

import cc.bamboo.framework.common.pojo.PageResult;
import cc.bamboo.framework.mybatis.core.mapper.BaseMapperX;
import cc.bamboo.framework.mybatis.core.query.LambdaQueryWrapperX;
import cc.bamboo.module.project.dal.dataobject.userchain.UserChainDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 用户链地址表= Mapper
 *
 * @author Swolf
 */
@Mapper
public interface UserChainMapper extends BaseMapperX<UserChainDO> {

    default UserChainDO selectByUserId(Long userId) {
        return selectOne(new LambdaQueryWrapperX<UserChainDO>()
                .eq(UserChainDO::getUserId, userId).last("limit 1"));
    }

}