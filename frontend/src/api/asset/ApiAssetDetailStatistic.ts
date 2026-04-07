import {Api} from "@/api";
import {ApiMethod, type ApiResult} from "@/types/api";
import type {CurrencyInfo} from "@/types/entity.ts";

export interface ApiAssetStatisticDetailParams {
	projectId?: number;
	orderId?: number;
}

export interface AssetDetailStatistic {
	totalAssets: CurrencyInfo[]; // 总资产
	totalIncome: CurrencyInfo[]; // 累计总收益
	todayIncome: CurrencyInfo[]; // 今日收益
	withdrawableBalance:number; //可分红金额
}

export class ApiAssetDetailStatistic extends Api<ApiResult<AssetDetailStatistic>>{
	constructor(params: ApiAssetStatisticDetailParams) {
		super('/project/order-balance/asset-detail', ApiMethod.GET, params);
	}
}

