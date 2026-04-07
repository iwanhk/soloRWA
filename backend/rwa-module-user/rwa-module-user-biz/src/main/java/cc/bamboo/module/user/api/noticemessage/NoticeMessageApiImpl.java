package cc.bamboo.module.user.api.noticemessage;

import cc.bamboo.framework.common.pojo.CommonResult;
import cc.bamboo.module.user.api.noticemessage.dto.NoticeMessageSendReqDTO;
import cc.bamboo.module.user.service.noticemessage.NoticeMessageSendService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;

import static cc.bamboo.framework.common.pojo.CommonResult.success;

/**
 * RPC 服务 - 消息发送 API 实现类
 *
 * @author Kiro
 */
@RestController
@Validated
public class NoticeMessageApiImpl implements NoticeMessageApi {

    @Resource
    private NoticeMessageSendService noticeMessageSendService;

    @Override
    public CommonResult<Boolean> sendSingleMessageAsync(NoticeMessageSendReqDTO reqDTO) {
        noticeMessageSendService.sendSingleMessageAsync(
                reqDTO.getUserId(),
                reqDTO.getTemplateCode(),
                reqDTO.getTemplateParams());
        return success(true);
    }

    @Override
    public CommonResult<Boolean> sendSystemMessageAsync(NoticeMessageSendReqDTO reqDTO) {
        noticeMessageSendService.sendSystemMessageAsync(
                reqDTO.getTemplateCode(),
                reqDTO.getTemplateParams());
        return success(true);
    }

    @Override
    public CommonResult<Boolean> sendOrderMessageAsync(NoticeMessageSendReqDTO reqDTO) {
        noticeMessageSendService.sendOrderMessageAsync(
                reqDTO.getUserId(),
                reqDTO.getOrderId(),
                reqDTO.getTemplateCode(),
                reqDTO.getTemplateParams());
        return success(true);
    }

}
