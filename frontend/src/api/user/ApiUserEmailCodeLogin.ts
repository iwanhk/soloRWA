import {Api, NoneTokenApi} from "@/api";
import {ApiMethod, type ApiResult} from "@/types/api";
import type {LoginResult} from "@/api/user/ApiUserLoginByPassword";

export interface ApiUserEmailCodeLoginParams {
	/**
	 * 邮箱
	 */
	email: string;
	/**
	 * 邮箱验证码
	 */
	code: string;
}

/**
 * 使用邮箱 + 验证码登录
 */
@NoneTokenApi
export class ApiUserEmailCodeLogin extends Api<ApiResult<LoginResult>>{
	constructor(params: ApiUserEmailCodeLoginParams) {
		super('/user/auth/email-code-login', ApiMethod.POST, params);
	}
}
