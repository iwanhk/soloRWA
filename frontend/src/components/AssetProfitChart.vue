<script setup lang="ts">
import type {DailyProfitEntity, OrderEntity, ProjectEntity} from "@/types/entity.ts";
import type {LabelOptions} from "@/types/common.ts";
import Switcher from "@/components/Switcher.vue";
import {useTabListManager} from "@/composable/useTabListManager.ts";
import {ApiNonPagedListHandlerFactory, EmptyListHandler} from "@/library/PagedListHandler.ts";
import {ApiAssetIncomeCalendar} from "@/api/asset/ApiAssetIncomeCalendar.ts";
import dayjs from "dayjs";
import timer from "@/library/Timer.ts";
import {$bgColor, $themeColor} from "@/library/GlobalVars.ts";
import {transformCurrencyInfoToUsdt} from "@/library/format.ts";
import {useI18n} from "vue-i18n";

const props=defineProps<{
	project:ProjectEntity,
	order?:OrderEntity,
}>()

function getChartRange(period:number){
	const lockEndTime = props.project.lockEndTime?dayjs(props.project.lockEndTime).valueOf(): Date.now();
	const lockStartTime = props.project.lockStartTime?dayjs(props.project.lockStartTime).valueOf(): Date.now();
	const endTime = Math.min(Date.now() - 86400000 + timer.diff.value, lockEndTime);
	let startTime = endTime - period * 86400000;
	if(period === 0){
		startTime = lockStartTime;
	}
	return {startTime, endTime, days: Math.floor(endTime - startTime) / 86400000};
}

const chartData = computed(()=>{
	let categories:string[] = [];
	let values:number[] = [];
	const {endTime, days} = getChartRange(selectedPeriod.value)
	
	if(tabListManager.records.value){
		const day = dayjs(endTime);
		for(let i=1; i<=days; i++ ){
			const date = day.subtract(i, 'day').format("YYYY-MM-DD");
			categories.push(date);
			const value = tabListManager.records.value.find(item=>item.date === date)?.dailyTotalIncome;
			values.push(value? transformCurrencyInfoToUsdt(value): 0);
		}
	}
	else{
		return undefined
	}

	if(!values.length){
		return undefined;
	}
	
	return {
		categories: categories.reverse(),
		series: [
			{
				name: t('components.assetProfitChart.income'),
				data: values.reverse(),
				textSize: 10,
				textColor: '#FBFBFB',
			}
		]
	}
})

const chartOpts = computed(()=>{
	return {
		color: ['#DE6224'],
		padding: [20, 30, 0, 10],
		enableScroll: true,
		xAxis: {
			disableGrid: true,
			boundaryGap: true,
			fontSize: 10,
			labelCount: 2,
			//rotateLabel: true,
			itemCount: 100,
			scrollShow:true,//新增是否显示滚动条，默认false
			scrollAlign:'right',//滚动条初始位置
			scrollBackgroundColor:'#4A4A4A',//默认为 #EFEBEF
			scrollColor:$themeColor,//默认为 #A6A6A6 
		},
		yAxis: {
			disableGrid: true,
			data:[
				{
					fontSize: 10,
				}
			],
		},
		dataLabel: false,
		dataPointShape: false,
		legend: {
			show: false
		},
		extra: {
			area: {
				type: 'curve',
				opacity: 0.75,
				gradient: true,
			}
		}
	}
})

function onChartClick(){
	console.log('chart click',arguments)
}

const { t } = useI18n()

const durationOptions: LabelOptions = [
	{label: t('components.assetProfitChart.period7d'), value: 7},
	{label: t('components.assetProfitChart.period1m'), value: 30},
	{label: t('components.assetProfitChart.period3m'), value: 90},
	{label: t('components.assetProfitChart.period6m'), value: 180},
	{label: t('components.assetProfitChart.periodAll'), value: 0},
]

const selectedPeriod = ref<number>(7);

const tabListManager = useTabListManager<DailyProfitEntity>()
tabListManager.setProvide((identifier)=>{
	const period = parseInt(identifier);

	if(props.order || props.project) {
		const {startTime, endTime} = getChartRange(period)
		let endDate = dayjs(endTime);
		let startDate = dayjs(startTime);

		return ApiNonPagedListHandlerFactory(ApiAssetIncomeCalendar, {
			orderId: props.order?.id,
			projectId: props.order? undefined: props.project?.projectId,
			startDate: startDate.format("YYYY-MM-DD"),
			endDate: endDate.format("YYYY-MM-DD"),
		})
	}
	else{
		return EmptyListHandler;
	}
})
//@ts-ignore
tabListManager.add('empty', undefined);
watch(selectedPeriod, (newDuration)=>{
	if(props.order || props.project){
		tabListManager.show(newDuration.toString());
	}
},{immediate:true})

</script>

<template>
	<view class="asset-profile-chart">
		<qiun-data-charts
			v-show="chartData"
			ref="chartRef"
			type="area"
			:chartData="chartData"
			:opts="chartOpts"
			width="100%"
			height="260px"
			:ontouch="true"
			:background="$bgColor"
			@click="onChartClick">
		</qiun-data-charts>
		<view class="actions">
			<Switcher v-model="selectedPeriod" :options="durationOptions" display="button"></Switcher>
		</view>
	</view>
</template>

<style scoped lang="scss">
.actions {
	margin-top: 10px;
	@include fs(10);
	position: relative;
	z-index: 1;

	.switcher.switcher-display-button{
		height: 26px;
		:deep(.switcher-item){
			background: #4A4A4A;
			border-radius: 5px;
			@include fw(medium);
			&.active{
				background: #ffffff;
				.text {
					color: $bg-color;
				}
			}
		}
	}
}
</style>