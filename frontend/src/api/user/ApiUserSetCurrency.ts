import {Api} from "@/api";
import {ApiMethod, type ApiResult} from "@/types/api";

export class ApiUserSetCurrency extends Api<ApiResult<boolean>>{
	constructor(currency: string) {
		super('/user/auth/set-currency', ApiMethod.POST, {currency});
	}
}

