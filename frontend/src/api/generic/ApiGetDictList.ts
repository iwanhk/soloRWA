import {Api, NoneTokenApi} from "@/api";
import {ApiMethod, type ApiResult} from "@/types/api";
import type {DictEntity} from "@/types/entity";

@NoneTokenApi
export class ApiGetDictList extends Api<ApiResult<DictEntity[]>> {
	constructor() {
		super('/system/dict-data/list-with-type', ApiMethod.GET);
	}
}

