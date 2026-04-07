import {Api} from "@/api";
import {ApiMethod, type ApiResult} from "@/types/api";
import {AssetType} from "@/types/enums.ts";


export interface AssetTypeCount {
	assetType: AssetType; // 资产类型
	count: number; // 数量
}

export class ApiGetAssetTypeCount extends Api<ApiResult<AssetTypeCount[]>>{
	constructor() {
		super('/project/info/asset-type-count', ApiMethod.GET);
	}
}

