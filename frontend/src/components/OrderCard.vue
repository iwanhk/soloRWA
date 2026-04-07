<script setup lang="ts">

import CompImage from "@/components/CompImage.vue";
import ENV from "@/library/env.ts";
import CenterEllipseText from "@/components/CenterEllipseText.vue";
import type {OrderListEntity} from "@/types/entity.ts";
import {formatPrice, transformCurrency} from "@/library/format.ts";
import {formatImageUrl, formatTime, previewImage} from "@/library/Utility.ts";
import {dictLabel} from "@/library/dict.ts";
import {orderStatusOptions, payTypeOptions} from "@/types/options.ts";
import {Currency, OrderStatus} from "@/types/enums.ts";
import {useProjectDetail} from "@/composable/useProjectDetail.ts";
import UniRouter from "@/library/UniRouter.ts";
import {ConfirmDelay} from "@/library/limits.ts";
import {useI18n} from "vue-i18n";

const props = defineProps<{
	order:OrderListEntity
}>()
const {project, load:loadProject} = useProjectDetail()

const { t } = useI18n();

onMounted(()=>{
	loadProject(props.order.projectId);
})

const projectImages = computed(()=>{
	return project.value?.projectImageUrls?.split(',')??[];
})

function previewPayVoucher(){
	previewImage(formatImageUrl(props.order.payVoucherUrl!));
}

function goDetail(){
	switch(props.order.orderStatus){
		case OrderStatus.PENDING_PAYMENT:
			UniRouter.to('/pages/order/pay', {id: props.order.id});
			break;
		case OrderStatus.CANCELLED:
			break;
		default:
			UniRouter.to('/pages/order/detail', {id: props.order.id});
	}
}
const estimatedPurchaseTime = computed(()=>{
	return props.order.createTime + ConfirmDelay;
})

</script>

<template>
	<view class="order-card" @click="goDetail">
		<view class="header">
			<view class="order-number">{{ t('components.orderCard.orderNumber') }}{{ props.order.orderNo }}</view>
			<view class="order-status" :class="`status-${props.order.orderStatus}`">{{ dictLabel(orderStatusOptions, props.order.orderStatus) }}</view>
		</view>
		<view class="basic-info">
			<view class="order-image">
				<view class="image-wrap">
					<CompImage uploaded mode="aspectFill" :src="projectImages[0]" />
				</view>
			</view>
			<view class="project-name">
				{{ t('components.orderCard.projectName') }}{{ props.order.projectName }}
			</view>
		</view>
		<view class="order-info">
			<view class="info-item">
				<text class="label">{{ t('components.orderCard.subscribeQuantity') }}</text>
				<text class="value">{{ props.order.subscribeQuantity }}</text>
			</view>
			<view class="info-item">
				<text class="label">{{ t('components.orderCard.amount') }}</text>
				<text class="value">
					{{formatPrice(transformCurrency(props.order.totalAmount, Currency.USDT, props.order.investmentCurrency))}}
				</text>
			</view>
			<view class="info-item">
				<text class="label">{{ t('components.orderCard.estimatedTime') }}</text>
				<text class="value">{{ formatTime(estimatedPurchaseTime, 'YYYY-MM-DD') }}</text>
			</view>
			<view class="info-item" v-if="ENV.foreign && props.order.chainAddress">
				<text class="label">{{ t('components.orderCard.chainAddress') }}</text>
				<text class="value"><CenterEllipseText :text="props.order.chainAddress"></CenterEllipseText></text>
			</view>
		</view>
		<view class="pay-info">
			<view class="info-item" v-if="props.order.payType">
				<text class="label">{{ t('components.orderCard.payType') }}</text>
				<text class="value">{{ dictLabel(payTypeOptions, props.order.payType) }}</text>
			</view>
			<view class="info-item">
				<text class="label">{{ t('components.orderCard.payee') }}</text>
				<text class="value">{{ order.bankAccountName }}</text>
			</view>
			<view class="info-item">
				<text class="label">{{ t('components.orderCard.account') }}</text>
				<text class="value">{{ order.bankAccount }}</text>
			</view>
			<view class="info-item">
				<text class="label">{{ t('common.payment.bankName') }}</text>
				<text class="value">{{ order.bankName }}</text>
			</view>
			<view class="info-item" v-if="props.order.payVoucherUrl">
				<text class="label">{{ t('components.orderCard.payVoucher') }}</text>
				<text class="value"><text class="view-link" @click.stop="previewPayVoucher">{{ t('components.orderCard.view') }}</text></text>
			</view>
			<view class="info-item" v-if="props.order.contractNo">
				<text class="label">{{ t('components.orderCard.contractNo') }}</text>
				<text class="value">{{ props.order.contractNo }}</text>
			</view>
			<view class="info-item" v-if="props.order.auditRemark">
				<text class="label">{{ t('components.orderCard.auditRemark') }}</text>
				<text class="value">{{ props.order.auditRemark }}</text>
			</view>
		</view>
		<view class="footer">
			<view class="order-time">
				{{ t('components.orderCard.submitTime') }}{{ formatTime(props.order.applyDate, 'YYYY-MM-DD HH:mm:ss') }}
			</view>
		</view>
	</view>
</template>

<style scoped lang="scss">
.order-card{
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
	.order-info, .pay-info{
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
	
	.order-number{
		color: $color-text-white;
	}
	.order-status{
		padding: 1px 5px;
		background-color: $color-text-white;
		color: $color-text-black;
		@include fs(12);
		border-radius: 6px;
		flex: none;
		margin-left: 0.5em;
		
		&.status-1{
			
		}
		&.status-2{
			background-color: $theme-color;
			color: $color-text-white;
		}
		&.status-3{
			background-color: $color-red;
			color: $color-text-white;
		}
		&.status-4{
			background-color: $color-gray;
			color: $color-text-white;
		}
	}
	.order-image{
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
	.order-time{
		
	}
}
</style>