import {Api, NoneTokenApi} from "@/api";
import {ApiMethod, type ApiResult} from "@/types/api";
import type {ExchangeRate} from "@/types/entity";

@NoneTokenApi
export class ApiGetExchangeRate extends Api<ApiResult<ExchangeRate>>{
	constructor() {
		super('/project/exchange-rate/usdt', ApiMethod.GET);
	}
}

