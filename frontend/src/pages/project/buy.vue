<script lang="ts" setup>
import {computed, ref, watch} from 'vue';
import {onLoad} from '@dcloudio/uni-app';
import {useI18n} from 'vue-i18n';
import IconFont from "@/components/icon/IconFont.vue";
import {IconFontType} from "@/types/icons";
import {$themeColor} from "@/library/GlobalVars";
import type {ProjectEntity} from "@/types/entity";
import PopupChainAddress from "@/pages/project/components/PopupChainAddress.vue";
import ENV from "@/library/env";
import {ApiCreateOrder} from "@/api/order/ApiCreateOrder";
import {ShowInfo} from "@/library/ShowInfo";
import UniRouter from "@/library/UniRouter";
import type {PopupInstance} from "@/types/popup";
import {useUserStore} from "@/store/user.ts";
import {Currency, ProjectState} from "@/types/enums.ts";
import {NwEvent} from "@/types/api.ts";
import {formatPrice, transformCurrency} from "@/library/format.ts";
import CenterEllipseText from "@/components/CenterEllipseText.vue";
import ContentPopup from "@/components/popup/ContentPopup.vue";
import {useProjectDetail} from "@/composable/useProjectDetail.ts";
import ProtocolPopup from "@/components/popup/ProtocolPopup.vue";
import type {SimpleProtocolEntity} from "@/types/entity.ts";
import {payTypeOptions, defaultPayType} from "@/types/options.ts";
import {dictLabel} from "@/library/dict.ts";
import {useConfigStore} from "@/store/config.ts";

const {t} = useI18n();
const configStore = useConfigStore();
const fontScale = computed(()=>configStore.fontScale);

const props = defineProps<{
	id: string
}>()
const userStore = useUserStore()

const quantity = ref(1);
const selectedChainAddressId = ref<number>();
const selectedChainAddress = computed(()=>{
	return userStore.address?.find(c => c.id === selectedChainAddressId.value)?.chainAddress
})

const {project, id:projectId, load: loadProject, state: projectState} = useProjectDetail();

const chainAddressOptions = computed(()=>{
	return userStore.address??[];
})

const handleQuantityChange = (delta: number) => {
	const newValue = quantity.value + delta;
	formatQuantityValue(newValue)
};
function formatQuantityValue(value:number){
	quantity.value = Math.max(minQuantity.value, Number(value)??0);
}

const chainAddressPopup = ref<PopupInstance<number>>();
const contentPopup = ref<PopupInstance>();
const protocolPopup = ref<{open(proto: SimpleProtocolEntity): void}>();

async function chooseChainAddress(){
	const result = await chainAddressPopup.value?.open(chainAddressOptions.value);
	return selectedChainAddressId.value = result;
}

async function handleBuy() {
	if (!selectedChainAddressId.value && ENV.foreign) {
		const result = await chooseChainAddress();
		if(!result){
			return;
		}
	}
	await handleConfirmBuy(project.value!, quantity.value, selectedChainAddressId.value!)
}

async function handleConfirmBuy (project: ProjectEntity, quantity: number, chainAddressId: number) {
	try {
		const api = new ApiCreateOrder({
			projectId: project.projectId,
			quantity: quantity,
			addressId: ENV.foreign ? chainAddressId : undefined,
		});

		const {data:order} = await api.call();
		ShowInfo.toast(t('pages.project.buy.completePayment'));
		uni.$emit(NwEvent.ORDER_CHANGED);
		await userStore.loadUserInfo();
		UniRouter.redirect('/pages/order/pay', { id: order.id });
	}
	catch (e){
		ShowInfo.toastError(e, t('pages.project.buy.buyButton'));
	}
}

const minQuantity = computed(()=>{
	return project.value?.minimumPurchase??0;
})
watch(minQuantity, (newValue)=>{
	if(quantity.value < newValue){
		quantity.value = newValue
	}
},{immediate:true})

onLoad((query) => {
	const id = parseInt(query?.id ?? '');
	if (id) {
		projectId.value = id;
		loadProject(id);
	}
});

const totalAmount = computed(()=>{
	const amount = quantity.value;
	const price = transformCurrency(project.value?.issueUnitPrice??0, Currency.USDT, project.value?.investmentCurrency);
	
	if(price === undefined || price === null){
		return undefined;
	} 
	
	return amount * price;
})

function goBindAddress(){
	UniRouter.to('/pages/user/chain-address');
}

async function showPurchaseNote(){
	contentPopup.value?.open(t('pages.project.buy.purchaseInstructions'), project.value?.purchaseInstructions);
}
</script>

