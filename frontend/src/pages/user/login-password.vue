<script setup lang="ts">
import {ApiUserResetPassword} from "@/api/user/ApiUserResetPassword";
import {ApiUserEmailResetPassword} from "@/api/user/ApiUserEmailResetPassword";
import {CodeScene} from "@/api/generic/ApiSendSmsCode";
import {useUserStore} from "@/store/user";
import CaptchaButton from "@/components/CaptchaButton.vue";
import CompInput from "@/components/CompInput.vue";
import type {UniFormsInstance, UniFormsRules} from "@/types/uni-ui";
import {useI18n} from "vue-i18n";
import {useConfigStore} from "@/store/config.ts";
import type {MobileEmail} from "@/types/enums";
import ENV from "@/library/env.ts";
const { t } = useI18n()

const userStore = useUserStore();
const configStore = useConfigStore();
const fontScale = computed(()=>configStore.fontScale);
const passwordForm = ref<UniFormsInstance>();
const isSubmitting = ref(false);

const mode = ref<MobileEmail>(ENV.foreign?'Mobile':'Email');

const formData = reactive({
	newPassword: '',
	confirmPassword: '',
	verificationCode: '',
});

const phoneNumber = computed(() => {
	return userStore.user?.mobile || '';
});

const userEmail = computed(() => {
	return userStore.user?.email || '';
});

function switchMode() {
	mode.value = mode.value === 'Mobile' ? 'Email' : 'Mobile'
	formData.verificationCode = ''
}

const verificationCodeLabel = computed(() => {
	return mode.value === 'Mobile' ? t('pages.user.loginPassword.smsCode') : t('pages.user.loginPassword.emailCode')
})

const rules = computed<UniFormsRules>(() => ({
	newPassword: {
		rules: [
			{ required: true, errorMessage: t('pages.user.loginPassword.newPasswordError') },
			{ minLength: 6, errorMessage: t('pages.user.loginPassword.passwordLengthError') },
		],
		label: t('pages.user.loginPassword.newPassword')
	},
	confirmPassword: {
		rules: [
			{ required: true, errorMessage: t('pages.user.loginPassword.confirmPasswordError') },
			{
				validateFunction: (rule: any, value: any, data: any, callback: any) => {
					if (value !== data.newPassword) {
						callback(t('pages.user.loginPassword.passwordMismatch'));
						return false;
					}
					return true;
				},
			},
		],
		label: t('pages.user.loginPassword.confirmPassword')
	},
	verificationCode: {
		rules: [
			{ required: true, errorMessage: verificationCodeLabel.value },
		],
		label: verificationCodeLabel.value
	},
}));

async function handleResetPassword() {
	try {
		// 验证表单
		await passwordForm.value?.validate();

		isSubmitting.value = true;
		let api:ApiUserEmailResetPassword|ApiUserResetPassword;
		if (mode.value === 'Email') {
			api = new ApiUserEmailResetPassword({
				email: userEmail.value,
				code: formData.verificationCode,
				newPassword: formData.newPassword,
			});
		} else {
			api = new ApiUserResetPassword({
				mobile: phoneNumber.value,
				code: formData.verificationCode,
				emailCode: '',
				newPassword: formData.newPassword,
			});
		}

		await api.call();

		uni.showToast({
			title: t('pages.user.loginPassword.resetSuccess'),
			icon: 'success',
		});

		// 重置表单
		formData.newPassword = '';
		formData.confirmPassword = '';
		formData.verificationCode = '';

		// 返回上一页
		setTimeout(() => {
			uni.navigateBack();
		}, 1500);
	} catch (error) {
		console.error('密码重置失败:', error);
		if (error instanceof Error) {
			uni.showToast({
				title: error.message,
				icon: 'error',
			});
		}
	} finally {
		isSubmitting.value = false;
	}
}
</script>

<template>
	<view class="page page-password">
		<uni-forms
			ref="passwordForm"
			class="form"
			label-width="7em"
			label-align="left"
			err-show-type="toast"
			:rules="rules"
			:model="formData"
		>
			<!-- 新密码 -->
			<uni-forms-item label-position="left" :label="t('pages.user.loginPassword.newPassword')" name="newPassword">
				<CompInput
					v-model="formData.newPassword"
					type="password"
					theme="dark"
					:placeholder="t('pages.user.loginPassword.newPassword')"
				/>
			</uni-forms-item>

			<!-- 确认新密码 -->
			<uni-forms-item label-position="left" :label="t('pages.user.loginPassword.confirmPassword')" name="confirmPassword">
				<CompInput
					v-model="formData.confirmPassword"
					type="password"
					theme="dark"
					:placeholder="t('pages.user.loginPassword.confirmPassword')"
				/>
			</uni-forms-item>

			<!-- 验证码 -->
			<uni-forms-item label-position="left" :label="verificationCodeLabel" name="verificationCode">
				<view class="captcha-input-wrapper">
					<CompInput
						v-model="formData.verificationCode"
						type="text"
						theme="dark"
						:placeholder="verificationCodeLabel"
						class="captcha-input"
					/>
					<CaptchaButton
						:phone="phoneNumber"
						:email="userEmail"
						:type="mode"
						:scene="CodeScene.RESET_PASSWORD"
					/>
				</view>
			</uni-forms-item>

			<!-- 切换验证方式 -->
			<view class="switch-action" v-if="phoneNumber && userEmail && ENV.foreign">
				<text class="link-action" @click="switchMode">
					<template v-if="mode === 'Mobile'">{{ t('popup.inputCode.switchToEmail') }}</template>
					<template v-else>{{ t('popup.inputCode.switchToSms') }}</template>
				</text>
			</view>

			<!-- 提交按钮 -->
			<view class="button-container">
				<up-button
					type="primary"
					shape="circle"
					@click="handleResetPassword"
					:loading="isSubmitting"
					:disabled="isSubmitting"
				>
					{{ t('pages.user.loginPassword.resetButton') }}
				</up-button>
			</view>
		</uni-forms>
	</view>
</template>

<style scoped lang="scss">
.page{
	--font-scale:v-bind(fontScale);
}
.form-input {
	flex: 1;
	background: transparent;
	color: $color-text-white;
	@include fs(12);
	outline: none;
	border: none;

	&::placeholder {
		color: $color-text-gray;
	}
}

.captcha-input-wrapper {
	display: flex;
	align-items: center;
	gap: 8px;
	flex: 1;

	.form-input {
		flex: 1;
	}
	.captcha-button{
		background-color: transparent;
		color: $theme-color;
		border: none;
		margin-right: 0;
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

.button-container {
	margin-top: 40px;
}
</style>

