import {Api} from "@/api";
import {type ApiListParams, type ApiListResult, ApiMethod} from "@/types/api";
import type {LoginHistoryEntity} from "@/types/entity";

export class ApiUserLoginLog extends Api<ApiListResult<LoginHistoryEntity>> {
	constructor(params: ApiListParams) {
		super('/user/login-log/page', ApiMethod.GET, params);
	}
}

