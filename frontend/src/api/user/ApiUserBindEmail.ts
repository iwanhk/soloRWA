import {Api} from "@/api";
import {ApiMethod, type ApiResult} from "@/types/api";

export interface ApiUserBindEmailParams {
	/**
	 * 邮箱
	 */
	email: string;
	/**
	 * 手机验证码
	 */
	smsCode: string;
	/**
	 * 邮箱验证码
	 */
	mailCode: string;
}

/**
 * 绑定邮箱（需要手机验证码 + 邮箱验证码双重验证）
 */
export class ApiUserBindEmail extends Api<ApiResult<boolean>>{
	constructor(params: ApiUserBindEmailParams) {
		super('/user/auth/bind-email', ApiMethod.POST, params);
	}
}
