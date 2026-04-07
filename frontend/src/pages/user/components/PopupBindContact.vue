<script setup lang="ts">
import CompPopup from "@/components/popup/CompPopup.vue";
import {defaultPopupExpose} from "@/library/PopupManager";
import type {PopupInstance} from "@/types/popup";
import {ShowInfo} from "@/library/ShowInfo";
import {useUserStore} from "@/store/user";
import {useI18n} from "vue-i18n";
import {ApiUserBindMobile} from "@/api/user/ApiUserBindMobile";
import {ApiUserBindEmail} from "@/api/user/ApiUserBindEmail";
import CaptchaButton from "@/components/CaptchaButton.vue";
import {CodeScene} from "@/api/generic/ApiSendSmsCode";
import CompInput from "@/components/CompInput.vue";
import type {UniFormsInstance, UniFormsRules} from "@/types/uni-ui";
import type {MobileEmail} from "@/types/enums.ts";
import CountryPhoneSelector from "@/components/CountryPhoneSelector.vue";
import ENV from "@/library/env.ts";
import {useConfigStore} from "@/store/config.ts";
import {formatPhoneNumber} from "@/library/PhoneNumberUtil.ts";

const props = defineProps<{
	type: MobileEmail
}>();

const { t } = useI18n()
const userStore = useUserStore();
const configStore = useConfigStore();
const isSubmitting = ref(false);
const bindForm = ref<UniFormsInstance>();

const formData = reactive({
	target: '', // mobile or email
	smsCode: '',
	mailCode: '',
	countryCode:  configStore.countryCode || (ENV.foreign ? 'HK' : 'CN'),
});

const formRules = computed<UniFormsRules>(() => ({
	target: {
		rules: [{ required: true, errorMessage: placeholder.value }],
		label: label.value
	},
	smsCode: {
		rules: [{ required: true, errorMessage: t('pages.user.bind.smsCodePlaceholder') }],
		label: t('pages.user.bind.smsCodeLabel')
	},
	mailCode: {
		rules: [{ required: true, errorMessage: t('pages.user.bind.mailCodePlaceholder') }],
		label: t('pages.user.bind.mailCodeLabel')
	}
}));

const dialog = ref<PopupInstance<boolean>>();
defineExpose(defaultPopupExpose(dialog, {
	open() {
		isSubmitting.value = false;
		formData.target = '';
		formData.smsCode = '';
		formData.mailCode = '';
	}
}));

const title = computed(() => props.type === 'Mobile' ? t('pages.user.bind.titleMobile') : t('pages.user.bind.titleEmail'));
const label = computed(() => props.type === 'Mobile' ? t('pages.user.bind.mobileLabel') : t('pages.user.bind.emailLabel'));
const placeholder = computed(() => props.type === 'Mobile' ? t('pages.user.bind.mobilePlaceholder') : t('pages.user.bind.emailPlaceholder'));

async function confirmBind() {
	await bindForm.value?.validate();
	
	try {
		isSubmitting.value = true;

		if (props.type === 'Mobile') {
			const api = new ApiUserBindMobile({
				mobile: formatPhoneNumber(formData.target, formData.countryCode),
				smsCode: formData.smsCode,
				mailCode: formData.mailCode
			});
			await api.call();
		} else {
			const api = new ApiUserBindEmail({
				email: formData.target,
				smsCode: formData.smsCode,
				mailCode: formData.mailCode
			});
			await api.call();
		}

		ShowInfo.toast(t('pages.user.bind.success'), "success");
		await userStore.loadUserInfo();
		dialog.value?.result(true);
	} catch (error: any) {
		console.error('绑定失败:', error);
		ShowInfo.toast(t('pages.user.bind.fail'), "error");
	} finally {
		isSubmitting.value = false;
	}
}
</script>

<template>
	<CompPopup ref="dialog" :title="title">
		<uni-forms
			ref="bindForm"
			class="form theme-light"
			label-width="7em"
			label-align="left"
			err-show-type="toast"
			:rules="formRules"
			:model="formData"
		>
			<template v-if="type === 'Mobile'">
				<uni-forms-item label-position="left" :label="label" name="target">
					<template #label>
						<CountryPhoneSelector class="uni-forms-item__label"
						      v-model:countryCode="formData.countryCode"
						/>
					</template>
					<CompInput v-model="formData.target" :placeholder="placeholder" theme="light" />
				</uni-forms-item>
			</template>
			<template v-if="type === 'Email'">
				<uni-forms-item label-position="left" :label="label" name="target">
					<CompInput v-model="formData.target" :placeholder="placeholder" theme="light" />
				</uni-forms-item>
			</template>

			<!-- 手机验证码 -->
			<uni-forms-item label-position="left" :label="t('pages.user.bind.smsCodeLabel')" name="smsCode">
				<view class="captcha-input-wrapper">
					<CompInput
						v-model="formData.smsCode"
						:placeholder="t('pages.user.bind.smsCodePlaceholder')"
						theme="light"
						class="captcha-input"
					/>
					<CaptchaButton 
						type="Mobile" 
						:phone="props.type === 'Mobile' ? formData.target : userStore.user?.mobile" 
						:scene="CodeScene.UPDATE_MOBILE"
						:countryCode="formData.countryCode"
					/>
				</view>
			</uni-forms-item>

			<!-- 邮箱验证码 -->
			<uni-forms-item label-position="left" :label="t('pages.user.bind.mailCodeLabel')" name="mailCode">
				<view class="captcha-input-wrapper">
					<CompInput
						v-model="formData.mailCode"
						:placeholder="t('pages.user.bind.mailCodePlaceholder')"
						theme="light"
						class="captcha-input"
					/>
					<CaptchaButton 
						type="Email" 
						:email="props.type === 'Email' ? formData.target : userStore.user?.email" 
						:scene="CodeScene.UPDATE_MOBILE"
					/>
				</view>
			</uni-forms-item>
		</uni-forms>
		<template #actions>
			<up-button
				type="primary"
				shape="circle"
				@click="confirmBind"
				size="normal"
				:disabled="isSubmitting"
				:loading="isSubmitting"
			>
				{{ t('common.button.confirm') }}
			</up-button>
		</template>
	</CompPopup>
</template>

<style scoped lang="scss">
:deep(.country-selector){
	.country-display{
		.flag,.dial-code{
			color: $color-text-black;
		}
	} 
}
.captcha-input-wrapper {
	display: flex;
	align-items: center;
	gap: 8px;
	flex: 1;

	.captcha-input {
		flex: 1;
	}
	:deep(.captcha-button) {
		width: 4em;
		background-color: transparent;
		color: $theme-color;
		border: none;
		margin-right: 0;
		padding: 0;
		@include fs(12);
		height: auto;
		line-height: 1;

		&.u-button--disabled {
			background-color: transparent !important;
			color: $color-text-gray !important;
		}
	}
}
</style>
