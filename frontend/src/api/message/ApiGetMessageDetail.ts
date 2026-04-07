import {Api} from "@/api";
import {ApiMethod, type ApiResult} from "@/types/api";
import type {MessageEntity} from "@/types/entity";

export class ApiGetMessageDetail extends Api<ApiResult<MessageEntity>>{
	constructor(id:number) {
		super('/user/notice-message/detail', ApiMethod.GET, {id});
	}
}

