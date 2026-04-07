import {Api} from "@/api";
import {ApiMethod, type ApiResult} from "@/types/api";

export interface ApiUnbindChainAddressParams {
	addressId?: number; // 地址ID
	sign?: string; // 签名
}

export class ApiUnbindChainAddress extends Api<ApiResult<Record<string, any>>> {
	constructor(params?: ApiUnbindChainAddressParams) {
		super('/user/chain/unbind', ApiMethod.POST, params);
	}
}

