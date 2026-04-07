<script setup lang="ts">
import {ApiRequestDividend} from "@/api/order/ApiRequestDividend.ts";
import {ShowInfo} from "@/library/ShowInfo.ts";
import UniRouter from "@/library/UniRouter.ts";
import {useOrderDetail} from "@/composable/useOrderDetail.ts";
import {onLoad} from "@dcloudio/uni-app";
import {useProjectDetail} from "@/composable/useProjectDetail.ts";
import {formatPrice, transformCurrencyInfoToUsdt} from "@/library/format.ts";
import PopupInputCode from "@/components/popup/PopupInputCode.vue";
import {CodeScene} from "@/api/generic/ApiSendSmsCode.ts";
import type {PopupInstance} from "@/types/popup.ts";
import {ApiUserAuditDetail} from "@/api/user/ApiUserAuditDetail.ts";
import {IconFontType} from "@/types/icons.ts";
import IconFont from "@/components/icon/IconFont.vue";
import ContentPopup from "@/components/popup/ContentPopup.vue";
import {ApiGetCommonConfig} from "@/api/generic/ApiGetCommonConfig.ts";
import {AuditStatus, ConfigKey} from "@/types/enums.ts";
import type {BankCardInfo} from "@/types/entity.ts";
import {dictLabel} from "@/library/dict.ts";
import {auditStatusOptions, payTypeOptions, defaultPayType} from "@/types/options.ts";
import {useI18n} from 'vue-i18n';
import {useConfigStore} from "@/store/config.ts";

const { t } = useI18n()
const configStore = useConfigStore();
const fontScale = computed(()=>configStore.fontScale);
const {order, loading:orderLoading, load:loadOrder, id: orderId }=useOrderDetail()
const {project, load:loadProject} = useProjectDetail();

const inputCodePopup = ref<PopupInstance<[string, string, string|undefined]>>();
const contentPopup = ref<PopupInstance>()

/*//分红确认时间 已取消
const confirmDevidiendTime = computed(()=>{
	return Date.now() + 86400*7*1000;
})*/

const dividendAmount = computed(()=>{
	if(order.value) {
		return transformCurrencyInfoToUsdt({coinCode: order.value.earningCurrency, total: order.value.withdrawableBalance})
	}
	else{
		return undefined;
	}
})

const paymentInfo = ref<BankCardInfo>()

async function handleConfirm(){
	if(!order.value){
		return
	}
	const result = await inputCodePopup.value?.open()
	if(result && order.value){
		try {
			const [smsCode, emailCode, ftaCode] = result
			const api = new ApiRequestDividend({
				orderId: order.value.id,
				dividendAmount: order.value.withdrawableBalance,
				smsCode,
				emailCode,
				ftaCode
			})
			await api.call()
			ShowInfo.toast(t('pages.order.checkout.applySuccess'))
			UniRouter.to('/pages/order/detail', {id: orderId.value})
		} catch (error) {
			console.error(error)
			ShowInfo.toastError(error, t('pages.order.checkout.applySuccess'))
		}
	}
}

async function loadUserAuth(){
	try {
		const api = new ApiUserAuditDetail();
		const {data} = await api.call();
		paymentInfo.value = data.bankCard;
	}
	catch (e){
		console.error(e);
	}
}

async function showPaymentDescription(){
	try {
		const api = new ApiGetCommonConfig(ConfigKey.PAYMENT_NOTICE);
		const {data} = await api.call();
		contentPopup.value?.open(t('pages.order.checkout.paymentDescription'), data.value);
	} catch (error) {
		console.error(error);
		ShowInfo.toastError(error, t('common.error.loadPaymentDesc'));
	}
}

onLoad((query) => {
	let id= parseInt(query?.id ?? '0');
	if (id) {
		loadOrder(id);
	}
})

watch(order,(newOrder)=>{
	if(newOrder?.projectId) {
		loadProject(newOrder.projectId)
	}
	if(newOrder){
		loadUserAuth();
	}
},{immediate:true})

</script>

