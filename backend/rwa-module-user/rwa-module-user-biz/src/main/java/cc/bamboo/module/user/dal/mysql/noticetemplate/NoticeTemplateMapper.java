package cc.bamboo.module.user.dal.mysql.noticetemplate;

import cc.bamboo.framework.common.pojo.PageResult;
import cc.bamboo.framework.mybatis.core.mapper.BaseMapperX;
import cc.bamboo.framework.mybatis.core.query.LambdaQueryWrapperX;
import cc.bamboo.module.user.controller.admin.noticetemplate.vo.NoticeTemplatePageReqVO;
import cc.bamboo.module.user.dal.dataobject.noticetemplate.NoticeTemplateDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 消息模板 Mapper
 *
 * @author Kiro
 */
@Mapper
public interface NoticeTemplateMapper extends BaseMapperX<NoticeTemplateDO> {

    default PageResult<NoticeTemplateDO> selectPage(NoticeTemplatePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<NoticeTemplateDO>()
                .likeIfPresent(NoticeTemplateDO::getName, reqVO.getName())
                .likeIfPresent(NoticeTemplateDO::getCode, reqVO.getCode())
                .eqIfPresent(NoticeTemplateDO::getType, reqVO.getType())
                .eqIfPresent(NoticeTemplateDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(NoticeTemplateDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(NoticeTemplateDO::getId));
    }

    default NoticeTemplateDO selectByCode(String code) {
        return selectOne(NoticeTemplateDO::getCode, code);
    }

}
