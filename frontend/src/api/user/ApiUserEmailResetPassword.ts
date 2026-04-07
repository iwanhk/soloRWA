import {Api, NoneTokenApi} from "@/api";
import {ApiMethod, type ApiResult} from "@/types/api";

export interface ApiUserEmailResetPasswordParams {
	/**
	 * 邮箱
	 */
	email: string;
	/**
	 * 邮箱验证码
	 */
	code: string;
	/**
	 * 新密码
	 */
	newPassword: string;
}

/**
 * 通过邮箱验证码重置密码
 */
@NoneTokenApi
export class ApiUserEmailResetPassword extends Api<ApiResult<boolean>>{
	constructor(params: ApiUserEmailResetPasswordParams) {
		super('/user/auth/email-reset-password', ApiMethod.POST, params);
	}
}