<template>
	<view class="page page-project-buy">
		<template v-if="project">
			<!-- 购买须知块 -->
			<view class="content-block">
				<view class="block-title">{{ project.projectName }}</view>
				<view class="notice-section">
					<view class="notice-text">
						<view class="notice-content">{{ t('pages.project.buy.purchaseInstructions') }} {{ project.purchaseInstructions }}</view>
					</view>
					<view class="notice-link" @click="showPurchaseNote">
						<text class="link-text">{{ t('pages.project.buy.view') }}</text>
						<IconFont :color="$themeColor" :type="IconFontType.CARET_RIGHT" size="8"/>
					</view>
				</view>
			</view>

			<!-- 购入份额块 -->
			<view class="content-block">
				<view class="block-header">
					<view class="block-title">{{ t('pages.project.buy.purchaseQuantity') }}</view>
					<view class="block-subtitle">{{ t('pages.project.buy.minimumStart', {quantity: minQuantity}) }}</view>
				</view>

				<view class="price-section">
					<view class="price">{{ formatPrice(totalAmount) }}</view>
				</view>

				<view class="quantity-input">
					<view class="input-btn" @click="quantity > minQuantity && handleQuantityChange(-1)">-</view>
					<u-input v-model.number="quantity" @blur="formatQuantityValue" class="input-field" type="number"/>
					<view class="input-btn" @click="handleQuantityChange(1)">+</view>
				</view>

				<!--<view class="info-row">
					<view class="info-label">手续费：</view>
					<view class="info-value">
						<text class="fee-percent">40%</text>
					</view>
				</view>

				<view class="info-row">
					<view class="info-label">确认购买时间：</view>
					<view class="info-value">{{ formatTime(project.subscriptionEndTime, 'YYYY年MM月DD日') }}</view>
				</view>-->

				<view class="remark">
					{{ t('pages.project.buy.remark') }}
				</view>
			</view>

			<!-- 支付方式块 -->
			<view class="content-block">
				<view class="block-row">
					<view class="info-label">{{ t('common.payment.paymentMethod') }}</view>
					<view class="info-value">{{ dictLabel(payTypeOptions, defaultPayType) }}</view>
				</view>
			</view>

			<!-- 链地址块 -->
			<view class="content-block" v-if="ENV.foreign">
				<view class="block-row">
					<view class="info-label">{{ t('pages.project.buy.chainAddress') }}</view>
					<view class="chain-select" @click="chooseChainAddress">
						<view class="select-value" :class="{empty : !selectedChainAddress}">
							<CenterEllipseText v-if="selectedChainAddress" :text="selectedChainAddress"></CenterEllipseText>
							<template v-else-if="chainAddressOptions.length > 0">{{ t('pages.project.buy.selectChainAddress') }}</template>
							<up-button v-else size="small" shape="circle" type="primary" @click="goBindAddress">{{ t('pages.project.buy.bindAddress') }}</up-button>
						</view>
					</view>
				</view>
			</view>

			<!-- 购买按钮 -->
			<view class="button-section">
				<up-button shape="circle" type="primary" @click="handleBuy">{{ t('pages.project.buy.buyButton') }}</up-button>
			</view>
		</template>
	</view>

	<PopupChainAddress ref="chainAddressPopup" />
	<ContentPopup ref="contentPopup" />
	<ProtocolPopup ref="protocolPopup" />
</template>

<style lang="scss" scoped>
.page{
	--font-scale:v-bind(fontScale);
}
.notice-section {
	@include flex-row(center, space-between);
	margin-top: 8px;
	gap: 12px;

	.notice-text {
		flex: 1;
		color: $color-gray;
		@include flex-row(center, flex-start, wrap);
		overflow: hidden;
	}

	.notice-label {
		@include fs(10);
		margin-right: 0.5em;
	}

	.notice-content {
		@include fs(10);
		@include ellipsis();
	}

	.notice-link {
		flex: none;
		@include flex-row(center);
		flex-shrink: 0;
		.link-text {
			@include fs(10);
			color: $theme-color;
		}
	}
}


.chain-select {
	flex: 1;
	margin-left: 8px;
	cursor: pointer;
	.select-value {
		@include fs(12);
		color: $color-text-white;
		text-align: right;

		&.empty{
			color: $theme-color;
		}
		
		.u-button{
			height: 2em;
			width: 5em;
			margin-right: 0;
		}
	}
}


.block-row{
	@include flex-row(center, space-between);
	@include fs(12);
}

.remark {
	@include fs(8);
	color: $theme-color;
	margin-top: 12px;
	line-height: 1.4;
}

.button-section {
	margin-top: 20px;
	padding: 0 $page-padding;
}
</style>

