import {Api} from "@/api";
import {type ApiListParams, type ApiListResult, ApiMethod} from "@/types/api";
import type {AssetEntity} from "@/types/entity";

export interface ApiGetAssetListParams extends ApiListParams {
	userId?: number;
	projectId?: number;
}

export class ApiGetAssetList extends Api<ApiListResult<AssetEntity>>{
	constructor(params: ApiGetAssetListParams) {
		super('/project/order-balance/list', ApiMethod.GET, params);
	}
}

