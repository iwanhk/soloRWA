import {Api, NoneTokenApi} from "@/api";
import {ApiMethod, type ApiResult} from "@/types/api";
import type {LoginResult} from "@/api/user/ApiUserLoginByPassword";

export interface ApiUserEmailLoginParams {
	/**
	 * 邮箱
	 */
	email: string;
	/**
	 * 密码
	 */
	password: string;
}

/**
 * 使用邮箱 + 密码登录
 */
@NoneTokenApi
export class ApiUserEmailLogin extends Api<ApiResult<LoginResult>>{
	constructor(params: ApiUserEmailLoginParams) {
		super('/user/auth/email-login', ApiMethod.POST, params);
	}
}
