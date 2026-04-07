import {Api} from "@/api";
import {ApiMethod, type ApiResult} from "@/types/api";

export interface ApiPayOrderParams {
	orderId: number; // 订单ID
	payVoucher: File; // 支付凭证文件
	contractNo: string; // 合同号
	smsCode: string; // 手机验证码
	emailCode: string;
	ftaCode?: string;
}

export class ApiPayOrder extends Api<ApiResult<boolean>>{
	constructor(params: ApiPayOrderParams) {
		super('/project/project-order/pay', ApiMethod.UPLOAD, params);
	}
}

