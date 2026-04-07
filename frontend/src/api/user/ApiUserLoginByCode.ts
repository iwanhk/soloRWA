import {Api, NoneTokenApi} from "@/api";
import {ApiMethod, type ApiResult} from "@/types/api";
import type {LoginResult} from "@/api/user/ApiUserLoginByPassword";

export interface ApiUserLoginByCodeParams{
	mobile: string,
	code: string,
}

@NoneTokenApi
export class ApiUserLoginByCode extends Api<ApiResult<LoginResult>>{
	constructor(params:ApiUserLoginByCodeParams) {
		super('/user/auth/sms-login', ApiMethod.POST, params);
	}
}