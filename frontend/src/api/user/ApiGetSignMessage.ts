import {Api} from "@/api";
import {ApiMethod, type ApiResult} from "@/types/api";

export class ApiGetSignMessage extends Api<ApiResult<string>> {
	constructor(chainAddress?: string) {
		super('/user/chain/sign', ApiMethod.GET, {chainAddress});
	}
}

