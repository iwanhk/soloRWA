import {Api, NoneTokenApi} from "@/api";
import {ApiMethod, type ApiResult} from "@/types/api";
import type {CommonConfigEntity} from "@/types/entity";
import {ConfigKey} from "@/types/enums.ts";

@NoneTokenApi
export class ApiGetCommonConfig extends Api<ApiResult<CommonConfigEntity>> {
	constructor(key:ConfigKey) {
		super('/system/dict-data/common-config', ApiMethod.GET, {key});
	}
}

