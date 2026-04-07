<script setup lang="ts">
import CompPopup from "@/components/popup/CompPopup.vue";
import {defaultPopupExpose} from "@/library/PopupManager";
import type {PopupInstance} from "@/types/popup";
import {ShowInfo} from "@/library/ShowInfo";
import {ApiUserInit2FA, type Init2FAResult} from "@/api/user/ApiUserInit2FA";
import {setClipboard} from "@/library/Utility";
import ENV from "@/library/env";
import {UniPlatformType} from "@/library/getPlatform";
import {useI18n} from "vue-i18n";

const { t } = useI18n();
const isLoading = ref(false);
const init2FAResult = ref<Init2FAResult | null>(null);

const dialog = ref<PopupInstance>();
defineExpose(defaultPopupExpose(dialog, {
	async open(){
		await init2FA();
	}
}))

// 初始化 2FA
async function init2FA(){
	try {
		isLoading.value = true;
		const api = new ApiUserInit2FA();
		const response = await api.call();
		init2FAResult.value = response.data;
	} catch (error) {
		ShowInfo.toast(t('popup.initBind2fa.initFailMsg'));
		dialog.value?.close();
	} finally {
		isLoading.value = false;
	}
}

// 打开二维码链接
function openQRCode(){
	if (init2FAResult.value?.qrCodeUrl) {
		if(ENV.platform === UniPlatformType.APP){
			plus.runtime.openURL(init2FAResult.value.qrCodeUrl)
		}
		else{
			window.open(init2FAResult.value.qrCodeUrl);
		}
	}
}

// 复制密钥
async function copySecret(){
	if (init2FAResult.value?.secret) {
		await setClipboard(init2FAResult.value.secret, '密钥');
	}
}

function verify(){
	dialog.value?.result(true);
}

</script>

<template>
	<CompPopup ref="dialog" :title="t('popup.initBind2fa.title')">
		<view class="content" v-if="!isLoading && init2FAResult">
			<view class="qrcode-container">
				<u-qrcode cid="qr2fa" :val="init2FAResult.qrCodeUrl" :use-root-height-and-width="false" :size="200"></u-qrcode>
			</view>
		</view>
		<view class="loading-container" v-else-if="isLoading">
			<up-loading-icon></up-loading-icon>
		</view>
		<template #actions>
			<up-button type="primary" shape="circle" @click="openQRCode" size="normal">
				{{ t('popup.initBind2fa.addBtn') }}
			</up-button>
			<up-button type="warning" shape="circle" @click="copySecret" size="normal">
				{{ t('popup.initBind2fa.copyBtn') }}
			</up-button>
			<up-button type="primary" shape="circle" @click="verify" size="normal">
				{{ t('popup.initBind2fa.verifyBtn') }}
			</up-button>
		</template>
	</CompPopup>
</template>

<style scoped lang="scss">
.content {
	padding: 20px 0;
	text-align: center;

	.qrcode-container {
		margin-bottom: 20px;
		display: flex;
		justify-content: center;
	}
}

.loading-container {
	padding: 40px 0;
	display: flex;
	justify-content: center;
	align-items: center;
}
</style>

