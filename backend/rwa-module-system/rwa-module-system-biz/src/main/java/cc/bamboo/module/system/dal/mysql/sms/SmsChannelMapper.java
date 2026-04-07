package cc.bamboo.module.system.dal.mysql.sms;

import cc.bamboo.framework.common.pojo.PageResult;
import cc.bamboo.framework.mybatis.core.mapper.BaseMapperX;
import cc.bamboo.framework.mybatis.core.query.LambdaQueryWrapperX;
import cc.bamboo.module.system.controller.admin.sms.vo.channel.SmsChannelPageReqVO;
import cc.bamboo.module.system.dal.dataobject.sms.SmsChannelDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SmsChannelMapper extends BaseMapperX<SmsChannelDO> {

    default PageResult<SmsChannelDO> selectPage(SmsChannelPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<SmsChannelDO>()
                .likeIfPresent(SmsChannelDO::getSignature, reqVO.getSignature())
                .eqIfPresent(SmsChannelDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(SmsChannelDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(SmsChannelDO::getId));
    }

    default SmsChannelDO selectByCode(String code) {
        return selectOne(SmsChannelDO::getCode, code);
    }

}
