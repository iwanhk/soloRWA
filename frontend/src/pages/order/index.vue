<template>
	<NavBar transparent side-width="auto" title="">
		<template #left>
			<view class="logo-wrap">
				<CompLogo class="logo" />
			</view>
		</template>
		<template #right>
			<view class="message-wrap">
				<MessageCount/>
			</view>
		</template>
	</NavBar>
	<view class="page page-asset">
		<template v-if="userStore.user">
			<template v-if="userStore.user.orderCount">
				<AssetStatistics ref="assetStatistic" />
				
				<view class="asset-list">
					<u-table fontSize="inherit" border-color="transparent" bg-color="transparent" color="#FBFBFB" padding="8px 0">
						<u-tr>
							<u-th text-align="left" width="100px">{{ t('pages.order.index.projectName') }}</u-th>
							<u-th width="80px">{{ t('pages.order.index.totalAmount') }}</u-th>
							<u-th width="51px">{{ t('pages.order.index.status') }}</u-th>
							<u-th text-align="right">{{ t('pages.order.index.cumulativeIncome') }}</u-th>
						</u-tr>
						<u-tr v-for="order in orderList" :key="order.id" @click="showOrder(order)">
							<u-td text-align="left" width="100px"><view class="title">{{ order.projectName }}</view></u-td>
							<u-td width="80px">{{ formatPrice(transformCurrency(order.holdAmount, Currency.USDT, order.investmentCurrency)) }}</u-td>
							<u-td width="51px">{{ dictLabel(assetStatusOptions, order.status) }}</u-td>
							<u-td text-align="right">
								<view class="currency">
									{{order.totalIncome}} {{order.earningCurrency}}
								</view>
								<view class="value" v-if="order.earningCurrency !== configStore.currency">
									<CompPrice :price="transformCurrency(order.totalIncome, undefined, order.earningCurrency)" />
								</view>
								<view class="rate">
									<CompPrice :price="transformCurrency(order.totalIncome, Currency.USDT, order.earningCurrency)/transformCurrency(order.holdAmount, Currency.USDT, order.investmentCurrency)*100" :format="false" :show-unit="false" :precise="2">
										<template #suffix>%</template>
									</CompPrice>
								</view>
							</u-td>
						</u-tr>
					</u-table>
					<CompLoadMore :status="orderListStatus" />
				</view>
			</template>
			<template v-else>
				<view class="empty">
					<view class="main-vision">
						<view class="note">{{ t('pages.order.index.noInvestment') }}</view>
					</view>
					<view class="action">
						<up-button type="primary" shape="circle" size="small" @click="goInvest">{{ t('pages.order.index.goInvest') }}</up-button>
					</view>
				</view>
			</template>
		</template>
		<view v-else class="login">
			<view class="main-vision">

			</view>
			<view class="action">
				<up-button type="primary" shape="circle" size="small" @click="goLogin">{{ t('pages.order.index.loginRegister') }}</up-button>
			</view>
		</view>
	</view>
</template>

<script lang="ts" setup>

import CompImage from "@/components/CompImage.vue";
import MessageCount from "@/components/MessageCount.vue";
import NavBar from "@/components/NavBar.vue";
import AssetStatistics from "@/components/AssetStatistics.vue";
import {useUserStore} from "@/store/user.ts";
import UniRouter from "@/library/UniRouter.ts";
import {isTabbarPage, loginPage, mainPage} from "@/library/GlobalVars.ts";
import {onHide, onPullDownRefresh, onShow} from "@dcloudio/uni-app";
import {useTabListManager} from "@/composable/useTabListManager.ts";
import {ApiPagedListHandlerFactory} from "@/library/PagedListHandler.ts";
import type {AssetEntity} from "@/types/entity.ts";
import {ApiGetAssetList} from "@/api/asset/ApiGetAssetList.ts";
import CompLoadMore from "@/components/CompLoadMore.vue";
import {dictLabel} from "@/library/dict.ts";
import {assetStatusOptions} from "@/types/options.ts";
import {formatPrice, transformCurrency} from "@/library/format.ts";
import {NwEvent} from "@/types/api.ts";
import CompPrice from "@/components/CompPrice.vue";
import {Currency} from "@/types/enums.ts";
import {useI18n} from 'vue-i18n';
import CompLogo from "@/components/CompLogo.vue";
import {useConfigStore} from "@/store/config.ts";


const { t } = useI18n()
const userStore = useUserStore();
const configStore = useConfigStore();
const fontScale = computed(()=>configStore.fontScale);

function goLogin(){
	UniRouter.to(loginPage)
}
function goInvest(){
	UniRouter.to(mainPage);
}

const assetStatistic = ref<{load():Promise<void>}>()

const tabListManager = useTabListManager<AssetEntity>();
tabListManager.add('default', ApiPagedListHandlerFactory(ApiGetAssetList), true);
tabListManager.show('default');

const orderList = tabListManager.records;
const orderListStatus = tabListManager.status;

function showOrder(order:AssetEntity){
	UniRouter.to('/pages/order/detail', {id: order.id});
}

uni.$on(NwEvent.ORDER_CHANGED, function (){
	tabListManager.refresh();
});

onShow(()=>{
	isTabbarPage.value = true
})
onHide(()=>{
	isTabbarPage.value = false
})

onPullDownRefresh(async ()=>{
	await assetStatistic.value?.load()
	uni.stopPullDownRefresh()
})
</script>

<style scoped lang="scss">
.page{
	--font-scale:v-bind(fontScale);
}
.nav-bar{
	:deep(.uni-navbar__header-btns){
		overflow: visible;
	}
}

.login{
	height: calc(100vh - 50px - 44px - 100px);
	@include flex-column(center, center);
	.main-vision{
		margin-left: auto;
		margin-right: auto;
		height: 212px;
		width: 212px;
		background: url("@/static/images/asset/login-bg.png") no-repeat center/contain;
	}
	.action{
		padding: 20px;
		.u-button{
			height: 2em;
			width: 178px;
			@include fs(16);
		}
	}
}
.empty{
	height: calc(100vh - 50px - 44px - 100px);
	@include flex-column(center, center);
	.main-vision{
		margin-left: auto;
		margin-right: auto;
		height: 244px;
		width: 244px;
		background: url("@/static/images/asset/empty-bg.png") no-repeat center/contain;
		@include flex-column(center, flex-end);
	}
	.note{
		@include fs(12);
		color: $color-text-white;
		margin-bottom: 20px;
	}
	.action{
		padding: 20px;
		.u-button{
			height: 2em;
			width: 178px;
			@include fs(16);
		}
	}
}

.asset-list{
	margin-top: 16px;
	.u-th{
		@include en-break;
		
		&:first-child{
			text-align: left!important;
		}
		&:last-child{
			text-align: right!important;
		}
	}
	
	.title{
		@include ellipsis(auto, 2);
	}
	.currency{
		color: $theme-color;
	}
	.value{
		@include fs(14);
	}
	:deep(.price-up), :deep(.price-down){
		color: $theme-color;
	}
}
</style>

