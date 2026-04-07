import {Api, OptionalTokenApi} from "@/api";
import {ApiMethod} from "@/types/api";

export const enum CodeScene {
	LOGIN = 1, // 手机号登陆
	UPDATE_MOBILE = 2, // 修改手机
	UPDATE_PASSWORD = 3, // 修改密码
	RESET_PASSWORD = 4, // 忘记密码
	REGISTER = 5, // 注册
	AUDIT = 6, // 用户认证
	UNBIND_2FA = 7, // 解绑
	TRANSACTION = 6, //交易
}

export interface ApiSendSmsCodeParams{
	mobile?: string,
	scene: CodeScene,
}

@OptionalTokenApi
export class ApiSendSmsCode extends Api{
	constructor(params:ApiSendSmsCodeParams) {
		super('/user/auth/send-sms-code', ApiMethod.POST, params);
	}
}