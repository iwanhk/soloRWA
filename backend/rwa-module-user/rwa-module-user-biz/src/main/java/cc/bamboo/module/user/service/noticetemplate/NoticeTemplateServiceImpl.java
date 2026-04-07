package cc.bamboo.module.user.service.noticetemplate;

import cc.bamboo.framework.common.pojo.PageResult;
import cc.bamboo.framework.common.util.object.BeanUtils;
import cc.bamboo.module.user.controller.admin.noticetemplate.vo.NoticeTemplatePageReqVO;
import cc.bamboo.module.user.controller.admin.noticetemplate.vo.NoticeTemplateSaveReqVO;
import cc.bamboo.module.user.dal.dataobject.noticetemplate.NoticeTemplateDO;
import cc.bamboo.module.user.dal.mysql.noticetemplate.NoticeTemplateMapper;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static cc.bamboo.framework.common.exception.util.ServiceExceptionUtil.exception;

/**
 * 消息模板 Service 实现类
 *
 * @author Kiro
 */
@Service
@Validated
@Slf4j
public class NoticeTemplateServiceImpl implements NoticeTemplateService {

    @Resource
    private NoticeTemplateMapper noticeTemplateMapper;

    @Override
    public Long createNoticeTemplate(NoticeTemplateSaveReqVO createReqVO) {
        // 校验模板编码唯一性
        validateTemplateCodeUnique(null, createReqVO.getCode());

        // 插入
        NoticeTemplateDO template = BeanUtils.toBean(createReqVO, NoticeTemplateDO.class);
        noticeTemplateMapper.insert(template);
        return template.getId();
    }

    @Override
    public void updateNoticeTemplate(NoticeTemplateSaveReqVO updateReqVO) {
        // 校验存在
        validateTemplateExists(updateReqVO.getId());
        // 校验模板编码唯一性
        validateTemplateCodeUnique(updateReqVO.getId(), updateReqVO.getCode());

        // 更新
        NoticeTemplateDO updateObj = BeanUtils.toBean(updateReqVO, NoticeTemplateDO.class);
        noticeTemplateMapper.updateById(updateObj);
    }

    @Override
    public void deleteNoticeTemplate(Long id) {
        // 校验存在
        validateTemplateExists(id);
        // 删除
        noticeTemplateMapper.deleteById(id);
    }

    private void validateTemplateExists(Long id) {
        if (noticeTemplateMapper.selectById(id) == null) {
            throw exception(new cc.bamboo.framework.common.exception.ErrorCode(50004, "消息模板不存在"));
        }
    }

    private void validateTemplateCodeUnique(Long id, String code) {
        NoticeTemplateDO template = noticeTemplateMapper.selectByCode(code);
        if (template == null) {
            return;
        }
        if (id == null || !id.equals(template.getId())) {
            throw exception(new cc.bamboo.framework.common.exception.ErrorCode(50005, "模板编码已存在"));
        }
    }

    @Override
    public NoticeTemplateDO getNoticeTemplate(Long id) {
        return noticeTemplateMapper.selectById(id);
    }

    @Override
    public NoticeTemplateDO getNoticeTemplateByCode(String code) {
        return noticeTemplateMapper.selectByCode(code);
    }

    @Override
    public PageResult<NoticeTemplateDO> getNoticeTemplatePage(NoticeTemplatePageReqVO pageReqVO) {
        return noticeTemplateMapper.selectPage(pageReqVO);
    }

    @Override
    public String formatNoticeTemplateContent(String content, Map<String, Object> params) {
        if (StrUtil.isBlank(content) || params == null || params.isEmpty()) {
            return content;
        }

        // 使用正则表达式替换 ${key} 格式的占位符
        Pattern pattern = Pattern.compile("\\$\\{([^}]+)\\}");
        Matcher matcher = pattern.matcher(content);
        StringBuffer result = new StringBuffer();

        while (matcher.find()) {
            String key = matcher.group(1);
            Object value = params.get(key);
            matcher.appendReplacement(result, value != null ? value.toString() : "");
        }
        matcher.appendTail(result);

        return result.toString();
    }

    public static  String TemplateFormatter(String content, Map<String, Object> params) {
        if (StrUtil.isBlank(content) || params == null || params.isEmpty()) {
            return content;
        }

        // 使用正则表达式替换 ${key} 格式的占位符
        Pattern pattern = Pattern.compile("\\$\\{([^}]+)\\}");
        Matcher matcher = pattern.matcher(content);
        StringBuffer result = new StringBuffer();

        while (matcher.find()) {
            String key = matcher.group(1);
            Object value = params.get(key);
            matcher.appendReplacement(result, value != null ? value.toString() : "");
        }
        matcher.appendTail(result);

        return result.toString();
    }

    public static void main(String[] args) {

        String content = "您的{project}项目已到达分红期";
        Map<String, Object> params = new HashMap<>();
        params.put("project", "XX科技分红项目");

        String result = TemplateFormatter(content, params);
        System.out.println(result); // 输出：您的XX科技分红项目已到达分红期
    }

}
