import {Api} from "@/api";
import {ApiMethod, type ApiResult} from "@/types/api";
import type {UserEntity} from "@/types/entity";

export class ApiGetUserInfo extends Api<ApiResult<UserEntity>> {
	constructor() {
		super('/user/auth/userinfo', ApiMethod.GET);
	}
}

