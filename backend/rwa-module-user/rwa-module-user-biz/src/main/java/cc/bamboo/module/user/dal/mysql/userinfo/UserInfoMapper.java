package cc.bamboo.module.user.dal.mysql.userinfo;

import java.util.*;

import cc.bamboo.framework.common.pojo.PageResult;
import cc.bamboo.framework.mybatis.core.query.LambdaQueryWrapperX;
import cc.bamboo.framework.mybatis.core.mapper.BaseMapperX;
import cc.bamboo.module.project.enums.AuditStatusEnum;
import cc.bamboo.module.user.dal.dataobject.userinfo.UserInfoDO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.ibatis.annotations.Mapper;
import cc.bamboo.module.user.controller.admin.userinfo.vo.*;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 用户基础信息 Mapper
 *
 * @author Swolf
 */
@Mapper
public interface UserInfoMapper extends BaseMapperX<UserInfoDO> {

    default PageResult<UserInfoDO> selectPage(UserInfoPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<UserInfoDO>()
                .eqIfPresent(UserInfoDO::getMobile, reqVO.getMobile())
                .eqIfPresent(UserInfoDO::getPassword, reqVO.getPassword())
                .likeIfPresent(UserInfoDO::getRealName, reqVO.getRealName())
                .eqIfPresent(UserInfoDO::getIdCard, reqVO.getIdCard())
                .eqIfPresent(UserInfoDO::getIdCardExpire, reqVO.getIdCardExpire())
                .eqIfPresent(UserInfoDO::getEmail, reqVO.getEmail())
                .eqIfPresent(UserInfoDO::getPhone, reqVO.getPhone())
                .eqIfPresent(UserInfoDO::getAuditStatus, reqVO.getAuditStatus())
                .eqIfPresent(UserInfoDO::getStatus, reqVO.getStatus())
                .eqIfPresent(UserInfoDO::getDefaultCurrency, reqVO.getDefaultCurrency())
                .eqIfPresent(UserInfoDO::getTwoFactorAuthStatus, reqVO.getTwoFactorAuthStatus())
                .eqIfPresent(UserInfoDO::getTwoFactorAuthSecret, reqVO.getTwoFactorAuthSecret())
                .betweenIfPresent(UserInfoDO::getTwoFactorAuthBindTime, reqVO.getTwoFactorAuthBindTime())
                .betweenIfPresent(UserInfoDO::getTwoFactorAuthLastVerifyTime, reqVO.getTwoFactorAuthLastVerifyTime())
                .betweenIfPresent(UserInfoDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(UserInfoDO::getId));
    }

    @Select("select count(*) from biz_project_order where user_id = #{userId} and order_status <= 2")
    int getUserOrderCount(@Param("userId") Long userId);

    /**
     * 统计待审核的用户数量
     * 
     * @return 待审核用户数
     */
    default Long countPendingAudit(){
        LambdaQueryWrapper<UserInfoDO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserInfoDO::getAuditStatus, AuditStatusEnum.PENDING.getStatus());
        return selectCount(lambdaQueryWrapper);
    }

}