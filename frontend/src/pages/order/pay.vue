<template>
	<view class="page page-order-pay">
		<!-- Loading 状态 -->
		<view v-if="orderLoading" class="loading-state">
			<up-loading-icon></up-loading-icon>
		</view>

		<!-- 支付页面 -->
		<view v-else-if="order" class="pay-container">
			<!-- 倒计时提示 -->
			<view class="countdown-alert" v-if="countDown !== undefined">
				<text class="alert-text">{{ t('pages.order.pay.countdownAlert', { time: formatCountdownTime(countDown) }) }}</text>
			</view>

			<view class="info-block">
				<view class="block-title">{{ order!.projectName }}</view>
			</view>
			
			<!-- 订单信息块 -->
			<view class="info-block">
				<view class="info-row">
					<text class="label">{{ t('pages.order.pay.purchaseQuantity') }}</text>
					<text class="value">{{ order!.subscribeQuantity }}{{ t('common.unit.share') }}</text>
				</view>
				<view class="info-row">
					<text class="value amount">${{ formatNumber(order.totalAmount) }}</text>
				</view>
				<view class="info-row">
					<text class="label">{{ t('pages.order.pay.estimatedConfirmTime') }}</text>
					<text class="value time">{{ formatTime(estimatedPurchaseDate, 'YYYY-MM-DD') }}</text>
				</view>
				<view class="remark">
					{{ t('pages.order.pay.remark') }}
				</view>
			</view>

			<!-- 支付信息块 -->
			<view class="info-block">
				<view class="payment-title">{{ t('pages.order.pay.paymentTitle') }}</view>
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

			<!-- 合同号输入 -->
			<view class="info-block group">
				<view class="block-title">{{ t('pages.order.pay.contractNo') }}</view>
				<up-input
					class="block-content"
					v-model="contractNo"
					:placeholder="t('pages.order.pay.contractNoPlaceholder')"
					type="text"
				></up-input>
			</view>

			<!-- 支付凭证上传 -->
			<view class="info-block group">
				<view class="block-title">
					<text class="block-title">{{ t('pages.order.pay.paymentVoucher') }}</text>
				</view>
				<view class="block-content">
					<CompUpload v-model="voucherFile" :preview="false">
						<template #default>
							<view class="upload-link">{{ t('pages.order.pay.upload') }}</view>
						</template>
						<template #clear>
							<view class="clear-link" @click="removeVoucher">{{ t('pages.order.pay.clear') }}</view>
						</template>
					</CompUpload>
				</view>
				<view v-if="voucherFile" class="voucher-preview">
					<CompImage :src="voucherFile.url"></CompImage>
				</view>
			</view>
			
			<view class="button-section">
				<up-button
					type="primary"
					shape="circle"
					:loading="isSubmitting"
					@click="handlePay"
				>
					{{ t('pages.order.pay.submitPayment') }}
				</up-button>
			</view>
		</view>

		<!-- 空状态 -->
		<view v-else class="empty-state">
			<text>{{ t('pages.order.pay.orderNotFound') }}</text>
		</view>

		<!-- 支付验证码弹窗 -->
		<PopupInputCode ref="payConfirmPopup" :smsScene="CodeScene.AUDIT" :confirm-text="t('pages.order.pay.paymentCompleted')" />
	</view>
</template>

<script lang="ts" setup>
import {onLoad} from '@dcloudio/uni-app'
import {ApiPayOrder} from '@/api/order/ApiPayOrder'
import {ShowInfo} from '@/library/ShowInfo'
import UniRouter from '@/library/UniRouter'
import {formatNumber, formatTime} from '@/library/Utility'
import timer from "@/library/Timer.ts";
import CompUpload from "@/components/CompUpload.vue";
import {ApiUploadFile} from "@/api/file.ts";
import CompImage from "@/components/CompImage.vue";
import PopupInputCode from "@/components/popup/PopupInputCode.vue";
import {CodeScene} from "@/api/generic/ApiSendSmsCode";
import {useOrderDetail} from "@/composable/useOrderDetail.ts";
import {NwEvent} from "@/types/api.ts";
import {ApiGetProjectPaymentInfo, type BankPaymentInfo} from "@/api/project/ApiGetProjectPaymentInfo";
import {ConfirmDelay} from "@/library/limits.ts";
import {useI18n} from 'vue-i18n';
import {useConfigStore} from "@/store/config.ts";
import type {PopupInstance} from "@/types/popup.ts";

const { t } = useI18n()
const configStore = useConfigStore();
const fontScale = computed(()=>configStore.fontScale);

const isSubmitting = ref(false)
const contractNo = ref('')
const voucherFile = ref<ApiUploadFile | undefined>(undefined)
const payConfirmPopup = ref<PopupInstance<[string, string, string|undefined]>>();
const paymentInfoLoading = ref(false)

const {order, loading:orderLoading, load:loadOrder, id:orderId }=useOrderDetail()

// 支付信息
const paymentInfo = ref<BankPaymentInfo>({
	bankAccountName: '',
	bankAccount: '',
	bankName: ''
})

const estimatedPurchaseDate = Date.now()+ConfirmDelay;

const countDown = computed(()=>{
	if(!order.value)return undefined;
	const now = timer.second.value;
	const expire = order.value.expireTime;
	
	return Math.max(Math.floor(expire - now)/1000, 0);
})

