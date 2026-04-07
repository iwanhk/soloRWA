import {Api} from "@/api";
import {ApiMethod, type ApiResult} from "@/types/api";
import type {ChainAddressEntity} from "@/types/entity";

export class ApiGetUserChainList extends Api<ApiResult<ChainAddressEntity[]>> {
	constructor() {
		super('/user/chain/list', ApiMethod.GET);
	}
}

