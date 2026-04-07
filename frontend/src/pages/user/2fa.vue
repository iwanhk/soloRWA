<script lang="ts" setup>
import PopupInitBind2Fa from "@/components/popup/PopupInitBind2FA.vue";
import PopupBind2FA from "@/components/popup/PopupBind2FA.vue";
import PopupInput2FA from "@/components/popup/PopupInput2FA.vue";
import {useUserStore} from "@/store/user";
import {TwoFactorAuthStatus} from "@/types/enums";
import {ShowInfo} from "@/library/ShowInfo";
import {ApiUserVerify2FA} from "@/api/user/ApiUserVerify2FA";
import type {PopupInstance} from "@/types/popup";
import {dictLabel} from "@/library/dict.ts";
import {twoFactorAuthStatusOptions} from "@/types/options.ts";
import {useI18n} from "vue-i18n";
import {useConfigStore} from "@/store/config.ts";
import PopupInputCode from "@/components/popup/PopupInputCode.vue";
import {CodeScene} from "@/api/generic/ApiSendSmsCode.ts";
import {ApiUserUnbind2FA} from "@/api/user/ApiUserUnbind2FA.ts";

const { t } = useI18n()

const userStore = useUserStore();
const configStore = useConfigStore();
const fontScale = computed(()=>configStore.fontScale);
const initBind2FAPopup = ref<PopupInstance<boolean>>();
const bind2FAPopup = ref<PopupInstance<boolean>>();
const input2FAPopup = ref<PopupInstance<string>>();
const unbind2FAPopup = ref<PopupInstance<[string, string]>>();

async function handleClick(){
	switch(userStore.user?.twoFactorAuthStatus) {
		case TwoFactorAuthStatus.ENABLED: {
			const result = await ShowInfo.choosAction([
				{label: t('pages.user.twofa.verify'), value: 'verify'},
				{label: t('pages.user.twofa.unbind'), value: 'unbind'},
			] as const);
			switch (result){
				case "verify":
					await verify2FA();
					break;
				case "unbind":
					await unbind2FA();
					break;
			}

			break;
		}
		case TwoFactorAuthStatus.PENDING_VERIFICATION:{
			const result = await ShowInfo.choosAction([
				{label: t('pages.user.twofa.verifyAndBind'), value: 'bind'},
				{label: t('pages.user.twofa.rebind'), value: 'init'},
			] as const);
			switch (result){
				case "init":
					await initBind2FA();
					break;
				case "bind":
					await bind2FA();
					break;
			}

			break;
		}
		case TwoFactorAuthStatus.DISABLED:{
			await initBind2FA();
		}
	}
}

async function initBind2FA(){
	const result = await initBind2FAPopup.value?.open();
	if(result){
		await bind2FA()
	}
}

async function bind2FA(){
	await bind2FAPopup.value?.open();
}

async function verify2FA(){
	try {
		const code = await input2FAPopup.value?.open();
		if (!code) {
			return;
		}

		const api = new ApiUserVerify2FA(code);
		const {data} = await api.call();
		if(data){
			ShowInfo.toast(t('pages.user.twofa.verifySuccess'), "success");
		}
		else{
			ShowInfo.toast(t('pages.user.twofa.verifyFailed'), "error");
		}

		await userStore.loadUserInfo();
	} catch (error) {
		ShowInfo.toast(t('pages.user.twofa.verifyError'));
	}
}

async function unbind2FA(){
	if(await ShowInfo.confirm(t('pages.user.twofa.confirmUnbind'))) {
		const result = await unbind2FAPopup.value?.open();
		if(!result){
			return;
		}

		try {
			const [smsCode, emailCode] = result;

			const api = new ApiUserUnbind2FA({
				emailCode: emailCode,
				code: smsCode,
			})
			const { data } = await api.call()
			if (data) {
				ShowInfo.toast(t('popup.unbind2fa.successMsg'), "success")
				await userStore.loadUserInfo()
			} else {
				ShowInfo.toast(t('popup.unbind2fa.failMsg'), "error")
			}
		} catch (error) {
			ShowInfo.toast(t('popup.unbind2fa.errorMsg'))
		}
	}
}
</script>

<template>
	<view class="page page-2fa">
		<view class="status-block" @click="handleClick">
			<view class="status-title">{{ t('pages.user.twofa.title') }}</view>
			<view class="status-badge" :class="`status-${userStore.user?.twoFactorAuthStatus}`">
				{{ dictLabel(twoFactorAuthStatusOptions, userStore.user?.twoFactorAuthStatus) }}
			</view>
		</view>
		<PopupInitBind2Fa ref="initBind2FAPopup" />
		<PopupBind2FA ref="bind2FAPopup" />
		<PopupInput2FA ref="input2FAPopup" />
		<PopupInputCode :sms-scene="CodeScene.UNBIND_2FA" ref="unbind2FAPopup" :need2-fa="false" />
	</view>
</template>

<style scoped lang="scss">
.page{
	--font-scale:v-bind(fontScale);
}
.page-2fa {
	.status-block {
		display: flex;
		justify-content: space-between;
		align-items: center;
		background-color: $bg-color-light;
		padding: 8px;
		border-radius: 8px;
		margin-bottom: 20px;

		.status-title {
			color: $color-text-white;
			@include fs(14);
			font-weight: 500;
		}

		.status-badge {
			padding: 4px 12px;
			border-radius: 4px;
			@include fs(12);
			font-weight: 500;
			color: $color-text-white;

			&.status-0 {
				color: #878787;
			}

			&.status-1 {
				color: $theme-color;
			}

			&.status-2 {
				color: #F0AD4E;
			}
		}
	}
}
</style>

