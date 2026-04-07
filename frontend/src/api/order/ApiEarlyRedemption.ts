import {Api} from "@/api";
import {ApiMethod, type ApiResult} from "@/types/api";

export interface ApiEarlyRedemptionParams {
	orderId: number; // 订单余额ID
	redemptionQuantity: number; // 赎回份额
	smsCode: string; // 手机验证码
	emailCode: string;
	ftaCode?: string;
}

export class ApiEarlyRedemption extends Api<ApiResult<boolean>>{
	constructor(params: ApiEarlyRedemptionParams) {
		super('/project/project-order/early-redemption', ApiMethod.POST, params);
	}
}

