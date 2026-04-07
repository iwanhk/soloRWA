<script setup lang="ts">

import CompPrice from "@/components/CompPrice.vue";
import {formatTime} from "@/library/Utility.ts";

const props = defineProps<{
	date: string,
	profit?: number,
}>()



</script>

<template>
	<view class="calendar-date">
		<view class="weekday">{{ formatTime(date, 'ddd') }}</view>
		<view class="day">{{ formatTime(date, 'D') }}</view>
		<view class="profit">
			<CompPrice :price="profit" show-unit />
		</view>
	</view>
</template>

<style scoped lang="scss">

</style>