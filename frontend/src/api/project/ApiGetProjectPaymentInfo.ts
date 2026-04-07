import {Api} from "@/api";
import {ApiMethod, type ApiResult} from "@/types/api";

export interface BankPaymentInfo {
	bankAccountName: string; // 开户名
	bankAccount: string; // 银行账号
	bankName: string; // 开户行
}

export class ApiGetProjectPaymentInfo extends Api<ApiResult<BankPaymentInfo>>{
	constructor(projectId: number) {
		super('/project/info/payment-info', ApiMethod.GET, {projectId});
	}
}

