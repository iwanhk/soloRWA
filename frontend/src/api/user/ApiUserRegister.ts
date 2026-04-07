import {Api, NoneTokenApi} from "@/api";
import {ApiMethod, type ApiResult} from "@/types/api";
import type {LoginResult} from "@/api/user/ApiUserLoginByPassword";

export interface ApiUserRegisterParams {
	mobile: string;
	password: string;
	code: string;
}

@NoneTokenApi
export class ApiUserRegister extends Api<ApiResult<LoginResult>>{
	constructor(params: ApiUserRegisterParams) {
		super('/user/auth/register', ApiMethod.POST, params);
	}
}