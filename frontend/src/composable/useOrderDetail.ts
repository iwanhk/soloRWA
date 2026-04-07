import type {OrderEntity} from "@/types/entity.ts";
import {ApiGetOrderDetail} from "@/api/order/ApiGetOrderDetail.ts";
import {ShowInfo} from "@/library/ShowInfo.ts";

export function useOrderDetail(){
	const order = ref<OrderEntity | undefined>(undefined)
	const loading = ref(false);
	const orderId = ref(0);
	
	async function load(id?:number) {
		if(id){
			orderId.value = id;
		}
		else{
			if(!orderId.value){
				throw new Error("orderID not provided")
			}
			id = orderId.value;
		}
		
		try {
			loading.value = true
			const api = new ApiGetOrderDetail( id )
			const { data } = await api.call()
			order.value = data
		} catch (error) {
			console.error('加载订单详情失败:', error)
			ShowInfo.toastError(error, '加载资产')
		} finally {
			loading.value = false
		}
	}
	
	return {
		order, loading, load, id:orderId,
	}
}