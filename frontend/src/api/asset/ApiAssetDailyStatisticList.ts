import {Api} from "@/api";
import {API_ERROR_CODE, ApiMethod, type ApiResult} from "@/types/api";
import type {OrderProfitEntity} from "@/types/entity.ts";
import dayjs from "dayjs";

export interface ApiAssetDailyStatisticListParams {
	date: string;
	projectId?: number;
	orderId?: number;
}

export class ApiAssetDailyStatisticList extends Api<ApiResult<OrderProfitEntity[]>>{
	constructor(params: ApiAssetDailyStatisticListParams) {
		super('/project/order-balance/daily-income-detail', ApiMethod.GET, params);
	}

	async call(): Promise<ApiResult<OrderProfitEntity[]>> {
		const result = await super.call();
		if(result.code === API_ERROR_CODE.OK){
			result.data = result.data.map(
				item=>{
					return {
						...item,
						incomeDate: dayjs()
						.set("year", Number(item.incomeDate[0]))
						.set('month',Number(item.incomeDate[1]) - 1)
						.set('date', Number(item.incomeDate[2]))
						.format('YYYY-MM-DD')
					}
				}
			)
		}
		return result
	}
}

