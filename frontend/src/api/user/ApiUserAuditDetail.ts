import {Api} from "@/api";
import {ApiMethod, type ApiResult} from "@/types/api";
import type {UserAuditDetail} from "@/types/entity";

export class ApiUserAuditDetail extends Api<ApiResult<UserAuditDetail>>{
	constructor() {
		super('/user/user-audit/detail', ApiMethod.GET);
	}
}

