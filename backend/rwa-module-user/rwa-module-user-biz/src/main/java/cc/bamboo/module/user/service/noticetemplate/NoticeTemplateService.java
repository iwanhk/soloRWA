package cc.bamboo.module.user.service.noticetemplate;

import cc.bamboo.framework.common.pojo.PageResult;
import cc.bamboo.module.user.controller.admin.noticetemplate.vo.NoticeTemplatePageReqVO;
import cc.bamboo.module.user.controller.admin.noticetemplate.vo.NoticeTemplateSaveReqVO;
import cc.bamboo.module.user.dal.dataobject.noticetemplate.NoticeTemplateDO;

import javax.validation.Valid;
import java.util.Map;

/**
 * 消息模板 Service 接口
 *
 * @author Kiro
 */
public interface NoticeTemplateService {

    /**
     * 创建消息模板
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createNoticeTemplate(@Valid NoticeTemplateSaveReqVO createReqVO);

    /**
     * 更新消息模板
     *
     * @param updateReqVO 更新信息
     */
    void updateNoticeTemplate(@Valid NoticeTemplateSaveReqVO updateReqVO);

    /**
     * 删除消息模板
     *
     * @param id 编号
     */
    void deleteNoticeTemplate(Long id);

    /**
     * 获得消息模板
     *
     * @param id 编号
     * @return 消息模板
     */
    NoticeTemplateDO getNoticeTemplate(Long id);

    /**
     * 根据编码获取消息模板
     *
     * @param code 模板编码
     * @return 消息模板
     */
    NoticeTemplateDO getNoticeTemplateByCode(String code);

    /**
     * 获得消息模板分页
     *
     * @param pageReqVO 分页查询
     * @return 消息模板分页
     */
    PageResult<NoticeTemplateDO> getNoticeTemplatePage(NoticeTemplatePageReqVO pageReqVO);

    /**
     * 格式化消息内容
     *
     * @param content 消息模板的内容
     * @param params 消息内容的参数
     * @return 格式化后的内容
     */
    String formatNoticeTemplateContent(String content, Map<String, Object> params);

}
