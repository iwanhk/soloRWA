<script setup lang="ts">
import {ApiEarlyRedemption} from "@/api/order/ApiEarlyRedemption.ts";
import {ShowInfo} from "@/library/ShowInfo.ts";
import UniRouter from "@/library/UniRouter.ts";
import {useOrderDetail} from "@/composable/useOrderDetail.ts";
import {onLoad} from "@dcloudio/uni-app";
import {useProjectDetail} from "@/composable/useProjectDetail.ts";
import {formatPrice, transformCurrency} from "@/library/format.ts";
import IconFont from "@/components/icon/IconFont.vue";
import {IconFontType} from "@/types/icons.ts";
import type {BankPaymentInfo} from "@/api/project/ApiGetProjectPaymentInfo.ts";
import {ApiUserAuditDetail} from "@/api/user/ApiUserAuditDetail.ts";
import PopupInputCode from "@/components/popup/PopupInputCode.vue";
import {CodeScene} from "@/api/generic/ApiSendSmsCode.ts";
import type {PopupInstance} from "@/types/popup.ts";
import ContentPopup from "@/components/popup/ContentPopup.vue";
import {ApiGetCommonConfig} from "@/api/generic/ApiGetCommonConfig.ts";
import {ConfigKey, Currency} from "@/types/enums.ts";
import {ref} from "vue";
import {ApiAssetDetailStatistic, type AssetDetailStatistic} from "@/api/asset/ApiAssetDetailStatistic.ts";
import {useI18n} from 'vue-i18n';
import {defaultPayType, payTypeOptions} from "@/types/options.ts";
import {dictLabel} from "@/library/dict.ts";
import {useConfigStore} from "@/store/config.ts";
import {ApiGetRedemptionFeeRate} from "@/api/order/ApiGetRedemptionFeeRate.ts";

const { t } = useI18n()
const configStore = useConfigStore();
const fontScale = computed(()=>configStore.fontScale);
const {order, loading:orderLoading, load:loadOrder, id: orderId }=useOrderDetail()
const {project, load:loadProject} = useProjectDetail();

const statistic = ref<AssetDetailStatistic>({
	totalAssets: [],
	totalIncome: [],
	todayIncome: [],
	withdrawableBalance: 0,
});
const redemptionFeeRate = ref<number | null>(null);

async function loadStatistic(id:number){
	const api = new ApiAssetDetailStatistic({
		orderId: id,
	});
	const result = await api.call();
	statistic.value = result.data;
}

async function loadRedemptionFeeRate(id:number){
	try {
		const api = new ApiGetRedemptionFeeRate(id);
		const {data} = await api.call();
		redemptionFeeRate.value = data;
	} catch (e) {
		console.error('Failed to load redemption fee rate:', e);
	}
}

const minQuantity = computed(()=>{
	return project.value?.minimumPurchase ?? 0
})
/*//赎回确认时间 已取消
const confirmRedeemTime = computed(()=>{
	return Date.now() + 86400*7*1000;
})*/

const paymentInfo = ref<BankPaymentInfo>()

const quantity = ref(minQuantity.value);
const handleQuantityChange = (delta: number) => {
	const newValue = quantity.value + delta;
	formatQuantityValue(newValue)
};
function formatQuantityValue(value:number){
	quantity.value = Math.min(Math.max(minQuantity.value, Number(value)??0), order.value?.holdQuantity??0);
}

const totalAmount = computed(()=>{
	if(order.value === undefined)return undefined;
	return (order.value.price * quantity.value);
})

const totalAmountInUSDT = computed(()=>{
	if(totalAmount.value === undefined)return undefined;
	return transformCurrency(totalAmount.value, Currency.USDT, order.value?.investmentCurrency)
})
const dividendAmountInUSDT = computed(()=>{
	return transformCurrency(order.value?.withdrawableBalance??0, Currency.USDT, order.value?.earningCurrency);
})

const redeemAmountInUSDT = computed(()=>{
	if(totalAmountInUSDT.value === undefined)return undefined;

	return totalAmountInUSDT.value + dividendAmountInUSDT.value;
})
const deductRedeemAmountInUSDT = computed(()=>{
	if(totalAmountInUSDT.value === undefined)return undefined;

	return totalAmountInUSDT.value * (100 - (redemptionFeeRate.value??0))/100 + dividendAmountInUSDT.value;
})


const dividendAmount = computed(()=>{
	if(order.value) {
		return transformCurrency(order.value.withdrawableBalance, Currency.USDT, order.value.earningCurrency);
	}
	return 0;
})

const inputCodePopup = ref<PopupInstance<[string, string, string|undefined]>>()
const contentPopup = ref<PopupInstance>()

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
		contentPopup.value?.open(t('common.payment.paymentDescription'), data.value);
	} catch (error) {
		console.error(error);
		ShowInfo.toastError(error, t('common.error.loadPaymentDesc'));
	}
}

function showRedeemRules(){
	if(project.value?.redemptionRules){
		contentPopup.value?.open(t('common.redeem.rules'), project.value.redemptionRules, false);
	} else {
		ShowInfo.toastError(t('common.error.redeemRulesNotFound'), t('common.error.loadRedeemRules'));
	}
}

