package cc.bamboo.module.system.api.mail;

import cc.bamboo.framework.common.pojo.CommonResult;
import cc.bamboo.module.system.api.mail.dto.MailCodeCreateReqDTO;
import cc.bamboo.module.system.api.mail.dto.MailCodeSendReqDTO;
import cc.bamboo.module.system.api.mail.dto.MailCodeUseReqDTO;
import cc.bamboo.module.system.api.mail.dto.MailSendSingleToUserReqDTO;
import cc.bamboo.module.system.service.mail.MailSendService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

import static cc.bamboo.framework.common.pojo.CommonResult.success;

@RestController // 提供 RESTful API 接口，给 Feign 调用
@Validated
public class MailSendApiImpl implements MailSendApi {

    @Resource
    private MailSendService mailSendService;

    @Override
    public CommonResult<Long> sendSingleMailToAdmin(MailSendSingleToUserReqDTO reqDTO) {
        return success(mailSendService.sendSingleMailToAdmin(reqDTO.getMail(), reqDTO.getUserId(),
                reqDTO.getTemplateCode(), reqDTO.getTemplateParams()));
    }

    @Override
    public CommonResult<Long> sendSingleMailToMember(MailSendSingleToUserReqDTO reqDTO) {
        return success(mailSendService.sendSingleMailToMember(reqDTO.getMail(), reqDTO.getUserId(),
                reqDTO.getTemplateCode(), reqDTO.getTemplateParams()));
    }

    @Override
    public CommonResult<Long> sendSingleMail(MailSendSingleToUserReqDTO reqDTO) {
        return success(mailSendService.sendSingleMail(reqDTO.getMail(), reqDTO.getUserId(), reqDTO.getUserType(),
                reqDTO.getTemplateCode(), reqDTO.getTemplateParams()));
    }

    @Override
    public CommonResult<String> createMailCode(MailCodeCreateReqDTO reqDTO) {
        return success(mailSendService.createMailCode(reqDTO.getMail(), reqDTO.getScene()));
    }

    @Override
    public CommonResult<Boolean> verifyMailCode(MailCodeUseReqDTO reqDTO) {
        return success(mailSendService.useMailCode(reqDTO.getMail(), reqDTO.getCode(), reqDTO.getScene()));
    }

    @Override
    public CommonResult<Long> sendMailCode(MailCodeSendReqDTO reqDTO) {
        return success(mailSendService.sendMailCode(reqDTO.getMail(), reqDTO.getUserId(), reqDTO.getUserType(),
                reqDTO.getTemplateCode(), reqDTO.getScene()));
    }


}
