<script setup lang="ts">
import timer from "@/library/Timer";
import {ApiSendSmsCode, type CodeScene} from "@/api/generic/ApiSendSmsCode";
import {ApiSendEmailCode} from "@/api/generic/ApiSendEmailCode";
import {useI18n} from "vue-i18n";
import {formatPhoneNumber, validatePhoneNumber} from "@/library/PhoneNumberUtil";
import type {MobileEmail} from "@/types/enums";

const props = withDefaults(defineProps<{
	phone?: string,
	email?: string,
	scene: CodeScene,
	countryCode?: string,
	type?: MobileEmail,
}>(), {
	type: 'Mobile'
})
const { t } = useI18n()
const phoneValid = computed(() => validatePhoneNumber(props.phone || '', props.countryCode))
const emailValid = computed(() => {
	const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
	return !!props.email && emailRegex.test(props.email);
})

const isValid = computed(() => props.type === 'Mobile' ? phoneValid.value : emailValid.value);

const SEND_INTERVAL = 60000;

const lastSendTime = ref(0);
const isSending = ref(false);
const remainingSeconds = computed(()=>{
	if(!lastSendTime.value)return 0;

	return Math.max(Math.ceil((SEND_INTERVAL - (timer.second.value - lastSendTime.value))/1000), 0);
})

defineExpose({
	lastSendTime
})

async function send(){
	if (!isValid.value || remainingSeconds.value > 0 || isSending.value) {
		return;
	}

	try {
		isSending.value = true;

		let api;
		if (props.type === 'Mobile') {
			// 格式化手机号（添加国家前缀）
			const formattedPhone = formatPhoneNumber(props.phone || '', props.countryCode || '');

			// 调用发送验证码 API
			api = new ApiSendSmsCode({
				mobile: formattedPhone,
				scene: props.scene,
			})
		} else {
			// 调用发送邮箱验证码 API
			api = new ApiSendEmailCode({
				email: props.email || '',
				scene: props.scene,
			})
		}

		await api.call()

		// 发送成功，记录发送时间
		lastSendTime.value = timer.second.value;

		// 显示成功提示
		uni.showToast({
			title: t('components.captchaButton.codeSent'),
			icon: 'success',
			duration: 2000
		})
	} catch (error) {
		console.error('发送验证码失败:', error)
		uni.showToast({
			title: error instanceof Error ? error.message : t('components.captchaButton.sendFailed'),
			icon: 'error',
			duration: 2000
		})
	} finally {
		isSending.value = false;
	}
}
</script>

<template>
	<up-button type="primary" size="mini" @click="send" class="captcha-button" :disabled="!isValid || !!remainingSeconds || isSending" :loading="isSending">
		<template v-if="!remainingSeconds"><slot>{{ t('components.captchaButton.get') }}</slot></template>
		<template v-else>{{remainingSeconds}}s</template>
	</up-button>
</template>

<style scoped lang="scss">
.captcha-button{
	@include fs(12);
	border-radius: 0.5em;
}
</style>