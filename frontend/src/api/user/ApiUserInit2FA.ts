import {Api} from "@/api";
import {ApiMethod, type ApiResult} from "@/types/api";

export interface Init2FAResult {
	secret: string;
	qrCodeUrl: string;
}

export class ApiUserInit2FA extends Api<ApiResult<Init2FAResult>>{
	constructor() {
		super('/user/auth/2fa/init', ApiMethod.POST);
	}
}

