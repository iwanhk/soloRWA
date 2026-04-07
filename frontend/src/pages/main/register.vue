<script setup lang="ts">
import UniRouter from "@/library/UniRouter";
import CompInput from "@/components/CompInput.vue";
import type {UniFormsInstance, UniFormsRules} from "@/types/uni-ui";
import CaptchaButton from "@/components/CaptchaButton.vue";
import NavBar from "@/components/NavBar.vue";
import {CodeScene} from "@/api/generic/ApiSendSmsCode";
import {ApiUserRegister} from "@/api/user/ApiUserRegister";
import {ApiUserEmailRegister} from "@/api/user/ApiUserEmailRegister";
import {useUserStore} from "@/store/user";
import type {MobileEmail} from "@/types/enums";
import {loginPage, mainPage} from "@/library/GlobalVars";
import ProtocolConfirm from "@/components/ProtocolConfirm.vue";
import {ShowInfo} from "@/library/ShowInfo";
import {useConfigStore} from "@/store/config.ts";
import { useI18n } from 'vue-i18n';
import CountryPhoneSelector from "@/components/CountryPhoneSelector.vue";
import { formatPhoneNumber, validatePhoneNumber } from "@/library/PhoneNumberUtil";
import ENV from "@/library/env.ts";

const { t } = useI18n();

const userStore = useUserStore();
const configStore = useConfigStore();
const fontScale = computed(()=>configStore.fontScale);
const isSubmitting = ref(false);
const agree = ref(configStore.protocolAgree);
const registerType = ref<MobileEmail>(ENV.foreign?'Mobile':'Email')
const registerForm = ref<UniFormsInstance>();
const registerFormData = reactive({
	phone: '',
	email: '',
	countryCode: configStore.countryCode || (ENV.foreign ? 'HK' : 'CN'),
	password: '',
	confirmPassword: '',
	code: '',
})
const registerFormRules = computed<UniFormsRules>(()=>{
	const rules: UniFormsRules = {
		password:{
			label: t('pages.register.form.passwordLabel'),
			rules:[
				{
					required: true,
					errorMessage: t('pages.register.form.passwordError'),
				},
				{
					minLength: 6,
					maxLength: 12,
					errorMessage: t('pages.register.form.passwordLengthError'),
				}
			]
		},
		confirmPassword:{
			label: t('pages.register.form.confirmPasswordLabel'),
			rules:[
				{
					required: true,
					errorMessage: t('pages.register.form.confirmPasswordError'),
				},
				{
					validateFunction: (rule, value, data, callback) => {
						if (value !== registerFormData.password) {
							callback(t('pages.register.form.confirmPasswordMismatch'))
							return false
						}
						return true
					}
				}
			]
		},
		code:{
			label: t('pages.register.form.codeLabel'),
			rules:[
				{
					required: true,
					errorMessage: t('pages.register.form.codeError'),
				}
			]
		}
	}

	if (registerType.value === 'Mobile') {
		rules.phone = {
			label: t('pages.register.form.phoneLabel'),
			rules:[
				{
					required: true,
					errorMessage: t('pages.register.form.phoneError'),
				},
				{
					validateFunction: (rule, value, data, callback) => {
						if (!validatePhoneNumber(value, registerFormData.countryCode)) {
							callback(t('pages.register.form.phoneError'))
							return false
						}
						return true
					}
				}
			]
		}
	} else {
		rules.email = {
			label: t('pages.login.form.emailLabel'),
			rules:[
				{
					required: true,
					errorMessage: t('pages.login.form.emailError'),
				},
				{
					format: 'email',
					errorMessage: t('pages.login.form.emailFormatError')
				}
			]
		}
	}

	return rules
})

function switchRegisterType() {
	registerType.value = registerType.value === 'Mobile' ? 'Email' : 'Mobile'
}

function goLogin() {
	UniRouter.redirect(loginPage)
}

async function submit(){
	configStore.protocolAgree = agree.value;

	// 检查是否同意协议
	if (!agree.value) {
		ShowInfo.toast(t('pages.register.message.agreeRequired'));
		return;
	}

	await ShowInfo.loading(submitForm(), t('pages.register.message.registering'));
}

