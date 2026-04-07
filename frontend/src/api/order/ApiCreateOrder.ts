import {Api} from "@/api";
import {ApiMethod, type ApiResult} from "@/types/api";
import type {ProjectOrderEntity} from "@/types/entity";

export interface ApiCreateOrderParams {
	projectId: number; // 项目ID
	addressId?: number; // 钱包地址
	quantity: number; // 购买数量
}

export class ApiCreateOrder extends Api<ApiResult<ProjectOrderEntity>>{
	constructor(params: ApiCreateOrderParams) {
		super('/project/project-order/create', ApiMethod.POST, params);
	}
}

