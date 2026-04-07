import {Api, NoneTokenApi} from "@/api";
import {ApiMethod, type ApiResult} from "@/types/api";
import type {SimpleProtocolEntity} from "@/types/entity";

@NoneTokenApi
export class ApiGetAgreementList extends Api<ApiResult<SimpleProtocolEntity[]>> {
	constructor() {
		super('/user/agreement/list', ApiMethod.GET);
	}
}

