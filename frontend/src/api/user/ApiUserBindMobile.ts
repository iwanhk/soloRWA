import {Api} from "@/api";
import {ApiMethod, type ApiResult} from "@/types/api";

export interface ApiUserBindMobileParams {
	/**
	 * 手机号
	 */
	mobile: string;
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
 * 绑定手机号（需要手机验证码 + 邮箱验证码双重验证）
 */
export class ApiUserBindMobile extends Api<ApiResult<boolean>>{
	constructor(params: ApiUserBindMobileParams) {
		super('/user/auth/bind-mobile', ApiMethod.POST, params);
	}
}
