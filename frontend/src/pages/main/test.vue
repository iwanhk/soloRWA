<template>
	<view class="page">
		<qiun-data-charts
			type="area"
			canvas-id="lineArea"
			:opts="opts"
			:chartData="chartData"
		/>
	</view>
</template>

<script setup>
import {ref, computed} from 'vue'
import {useConfigStore} from "@/store/config.ts";

const configStore = useConfigStore();
const fontScale = computed(()=>configStore.fontScale);

const chartData = ref({
	categories: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri'],
	series: [
		{
			name: '访问量',
			data: [30, 45, 28, 60, 50],
			color: '#3A86FF'
		}
	]
})

const opts = ref({
	background: 'transparent',
	xAxis: {
		disableGrid: true
	},
	yAxis: {
		gridColor: 'rgba(255,255,255,0.1)'
	},
})
</script>

<style>
.page {
	--font-scale:v-bind(fontScale);
	min-height: 100vh;
	padding: 24rpx;
	background: #020617;
}
</style>