async function submitForm(){
	if (isSubmitting.value) {
		return;
	}
	// 验证表单
	await registerForm.value?.validate()

	try {
		isSubmitting.value = true;

		let api;
		if (registerType.value === 'Mobile') {
			// 格式化手机号（添加国家前缀）
			const formattedPhone = formatPhoneNumber(registerFormData.phone, registerFormData.countryCode);

			// 调用注册API
			api = new ApiUserRegister({
				mobile: formattedPhone,
				password: registerFormData.password,
				code: registerFormData.code,
			})
		} else {
			// 调用邮箱注册API
			api = new ApiUserEmailRegister({
				email: registerFormData.email,
				password: registerFormData.password,
				code: registerFormData.code,
			})
		}

		const {data: registerResult} = await api.call()
		await userStore.login(registerResult);

		// 注册成功提示
		uni.showToast({
			title: t('pages.register.message.registerSuccess'),
			icon: 'success',
			duration: 2000
		})

		// 延迟后返回登录页
		setTimeout(() => {
			UniRouter.redirect(mainPage);
		}, 2000)
	} catch (error) {
		console.error('注册失败:', error)
		uni.showToast({
			title: error instanceof Error ? error.message : t('pages.register.message.registerFailed'),
			icon: 'error',
			duration: 2000
		})
	} finally {
		isSubmitting.value = false;
	}
}
</script>

<template>
	<NavBar back back-color="#fff"/>
	<view class="page page-register">
		<view class="main-vision wide-section"></view>
		<view class="register-area">
			<uni-forms err-show-type="toast" class="login-form" ref="registerForm" label-width="6em" label-align="center" :rules="registerFormRules" :model="registerFormData" >
				<uni-forms-item v-if="registerType === 'Mobile'" label-position="left" name="phone">
					<template #label>
						<CountryPhoneSelector class="uni-forms-item__label"
							v-model="registerFormData.phone"
							v-model:countryCode="registerFormData.countryCode"
						/>
					</template>
					<CompInput v-model="registerFormData.phone" type="phone" theme="dark" :placeholder="t('pages.register.form.phonePlaceholder')" />
				</uni-forms-item>
				<uni-forms-item v-if="registerType === 'Email'" label-position="left" :label="t('pages.login.form.emailLabel')" name="email">
					<CompInput v-model="registerFormData.email" type="text" theme="dark" :placeholder="t('pages.login.form.emailPlaceholder')" />
				</uni-forms-item>
				<uni-forms-item label-position="left" :label="t('pages.register.form.passwordLabel')" name="password">
					<CompInput v-model="registerFormData.password" type="password" theme="dark" :placeholder="t('pages.register.form.passwordPlaceholder')" />
				</uni-forms-item>
				<uni-forms-item label-position="left" :label="t('pages.register.form.confirmPasswordLabel')" name="confirmPassword">
					<CompInput v-model="registerFormData.confirmPassword" type="password" theme="dark" :placeholder="t('pages.register.form.confirmPasswordPlaceholder')" />
				</uni-forms-item>
				<uni-forms-item label-position="left" :label="t('pages.register.form.codeLabel')" name="code">
					<CompInput v-model="registerFormData.code" type="number" theme="dark" :placeholder="t('pages.register.form.codePlaceholder')">
						<template #suffix>
							<CaptchaButton :type="registerType" :phone="registerFormData.phone" :email="registerFormData.email" :countryCode="registerFormData.countryCode" :scene="CodeScene.REGISTER" />
						</template>
					</CompInput>
				</uni-forms-item>

				<ProtocolConfirm with-register v-model="agree" />

				<view class="form-action">
					<up-button type="primary" shape="circle" @click="submit" :disabled="isSubmitting" :loading="isSubmitting">{{ t('pages.register.button.register') }}</up-button>
					<view class="switch-action" v-if="ENV.foreign">
						<text class="link-action" @click="switchRegisterType">{{ registerType === 'Mobile' ? t('pages.register.button.emailRegister') : t('pages.register.button.mobileRegister') }}</text>
					</view>
					<view class="switch-action">
						<text class="link-action" @click="goLogin">{{ t('pages.register.button.goLogin') }}</text>
					</view>
				</view>
			</uni-forms>
			
		</view>
	</view>
</template>

<style scoped lang="scss">
.page{
	--font-scale:v-bind(fontScale);
}
.page-register{
	background: url("@/static/images/register/main-vision.png") no-repeat center -80px/100% auto;
}
:deep(.country-display){
	height: 2em;
}
.main-vision{
	padding-bottom: 70%;
}
.register-area{
	margin-top: 20px;
}
.form-action{
	.switch-action{
		margin-top: 10px;
		@include flex-row(center, center);
		.link-action{
			@include fs(10);
			color: $theme-color;
		}
	}
}

.protocol-confirm{
	color: $color-text-white;
	margin-bottom: 10px;
}
</style>

