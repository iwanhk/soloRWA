<script setup lang="ts">
import {useUserStore} from "@/store/user";
import ChainAddressCard from "@/components/ChainAddressCard.vue";
import {ApiUnbindChainAddress} from "@/api/user/ApiUnbindChainAddress";
import type {PopupInstance} from "@/types/popup";
import PopupBindChainAddress from "@/pages/user/components/PopupBindChainAddress.vue";
import {onLoad, onNavigationBarButtonTap, onPullDownRefresh} from "@dcloudio/uni-app";
import {useI18n} from "vue-i18n";
import {useConfigStore} from "@/store/config.ts";
const { t } = useI18n()
const configStore = useConfigStore();
const fontScale = computed(()=>configStore.fontScale);
const userStore = useUserStore();

const bindChainAddressPopup = ref<PopupInstance<boolean>>();

// 页面加载时确保链地址列表已加载
onLoad(async () => {
	if (!userStore.address) {
		await userStore.loadUserChainAddressList();
	}
});

onNavigationBarButtonTap(()=>{
	bindChainAddress()
})

// 解绑链地址
async function handleUnbind(id: number) {
	const chainAddress = userStore.address?.find(c => c.id === id)?.chainAddress;
	uni.showModal({
		title: t('pages.user.chainAddress.confirmUnbind'),
		content: t('pages.user.chainAddress.unbindMessage', { address: chainAddress }),
		success: async (res) => {
			if (res.confirm) {
				try {
					const api = new ApiUnbindChainAddress({ addressId: id });
					await api.call();
					uni.showToast({
						title: t('pages.user.chainAddress.unbindSuccess'),
						icon: 'success',
					});
					// 重新加载链地址列表
					await userStore.loadUserChainAddressList();
				} catch (error) {
					uni.showToast({
						title: t('pages.user.chainAddress.unbindFailed'),
						icon: 'error',
					});
				}
			}
		}
	});
}

// 绑定链地址
async function bindChainAddress() {
	const result = await bindChainAddressPopup.value?.open();
	if(result){
		await userStore.loadUserChainAddressList();
		await userStore.loadUserInfo();
	}
}

onPullDownRefresh(()=>{
	userStore.loadUserChainAddressList();
	uni.stopPullDownRefresh();
})
</script>

<template>
	<view class="page page-chain-address">
		<!-- 链地址列表 -->
		<view class="address-list" v-if="userStore.address && userStore.address.length > 0">
			<ChainAddressCard
				v-for="item in userStore.address"
				:key="item.chainAddress"
				:item="item"
				@unbind="handleUnbind"
			></ChainAddressCard>
		</view>

		<!-- 空状态 -->
		<view v-else class="empty-state">
			<u-empty :text="t('pages.user.chainAddress.empty')"></u-empty>
		</view>

		<!-- 绑定链地址弹窗 -->
		<PopupBindChainAddress ref="bindChainAddressPopup" />
	</view>
</template>

<style scoped lang="scss">
.page{
	--font-scale:v-bind(fontScale);
}
</style>

