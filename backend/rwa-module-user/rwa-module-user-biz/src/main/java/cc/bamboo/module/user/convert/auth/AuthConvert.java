package cc.bamboo.module.user.convert.auth;


import cc.bamboo.module.system.api.oauth2.dto.OAuth2AccessTokenRespDTO;
import cc.bamboo.module.system.api.sms.dto.code.SmsCodeSendReqDTO;
import cc.bamboo.module.system.api.sms.dto.code.SmsCodeUseReqDTO;
import cc.bamboo.module.user.controller.app.userinfo.vo.AppAuthLoginRespVO;
import cc.bamboo.module.user.controller.app.userinfo.vo.AppAuthRegisterReqVO;
import cc.bamboo.module.user.controller.app.userinfo.vo.AppAuthResetPasswordReqVO;
import cc.bamboo.module.user.controller.app.userinfo.vo.AppAuthSmsLoginReqVO;
import cc.bamboo.module.user.controller.app.userinfo.vo.AppAuthSmsSendReqVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AuthConvert {

    AuthConvert INSTANCE = Mappers.getMapper(AuthConvert.class);

    AppAuthLoginRespVO convert(OAuth2AccessTokenRespDTO bean);


    SmsCodeSendReqDTO convert(AppAuthSmsSendReqVO reqVO);
    SmsCodeUseReqDTO convert(AppAuthSmsLoginReqVO reqVO, Integer scene, String usedIp);
    SmsCodeUseReqDTO convert(AppAuthRegisterReqVO reqVO, Integer scene, String usedIp);
    SmsCodeUseReqDTO convert(AppAuthResetPasswordReqVO reqVO, Integer scene, String usedIp);
}
