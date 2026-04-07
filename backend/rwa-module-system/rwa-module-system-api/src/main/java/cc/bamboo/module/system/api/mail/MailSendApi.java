package cc.bamboo.module.system.api.mail;

import cc.bamboo.framework.common.pojo.CommonResult;
import cc.bamboo.module.system.api.mail.dto.MailCodeCreateReqDTO;
import cc.bamboo.module.system.api.mail.dto.MailCodeSendReqDTO;
import cc.bamboo.module.system.api.mail.dto.MailCodeUseReqDTO;
import cc.bamboo.module.system.api.mail.dto.MailSendSingleToUserReqDTO;
import cc.bamboo.module.system.enums.ApiConstants;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

@FeignClient(name = ApiConstants.NAME) // TODO 芋艿：fallbackFactory =
@Tag(name = "RPC 服务 - 邮件发送")
public interface MailSendApi {

    String PREFIX = ApiConstants.PREFIX + "/mail/send";

    @PostMapping(PREFIX + "/send-single-admin")
    @Operation(summary = "发送单条邮件给 Admin 用户", description = "在 mail 为空时，使用 userId 加载对应 Admin 的邮箱")
    CommonResult<Long> sendSingleMailToAdmin(@Valid @RequestBody MailSendSingleToUserReqDTO reqDTO);

    @PostMapping(PREFIX + "/send-single-member")
    @Operation(summary = "发送单条邮件给 Member 用户", description = "在 mail 为空时，使用 userId 加载对应 Member 的邮箱")
    CommonResult<Long> sendSingleMailToMember(@Valid @RequestBody MailSendSingleToUserReqDTO reqDTO);

    @PostMapping(PREFIX + "/send-single")
    @Operation(summary = "发送单条邮件", description = "根据 userType 发送给 Admin 或 Member 用户")
    CommonResult<Long> sendSingleMail(@Valid @RequestBody MailSendSingleToUserReqDTO reqDTO);

    //创建邮箱验证码（存储在 Redis 中）
    @PostMapping(PREFIX + "/create-code")
    @Operation(summary = "创建邮箱验证码", description = "根据 mail 和 scene 创建验证码")
    CommonResult<String> createMailCode(@Valid @RequestBody MailCodeCreateReqDTO reqDTO);

    //校验邮箱验证码（从 Redis 中获取）
    @PostMapping(PREFIX + "/verify-code")
    @Operation(summary = "校验邮箱验证码", description = "根据 mail 和 scene 校验验证码")
    CommonResult<Boolean> verifyMailCode(@Valid @RequestBody MailCodeUseReqDTO reqDTO);

    //发送邮箱验证码
    @PostMapping(PREFIX + "/send-code")
    @Operation(summary = "发送邮箱验证码", description = "根据 mail 和 scene 发送验证码")
    CommonResult<Long> sendMailCode(@Valid @RequestBody MailCodeSendReqDTO reqDTO);

}