// 格式化倒计时时间
function formatCountdownTime(seconds: number): string {
	const hours = Math.floor(seconds / 3600)
	const minutes = Math.floor((seconds % 3600) / 60)
	const secs = seconds % 60
	return `${String(hours).padStart(2, '0')}:${String(minutes).padStart(2, '0')}:${secs.toFixed(0).padStart(2, '0')}`
}


// 加载支付信息
async function loadPaymentInfo(projectId: number) {
	try {
		paymentInfoLoading.value = true
		const api = new ApiGetProjectPaymentInfo(projectId)
		const result = await api.call()
		paymentInfo.value = result.data
	} catch (error) {
		console.error('加载支付信息失败:', error)
		ShowInfo.toastError(error, '加载支付信息')
	} finally {
		paymentInfoLoading.value = false
	}
}

// 监听 order 变化，当订单加载完成后加载支付信息
watch(order, (newOrder) => {
	if (newOrder?.projectId) {
		loadPaymentInfo(newOrder.projectId)
	}
})

onLoad((query) => {
	let id= parseInt(query?.id ?? '0');
	if (id) {
		loadOrder(id);
	}
})



// 移除支付凭证
function removeVoucher() {
	voucherFile.value = undefined
}

// 提交支付
async function handlePay() {
	// 检查必填项
	if (!contractNo.value.trim()) {
		return ShowInfo.toast(t('pages.order.pay.enterContractNo'))
	}
	if (!voucherFile.value) {
		return ShowInfo.toast(t('pages.order.pay.uploadVoucher'))
	}

	// 打开验证码弹窗
	try {
		const result = await payConfirmPopup.value?.open()
		if (!result) {
			return
		}

		// 提交支付
		const [smsCode, emailCode, ftaCode] = result
		await submitPay(smsCode, emailCode, ftaCode)
	} catch (error) {
		console.error('支付流程出错:', error)
	}
}

// 提交支付到服务器
async function submitPay(smsCode: string, emailCode:string, ftaCode?: string) {
	try {
		isSubmitting.value = true

		const api = new ApiPayOrder({
			orderId: orderId.value,
			payVoucher: voucherFile.value as any,
			contractNo: contractNo.value,
			smsCode,
			emailCode,
			ftaCode
		})

		await api.call()
		ShowInfo.toastSuccess(t('pages.order.pay.submitSuccess'))
		uni.$emit(NwEvent.ORDER_CHANGED);
		UniRouter.redirect('/pages/order/list')
	} catch (error) {
		console.error('支付失败:', error)
		ShowInfo.toastError(error, '支付信息提交')
	} finally {
		isSubmitting.value = false
	}
}
</script>

<style lang="scss" scoped>
.page{
	--font-scale:v-bind(fontScale);
}
.page-order-pay {
	padding-bottom: 60px;

	.pay-container {
		.countdown-alert {
			background-color: $color-text-white;
			padding: 4px 8px;
			border-radius: 4px;
			margin-bottom: 20px;

			.alert-text {
				@include fs(10);
				color: #DF1818;
				text-align: center;
				display: block;
			}
		}

		.info-block {
			background-color: $bg-color-light;
			padding: 8px;
			border-radius: 8px;
			margin-bottom: 8px;

			.block-title {
				@include fs(13);
				@include fw(medium);
				color: $color-text-white;
			}

			.upload-link-wrap{
				text-align: right;
			}
			.upload-link,.clear-link {
				@include fs(12);
				color: $theme-color;
				cursor: pointer;
			}
			
			&.group{
				@include flex-row(center, flex-start, wrap);
				.block-content{
					margin-left: 1em;
					flex: 1;
				}
				.u-input{
					border: 0;
					padding: 0;
					@include fs(12);
					color: $color-text-white;
				}
			}

			.info-row {
				@include flex-row(center,space-between);
				padding: 4px 0;

				&:last-child {
					border-bottom: none;
				}

				.label {
					@include fs(12);
					color: $color-gray;
					margin-right: 1em;
				}

				.value {
					@include fs(12);
					color: $color-text-white;

					&.amount {
						color: $theme-color;
						@include fs(20);
						@include fw(medium);
					}
					&.time{
						color: $color-gray;
					}
				}
			}

			.remark {
				@include fs(11);
				color: $color-gray;
				margin-top: 12px;
				line-height: 1.5;
			}

			.payment-title{
				@include fs(10);
				color: $color-gray;
				margin-bottom: 1.5em;
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

			.comp-upload{
				width: 3em;
				margin-left: auto;
				:deep(.u-upload__wrap){
					>view{
						@include flex-row(center);
					}
				}
			}
			
			.voucher-preview {
				width: 100%;
				margin-top: 10px;
				.comp-image{
					width: 100%;
				}
			}

			.voucher-placeholder {
				@include flex-center();
				background-color: rgba(255, 255, 255, 0.05);
				padding: 40px 12px;
				border-radius: 4px;
				color: $color-gray;
				@include fs(12);
			}
		}

		.button-section {
			background-color: $bg-color;
			position: fixed;
			left: 0;
			right: 0;
			bottom: 0;
			padding: 10px $page-padding;
		}
	}
}
</style>

