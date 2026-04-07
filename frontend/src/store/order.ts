import {defineStore} from "pinia";
import type {DailyProfitEntity, OrderProfitEntity} from "@/types/entity.ts";
import {ApiAssetIncomeCalendar} from "@/api/asset/ApiAssetIncomeCalendar.ts";
import dayjs from "dayjs";
import {ApiAssetDailyStatisticList} from "@/api/asset/ApiAssetDailyStatisticList.ts";

export const useOrderStore = defineStore('order', ()=>{
	const dailyProfits = reactive<Record<string, DailyProfitEntity[]>>({});
	const orderProfits = reactive<Record<string, OrderProfitEntity[]>>({})
	
	const dailyProfitsLoading=reactive<Record<string, boolean>>({});
	const orderProfitsLoading = reactive<Record<string, boolean>>({});
	
	async function loadDailyProfits(month:string, preload = false ){
		month = dayjs(month).format("YYYY-MM");
		
		if(dailyProfitsLoading[month])return;
		dailyProfitsLoading[month] = true;

		try {
			if (!dailyProfits[month]) {
				const api = new ApiAssetIncomeCalendar({
					startDate: dayjs(month).startOf('month').format('YYYY-MM-DD'),
					endDate: dayjs(month).endOf('month').format('YYYY-MM-DD'),
				});
				const {data} = await api.call();
				dailyProfits[month] = data;
			}
		}
		finally {
			dailyProfitsLoading[month] = false;
		}
		if(preload){
			loadDailyProfits(dayjs(month).subtract(1, 'month').format('YYYY-MM'), false)
			loadDailyProfits(dayjs(month).add(1, 'month').format('YYYY-MM'), false)
		}
	}

	async function loadOrderProfits(date:string, preload = false ){
		if(orderProfitsLoading[date])return;
		orderProfitsLoading[date] = true;
		try {
			if (!orderProfits[date]) {
				const api = new ApiAssetDailyStatisticList({
					date,
				});
				const {data} = await api.call();
				orderProfits[date] = data;
			}
		}
		finally {
			orderProfitsLoading[date] = false;
		}

		if(preload){
			loadOrderProfits(dayjs(date).subtract(1, 'day').format('YYYY-MM'), false)
			loadOrderProfits(dayjs(date).add(1, 'day').format('YYYY-MM'), false)
		}
	}
	
	return {
		dailyProfits,
		orderProfits,
		orderProfitsLoading,
		dailyProfitsLoading,
		loadDailyProfits,
		loadOrderProfits,
	}
})