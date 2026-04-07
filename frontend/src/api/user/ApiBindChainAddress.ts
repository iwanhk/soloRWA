import {Api} from "@/api";
import {ApiMethod, type ApiResult} from "@/types/api";

export interface ApiBindChainAddressParams {
	chainAddress: string; // 链地址
	sign: string; // 签名
}

export class ApiBindChainAddress extends Api<ApiResult<number>> {
	constructor(params: ApiBindChainAddressParams) {
		super('/user/chain/bind', ApiMethod.POST, params);
	}
}

