<script setup lang="ts">

import IconFont from "@/components/icon/IconFont.vue";
import {IconFontType} from "@/types/icons.ts";
import CalendarDate from "@/pages/order/components/CalendarDate.vue";
import PopupSelectDate from "@/pages/order/components/PopupSelectOrderProfitDate.vue";
import {formatTime, getNextTickPromise} from "@/library/Utility.ts";
import type {PopupInstance} from "@/types/popup.ts";
import {useOrderProfitStore} from "@/store/orderProfit.ts";
import dayjs from "dayjs";
import CompPrice from "@/components/CompPrice.vue";
import type {OrderProfitEntity} from "@/types/entity.ts";
import UniRouter from "@/library/UniRouter.ts";
import {transformCurrencyInfoToUsdt} from "@/library/format.ts";
import {onLoad} from "@dcloudio/uni-app";
import {useI18n} from 'vue-i18n';
import {useConfigStore} from "@/store/config.ts";

const { t } = useI18n()
const configStore = useConfigStore();
const fontScale = computed(()=>configStore.fontScale);

const props = defineProps<{
	orderId?: string
}>();

const orderProfitStore = useOrderProfitStore();
const orderId = computed(() => props.orderId ? parseInt(props.orderId) : undefined);

const selectedDate = ref<string>(formatTime(Date.now(),'YYYY-MM-DD'));
const popupSelectDate = ref<PopupInstance<string, [string]>>();

async function handleOpenDatePicker() {
	const date = await popupSelectDate.value?.open(selectedDate.value);
	if(date) {
		selectedDate.value = date;
	}
}

function showOrder(profitInfo: OrderProfitEntity) {
	UniRouter.to("/pages/order/detail", {id: profitInfo.orderId});
}

function selectDate(date: string) {
	selectedDate.value = date;
}

function clearOrderId() {
	UniRouter.redirect('/pages/order/profit');
}

const calendarInfo = computed(() => {
	let day = dayjs(selectedDate.value).startOf('month');
	let month = day.format("YYYY-MM");
	if(!orderProfitStore.dailyProfits[month]) return undefined;
	
	let endDate = dayjs(selectedDate.value).endOf('month').format("YYYY-MM-DD");
	const result: {date: string, profit: number|undefined}[] = [];
	while(true) {
		const date = day.format("YYYY-MM-DD");
		let dailyTotalIncomeInfo = orderProfitStore.dailyProfits[month]?.find(d => d.date === date)?.dailyTotalIncome;
		let dailyTotalIncome = dailyTotalIncomeInfo === undefined ? undefined : transformCurrencyInfoToUsdt(dailyTotalIncomeInfo);
		
		result.push({ date, profit: dailyTotalIncome });
		day = day.add(1, 'day');
		if(date === endDate) break;
	}
	return result;
});

const calendarDefaultCurrentIndex = computed(() => {
	if(!calendarInfo.value) return undefined;
	return calendarInfo.value.findIndex(item => item.date === selectedDate.value);
});

const scrollLeft = ref<number>(0);
watch(calendarDefaultCurrentIndex, async (value, oldValue) => {
	if(value && value !== oldValue) {
		await getNextTickPromise();
		scrollLeft.value = Math.max(value - 2, 0) * 49;
	}
}, {immediate: true});

const selectedOrderProfits = computed(() => {
	return orderProfitStore.orderProfits[selectedDate.value]?.map(item => {
		const orderTotalAmount = transformCurrencyInfoToUsdt({ coinCode: item.investmentCurrency, total: item.holdAmount });
		const profitTotal = transformCurrencyInfoToUsdt({ coinCode: item.earningCurrency, total: item.orderDailyIncome});
		
		return {
			...item,
			orderDailyIncome: profitTotal,
			incomeRate: profitTotal / orderTotalAmount * 100,
		};
	});
});

const selectedOrderProfitsLoading = computed(() => {
	return orderProfitStore.orderProfitsLoading[selectedDate.value];
});

onLoad((query) => {
	if(orderId.value) {
		orderProfitStore.setCurrentOrderId(orderId.value);
		watch(selectedDate, newDate => {
			orderProfitStore.loadDailyProfits(newDate);
			orderProfitStore.loadOrderProfits(newDate);
		}, { immediate: true});
	}
});

</script>

