import {Api} from "@/api";
import {type ApiListParams, type ApiListResult, ApiMethod} from "@/types/api";
import type {MessageEntity} from "@/types/entity";
import {MessageType} from "@/types/enums";

export interface ApiGetMessageListParams extends ApiListParams{
	noticeType?: MessageType;
	readStatus?: boolean;
}

export class ApiGetMessageList extends Api<ApiListResult<MessageEntity>>{
	constructor(params: ApiGetMessageListParams) {
		super('/user/notice-message/page', ApiMethod.GET, params);
	}
}

