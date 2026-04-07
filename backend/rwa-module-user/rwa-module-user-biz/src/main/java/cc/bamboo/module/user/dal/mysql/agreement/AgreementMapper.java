package cc.bamboo.module.user.dal.mysql.agreement;

import java.util.*;

import cc.bamboo.framework.common.pojo.PageResult;
import cc.bamboo.framework.mybatis.core.query.LambdaQueryWrapperX;
import cc.bamboo.framework.mybatis.core.mapper.BaseMapperX;
import cc.bamboo.module.user.dal.dataobject.agreement.AgreementDO;
import org.apache.ibatis.annotations.Mapper;
import cc.bamboo.module.user.controller.admin.agreement.vo.*;

/**
 * 系统协议表 Mapper
 *
 * @author Swolf
 */
@Mapper
public interface AgreementMapper extends BaseMapperX<AgreementDO> {

    default PageResult<AgreementDO> selectPage(AgreementPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<AgreementDO>()
                .eqIfPresent(AgreementDO::getAgreementType, reqVO.getAgreementType())
                .eqIfPresent(AgreementDO::getAgreementTitle, reqVO.getAgreementTitle())
                .eqIfPresent(AgreementDO::getAgreementContent, reqVO.getAgreementContent())
                .eqIfPresent(AgreementDO::getVersion, reqVO.getVersion())
                .eqIfPresent(AgreementDO::getIsCurrent, reqVO.getIsCurrent())
                .betweenIfPresent(AgreementDO::getEffectiveTime, reqVO.getEffectiveTime())
                .betweenIfPresent(AgreementDO::getExpireTime, reqVO.getExpireTime())
                .betweenIfPresent(AgreementDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(AgreementDO::getId));
    }

}