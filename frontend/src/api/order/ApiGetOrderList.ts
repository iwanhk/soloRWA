import {Api} from "@/api";
import {type ApiListParams, type ApiListResult, ApiMethod} from "@/types/api";
import type {OrderListEntity} from "@/types/entity";

export interface ApiGetOrderListParams extends ApiListParams{
	orderStatus?: number;
}

export class ApiGetOrderList extends Api<ApiListResult<OrderListEntity>>{
	constructor(params: ApiGetOrderListParams) {
		super('/project/project-order/list', ApiMethod.GET, params);
	}
}

