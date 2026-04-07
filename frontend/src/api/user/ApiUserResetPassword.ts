import {Api} from "@/api";
import {ApiMethod, type ApiResult} from "@/types/api";

export interface ApiUserResetPasswordParams {
	mobile: string;
	code: string;
	emailCode: string;
	newPassword: string;
}

export class ApiUserResetPassword extends Api<ApiResult<boolean>>{
	constructor(params: ApiUserResetPasswordParams) {
		super('/user/auth/reset-password', ApiMethod.POST, params);
	}
}

