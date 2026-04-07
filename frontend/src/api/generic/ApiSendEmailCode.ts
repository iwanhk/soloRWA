import {Api, OptionalTokenApi} from "@/api";
import {ApiMethod, type ApiResult} from "@/types/api";
import {CodeScene} from "@/api/generic/ApiSendSmsCode";

export interface ApiSendEmailCodeParams {
	/**
	 * 邮箱
	 */
	email: string;
	/**
	 * 发送场景
	 */
	scene: CodeScene;
}

/**
 * 发送邮箱验证码
 */
@OptionalTokenApi
export class ApiSendEmailCode extends Api<ApiResult<boolean>>{
	constructor(params: ApiSendEmailCodeParams) {
		super('/user/auth/send-email-code', ApiMethod.POST, params);
	}
}
