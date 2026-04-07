<script setup lang="ts">
import {onLoad} from "@dcloudio/uni-app";
import {accessToken, loginPage, mainPage} from "@/library/GlobalVars";
import UniRouter from "@/library/UniRouter";
import CompImage from "@/components/CompImage.vue";
import CompInput from "@/components/CompInput.vue";
import type {UniFormsInstance, UniFormsRules} from "@/types/uni-ui";
import CaptchaButton from "@/components/CaptchaButton.vue";
import {CodeScene} from "@/api/generic/ApiSendSmsCode";
import {ApiUserLoginByPassword} from "@/api/user/ApiUserLoginByPassword";
import {ApiUserLoginByCode} from "@/api/user/ApiUserLoginByCode";
import {ApiUserEmailLogin} from "@/api/user/ApiUserEmailLogin";
import {ApiUserEmailCodeLogin} from "@/api/user/ApiUserEmailCodeLogin";
import {useUserStore} from "@/store/user";
import type {MobileEmail} from "@/types/enums";
import {ShowInfo} from "@/library/ShowInfo";
import ProtocolConfirm from "@/components/ProtocolConfirm.vue";
import {useConfigStore} from "@/store/config.ts";
import ENV from "@/library/env.ts";
import NavBar from "@/components/NavBar.vue";
import { useI18n } from 'vue-i18n';
import CountryPhoneSelector from "@/components/CountryPhoneSelector.vue";
import { formatPhoneNumber, validatePhoneNumber } from "@/library/PhoneNumberUtil";
import CompLogo from "@/components/CompLogo.vue";

const { t } = useI18n();

// 登录方式: 'Mobile' 手机号登录, 'Email' 邮件登录
const loginType = ref<MobileEmail>(ENV.foreign?'Mobile':'Email')
// 登录模式: 'password' 普通登录, 'captcha' 验证码登录
const loginMode = ref<'password' | 'captcha'>('password')

const userStore = useUserStore();
const configStore = useConfigStore();
const fontScale = computed(()=>configStore.fontScale);
const isSubmitting = ref(false);
const agree = ref(configStore.protocolAgree);

let redirectPage: string | undefined = undefined;
function goRedirect() {
	if(!ENV.dev){//requested
		goHome();
		return;
	}
	
	UniRouter.redirect(redirectPage ?? mainPage);
}

const loginForm = ref<UniFormsInstance>();

