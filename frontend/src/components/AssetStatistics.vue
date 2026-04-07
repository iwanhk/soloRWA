<template>
	<view class="asset-statistics">
		<view class="asset-statistic wide">
			<template v-if="order">
				<view class="state" :class="`state-${order.status}`">{{dictLabel(assetStatusOptions,order.status)}}</view>
			</template>
			<view class="label">
				<template v-if="order">{{ t('components.assetStatistics.totalAmount') }}</template>
				<template v-else>{{ t('components.assetStatistics.totalAssets') }}</template>
				
				<view class="extra">
					<uni-icons 
						v-if="!isHidden"
						class="action" 
						:size="14 * configStore.fontScale" 
						type="eye-filled" 
						color="#A5A5A5"
						@click="toggleVisibility"
					/>
					<uni-icons 
						v-else
						class="action" 
						:size="14 * configStore.fontScale" 
						type="eye-slash-filled" 
						color="#A5A5A5"
						@click="toggleVisibility"
					/>
				</view>
			</view>
			<view class="value">
				<template v-if="isHidden">****</template>
				<template v-else>
					{{ formatPrice(totalAmount, true, 2, true) }}
				</template>
			</view>
			<template v-if="totalIncome">
				<view class="extra">
					<text class="extra-text">{{ t('components.assetStatistics.estimatedIncome') }} {{formatPrice(totalIncome, true, 2, true)}}</text>
				</view>
			</template>
			<template v-if="props.order">
				<view class="extra">
					<text class="extra-text">{{ t('components.assetStatistics.holdQuantity') }} {{props.order.holdQuantity}}</text>
				</view>
			</template>
		</view>
		<view class="asset-statistic" v-if="!order || order.orderStatus === OrderStatus.AUDIT_PASSED" @click="goOrderProfit">
			<view class="label">
				{{ t('components.assetStatistics.totalIncomeRate') }}
				<view class="extra" v-if="totalIncomeRate">
					<view class="rate" :class="{'rate-up':totalIncomeRate > 0, 'rate-down':totalIncomeRate < 0}">
						<template v-if="isHidden">****</template>
						<CompPrice v-else :price="totalIncomeRate" :format="false" :show-unit="false" :precise="2">
							<template #suffix>%</template>
						</CompPrice>
					</view>
				</view>
			</view>
			<view class="value">
				<template v-if="isHidden">****</template>
				<template v-else>
					<CompPrice :price="totalIncome" exact-precise></CompPrice>
					<view class="extra" v-if="props.order && statistic.totalIncome[0] && statistic.totalIncome[0].coinCode !== configStore.currency">
						<text class="extra-text">{{statistic.totalIncome[0].total}} {{statistic.totalIncome[0].coinCode}}</text>
					</view>
				</template>
			</view>
		</view>
		<view class="asset-statistic" v-if="!order || order.orderStatus === OrderStatus.AUDIT_PASSED" @click="goOrderProfit">
			<view class="label">
				{{ t('components.assetStatistics.todayIncomeRate') }}
				<view class="extra" v-if="todayIncomeRate">
					<view class="rate">
						<template v-if="isHidden">****</template>
						<CompPrice v-else :price="todayIncomeRate" :format="false" :show-unit="false" :precise="2">
							<template #suffix>%</template>
						</CompPrice>
					</view>
				</view>
			</view>
			<view class="value">
				<template v-if="isHidden">****</template>
				<template v-else>
					<CompPrice :price="todayIncome" exact-precise></CompPrice>
					<view class="extra" v-if="props.order && statistic.todayIncome[0]">
						<text class="extra-text">{{statistic.todayIncome[0].total}} {{statistic.todayIncome[0].coinCode}}</text>
					</view>
				</template>
			</view>
		</view>
	</view>
</template>

<script lang="ts" setup>
import {computed, createCommentVNode, onMounted, ref} from 'vue';
import type {OrderEntity, ProjectEntity} from '@/types/entity';
import {ApiAssetDetailStatistic, type AssetDetailStatistic} from '@/api/asset/ApiAssetDetailStatistic';
import {formatPrice, transformCurrency, transformCurrencyInfoToUsdt} from "@/library/format.ts";
import {dictLabel} from "@/library/dict.ts";
import {assetStatusOptions, projectStateOptions} from "@/types/options.ts";
import {Currency, OrderStatus, ProjectState} from "@/types/enums.ts";
import CompPrice from "@/components/CompPrice.vue";
import {useConfigStore} from "@/store/config";
import UniRouter from "@/library/UniRouter.ts";
import {useI18n} from "vue-i18n";

