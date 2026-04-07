import {Api} from "@/api";
import {ApiMethod, type ApiResult} from "@/types/api";

export class ApiUserVerify2FA extends Api<ApiResult<boolean>>{
	constructor(code: string) {
		super('/user/auth/2fa/verify', ApiMethod.POST, {code});
	}
}

