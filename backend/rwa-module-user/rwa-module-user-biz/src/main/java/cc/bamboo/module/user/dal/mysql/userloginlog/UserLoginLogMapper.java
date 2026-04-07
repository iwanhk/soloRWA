package cc.bamboo.module.user.dal.mysql.userloginlog;

import java.util.*;

import cc.bamboo.framework.common.pojo.PageResult;
import cc.bamboo.framework.mybatis.core.query.LambdaQueryWrapperX;
import cc.bamboo.framework.mybatis.core.mapper.BaseMapperX;
import cc.bamboo.module.user.dal.dataobject.userloginlog.UserLoginLogDO;
import org.apache.ibatis.annotations.Mapper;
import cc.bamboo.module.user.controller.admin.userloginlog.vo.*;

/**
 * 用户登录日志 Mapper
 *
 * @author Swolf
 */
@Mapper
public interface UserLoginLogMapper extends BaseMapperX<UserLoginLogDO> {

    default PageResult<UserLoginLogDO> selectPage(UserLoginLogPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<UserLoginLogDO>()
                .eqIfPresent(UserLoginLogDO::getUserId, reqVO.getUserId())
                .eqIfPresent(UserLoginLogDO::getLoginType, reqVO.getLoginType())
                .betweenIfPresent(UserLoginLogDO::getLoginTime, reqVO.getLoginTime())
                .eqIfPresent(UserLoginLogDO::getLoginIp, reqVO.getLoginIp())
                .eqIfPresent(UserLoginLogDO::getLoginCity, reqVO.getLoginCity())
                .eqIfPresent(UserLoginLogDO::getDeviceInfo, reqVO.getDeviceInfo())
                .eqIfPresent(UserLoginLogDO::getLoginStatus, reqVO.getLoginStatus())
                .eqIfPresent(UserLoginLogDO::getFailReason, reqVO.getFailReason())
                .betweenIfPresent(UserLoginLogDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(UserLoginLogDO::getId));
    }

}