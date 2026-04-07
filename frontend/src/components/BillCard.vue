<script setup lang="ts">

import CompImage from "@/components/CompImage.vue";
import type {BillEntity} from "@/types/entity.ts";
import {formatPrice} from "@/library/format.ts";
import {formatTime} from "@/library/Utility.ts";
import {dictLabel} from "@/library/dict.ts";
import {useProjectDetail} from "@/composable/useProjectDetail.ts";
import {auditStatusOptions, billTypeOptions} from "@/types/options.ts";
import {useI18n} from "vue-i18n";
import {BillType} from "@/types/enums.ts";

const props = defineProps<{
	bill:BillEntity
}>()

const {project, load:loadProject} = useProjectDetail()
const { t } = useI18n()

onMounted(()=>{
	loadProject(props.bill.projectId);
})

const projectImages = computed(()=>{
	return project.value?.projectImageUrls?.split(',')??[];
})

</script>

<template>
	<view class="bill-card">
		<view class="header">
			<view class="bill-number">{{ t('components.billCard.billNumber') }}{{ props.bill.billNo }}</view>
			<view class="bill-status" :class="`status-${props.bill.auditStatus}`">{{ dictLabel(auditStatusOptions,props.bill.auditStatus) }}</view>
		</view>
		<view class="basic-info">
			<view class="bill-image">
				<view class="image-wrap">
					<CompImage uploaded mode="aspectFill" :src="projectImages[0]" />
				</view>
			</view>
			<view class="project-name">
				{{ t('components.billCard.projectName') }}{{ props.bill.projectName }}
			</view>
		</view>
		<view class="bill-info">
			<view class="info-item">
				<text class="label">{{ t('components.billCard.billType') }}</text>
				<text class="value">{{ dictLabel(billTypeOptions, props.bill.billType) }}</text>
			</view>
			<view class="info-item" v-if="props.bill.billType !== BillType.DIVIDEND">
				<text class="label">{{ t('components.billCard.quantity') }}</text>
				<text class="value">{{ props.bill.quantity }}</text>
			</view>
			<view class="info-item">
				<text class="label">{{ t('components.billCard.amount') }}</text>
				<text class="value">{{ props.bill.billAmount }} {{props.bill.billCoin}}</text>
			</view>
			<view class="info-item">
					<text class="label">{{ t('components.billCard.actualAmount') }}</text>
				<text class="value">{{ props.bill.actualAmount }} {{props.bill.actualCoin}}</text>
			</view>
			<view class="info-item">
				<text class="label">{{ t('components.billCard.auditTime') }}</text>
				<text class="value">{{ formatTime(props.bill.auditTime, 'YYYY-MM-DD') }}</text>
			</view>
			<view class="info-item" v-if="props.bill.auditRemark">
				<text class="label">{{ t('components.billCard.auditRemark') }}</text>
				<text class="value">{{ props.bill.auditRemark }}</text>
			</view>
		</view>
		<view class="footer">
			<view class="bill-time">
				{{ t('components.billCard.applyTime') }}{{ formatTime(props.bill.applyTime, 'YYYY-MM-DD HH:mm:ss') }}
			</view>
		</view>
	</view>
</template>

<style scoped lang="scss">
.bill-card{
	background-color: $bg-color-light;
	border-radius: 10px;
	padding: 10px;
	color: $color-gray;
	@include fs(10);
	
	.header{
		@include flex-row(center, space-between);
		margin-bottom: 10px;
	}
	.basic-info{
		margin-bottom: 10px;
		@include flex-row(center);
	}
	.bill-image{
		flex: 1;
		margin-right: 10px;
		.image-wrap{
			border-radius: 10px;
			overflow: hidden;
			position: relative;
			padding-bottom: 66.66%;
			.comp-image{
				position: absolute;
				left: 0;
				top: 0;
				width: 100%;
				height: 100%;
			}
		}
	}
	.project-name{
		width: 60%;
		flex: none;
	}
	.bill-info{
		margin-bottom: 10px;
		padding-bottom: 10px;
		border-bottom: 1px solid #3a3a3a;
		
		.info-item{
			@include flex-row(center);
		}
		.value{
			flex: 1;
		}
	}
	
	.bill-number{
		color: $color-text-white;
	}
	.bill-status{
		padding: 1px 5px;
		background-color: $color-text-white;
		color: $color-text-black;
		@include fs(12);
		border-radius: 6px;
		
		&.status-1{
			
		}
		&.status-2{
			background-color: $theme-color;
			color: $color-text-white;
		}
		&.status-3{
			background-color: #ff6b6b;
			color: $color-text-white;
		}
	}
	.bill-time{
		
	}
}
</style>

