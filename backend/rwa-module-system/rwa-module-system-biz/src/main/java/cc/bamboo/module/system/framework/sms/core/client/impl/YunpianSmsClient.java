package cc.bamboo.module.system.framework.sms.core.client.impl;

import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cc.bamboo.framework.common.core.KeyValue;
import cc.bamboo.framework.common.util.http.HttpUtils;
import cc.bamboo.module.system.framework.sms.core.client.dto.SmsReceiveRespDTO;
import cc.bamboo.module.system.framework.sms.core.client.dto.SmsSendRespDTO;
import cc.bamboo.module.system.framework.sms.core.client.dto.SmsTemplateRespDTO;
import cc.bamboo.module.system.framework.sms.core.property.SmsChannelProperties;
import cc.bamboo.module.system.dal.dataobject.sms.SmsTemplateDO;
import cc.bamboo.module.system.dal.mysql.sms.SmsTemplateMapper;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 云片短信客户端的实现类
 *
 * @author Swolf
 * @since 2026/02/11
 */
@Slf4j
public class YunpianSmsClient extends AbstractSmsClient {

    /**
     * 云片短信 API 地址
     */
    private static final String URL = "https://yunpian.com/v2/sms/single_send.json";

    /**
     * 成功状态码
     */
    private static final Integer SUCCESS_CODE = 0;

    /**
     * 短信模板 Mapper
     */


    public YunpianSmsClient(SmsChannelProperties properties) {
        super(properties);
        Assert.notEmpty(properties.getApiKey(), "apiKey 不能为空");
    }

    @Override
    public SmsSendRespDTO sendSms(Long sendLogId, String mobile, String apiTemplateId,
            List<KeyValue<String, Object>> templateParams) throws Throwable {
        // 1. 查询模板内容


        // 2. 构建短信内容 - 使用模板的 content 并替换参数
        String text = buildSmsText(apiTemplateId, templateParams);

        // 3. 构建请求参数
        Map<String, String> params = new HashMap<>();
        params.put("apikey", properties.getApiKey());
        params.put("text", text);
        params.put("mobile", mobile);

        // 4. 发起请求 - 使用 form-encoded 格式
        String requestBody = buildFormBody(params);
        String responseBody = HttpUtils.post(URL, new HashMap<>(), requestBody);
        JSONObject response = JSONUtil.parseObj(responseBody);
        log.info("[sendSms][sendLogId: {}, mobile: {}, apiTemplateId: {}, text: {}, response: {}]",
                sendLogId, mobile, apiTemplateId, text, response);
        // 5. 解析响应
        Integer code = response.getInt("code");
        boolean success = SUCCESS_CODE.equals(code);

        return new SmsSendRespDTO()
                .setSuccess(success)
                .setSerialNo(response.getStr("sid")) // 云片返回的短信ID
                .setApiRequestId(response.getStr("sid"))
                .setApiCode(String.valueOf(code))
                .setApiMsg(response.getStr("msg"));
    }

    /**
     * 构建短信文本内容
     * 
     * @param apiTemplateId  模板ID
     * @param templateParams 模板参数
     * @return 短信文本
     */
    private String buildSmsText(String apiTemplateId, List<KeyValue<String, Object>> templateParams) {
        // 云片需要使用已审核的模板文本
        // 这里简化处理：如果apiTemplateId本身就是模板文本，直接使用
        // 如果有参数，则替换参数
        String text = apiTemplateId;

        if (templateParams != null && !templateParams.isEmpty()) {
            // 替换模板参数，格式如：#code#、#name# 等
            Map<String, Object> paramMap = CollStreamUtil.toMap(templateParams,
                    KeyValue::getKey, KeyValue::getValue);
            for (Map.Entry<String, Object> entry : paramMap.entrySet()) {
                text = text.replace("{" + entry.getKey() + "}", String.valueOf(entry.getValue()));
            }
        }

        return text;
    }

    @Override
    public List<SmsReceiveRespDTO> parseSmsReceiveStatus(String text) throws Throwable {
        // 云片的回调格式需要根据实际文档解析
        // 这里提供基础实现框架
        log.warn("[parseSmsReceiveStatus][云片短信回调解析暂未实现，text: {}]", text);
        return null;
    }

    @Override
    public SmsTemplateRespDTO getSmsTemplate(String apiTemplateId) throws Throwable {
        // 云片的模板查询 API
        // 根据云片文档，模板查询接口为：https://sms.yunpian.com/v2/tpl/get.json
        Map<String, String> params = new HashMap<>();
        params.put("apikey", properties.getApiKey());
        params.put("tpl_id", apiTemplateId);

        String requestBody = buildFormBody(params);
        String responseBody = HttpUtils.post("https://sms.yunpian.com/v2/tpl/get.json", new HashMap<>(), requestBody);
        JSONObject response = JSONUtil.parseObj(responseBody);

        // 解析响应
        Integer code = response.getInt("code");
        if (!SUCCESS_CODE.equals(code)) {
            log.error("[getSmsTemplate][模版编号({}) 查询失败，响应: {}]", apiTemplateId, response);
            return null;
        }

        // 云片返回的模板信息
        JSONObject tpl = response.getJSONObject("tpl");
        if (tpl == null) {
            return null;
        }

        return new SmsTemplateRespDTO()
                .setId(tpl.getStr("tpl_id"))
                .setContent(tpl.getStr("tpl_content"))
                .setAuditStatus(convertAuditStatus(tpl.getStr("check_status")))
                .setAuditReason(tpl.getStr("reason"));
    }

    /**
     * 转换审核状态
     * 
     * @param checkStatus 云片审核状态
     * @return 系统审核状态
     */
    private Integer convertAuditStatus(String checkStatus) {
        if (StrUtil.isEmpty(checkStatus)) {
            return null;
        }

        // 云片模板状态：CHECKING-审核中, SUCCESS-审核通过, FAIL-审核失败
        switch (checkStatus) {
            case "CHECKING":
                return 0; // 审核中
            case "SUCCESS":
                return 1; // 审核通过
            case "FAIL":
                return 2; // 审核失败
            default:
                log.warn("[convertAuditStatus][未知审核状态: {}]", checkStatus);
                return null;
        }
    }

    /**
     * 构建 form-encoded 请求体
     * 
     * @param params 参数Map
     * @return form-encoded 字符串
     */
    private String buildFormBody(Map<String, String> params) {
        StringBuilder body = new StringBuilder();
        try {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (body.length() > 0) {
                    body.append("&");
                }
                body.append(entry.getKey())
                        .append("=")
                        .append(java.net.URLEncoder.encode(entry.getValue(), "UTF-8"));
            }
        } catch (Exception e) {
            log.error("[buildFormBody][URL编码失败]", e);
        }
        return body.toString();
    }
}
