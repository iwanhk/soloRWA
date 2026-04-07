<script setup lang="ts">
import {useOrderDetail} from "@/composable/useOrderDetail.ts";
import {onLoad, onNavigationBarButtonTap, onPullDownRefresh} from "@dcloudio/uni-app";
import AssetStatistics from "@/components/AssetStatistics.vue";
import AssetProfitChart from "@/components/AssetProfitChart.vue";
import ProjectDetailInfo from "@/pages/project/components/ProjectInfo.vue";
import {useProjectDetail} from "@/composable/useProjectDetail.ts";
import {AssetStatus, OrderStatus, ProjectState} from "@/types/enums.ts";
//@ts-ignore
import UpPopover from "@/uni_modules/uview-plus/components/u-popover/u-popover.vue";
import IconFont from "@/components/icon/IconFont.vue";
import {IconFontType} from "@/types/icons.ts";
import {$themeColor} from "@/library/GlobalVars.ts";
import PopupConfirmProjectEarlyRedeem from "./components/PopupConfirmProjectEarlyRedeem.vue";
import UniRouter from "@/library/UniRouter.ts";
import PopupContactService from "@/components/popup/PopupContactService.vue";
import {useI18n} from 'vue-i18n';
import {useConfigStore} from "@/store/config.ts";
import {ApiGetRedemptionFeeRate} from "@/api/order/ApiGetRedemptionFeeRate.ts";
import {projectTypeClosed} from "@/types/options.ts";
import dayjs from "dayjs";
import {ShowInfo} from "@/library/ShowInfo.ts";

const { t } = useI18n()
const {order, loading:orderLoading, load:loadOrder, id: orderId }=useOrderDetail()

const {project, load: loadProject, state:projectState} = useProjectDetail();

const configStore = useConfigStore();
const fontScale = computed(()=>configStore.fontScale);

const popoverMenu = ref<{open():void, close():void}>()
const earlyRedeemPopup = ref<{open():Promise<any>}>()
const contactServicePopup = ref<{open():void}>()
const redemptionFeeRate = ref<number | null>(null)

let propOverShown = false;

onNavigationBarButtonTap(()=>{
	if(!propOverShown) {
		popoverMenu.value?.open()
	}
	else{
		popoverMenu.value?.close()
	}
	propOverShown = !propOverShown;
})

async function loadRedemptionFeeRate() {
	try {
		if (orderId.value) {
			const api = new ApiGetRedemptionFeeRate(orderId.value);
			const {data} = await api.call();
			redemptionFeeRate.value = data;
		}
	} catch (e) {
		console.error('Failed to load redemption fee rate:', e);
	}
}

watch(order, (newOrder, oldOrder)=>{
	if(newOrder?.projectId && newOrder?.projectId !== oldOrder?.projectId){
		loadProject(newOrder.projectId);
	}
	if(newOrder?.id && newOrder?.id !== oldOrder?.id){
		loadRedemptionFeeRate();
	}
})

onLoad((query) => {
	let id= parseInt(query?.id ?? '0');
	if (id) {
		loadOrder(id);
	}
})

function contactService(){
	popoverMenu.value?.close()
	propOverShown = false
	contactServicePopup.value?.open()
}

function requestEarlyRedemption(){
	popoverMenu.value?.close()
	propOverShown = false;
	UniRouter.to('/pages/order/early-redeem', {id: orderId.value});
}

function requestDividend(){
	popoverMenu.value?.close()
	propOverShown = false;
	UniRouter.to('/pages/order/checkout', {id: orderId.value});
}

function requestRedeem(){
	popoverMenu.value?.close()
	propOverShown = false;
	UniRouter.to('/pages/order/redeem', {id: orderId.value});
}

function goBill(){
	popoverMenu.value?.close()
	propOverShown = false;
	UniRouter.to('/pages/bill/list', {orderId: orderId.value});
}

