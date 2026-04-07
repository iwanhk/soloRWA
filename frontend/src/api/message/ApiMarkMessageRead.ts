import {Api} from "@/api";
import {ApiMethod, type ApiResult} from "@/types/api";

export class ApiMarkMessageRead extends Api<ApiResult<boolean>>{
	constructor(id:number) {
		super('/user/notice-message/mark-read', ApiMethod.PUT, {id});
	}
}

