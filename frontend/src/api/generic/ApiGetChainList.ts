import {Api, NoneTokenApi} from "@/api";
import {ApiMethod, type ApiResult} from "@/types/api";
import type {ChainEntity} from "@/types/entity";

@NoneTokenApi
export class ApiGetChainList extends Api<ApiResult<ChainEntity[]>> {
	constructor() {
		super('/user/chain/chain-list', ApiMethod.GET);
	}
}