async function handleConfirm(){
	const result = await inputCodePopup.value?.open()
	if(result && order.value){
		try {
			const [smsCode, emailCode, ftaCode] = result
			const api = new ApiEarlyRedemption({
				orderId: order.value.id,
				redemptionQuantity: quantity.value,
				smsCode,
				emailCode,
				ftaCode
			})
			await api.call()
			ShowInfo.toast(t('pages.order.earlyRedeem.applySuccess'))
			UniRouter.to('/pages/order/detail', {id: orderId})
		} catch (error) {
			console.error(error)
			ShowInfo.toastError(error, t('pages.order.earlyRedeem.applySuccess'))
		}
	}
}


onLoad((query) => {
	let id= parseInt(query?.id ?? '0');
	if (id) {
		loadOrder(id);
		loadStatistic(id);
		loadRedemptionFeeRate(id);
	}
})
watch(order,(newOrder)=>{
	if(newOrder?.projectId) {
		loadProject(newOrder.projectId)
	}
	if(newOrder){
		loadUserAuth();
		loadRedemptionFeeRate(newOrder.id);
	}
},{immediate:true})
watch(minQuantity, ()=>{
	formatQuantityValue(quantity.value)
})


</script>

<template>
	<view class="page page-order-early-redeem">
		<view v-if="orderLoading" class="loading-state">
			<up-loading-icon></up-loading-icon>
		</view>
		<template v-else-if="order">
			<view class="content-block" v-if="project">
				<view class="block-title">{{ project.projectName }}</view>
			</view>

			<view class="content-block">
				<view class="block-header">
					<view class="block-title">{{ t('common.redeem.quantity') }}</view>
					<view class="block-subtitle">{{ t('pages.order.earlyRedeem.minimumStart', { quantity: minQuantity }) }}</view>
				</view>

				<view class="price-section">
					<view class="currency">{{ order.holdAmount }} {{order.investmentCurrency}}</view>
					<view class="currency sub">{{ order.withdrawableBalance }} {{order.earningCurrency}}</view>
				</view>
				<view class="price-section">
					<view class="label">{{ t('common.label.estimated') }}</view>
					<view class="price">{{ formatPrice({total:redeemAmountInUSDT!, coinCode:Currency.USDT}) }}</view>
					<view class="price sub" v-if="dividendAmount">{{ t('common.redeem.includeIncome', { amount: formatPrice({total:dividendAmount, coinCode:Currency.USDT}) }) }}</view>
				</view>

				<view class="quantity-input">
					<view class="input-btn" @click="quantity > minQuantity && handleQuantityChange(-1)">-</view>
					<u-input @blur="formatQuantityValue" v-model.number="quantity" class="input-field" type="number"/>
					<view class="input-btn" @click="handleQuantityChange(1)">+</view>
				</view>

				<view class="info-row">
					<view class="info-label">{{ t('pages.order.earlyRedeem.fee') }}</view>
					<view class="info-value">
						<text class="fee-percent">{{ redemptionFeeRate?? '--' }}%</text>
					</view>
				</view>
				<view class="info-row">
					<view class="info-label">{{ t('pages.order.earlyRedeem.estimatedRedeemAmount') }}</view>
					<view class="info-value">
						<text class="fee-percent">{{ formatPrice({total: deductRedeemAmountInUSDT!, coinCode: Currency.USDT}) }}</text>
					</view>
				</view>
				<!--<view class="info-row">
					<view class="info-label">平台确认时间：</view>
					<view class="info-value">{{ formatTime(confirmRedeemTime, 'YYYY年MM月DD日') }}</view>
				</view>-->
			</view>

			<view class="content-block">
				<view class="block-row">
					<view class="info-label">{{ t('common.payment.paymentMethod') }}</view>
					<view class="info-value">{{ dictLabel(payTypeOptions, defaultPayType) }}</view>
				</view>
			</view>

			<view class="content-block">
				<view class="block-row">
					<view class="info-label">{{ t('common.payment.paymentDescription') }}</view>
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
				</view>
			</view>

			<view class="content-block">
				<view class="block-row">
					<view class="info-label">{{ t('common.redeem.rules') }}</view>
					<view class="info-value">
						<text class="view-link" @click="showRedeemRules">{{ t('common.label.view') }}</text>
					</view>
				</view>
			</view>

			<view class="button-section">
				<up-button shape="circle" type="primary" @click="handleConfirm">{{ t('pages.order.earlyRedeem.applyEarlyRedeem') }}</up-button>
			</view>
		</template>
		<view v-else class="empty-state">
			<text>{{ t('common.error.assetNotFound') }}</text>
		</view>
	</view>
	<PopupInputCode :title="t('pages.order.earlyRedeem.inputCodeTitle')" ref="inputCodePopup" :sms-scene="CodeScene.TRANSACTION" :confirm-text="t('pages.order.earlyRedeem.confirmApplyRedeem')" />
	<ContentPopup ref="contentPopup" />
</template>

<style scoped lang="scss">
.page{
	--font-scale:v-bind(fontScale);
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

.price-section{
	@include flex-row(center);
	.currency.sub{
		margin-left: 10px;
		background: $theme-color;
		color: $color-text-white;
		@include fs(10);
		border-radius: 999em;
		padding: 2px 1em;
	}
	.price.sub{
		margin-left: 10px;
		background: $theme-color;
		color: $color-text-white;
		@include fs(10);
		border-radius: 999em;
		padding: 2px 1em;
	}
}

.button-section {
	margin-top: 20px;
}
</style>