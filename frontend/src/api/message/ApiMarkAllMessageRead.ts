import {Api} from "@/api";
import {ApiMethod, type ApiResult} from "@/types/api";

export class ApiMarkAllMessageRead extends Api<ApiResult<boolean>>{
	constructor() {
		super('/user/notice-message/mark-all-read', ApiMethod.PUT);
	}
}

