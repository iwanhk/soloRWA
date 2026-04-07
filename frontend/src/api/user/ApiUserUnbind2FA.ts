import {Api} from "@/api";
import {ApiMethod, type ApiResult} from "@/types/api";

export interface ApiUserUnbind2FAParams{
	code: string;
	emailCode: string;
}

export class ApiUserUnbind2FA extends Api<ApiResult<boolean>>{
	constructor(params: ApiUserUnbind2FAParams) {
		super('/user/auth/2fa/unbind-request', ApiMethod.POST, params);
	}
}

