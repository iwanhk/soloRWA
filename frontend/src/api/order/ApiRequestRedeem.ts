import {Api} from "@/api";
import {ApiMethod, type ApiResult} from "@/types/api";

export interface ApiRequestRedeemParams {
	orderId: number; // 订单余额ID
	smsCode: string; // 手机验证码
	emailCode: string;
	ftaCode?: string;
}

export class ApiRequestRedeem extends Api<ApiResult<boolean>>{
	constructor(params: ApiRequestRedeemParams) {
		super('/project/project-order/maturity-redemption', ApiMethod.POST, params);
	}
}

