<template>
	<view class="page page-bill-list">
		<view class="bill-switcher">
			<Switcher :options="auditStatusOptions" v-model="selectedAuditStatus" />
		</view>

		<view class="order-filter-tip" v-if="orderId">
			<text>{{ t('pages.bill.list.orderFilterTip', {orderId: orderId}) }}</text>
			<text class="close-btn" @click="clearOrderId">×</text>
		</view>

		<view class="bill-list" v-if="billList">
			<BillCard :bill="bill" v-for="bill of billList"></BillCard>
		</view>

		<CompLoadMore :status="billListStatus" />
	</view>
</template>

<script lang="ts" setup>
import {onLoad, onUnload} from "@dcloudio/uni-app";
import {useTabListManager} from "@/composable/useTabListManager.ts";
import {ApiPagedListHandlerFactory} from "@/library/PagedListHandler.ts";
import type {BillEntity} from "@/types/entity.ts";
import {ApiGetBillList} from "@/api/order/ApiGetBillList.ts";
import {NwEvent} from "@/types/api.ts";
import BillCard from "@/components/BillCard.vue";
import CompLoadMore from "@/components/CompLoadMore.vue";
import Switcher from "@/components/Switcher.vue";
import {auditStatusOptions} from "@/types/options.ts";
import {AuditStatus} from "@/types/enums.ts";
import UniRouter from "@/library/UniRouter.ts";
import {useI18n} from "vue-i18n";
import {useConfigStore} from "@/store/config.ts";

const props = defineProps<{
	orderId?: string
}>()

const {t} = useI18n();
const configStore = useConfigStore();
const fontScale = computed(()=>configStore.fontScale);
const orderId = computed(()=>props.orderId? parseInt(props.orderId) : undefined)

const selectedAuditStatus = ref<AuditStatus | -1>(-1);

const tabListManager = useTabListManager<BillEntity>();
tabListManager.setProvide((identifier)=>{
	const status = parseInt(identifier);
	if(status === -1){
		return ApiPagedListHandlerFactory(ApiGetBillList, {orderId:orderId.value})
	}
	else{
		return ApiPagedListHandlerFactory(ApiGetBillList, {auditStatus: status, orderId: orderId.value})
	}
})

const billList = tabListManager.records;
const billListStatus = tabListManager.status;

watch(selectedAuditStatus, (newStatus)=>{
	tabListManager.show(newStatus.toString());
}, {immediate:true})

function refreshBillList(){
	tabListManager.refresh();
}

function clearOrderId(){
	UniRouter.redirect('/pages/bill/list');
}

onLoad(()=>{
	uni.$on(NwEvent.ORDER_CHANGED, refreshBillList);
})
onUnload(()=>{
	uni.$off(NwEvent.ORDER_CHANGED, refreshBillList);
})

</script>

<style scoped lang="scss">
.page{
	--font-scale:v-bind(fontScale);
}
.bill-switcher{
	color: $color-text-white;
	.switcher{
		@include fs(14, 28);
		&.switcher-display-tab{
			:deep(.switcher-item:not(:last-child)){
				margin-right: 1.5em;
			}
		}

		margin-bottom: 20px;
	}
}

.order-filter-tip{
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding: 4px 8px;
	margin-bottom: 12px;
	background-color: $bg-color-light;
	border-radius: 4px;
	color: $color-gray;
	@include fs(12);

	.close-btn{
		color: $color-gray;
		cursor: pointer;
		padding: 4px;
		line-height: 1;

		&:active{
			color: $color-text-white;
		}
	}
}

.bill-list{
	.bill-card{
		margin-bottom: 10px;
	}
}
</style>

