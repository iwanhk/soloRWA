<script setup lang="ts">
import CompPopup from "@/components/popup/CompPopup.vue";
import type {PopupInstance} from "@/types/popup.ts";
import {defaultPopupExpose} from "@/library/PopupManager.ts";
import {useOrderStore} from "@/store/order.ts";
import {useOrderProfitStore} from "@/store/orderProfit.ts";
import dayjs from "dayjs";
import {formatPrice, transformCurrencyInfoToUsdt} from "@/library/format.ts";
import {dictKey, dictLabel} from "@/library/dict.ts";
import {currencyOptions} from "@/types/options.ts";
import {useConfigStore} from "@/store/config.ts";
import {Currency} from "@/types/enums.ts";
import {useI18n} from 'vue-i18n';

const props = defineProps<{
	orderId?: number
}>();

const orderStore = useOrderStore();
const orderProfitStore = useOrderProfitStore();
const configStore = useConfigStore();
const { t } = useI18n();

// 根据是否有 orderId 选择使用哪个 store
const currentStore = computed(() => {
	if (props.orderId !== undefined) {
		orderProfitStore.setCurrentOrderId(props.orderId);
		return orderProfitStore;
	}
	return orderStore;
});

const popup = ref<PopupInstance>();
defineExpose(defaultPopupExpose(popup, {
	open(date:string){
		selectedDate.value = date;
	}
}))
const calendar = ref();
const selectedDate = ref<string>();

const handleConfirm = () => {
	if (selectedDate.value) {
		popup.value?.result(selectedDate.value);
	}
}

const handleDateChange = (e: any) => {
	selectedDate.value = e.fulldate;
}

const selectedMonth = ref<string>();
watch(selectedDate,(newDate)=>{
	selectedMonth.value = dayjs(newDate).format("YYYY-MM");
})

function handleMonthChange(info:{month:number, year:number}){
	selectedMonth.value = dayjs().set('year', info.year).set("month", info.month - 1).format("YYYY-MM")
	selectedDate.value = selectedMonth.value + '-01';
}
watch(selectedMonth, month=>{if(month)currentStore.value.loadDailyProfits(month, true)});

const selectedData = computed(()=>{
	const month = dayjs(selectedDate.value).format("YYYY-MM");
	const dailyProfits = currentStore.value.dailyProfits[month];
	if(!dailyProfits)return [];

	return dailyProfits.map(profitInfo=> {
		const dailyTotalIncome = transformCurrencyInfoToUsdt(profitInfo.dailyTotalIncome);
		return {
			date: profitInfo.date,
			info:
				configStore.currency === Currency.USDT?
					[
						formatPrice(profitInfo.dailyTotalIncome, false),
						dictKey(currencyOptions, configStore.currency, 'unit'),
					]
					:[
						dictKey(currencyOptions, configStore.currency, 'unit'),
						formatPrice(profitInfo.dailyTotalIncome, false)
					],
			class: dailyTotalIncome > 0 ? 'rate-up' : (dailyTotalIncome < 0 ? 'rate-up' : undefined),
		}
	});
})





</script>

<template>
	<CompPopup
		ref="popup"
		position="bottom"
		closable
		hideClose
	>
		<template #actions>
			<up-button type="warning" shape="circle" @click="handleConfirm">{{ t('common.button.confirm') }}</up-button>
		</template>
		<view class="popup-select-date">
			<uni-calendar
				ref="calendar"
				:selected="selectedData"
				:date="selectedDate"
				@change="handleDateChange"
				@monthSwitch="handleMonthChange"
			/>
		</view>
	</CompPopup>
</template>

<style scoped lang="scss">
.popup-select-date {
	padding: 10px 0;
}

.btn-cancel,
.btn-confirm {
	flex: 1;
	padding: 10px 20px;
	border: none;
	border-radius: 4px;
	@include fs(14);
	font-weight: 500;
	cursor: pointer;
	transition: all 0.3s ease;
}

.btn-cancel {
	background-color: #f0f0f0;
	color: #333;

	&:active {
		background-color: #e0e0e0;
	}
}

.btn-confirm {
	background-color: #007aff;
	color: #fff;

	&:active {
		background-color: #0051d5;
	}
}

.uni-calendar{
	:deep(.uni-calendar-item__weeks-box-item){
		width: auto;
	}
	:deep(.uni-calendar-item__weeks-box-circle){
		display: none;
	}
	:deep(.uni-calendar-item--extra){
		@include fs(10);
		margin-left: 2px;
		margin-right: 2px;
		word-break: break-all;
		word-wrap: break-word;
		text-align: center;
		&.rate-up{
			color: $color-red;
		}
		&.rate-down{
			color: $color-green;
		}
		color: $color-gray;
	}
	:deep(.uni-calendar-item--extra-item){
		display: inline-block;
	}
}
</style>

