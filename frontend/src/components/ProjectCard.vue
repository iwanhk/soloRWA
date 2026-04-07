<script setup lang="ts">
import CompImage from "@/components/CompImage.vue";
import type {SimpleProjectEntity} from "@/types/entity";
import UniRouter from "@/library/UniRouter";
import {formatPriceInfo, transformCurrency} from "@/library/format.ts";
import {Currency} from "@/types/enums.ts";
import {useI18n} from "vue-i18n";
import {dictKey} from "@/library/dict.ts";
import {currencyOptions} from "@/types/options.ts";

const props = defineProps<{
	project: SimpleProjectEntity;
}>();

const { t } = useI18n()

const coverImage = computed(()=>{
	return props.project.projectImageUrls?.split(',')[0];
})
const serial = computed(()=>{
	return props.project.projectId.toString().padStart(5, "0")
})
const priceInfo = computed(()=>{
	return formatPriceInfo(transformCurrency(props.project.issueUnitPrice, Currency.USDT, props.project.investmentCurrency));
})

function goDetail(){
	UniRouter.to('/pages/project/detail', { id: props.project.projectId });
}

</script>

<template>
	<view class="project-card" @click="goDetail">
		<view class="image">
			<CompImage 
				:src="coverImage" 
				mode="aspectFill"
				uploaded
			></CompImage>
		</view>
		<view class="info">
			<view class="info-item">
				<view class="label">{{ t('components.projectCard.remainingQuantity') }}</view>
				<view class="value">{{ project.remainingQuantity }}</view>
			</view>
			<view class="info-item">
				<view class="label">{{ t('components.projectCard.expectedReturn') }}</view>
				<view class="value">{{ project.expectedAnnualReturn }}%</view>
			</view>
			<view class="info-item">
				<view class="label">{{ t('components.projectCard.issuePrice') }}</view>
				<view class="value">
					<text style="margin-right: 5px">{{dictKey(currencyOptions, priceInfo[1], 'unit')}}</text>
					<text class="i-block">{{priceInfo[0]}}</text>
				</view>
			</view>
		</view>
		<view class="title">
			{{ project.projectName }}
			<text class="serial">{{ serial }}</text>
		</view>
	</view>
</template>

<style scoped lang="scss">
.project-card{
	.image{
		padding-bottom: 55.4%;
		position: relative;
		.comp-image{
			position: absolute;
			left: 0;
			top: 0;
			width: 100%;
			height: 100%;
			border-radius: 10px;
			overflow: hidden;
		}
	}
	.info{
		margin-top: 6px;
		@include flex-row();
		.info-item{
			padding-left: 5px;
			padding-right: 5px;
			flex: 1;
			@include flex-column(center,center);
			flex-direction: column-reverse;
			.label{
				color: $color-gray;
				@include fs(11);
				@include en-break;
				text-align: center;
			}
			.value{
				color: $theme-color;
				@include fs(17);
				text-align: center;
			}
			+.info-item{
				border-left: 1px solid #fff;
			}
		}
	}
	.title{
		margin-top: 6px;
		@include fs(14);
		color: $color-text-white;
		.serial{
			display: inline-block;
			background: $theme-color;
			color: $color-text-white;
			@include fs(8);
			border-radius: 4px;
			padding: 1px 4px;
		}
	}
}
</style>

