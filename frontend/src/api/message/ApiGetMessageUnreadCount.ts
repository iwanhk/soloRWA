import {Api} from "@/api";
import {ApiMethod, type ApiResult} from "@/types/api";

export class ApiGetMessageUnreadCount extends Api<ApiResult<number>>{
	constructor() {
		super('/user/notice-message/unread-count', ApiMethod.GET);
	}
}

