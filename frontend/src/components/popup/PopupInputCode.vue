<script setup lang="ts">

import CompPopup from "@/components/popup/CompPopup.vue";
import {CodeScene} from "@/api/generic/ApiSendSmsCode.ts";
import {ShowInfo} from "@/library/ShowInfo.ts";
import type {PopupInstance} from "@/types/popup.ts";
import {defaultPopupExpose} from "@/library/PopupManager.ts";
import {useUserStore} from "@/store/user.ts";
import {InfoMask} from "@/library/InfoMask.ts";
import CaptchaButton from "@/components/CaptchaButton.vue";
import {type MobileEmail, TwoFactorAuthStatus} from "@/types/enums.ts";
import {useI18n} from "vue-i18n";
import ENV from "@/library/env.ts";

const { t } = useI18n();
const userStore = useUserStore()
const dialog = ref<PopupInstance>()
const isSubmitting = ref(false)
const code = ref('')
const ftaCode = ref('')

defineExpose(defaultPopupExpose(dialog, {
	open() {
		code.value = ''
		ftaCode.value = ''
	}
}))

const mode = ref<MobileEmail>("Email");
const userPhone = computed(() => userStore.user?.mobile ? InfoMask.phone(userStore.user.mobile) : '')
const userEmail = computed(() => userStore.user?.email ? InfoMask.email(userStore.user.email) : '');

if(!userEmail.value && userPhone.value){
	mode.value = 'Mobile';
}

const props = withDefaults(defineProps<{
	smsScene:CodeScene,
	confirmText?: string,
	need2Fa?: boolean,
}>(), {
	confirmText: '确认',
	need2Fa:true,
})

const smsButton = ref<{ lastSendTime:number }>()

const codeSent = computed(()=>{
	return (smsButton.value?.lastSendTime??0) > 0
})

const is2FAEnabled = computed(() =>
	userStore.user?.twoFactorAuthStatus === TwoFactorAuthStatus.ENABLED && props.need2Fa
)

function switchMode() {
	mode.value = mode.value === 'Mobile' ? 'Email' : 'Mobile'
	code.value = ''
}

function confirm() {
	if (!code.value?.match(/^\d{4}$/)) {
		ShowInfo.toast(t('popup.inputCode.codeInputHint'))
		return
	}

	if (is2FAEnabled.value && !ftaCode.value?.match(/^\d{6}$/)) {
		ShowInfo.toast(t('popup.inputCode.twoFactorHint'))
		return
	}

	// 返回验证码给父组件，由父组件处理提交
	// 返回数组 [smsCode, emailCode, ftaCode]
	if(mode.value === "Mobile"){
		dialog.value?.result([code.value, '', ftaCode.value || ''])
	}
	else{
		dialog.value?.result(['', code.value, ftaCode.value || '' ])
	}
}

</script>

<template>
	<CompPopup ref="dialog" :title="t('popup.inputCode.title')">
		<template #actions>
			<slot name="actions">
				<up-button
				:disabled="isSubmitting"
				type="warning"
				shape="circle"
				@click="confirm"
				size="normal"
			>
				{{ confirmText }}
			</up-button>
			</slot>
		</template>
		<view class="content">
			<slot name="prefix"></slot>
			<view class="form">
				<view class="title-code" v-if="codeSent">
					<template v-if="mode === 'Mobile'">{{ t('popup.inputCode.smsCodeSentHint', { phone: userPhone }) }}</template>
					<template v-else-if="mode === 'Email'">{{ t('popup.inputCode.emailCodeSentHint', { email: userEmail }) }}</template>
				</view>
				<view class="code-input">
					<up-code-input :maxlength="4" :size="30" color="#000000" v-model="code"></up-code-input>
				</view>
				<view class="send-code-action">
					<CaptchaButton ref="smsButton" :scene="smsScene" :phone="userStore.user?.mobile" :email="userStore.user?.email" :type="mode">
						<template v-if="mode === 'Mobile'">{{ t('popup.inputCode.getSmsCodeBtn') }}</template>
						<template v-else>{{ t('popup.inputCode.getEmailCodeBtn') }}</template>
					</CaptchaButton>
				</view>
				<view class="switch-action" v-if="userPhone && userEmail && ENV.foreign">
					<text class="link-action" @click="switchMode">
						<template v-if="mode === 'Mobile'">{{ t('popup.inputCode.switchToEmail') }}</template>
						<template v-else>{{ t('popup.inputCode.switchToSms') }}</template>
					</text>
				</view>
			</view>
			<!-- 2FA 验证码输入 -->
			<view v-if="is2FAEnabled" class="form form-2fa">
				<view class="title-code">{{ t('popup.input2fa.verifyCodeHint') }}</view>
				<view class="code-input">
					<up-code-input :maxlength="6" :size="30" color="#000000" v-model="ftaCode"></up-code-input>
				</view>
			</view>
			<slot name="suffix"></slot>
		</view>
	</CompPopup>
</template>

<style scoped lang="scss">
.form {
	margin-left: auto;
	margin-right: auto;
	text-align: center;

	.title-code {
		@include fs(12, 18);
		color: $color-text-black;
		text-align: center;
		margin-bottom: 20px;
	}

	.u-code-input {
		:deep(.u-code-input__item) {
			background: #fff;
			border-radius: 3px;
		}
	}

	.send-code-action {
		margin-top: 12px;
		text-align: center;

		.countdown {
			@include fs(12);
			color: $color-gray;
		}
	}

	.switch-action {
		margin-top: 10px;
		@include flex-row(center, center);

		.link-action {
			@include fs(10);
			color: $theme-color;
		}
	}

	&.form-2fa {
		margin-top: 20px;
		width: 240px;

		.title-code {
			margin-bottom: 20px;
		}
	}
}
</style>