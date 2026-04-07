import {Api, NoneTokenApi} from "@/api";
import {ApiMethod, type ApiResult} from "@/types/api";
import type {LoginResult} from "@/api/user/ApiUserLoginByPassword";

@NoneTokenApi
export class ApiUserRefreshToken extends Api<ApiResult<LoginResult>>{
	constructor(refreshToken: string) {
		super('/user/auth/refresh-token', ApiMethod.POST, {refreshToken});
	}
}

