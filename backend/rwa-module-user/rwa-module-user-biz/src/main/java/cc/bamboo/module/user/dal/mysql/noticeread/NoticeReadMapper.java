package cc.bamboo.module.user.dal.mysql.noticeread;

import cc.bamboo.framework.common.pojo.PageResult;
import cc.bamboo.framework.mybatis.core.mapper.BaseMapperX;
import cc.bamboo.framework.mybatis.core.query.LambdaQueryWrapperX;
import cc.bamboo.module.user.controller.admin.noticeread.vo.NoticeReadPageReqVO;
import cc.bamboo.module.user.controller.admin.noticetemplate.vo.NoticeTemplatePageReqVO;
import cc.bamboo.module.user.dal.dataobject.noticeread.NoticeReadDO;
import cc.bamboo.module.user.dal.dataobject.noticetemplate.NoticeTemplateDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统消息已读记录 Mapper
 *
 * @author Kiro
 */
@Mapper
public interface NoticeReadMapper extends BaseMapperX<NoticeReadDO> {

    default NoticeReadDO selectByNoticeIdAndUserId(Long noticeId, Long userId) {
        return selectOne(new LambdaQueryWrapperX<NoticeReadDO>()
                .eq(NoticeReadDO::getNoticeId, noticeId)
                .eq(NoticeReadDO::getUserId, userId));
    }

    default PageResult<NoticeReadDO> selectPage(NoticeReadPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<NoticeReadDO>()
                .betweenIfPresent(NoticeReadDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(NoticeReadDO::getId));
    }
}