onPullDownRefresh(async ()=>{
	await loadOrder();
	await loadRedemptionFeeRate();
	uni.stopPullDownRefresh()
})

const canEarlyRedeem = computed(()=>{
	if(order.value?.orderStatus !== OrderStatus.AUDIT_PASSED){
		return false;
	}
	if(project.value?.projectType === projectTypeClosed){
		const now = Date.now();
		const lockStartTime = project.value?.lockStartTime?dayjs(project.value.lockStartTime).valueOf(): undefined;
		return !(lockStartTime && now <= lockStartTime);
	}
	return redemptionFeeRate.value !== null && redemptionFeeRate.value > 0;
})

</script>

<template>
	<view class="page page-order-detail">
		<PopupConfirmProjectEarlyRedeem ref="earlyRedeemPopup" />
		<PopupContactService ref="contactServicePopup" />
		<up-popover :class="configStore.language" ref="popoverMenu" direction="bottom" popupBgColor="#FBFBFB" :show-copy="false">
			<template #content>
				<view class="popover-menus">
					<view class="popover-menu" @click="contactService">
						<IconFont :type="IconFontType.CHAT" size="16" :color="$themeColor" />
						{{ t('pages.order.detail.contactService') }}
					</view>
					<view class="popover-menu" @click="requestEarlyRedemption" v-if="canEarlyRedeem">
						<IconFont :type="IconFontType.REDEEM" size="16" :color="$themeColor" />
						{{ t('pages.order.detail.earlyRedeem') }}
					</view>
					<view class="popover-menu" @click="goBill">
						<IconFont :type="IconFontType.MONEY_SQUARE" size="16" :color="$themeColor" />
						{{ t('pages.order.detail.viewBill') }}
					</view>
				</view>
			</template>
		</up-popover>
		<!-- Loading 状态 -->
		<view v-if="orderLoading" class="loading-state">
			<up-loading-icon></up-loading-icon>
		</view>
		<template v-else-if="order">
			<AssetStatistics :order="order" :project="project" :project-state="projectState" />

			<view class="actions" v-if="order.orderStatus === OrderStatus.AUDIT_PASSED && redemptionFeeRate !== null">
				<up-button v-if="projectState === ProjectState.DIVIDEND && order.withdrawableBalance" @click="requestDividend" type="primary" shape="circle">{{ t('pages.order.detail.applyDividend') }}</up-button>

				<up-button v-if="!redemptionFeeRate" @click="requestRedeem" type="primary" shape="circle">{{ t('pages.order.detail.fullRedeem') }}</up-button>
			</view>
			
			<AssetProfitChart v-if="order.status >= AssetStatus.PROFITTING && project" :order="order" :project="project" />
			
			<ProjectDetailInfo v-if="project" :project="project" :order="order" />
		</template>
		<!-- 空状态 -->
		<view v-else class="empty-state">
			<text>{{ t('common.error.assetNotFound') }}</text>
		</view>
	</view>
</template>

<style scoped lang="scss">
.page{
	--font-scale:v-bind(fontScale);
}
.page-order-detail{
	position: relative;
	.up-popover{
		position:absolute;
		right: 0;
		top: 0;
		:deep(.u-transition){
			transform: translateY(0)!important;
			right: 10px!important;
			.up-popover__content{
				padding: 0;
			}
		}
	}
}
.actions{
	margin-top: 20px;
}
.popover-menus{
	padding: 6px 10px;
	
	.popover-menu{
		@include flex-row(center);
		@include fs(16);
		color: $color-text-black;
		@include en-break;
		.icon-font{
			margin-right: 0.25em;
		}
	}
}
.up-popover{
	:deep(.u-transition){
		@include fs(16);
		width: 7em;
	}
	&.en{
		:deep(.u-transition){
			width: 10em;
		}
		.popover-menu{
			@include fs(14);
		}
	}
}
.project-detail-info{
	margin-top: 20px;
}
</style>