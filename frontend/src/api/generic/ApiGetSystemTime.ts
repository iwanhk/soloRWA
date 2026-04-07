import {Api, NoneTokenApi} from "@/api";
import {ApiMethod, type ApiResult} from "@/types/api";

@NoneTokenApi
export class ApiGetSystemTime extends Api<ApiResult<number>> {
	constructor() {
		super('/system/base/time', ApiMethod.GET);
	}
}

