import {Api} from "@/api";
import {ApiMethod, type ApiResult} from "@/types/api";
import type {OrderEntity} from "@/types/entity";

export class ApiGetOrderDetail extends Api<ApiResult<OrderEntity>>{
	constructor(id: number) {
		super('/project/project-order/detail', ApiMethod.GET, {id});
	}
}

