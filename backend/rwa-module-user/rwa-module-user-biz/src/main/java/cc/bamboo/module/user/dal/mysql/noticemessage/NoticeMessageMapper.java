package cc.bamboo.module.user.dal.mysql.noticemessage;

import cc.bamboo.framework.common.pojo.PageResult;
import cc.bamboo.framework.mybatis.core.mapper.BaseMapperX;
import cc.bamboo.framework.mybatis.core.query.LambdaQueryWrapperX;
import cc.bamboo.module.user.controller.admin.noticemessage.vo.NoticeMessagePageReqVO;
import cc.bamboo.module.user.controller.app.noticemessage.vo.AppNoticeMessagePageReqVO;
import cc.bamboo.module.user.dal.dataobject.noticemessage.NoticeMessageDO;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;

/**
 * 用户消息 Mapper
 *
 * @author Kiro
 */
@Mapper
public interface NoticeMessageMapper extends BaseMapperX<NoticeMessageDO> {

    default PageResult<NoticeMessageDO> selectPage(Long userId, AppNoticeMessagePageReqVO reqVO, LocalDateTime userRegisterTime) {
        LambdaQueryWrapperX<NoticeMessageDO> query = new LambdaQueryWrapperX<>();
        
        // 查询条件：个人消息或系统消息
     /*   query.nested(wrapper -> wrapper
                .eq(NoticeMessageDO::getUserId, userId)
                .or()
                .and(w -> w
                .isNull(NoticeMessageDO::getUserId)
                .gt(NoticeMessageDO::getCreateTime, userRegisterTime) // 仅保留注册后的系统消息
        )); // 包含系统消息（userId为null）*/
        query.nested(wrapper -> wrapper
                .eq(NoticeMessageDO::getUserId, userId)
                .or()
                .isNull(NoticeMessageDO::getUserId)); // 包含系统消息（userId为null）
        
        // 添加可选条件
        query.eqIfPresent(NoticeMessageDO::getNoticeType, reqVO.getNoticeType());
        query.eqIfPresent(NoticeMessageDO::getReadStatus, reqVO.getReadStatus());

        query.orderByDesc(NoticeMessageDO::getId);

        return selectPage(reqVO, query);
    }

    default PageResult<NoticeMessageDO> selectPage(NoticeMessagePageReqVO reqVO) {
        LambdaQueryWrapperX<NoticeMessageDO> query = new LambdaQueryWrapperX<>();

        // 添加可选条件
        query.eqIfPresent(NoticeMessageDO::getNoticeType, reqVO.getNoticeType());
        query.eqIfPresent(NoticeMessageDO::getReadStatus, reqVO.getReadStatus());
        query.orderByDesc(NoticeMessageDO::getId);

        return selectPage(reqVO, query);
    }

    default Long selectUnreadCount(Long userId) {
        LambdaQueryWrapperX<NoticeMessageDO> query = new LambdaQueryWrapperX<>();
        query.nested(wrapper -> wrapper
                .eq(NoticeMessageDO::getUserId, userId)
                .or()
                .isNull(NoticeMessageDO::getUserId)); // 包含系统消息
        query.eq(NoticeMessageDO::getReadStatus, false);
        return selectCount(query);
    }

}