<template>
	<view class="page page-order-dividend">
		<view v-if="orderLoading" class="loading-state">
			<up-loading-icon></up-loading-icon>
		</view>
		<template v-else-if="order">
			<view class="content-block">
				<view class="block-title" v-if="project">{{ project.projectName }}</view>
				<view class="block-header">
					<view class="block-subtitle" style="margin-left: 0">{{ t('pages.order.checkout.dividendAmount') }}</view>
				</view>

				<view class="price-section">
					<view class="currency">{{order.withdrawableBalance}} {{order.earningCurrency}}</view>
				</view>
				<view class="price-section">
					<view class="label">{{ t('common.label.estimated') }}</view>
					<view class="price">{{ formatPrice(dividendAmount) }}</view>
				</view>

				<!--<view class="info-row">
					<view class="info-label">平台确认时间：</view>
					<view class="info-value">{{ formatTime(confirmDevidiendTime, 'YYYY年MM月DD日') }}</view>
				</view>-->
				<view class="info-row">
					<view class="info-label">{{ t('pages.order.checkout.arrivalTime') }}</view>
					<view class="info-value">{{ t('pages.order.checkout.arrivalTimeValue') }}</view>
				</view>
			</view>

			<view class="content-block">
				<view class="block-row">
					<view class="info-label">{{ t('common.payment.paymentMethod') }}</view>
					<view class="info-value">{{ dictLabel(payTypeOptions, defaultPayType) }}</view>
				</view>
			</view>

			<view class="content-block">
				<view class="block-row">
					<view class="info-label">{{ t('pages.order.checkout.paymentDescription') }}</view>
					<view class="info-value"><text @click="showPaymentDescription"><IconFont :type="IconFontType.QUESTION_FILLED" :size="16" color="#878787" /></text></view>
				</view>
			</view>
			
			<view class="content-block" v-if="paymentInfo">
				<view class="payment-info">
					<view class="info-item">
						<text class="label">{{ t('common.payment.payee') }}</text>
						<text class="value">{{ paymentInfo.bankAccountName }}</text>
					</view>
					<view class="info-item">
						<text class="label">{{ t('common.payment.account') }}</text>
						<text class="value">{{ paymentInfo.bankAccount }}</text>
					</view>
					<view class="info-item">
						<text class="label">{{ t('common.payment.bankName') }}</text>
						<text class="value">{{ paymentInfo.bankName }}</text>
					</view>
					<view class="bank-card-status" :class="`status-${paymentInfo.auditStatus}`" v-if="paymentInfo.auditStatus !== AuditStatus.AUDIT_PASSED">
						{{dictLabel(auditStatusOptions, paymentInfo.auditStatus)}}
					</view>
				</view>
			</view>
			
			<view class="button-section">
				<up-button shape="circle" :disabled="paymentInfo?.auditStatus !== AuditStatus.AUDIT_PASSED" type="primary" @click="handleConfirm">{{ t('pages.order.checkout.applyDividend') }}</up-button>
			</view>
		</template>
		<view v-else class="empty-state">
			<text>{{ t('common.error.assetNotFound') }}</text>
		</view>
	</view>
	<PopupInputCode :title="t('pages.order.checkout.inputCodeTitle')" ref="inputCodePopup" :sms-scene="CodeScene.TRANSACTION" :confirm-text="t('pages.order.checkout.confirmApplyDividend')" />
	<ContentPopup ref="contentPopup" />
</template>

<style scoped lang="scss">
.page{
	--font-scale:v-bind(fontScale);
}
.block-header {
	@include flex-row(center);
	margin-bottom: 12px;
}

.block-title {
	@include fs(12);
	color: $color-text-white;
	font-weight: 500;
}
.block-subtitle{
	@include fs(8);
	color: $color-gray;
	margin-left: 0.5em;
}

.price-section {
	margin: 12px 0;
	.price {
		@include fs(20);
		color: $theme-color;
		font-weight: 500;
	}
}

.info-row {
	@include flex-row(center);
	margin: 8px 0;
	@include fs(10);

	.info-label {
		color: $color-gray;
	}

	.info-value {
		color: $color-gray;
	}
}

.block-row{
	@include flex-row(center, space-between);
	@include fs(12);
}

.payment-info {
	.info-item {
		@include fs(10);
		color: $color-gray;
		word-break: break-all;

		.label {
			color: $color-gray;
		}
	}
}

.bank-card-status {
	width: 100%;
	margin-top: 8px;
	padding-top: 8px;
	padding-bottom: 8px;
	border-top: 1px solid $color-gray;
	text-align: center;
	@include fs(11);

	// 待审核 - 黄色
	&.status-1 {
		color: $color-text-white;
	}

	// 不通过 - 红色
	&.status-3 {
		color: $color-red;
	}
}

.button-section {
	margin-top: 20px;
}
</style>