interface Props {
	order?: OrderEntity;
	project?: ProjectEntity;
	projectState?: ProjectState;
}

const props = withDefaults(defineProps<Props>(), {});
const configStore = useConfigStore();
const { t } = useI18n();

const isHidden = computed({
	get: () => configStore.assetHidden ?? false,
	set: (value) => {
		configStore.assetHidden = value;
	}
});
const statistic = ref<AssetDetailStatistic>({
	totalAssets: [],
	totalIncome: [],
	todayIncome: [],
	withdrawableBalance: 0,
});

const totalAmount = computed(()=>{
	if(props.order){
		return transformCurrency(props.order.holdAmount, Currency.USDT, props.order.investmentCurrency) + totalIncome.value;
	}
	else{
		return transformCurrencyInfoToUsdt(statistic.value.totalAssets);
	}
})
const investAmount = computed(()=>{
	if(props.order){
		return transformCurrency(props.order.holdAmount, Currency.USDT, props.order.investmentCurrency);
	}
	else{
		return transformCurrencyInfoToUsdt(statistic.value.totalAssets);
	}
})

const totalIncome = computed(()=>{
	return transformCurrencyInfoToUsdt(statistic.value.totalIncome)
})
const todayIncome = computed(()=>{
	return transformCurrencyInfoToUsdt(statistic.value.todayIncome)
})

function goOrderProfit(){
	if(!props.order) {
		UniRouter.to('/pages/order/profit');
	}
	else{
		UniRouter.to('/pages/order/individual-profit', {orderId: props.order.id});
	}
}

const totalIncomeRate = computed(() => {
	if (totalAmount.value === 0) return 0;
	return (totalIncome.value / investAmount.value) * 100;
})

const todayIncomeRate = computed(() => {
	if (totalAmount.value === 0) return 0;
	return (todayIncome.value / investAmount.value) * 100;
})

function toggleVisibility() {
	isHidden.value = !isHidden.value;
	// 状态已自动保存到 configStore
}

async function loadStatistic(){
	try {
		const api = new ApiAssetDetailStatistic({
			orderId: props.order?.id,
		});
		const result = await api.call();
		statistic.value = result.data;
	} catch (error) {
		console.error('Failed to fetch asset statistics:', error);
	}
}

onMounted(async () => {
	loadStatistic();
});

defineExpose({
	load: loadStatistic,
})
</script>

<style scoped lang="scss">
.asset-statistics {
	@include flex-row(stretch, space-between, wrap);
	gap: 6px;
	
	.asset-statistic {
		border: 1px solid #828284;
		border-radius: 10px;
		flex: 1;
		text-align: center;
		padding: 15px 0;
		box-sizing: border-box;
		overflow: hidden;
		
		&.wide {
			position: relative;
			.state{
				position: absolute;
				left: 0;
				top: 0;
				@include fs(10, 15);
				padding: 0 10px;
				border-bottom-right-radius: 10px;
				background-color: $theme-color;
				color: $color-text-white;
			}
			
			background: radial-gradient(
					120% 80% at 80% 10%,
					rgba(255,255,255,0.18) 0%,
					rgba(255,255,255,0.05) 35%,
					rgba(255,255,255,0.00) 60%
			),
			linear-gradient(180deg, #1b1c1f, #121316);
			
			width: 100%;
			flex: none;
			
			.value {
				@include fs(29);
			}
		}

		.label {
			@include fs(14);
			color: $color-text-white;
			@include flex-row(center, center);
			margin-bottom: 5px;
			@include en-break;

			.extra {
				flex: none;
				margin-left: 5px;
				
				.action {
					cursor: pointer;
				}
				
				.rate {
					&.rate-up{
						background: $color-red;
					}
					&.rate-down{
						background: $color-green;
					}
					@include fs(8);
					padding: 1px 4px;
					@include flex(center);
					border-radius: 999em;
					.comp-price{
						color: $color-text-white!important;
					}
				}
			}
		}
		
		.value {
			color: $theme-color;
			@include fs(19);
			.comp-price{
				color: $theme-color!important;
			}
		}
		
		.currency{
			@include fs(10);
		}
		
		.extra{
			margin-top: 4px;
			text-align: center;
			@include fs(9);
			
			.extra-text {
				color: $theme-color;
				background-color: $bg-color-light;
				border-radius: 999em;
				padding: 1px 1em;
				.comp-price{
					color: $theme-color;
				}
			}
		}
	}
}
</style>

