<template>
	<view class="page page-order-list">
		<view class="order-switcher">
			<Switcher :options="orderStatusOptions" v-model="selectedOrderStatus" />
		</view>

		<view class="order-list" v-if="orderList">
			<OrderCard :order="order" v-for="order of orderList"></OrderCard>
		</view>

		<CompLoadMore :status="orderListStatus" />
	</view>
</template>

<script lang="ts" setup>

import {useUserStore} from "@/store/user.ts";
import UniRouter from "@/library/UniRouter.ts";
import {onLoad, onUnload} from "@dcloudio/uni-app";
import {useTabListManager} from "@/composable/useTabListManager.ts";
import {ApiPagedListHandlerFactory} from "@/library/PagedListHandler.ts";
import type {OrderListEntity} from "@/types/entity.ts";
import {ApiGetOrderList} from "@/api/order/ApiGetOrderList.ts";
import {OrderStatus} from "@/types/enums.ts";
import {NwEvent} from "@/types/api.ts";
import OrderCard from "@/components/OrderCard.vue";
import CompLoadMore from "@/components/CompLoadMore.vue";
import Switcher from "@/components/Switcher.vue";
import {orderStatusOptions} from "@/types/options.ts";
import {useConfigStore} from "@/store/config.ts";


const userStore = useUserStore();
const configStore = useConfigStore();
const fontScale = computed(()=>configStore.fontScale);

const selectedOrderStatus = ref<OrderStatus | -1>(-1);

const tabListManager = useTabListManager<OrderListEntity>();
tabListManager.setProvide((identifier)=>{
	const status = parseInt(identifier);
	if(status === -1){
		return ApiPagedListHandlerFactory(ApiGetOrderList)
	}
	else{
		return ApiPagedListHandlerFactory(ApiGetOrderList, {orderStatus: status})
	}
})

const orderList = tabListManager.records;
const orderListStatus = tabListManager.status;

watch(selectedOrderStatus, (newStatus)=>{
	tabListManager.show(newStatus.toString());
}, {immediate:true})

function showOrder(order:OrderListEntity){
	switch(order.orderStatus){
		case OrderStatus.PENDING_PAYMENT:
			UniRouter.to('/pages/order/pay', {id: order.id});
			break;
		case OrderStatus.CANCELLED:
			break;
		default:
			UniRouter.to('/pages/order/detail', {id: order.id});
	}
}

function refreshOrderList(){
	tabListManager.refresh();
}

onLoad(()=>{
	uni.$on(NwEvent.ORDER_CHANGED, refreshOrderList);
})
onUnload(()=>{
	uni.$off(NwEvent.ORDER_CHANGED, refreshOrderList);
})

</script>

<style scoped lang="scss">
.page{
	--font-scale:v-bind(fontScale);
}
.order-switcher{
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

.order-list{
	.order-card{
		margin-bottom: 10px;
	}
}
</style>

