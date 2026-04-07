import {Api} from "@/api";
import {ApiMethod, type ApiResult} from "@/types/api";

export interface ApiUserBind2FAParams{
	code: string;
}

export class ApiUserBind2FA extends Api<ApiResult<boolean>>{
	constructor(params:ApiUserBind2FAParams) {
		super('/user/auth/2fa/bind', ApiMethod.POST, params);
	}
}

