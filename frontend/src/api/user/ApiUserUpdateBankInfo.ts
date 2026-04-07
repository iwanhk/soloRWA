import {Api} from "@/api";
import {ApiMethod, type ApiResult} from "@/types/api";

export interface ApiUserUpdateBankInfoParams {
	id: number;
	bankAccountName: string;
	bankAccount: string;
	bankName: string;
}

export class ApiUserUpdateBankInfo extends Api<ApiResult<boolean>>{
	constructor(params: ApiUserUpdateBankInfoParams) {
		super('/user/auth/update-bank-info', ApiMethod.POST, params);
	}
}

