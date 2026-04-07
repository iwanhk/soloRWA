import {Api, NoneTokenApi} from "@/api";
import {ApiMethod, type ApiResult} from "@/types/api";

export interface LoginResult{
	userId: number,
	accessToken: string,
	refreshToken: string,
	expiresTime: number,
	refreshTokenExpiresTime: number|null,
}

export interface ApiUserLoginByPasswordParams{
	mobile: string,
	password: string,
}

@NoneTokenApi
export class ApiUserLoginByPassword extends Api<ApiResult<LoginResult>>{
	constructor(params: ApiUserLoginByPasswordParams) {
		super('/user/auth/login', ApiMethod.POST, params);
	}
}