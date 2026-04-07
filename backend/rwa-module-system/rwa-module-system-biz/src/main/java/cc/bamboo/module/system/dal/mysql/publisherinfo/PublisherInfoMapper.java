package cc.bamboo.module.system.dal.mysql.publisherinfo;

import java.util.*;

import cc.bamboo.framework.common.pojo.PageResult;
import cc.bamboo.framework.mybatis.core.query.LambdaQueryWrapperX;
import cc.bamboo.framework.mybatis.core.mapper.BaseMapperX;
import cc.bamboo.module.system.dal.dataobject.publisherinfo.PublisherInfoDO;
import org.apache.ibatis.annotations.Mapper;
import cc.bamboo.module.system.controller.admin.publisherinfo.vo.*;

/**
 * 发行商 Mapper
 *
 * @author Swolf
 */
@Mapper
public interface PublisherInfoMapper extends BaseMapperX<PublisherInfoDO> {

    default PageResult<PublisherInfoDO> selectPage(PublisherInfoPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<PublisherInfoDO>()
                .eqIfPresent(PublisherInfoDO::getUserPhone, reqVO.getUserPhone())
                .betweenIfPresent(PublisherInfoDO::getRegisterTime, reqVO.getRegisterTime())
                .eqIfPresent(PublisherInfoDO::getIdentityAuthStatus, reqVO.getIdentityAuthStatus())
                .eqIfPresent(PublisherInfoDO::getAuthIdentity, reqVO.getAuthIdentity())
                .likeIfPresent(PublisherInfoDO::getCompanyName, reqVO.getCompanyName())
                .eqIfPresent(PublisherInfoDO::getCompanyCreditCode, reqVO.getCompanyCreditCode())
                .eqIfPresent(PublisherInfoDO::getBusinessLicenseUrl, reqVO.getBusinessLicenseUrl())
                .eqIfPresent(PublisherInfoDO::getQualificationFileUrls, reqVO.getQualificationFileUrls())
                .eqIfPresent(PublisherInfoDO::getAuthorizationFileUrls, reqVO.getAuthorizationFileUrls())
                .likeIfPresent(PublisherInfoDO::getIdCardName, reqVO.getIdCardName())
                .eqIfPresent(PublisherInfoDO::getIdCardNo, reqVO.getIdCardNo())
                .betweenIfPresent(PublisherInfoDO::getIdCardExpireTime, reqVO.getIdCardExpireTime())
                .eqIfPresent(PublisherInfoDO::getIdCardFrontUrl, reqVO.getIdCardFrontUrl())
                .eqIfPresent(PublisherInfoDO::getIdCardBackUrl, reqVO.getIdCardBackUrl())
                .eqIfPresent(PublisherInfoDO::getEmail, reqVO.getEmail())
                .likeIfPresent(PublisherInfoDO::getBankAccountName, reqVO.getBankAccountName())
                .eqIfPresent(PublisherInfoDO::getBankAccount, reqVO.getBankAccount())
                .likeIfPresent(PublisherInfoDO::getBankName, reqVO.getBankName())
                .betweenIfPresent(PublisherInfoDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(PublisherInfoDO::getId));
    }

}