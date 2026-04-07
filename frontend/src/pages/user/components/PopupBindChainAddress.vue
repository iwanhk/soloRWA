<script setup lang="ts">
import CompPopup from "@/components/popup/CompPopup.vue";
import {defaultPopupExpose} from "@/library/PopupManager";
import type {PopupInstance} from "@/types/popup";
import {ShowInfo} from "@/library/ShowInfo";
import {ApiGetSignMessage} from "@/api/user/ApiGetSignMessage";
import {ApiBindChainAddress} from "@/api/user/ApiBindChainAddress";
import {useUserStore} from "@/store/user";
import CenterEllipseText from "@/components/CenterEllipseText.vue";
import {useWallet} from "@/composable/useWallet.ts";
import {useI18n} from "vue-i18n";
const { t } = useI18n()
const userStore = useUserStore();
const isSubmitting = ref(false);

const {address, isConnected, connect, switchAddress, signMessage} = useWallet();

const dialog = ref<PopupInstance<boolean>>();
defineExpose(defaultPopupExpose(dialog, {
	open() {
		isSubmitting.value = false;
	}
}));

// 连接钱包
async function connectWallet() {
	try {
		await connect();
	} catch (error) {
		ShowInfo.toast(t('pages.user.bindChainAddress.connectError'));
	}
}

// 切换钱包地址
async function handleSwitchAddress() {
	try {
		await switchAddress();
	} catch (error) {
		ShowInfo.toast(t('pages.user.bindChainAddress.switchError'));
	}
}

// 确认绑定
async function confirmBind() {
	if (!isConnected.value || !address.value) {
		ShowInfo.toast(t('pages.user.bindChainAddress.notConnected'));
		return;
	}

	isSubmitting.value = true;

	try {
		// 1. 获取待签名消息
		const signMessageApi = new ApiGetSignMessage(address.value);
		const {data: message} = await signMessageApi.call();

		if (!message) {
			ShowInfo.toast(t('pages.user.bindChainAddress.messageError'));
			return;
		}

		// 2. 使用钱包签名
		let signature: string;
		try {
			signature = await signMessage(message);
		} catch (error: any) {
			ShowInfo.toastError(t('pages.user.bindChainAddress.getSignatureFailed'));
			return;
		}

		// 3. 调用绑定接口
		try {
			const bindApi = new ApiBindChainAddress({
				chainAddress: address.value,
				sign: signature
			});
			await bindApi.call();
		} catch (error: any) {
			ShowInfo.toastError(t('pages.user.bindChainAddress.submitBindFailed'));
			return;
		}

		ShowInfo.toast(t('pages.user.bindChainAddress.bindSuccess'), "success");

		// 重新加载链地址列表
		await userStore.loadUserChainAddressList();

		dialog.value?.result(true);
	} catch (error: any) {
		ShowInfo.toastError(error || t('pages.user.bindChainAddress.confirmButton'));
	} finally {
		isSubmitting.value = false;
	}
}
</script>

<template>
	<CompPopup ref="dialog" :title="t('pages.user.bindChainAddress.title')">
		<view class="content">
			<view v-if="isConnected && address" class="connected-info">
				<view class="address-label">{{ t('pages.user.bindChainAddress.connectedAddress') }}</view>
				<view class="address-value">
					<CenterEllipseText :text="address"></CenterEllipseText>
					<view class="switch-action">
						<up-button type="warning" size="small" shape="circle" @click="handleSwitchAddress">
							{{ t('pages.user.bindChainAddress.switch') }}
						</up-button>
					</view>
				</view>
			</view>
			<view v-else class="connect-prompt">
				<view class="prompt-text">{{ t('pages.user.bindChainAddress.connectPrompt') }}</view>
				<up-button type="primary" shape="circle" @click="connectWallet">
					{{ t('pages.user.bindChainAddress.connectButton') }}
				</up-button>
			</view>
		</view>
		<template #actions>
			<up-button
				v-if="isConnected"
				type="primary"
				shape="circle"
				@click="confirmBind"
				size="normal"
				:disabled="isSubmitting || !address"
				:loading="isSubmitting"
			>
				{{ t('pages.user.bindChainAddress.confirmButton') }}
			</up-button>
		</template>
	</CompPopup>
</template>

<style scoped lang="scss">
.content {
	padding: 20px 0;

	.connected-info {
		text-align: center;

		.address-label {
			@include fs(12);
			color: $color-gray;
			margin-bottom: 10px;
		}

		.address-value {
			@include fs(14);
			color: $color-text-black;
			margin-bottom: 16px;
			@include flex-row(center);
		}

		.switch-action {
			margin-left: 0.5em;
			width: 3em;
			flex: none;
			.u-button{
				height: 2em;
			}
		}
	}

	.connect-prompt {
		text-align: center;

		.prompt-text {
			@include fs(14);
			color: $color-text-black;
			margin-bottom: 20px;
		}
	}
}
</style>

