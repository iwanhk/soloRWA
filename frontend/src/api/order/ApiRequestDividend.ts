import {Api} from "@/api";
import {ApiMethod, type ApiResult} from "@/types/api";

export interface ApiRequestDividendParams {
	orderId: number; // 订单余额ID
	dividendAmount: number; // 分红金额
	smsCode: string; // 手机验证码
	emailCode: string;
	ftaCode?: string;
}

export class ApiRequestDividend extends Api<ApiResult<boolean>>{
	constructor(params: ApiRequestDividendParams) {
		super('/project/project-order/dividend', ApiMethod.POST, params);
	}
}

