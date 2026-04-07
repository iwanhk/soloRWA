import {Api} from "@/api";
import {API_ERROR_CODE, ApiMethod, type ApiResult} from "@/types/api";
import type {DailyProfitEntity} from "@/types/entity.ts";
import dayjs from "dayjs";

export interface ApiAssetIncomeCalendarParams {
	startDate: string;
	endDate: string;
	projectId?: number;
	orderId?: number;
}

export class ApiAssetIncomeCalendar extends Api<ApiResult<DailyProfitEntity[]>>{
	constructor(params: ApiAssetIncomeCalendarParams) {
		super('/project/order-balance/income-calendar', ApiMethod.GET, params);
	}

	async call(): Promise<ApiResult<DailyProfitEntity[]>> {
		const result = await super.call();
		if(result.code === API_ERROR_CODE.OK){
			result.data = result.data.map(
				item=>{
					return {
						...item,
						date: dayjs()
							.set("year", Number(item.date[0]))
							.set('month',Number(item.date[1]) - 1)
							.set('date', Number(item.date[2]))
							.format('YYYY-MM-DD')
					}
				}
			)
		}
		return result
	}
}

