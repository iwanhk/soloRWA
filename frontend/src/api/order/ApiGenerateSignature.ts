import {Api} from "@/api";
import {ApiMethod, type ApiResult} from "@/types/api";

export interface ApiGenerateSignatureParams {
	projectId: number; // 项目ID
	chainAddress: string; // 钱包地址
	quantity: number; // 购买数量
}

export interface ApiGenerateSignatureResult {
	signature: string; // 签名字符串
	orderNo: string; // 订单号
}

export class ApiGenerateSignature extends Api<ApiResult<ApiGenerateSignatureResult>>{
	constructor(params: ApiGenerateSignatureParams) {
		super('/project/project-order/generate-signature', ApiMethod.POST, params);
	}
}

