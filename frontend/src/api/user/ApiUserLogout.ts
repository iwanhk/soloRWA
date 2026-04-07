import {Api} from "@/api";
import {ApiMethod, type ApiResult} from "@/types/api";

export class ApiUserLogout extends Api<ApiResult<boolean>> {
	constructor() {
		super('/user/auth/logout', ApiMethod.POST);
	}
}

