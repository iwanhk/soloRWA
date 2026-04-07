import {Api} from "@/api";
import {ApiMethod, type ApiResult} from "@/types/api";

export class ApiGetRedemptionFeeRate extends Api<ApiResult<number>>{
	constructor(orderId: number) {
		super('/project/project-order/get-redemption-fee-rate', ApiMethod.GET, {orderId});
	}
}