<template>
	<view class="page page-order-profile">
		<view class="order-filter-tip" v-if="orderId">
			<text>{{ t('pages.order.individualProfit.filterTip', { orderId }) }}</text>
			<text class="close-btn" @click="clearOrderId">×</text>
		</view>

		<view class="content-block">
			<view class="calendar">
				<view class="selector" @click="handleOpenDatePicker">
					<IconFont :type="IconFontType.CALENDAR" size="20" />
					{{ t('common.label.calendar') }}
				</view>
				<scroll-view scroll-x :scroll-left="scrollLeft">
					<view class="date-list">
						<CalendarDate @click="selectDate(info.date)" :class="{selected: selectedDate === info.date}" v-for="info of calendarInfo" :key="info.date" :date="info.date" :profit="info.profit" />
					</view>
				</scroll-view>
			</view>
		</view>
		
		<view class="content-block profit-list">
			<view class="loading-state" v-if="selectedOrderProfitsLoading">
				<up-loading-icon />
			</view>
			<template v-else-if="selectedOrderProfits && selectedOrderProfits.length">
				<view class="profit-header">
					<view class="label">{{ t('pages.order.individualProfit.profitDetails') }}</view>
					<view class="value">{{ t('pages.order.individualProfit.profitRate') }}</view>
				</view>
				<view class="profit-row" v-for="orderProfit of selectedOrderProfits" @click="showOrder(orderProfit)">
					<view class="label">
						<view class="project-name">{{ orderProfit.projectName }}</view>
						<view class="project-serial">{{ orderProfit.projectId.toString().padStart(5, "0") }}</view>
					</view>
					<view class="value">
						<view class="profit-amount">
							<CompPrice :price="orderProfit.orderDailyIncome" show-unit />
						</view>
						<view class="profit-rate">
							<CompPrice :price="orderProfit.incomeRate" :format="false" :show-unit="false" :precise="2">
								<template #suffix>%</template>
							</CompPrice>
						</view>
					</view>
				</view>
			</template>
			<view v-else class="empty-state">
				<text>{{ t('pages.order.individualProfit.noRecord') }}</text>
			</view>
		</view>
		
		<PopupSelectDate
			ref="popupSelectDate"
			:default-date="selectedDate"
			:orderId="orderId"
		/>
	</view>
</template>

<style scoped lang="scss">
.page{
	--font-scale:v-bind(fontScale);
}
.order-filter-tip{
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding: 4px 8px;
	margin-bottom: 12px;
	background-color: $bg-color-light;
	border-radius: 4px;
	color: $color-gray;
	@include fs(12);

	.close-btn{
		color: $color-gray;
		cursor: pointer;
		padding: 4px;
		line-height: 1;

		&:active{
			color: $color-text-white;
		}
	}
}

.calendar{
	padding: 10px 0;
	height: 50px;
	@include flex-row();
	.selector{
		+*{
			flex: 1;
			width: calc(100% - 40px);
		}
		flex: none;
		width: 22px;
		@include fs(10, 10);
		text-align: center;
		@include flex-column(center, space-around);
		padding: 2px 5px;
		&:after{
			content: '';
			display: inline-block;
			border-top: 6px solid $color-text-white;
			border-left: 5px solid transparent;
			border-right: 5px solid transparent;
		}
	}
	.date-list{
		@include flex-row();
		.calendar-date{
			flex: none;
			background-color: #3B3B3B;
			border-radius: 4px;
			padding: 4px 1px;
			width: 42px;
			height: 100%;
			@include flex-column(center, center);
			
			:deep(.weekday){
				@include fs(8);
			}
			
			:deep(.day){
				color:$color-gray;
			}
			:deep(.profit){
				@include fs(8);
				height: 1em;
				overflow: hidden;
			}
			
			&.selected{
				background-color: $color-gray;
				:deep(.day){
					color:$color-text-white;
				}
			}
			+.calendar-date{
				margin-left: 5px;
			}
		}
	}
}

.profit-header{
	@include flex-row(center, space-between);
	@include fs(13);
	padding-bottom: 10px;
	border-bottom: 1px solid $color-text-white;
	margin-bottom: 10px;
}
.profit-row{
	@include flex-row(center, space-between);
	@include fs(12);
	border-bottom: 1px solid #3e3e3e;
	padding-bottom: 10px;
	margin-bottom: 10px;
	
	&:last-child{
		border-bottom: 0;
		margin-bottom: 0;
		padding-bottom: 0;
	}
	.value{
		text-align: right;
	}
	.profit-amount{
		@include fs(16);
	}
	.profit-rate{
		@include fs(10);
	}
}
</style>

