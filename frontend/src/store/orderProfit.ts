import {defineStore} from "pinia";
import type {DailyProfitEntity, OrderProfitEntity} from "@/types/entity.ts";
import {ApiAssetIncomeCalendar} from "@/api/asset/ApiAssetIncomeCalendar.ts";
import dayjs from "dayjs";
import {ApiAssetDailyStatisticList} from "@/api/asset/ApiAssetDailyStatisticList.ts";

export const useOrderProfitStore = defineStore('orderProfit', () => {
	// 按 orderId 缓存数据
	const dailyProfitsByOrderId = reactive<Record<number, Record<string, DailyProfitEntity[]>>>({});
	const orderProfitsByOrderId = reactive<Record<number, Record<string, OrderProfitEntity[]>>>({});
	
	const dailyProfitsLoadingByOrderId = reactive<Record<number, Record<string, boolean>>>({});
	const orderProfitsLoadingByOrderId = reactive<Record<number, Record<string, boolean>>>({});
	
	// 当前选中的 orderId
	const currentOrderId = ref<number | undefined>(undefined);
	
	/**
	 * 设置当前订单ID
	 */
	function setCurrentOrderId(orderId: number | undefined) {
		currentOrderId.value = orderId;
	}
	
	/**
	 * 获取当前订单的日常收益数据
	 */
	const dailyProfits = computed(() => {
		if (!currentOrderId.value) return {};
		return dailyProfitsByOrderId[currentOrderId.value] || {};
	});
	
	/**
	 * 获取当前订单的订单收益数据
	 */
	const orderProfits = computed(() => {
		if (!currentOrderId.value) return {};
		return orderProfitsByOrderId[currentOrderId.value] || {};
	});
	
	/**
	 * 获取当前订单的日常收益加载状态
	 */
	const dailyProfitsLoading = computed(() => {
		if (!currentOrderId.value) return {};
		return dailyProfitsLoadingByOrderId[currentOrderId.value] || {};
	});
	
	/**
	 * 获取当前订单的订单收益加载状态
	 */
	const orderProfitsLoading = computed(() => {
		if (!currentOrderId.value) return {};
		return orderProfitsLoadingByOrderId[currentOrderId.value] || {};
	});
	
	async function loadDailyProfits(month: string, preload = false) {
		if (!currentOrderId.value) return;

		month = dayjs(month).format("YYYY-MM");
		const orderId = currentOrderId.value;

		// 初始化该 orderId 的数据结构
		if (!dailyProfitsByOrderId[orderId]) {
			dailyProfitsByOrderId[orderId] = {};
		}
		if (!dailyProfitsLoadingByOrderId[orderId]) {
			dailyProfitsLoadingByOrderId[orderId] = {};
		}

		if (dailyProfitsLoadingByOrderId[orderId][month]) return;
		dailyProfitsLoadingByOrderId[orderId][month] = true;

		try {
			if (!dailyProfitsByOrderId[orderId][month]) {
				const api = new ApiAssetIncomeCalendar({
					startDate: dayjs(month).startOf('month').format('YYYY-MM-DD'),
					endDate: dayjs(month).endOf('month').format('YYYY-MM-DD'),
					orderId: orderId,
				});
				const {data} = await api.call();
				dailyProfitsByOrderId[orderId][month] = data;
			}
		}
		finally {
			dailyProfitsLoadingByOrderId[orderId][month] = false;
		}

		if (preload) {
			loadDailyProfits(dayjs(month).subtract(1, 'month').format('YYYY-MM'), false);
			loadDailyProfits(dayjs(month).add(1, 'month').format('YYYY-MM'), false);
		}
	}

	async function loadOrderProfits(date: string, preload = false) {
		if (!currentOrderId.value) return;

		const orderId = currentOrderId.value;

		// 初始化该 orderId 的数据结构
		if (!orderProfitsByOrderId[orderId]) {
			orderProfitsByOrderId[orderId] = {};
		}
		if (!orderProfitsLoadingByOrderId[orderId]) {
			orderProfitsLoadingByOrderId[orderId] = {};
		}

		if (orderProfitsLoadingByOrderId[orderId][date]) return;
		orderProfitsLoadingByOrderId[orderId][date] = true;

		try {
			if (!orderProfitsByOrderId[orderId][date]) {
				const api = new ApiAssetDailyStatisticList({
					date,
					orderId: orderId,
				});
				const {data} = await api.call();
				orderProfitsByOrderId[orderId][date] = data;
			}
		}
		finally {
			orderProfitsLoadingByOrderId[orderId][date] = false;
		}

		if (preload) {
			loadOrderProfits(dayjs(date).subtract(1, 'day').format('YYYY-MM-DD'), false);
			loadOrderProfits(dayjs(date).add(1, 'day').format('YYYY-MM-DD'), false);
		}
	}
	
	return {
		currentOrderId,
		dailyProfits,
		orderProfits,
		orderProfitsLoading,
		dailyProfitsLoading,
		setCurrentOrderId,
		loadDailyProfits,
		loadOrderProfits,
	};
});

