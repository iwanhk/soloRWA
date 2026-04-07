import {Api, NoneTokenApi} from "@/api";
import {ApiMethod, type ApiResult} from "@/types/api";
import type {ProtocolEntity} from "@/types/entity";

@NoneTokenApi
export class ApiGetAgreementDetail extends Api<ApiResult<ProtocolEntity>> {
	constructor(id: number) {
		super('/user/agreement/detail', ApiMethod.GET, {id});
	}
}