const loginFormData = reactive({
	phone: '',
	email: '',
	countryCode: configStore.countryCode || (ENV.foreign ? 'HK' : 'CN'),
	password: '',
	code: '',
})
const loginFormRules = computed<UniFormsRules>(()=>{
	const rules: UniFormsRules = {}

	if (loginType.value === 'Mobile') {
		rules.phone = {
			label: t('pages.login.form.phoneLabel'),
			rules:[
				{
					required: true,
					errorMessage: t('pages.login.form.phoneError'),
				},
				{
					validateFunction: (rule, value, data, callback) => {
						if (!validatePhoneNumber(value, loginFormData.countryCode)) {
							callback(t('pages.login.form.phoneFormatError'))
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

	if (loginMode.value === 'password') {
		rules.password = {
			label: t('pages.login.form.passwordLabel'),
			rules:[
				{
					required: true,
					errorMessage: t('pages.login.form.passwordError'),
				}
			]
		}
	} else {
		rules.code = {
			label: t('pages.login.form.codeLabel'),
			rules:[
				{
					required: true,
					errorMessage: t('pages.login.form.codeError'),
				}
			]
		}
	}

	return rules
})

function switchLoginMode() {
	loginMode.value = loginMode.value === 'password' ? 'captcha' : 'password'
}

function switchLoginType() {
	loginType.value = loginType.value === 'Mobile' ? 'Email' : 'Mobile'
}

function goRegister() {
	UniRouter.to('/pages/main/register')
}

function goHome(){
	UniRouter.to(mainPage);
}

async function submit(){
	configStore.protocolAgree = agree.value;
	// 检查是否同意协议
	if (!agree.value) {
		ShowInfo.toast(t('pages.login.message.agreeRequired'));
		return;
	}

	await ShowInfo.loading(submitForm(), t('pages.login.message.loggingIn'));
}

async function submitForm(){
	if (isSubmitting.value) {
		return;
	}
	// 验证表单
	await loginForm.value?.validate();
	try {
		isSubmitting.value = true;

		let loginResult;
		if (loginType.value === 'Mobile') {
			// 格式化手机号（添加国家前缀）
			const formattedPhone = formatPhoneNumber(loginFormData.phone, loginFormData.countryCode);

			if (loginMode.value === 'password') {
				// 密码登录
				const api = new ApiUserLoginByPassword({
					mobile: formattedPhone,
					password: loginFormData.password,
				});

				const response = await api.call();
				loginResult = response.data;
			} else {
				// 验证码登录
				const api = new ApiUserLoginByCode({
					mobile: formattedPhone,
					code: loginFormData.code,
				});

				const response = await api.call();
				loginResult = response.data;
			}
		} else {
			if (loginMode.value === 'password') {
				// 邮件密码登录
				const api = new ApiUserEmailLogin({
					email: loginFormData.email,
					password: loginFormData.password,
				});

				const response = await api.call();
				loginResult = response.data;
			} else {
				// 邮件验证码登录
				const api = new ApiUserEmailCodeLogin({
					email: loginFormData.email,
					code: loginFormData.code,
				});

				const response = await api.call();
				loginResult = response.data;
			}
		}

		// 调用 user store 的 login 方法
		await userStore.login(loginResult);

		// 登录成功提示
		uni.showToast({
			title: t('pages.login.message.loginSuccess'),
			icon: 'success',
			duration: 2000
		});

		goRedirect();
	} catch (error) {
		console.error('登录失败:', error);
		uni.showToast({
			title: error instanceof Error ? error.message : t('pages.login.message.loginFailed'),
			icon: 'error',
			duration: 2000
		});
	} finally {
		isSubmitting.value = false;
	}
}

onLoad((query)=>{
	if (query && query.redirect) {
		if (query.redirect.startsWith('/pages/') && !query.redirect.startsWith(loginPage)) {
			redirectPage = query.redirect;
		}
	}
	
	if(accessToken.value){
		goRedirect();
		return;
	}
	
	/*if(ENV.dev){
		loginFormData.phone = '13408494181';
		loginFormData.password = 'hkuclion';
		submitForm()
	}*/
})
</script>

<template>
	<NavBar title="" back transparent back-color="#ffffff" />
	<view class="page page-login">
		<view class="logo">
			<CompLogo />
		</view>
		<view class="main-vision wide-section"></view>
		<view class="login-area">
			<uni-forms err-show-type="toast" class="login-form" ref="loginForm" label-width="6em" label-align="center" :rules="loginFormRules" :model="loginFormData">
				<view class="form-title">{{ t('pages.login.form.title') }}</view>
				<uni-forms-item v-if="loginType === 'Mobile'" label-position="left" name="phone">
					<template #label>
						<CountryPhoneSelector class="uni-forms-item__label"
							v-model="loginFormData.phone"
							v-model:countryCode="loginFormData.countryCode"
						/>
					</template>
					<CompInput v-model="loginFormData.phone" type="phone" theme="dark" :placeholder="t('pages.login.form.phonePlaceholder')" />
				</uni-forms-item>
				<uni-forms-item v-if="loginType === 'Email'" label-position="left" :label="t('pages.login.form.emailLabel')" name="email">
					<CompInput v-model="loginFormData.email" type="text" theme="dark" :placeholder="t('pages.login.form.emailPlaceholder')" />
				</uni-forms-item>
				<uni-forms-item v-if="loginMode === 'password'" label-position="left" :label="t('pages.login.form.passwordLabel')" name="password">
					<CompInput v-model="loginFormData.password" type="password" theme="dark" :placeholder="t('pages.login.form.passwordPlaceholder')" />
				</uni-forms-item>
				<uni-forms-item v-if="loginMode === 'captcha'" label-position="left" :label="t('pages.login.form.codeLabel')" name="code">
					<CompInput v-model="loginFormData.code" type="number" theme="dark" :placeholder="t('pages.login.form.codePlaceholder')">
						<template #suffix>
							<CaptchaButton :type="loginType" :phone="loginFormData.phone" :email="loginFormData.email" :countryCode="loginFormData.countryCode" :scene="CodeScene.LOGIN" />
						</template>
					</CompInput>
				</uni-forms-item>

				<ProtocolConfirm v-model="agree" />

				<view class="form-action">
					<up-button type="primary" shape="circle" @click="submit" :disabled="isSubmitting" :loading="isSubmitting">{{ t('pages.login.button.login') }}</up-button>
					<view class="switch-action">
						<text v-if="ENV.foreign" class="link-action" @click="switchLoginType">{{ loginType === 'Mobile' ? t('pages.login.button.emailLogin') : t('pages.login.button.mobileLogin') }}</text>
						<text class="link-action" @click="switchLoginMode">{{ loginMode === 'password' ? t('pages.login.button.switchToCode') : t('pages.login.button.switchToPassword') }}</text>
						<text class="link-action" @click="goRegister">{{ t('pages.login.button.register') }}</text>
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
.page-login{
	background: url("@/static/images/login/main-vision.png") no-repeat center 80px/100% auto;
}
:deep(.country-display){
	height: 2em;
}
.logo{
	margin-top: 25px;
	margin-bottom: 15px;
	text-align: center;
	.comp-image{
		width: 125px;
	}
}
.main-vision{
	padding-bottom: 75%;
}
.login-area{
	margin-top: 20px;
	.form-title{
		@include fs(15);
		color: $color-text-white;
		text-align: center;
		margin-bottom: 12px;
	}
}
.form-action{
	.switch-action{
		margin-top: 10px;
		@include flex-row(center, center);
		.link-action{
			@include fs(10);
			color: $theme-color;
			+.link-action{
				margin-left: 4em;
			}
		}
	}
}
.protocol-confirm{
	color: $color-text-white;
	margin-bottom: 10px;
}
</style>