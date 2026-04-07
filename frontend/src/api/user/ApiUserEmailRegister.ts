import {Api, NoneTokenApi} from "@/api";
import {ApiMethod, type ApiResult} from "@/types/api";
import type {LoginResult} from "@/api/user/ApiUserLoginByPassword";

export interface ApiUserEmailRegisterParams {
	/**
	 * 邮箱
	 */
	email: string;
	/**
	 * 邮箱验证码
	 */
	code: string;
	/**
	 * 密码
	 */
	password: string;
}

/**
 * 使用邮箱 + 验证码 + 密码注册
 */
@NoneTokenApi
export class ApiUserEmailRegister extends Api<ApiResult<LoginResult>>{
	constructor(params: ApiUserEmailRegisterParams) {
		super('/user/auth/email-register', ApiMethod.POST, params);
	}
}
