<script setup lang="ts">
import CompPopup from "@/components/popup/CompPopup.vue"
import {defaultPopupExpose} from "@/library/PopupManager"
import type {PopupInstance} from "@/types/popup"
import {ShowInfo} from "@/library/ShowInfo"
import {ApiSendSmsCode, CodeScene} from "@/api/generic/ApiSendSmsCode"
import {useUserStore} from "@/store/user"
import {useI18n} from 'vue-i18n'

const userStore = useUserStore()
const { t } = useI18n()
const dialog = ref<PopupInstance>()
const isSubmitting = ref(false)
const codeSent = ref(false)
const countdownTime = ref(0)
const code = ref('')
let countdownTimer: any = null

defineExpose(defaultPopupExpose(dialog, {
	open() {
		code.value = ''
		codeSent.value = false
		countdownTime.value = 0
	}
}))

const userPhone = computed(() => userStore.user?.mobile)

// 发送验证码
async function sendCode() {
	if (codeSent.value) {
		ShowInfo.toast(t('common.sms.codeSentWait'))
		return
	}

	try {
		isSubmitting.value = true
		const api = new ApiSendSmsCode({
			mobile: userPhone.value,
			scene: CodeScene.AUDIT
		})
		await api.call()
		ShowInfo.toast(t('common.sms.codeSent'))
		codeSent.value = true

		// 倒计时 60 秒
		countdownTime.value = 60
		countdownTimer = setInterval(() => {
			countdownTime.value--
			if (countdownTime.value <= 0) {
				clearInterval(countdownTimer)
				codeSent.value = false
			}
		}, 1000)
	} catch (error) {
		ShowInfo.toast(t('common.sms.sendFailed'))
	} finally {
		isSubmitting.value = false
	}
}

// 提交验证码
function confirm() {
	if (!code.value?.match(/^\d{4}$/)) {
		ShowInfo.toast(t('common.sms.codeInputError'))
		return
	}

	// 返回验证码给父组件，由父组件处理提交
	dialog.value?.result(code.value)
}
</script>

<template>
	<CompPopup ref="dialog" :title="t('pages.order.popup.earlyRedeemTitle')">
		<template #actions>
			<up-button
				type="warning"
				shape="circle"
				@click="confirm"
				size="normal"
			>
				{{ t('pages.order.popup.earlyRedeemButton') }}
			</up-button>
		</template>
		<view class="content">
			<view class="message">{{ t('pages.order.popup.earlyRedeemMessage') }}</view>
			<view class="form">
				<view class="title-code" v-if="codeSent">{{ t('common.sms.codeSentTo', { phone: userPhone }) }}</view>
				<view class="code-input">
					<up-code-input :maxlength="4" :size="30" color="#000000" v-model="code"></up-code-input>
				</view>
				<view class="send-code-action">
					<up-button
						v-if="!codeSent"
						type="primary"
						size="small"
						@click="sendCode"
						:disabled="isSubmitting"
						:loading="isSubmitting"
					>
						{{ t('common.sms.sendButton') }}
					</up-button>
					<view v-else class="countdown">
						{{ t('common.sms.resendCountdown', { time: countdownTime }) }}
					</view>
				</view>
			</view>
			<view class="action">

			</view>
		</view>
	</CompPopup>
</template>

<style scoped lang="scss">

.message {
	@include fs(12);
	color: $color-gray;
	margin-bottom: 20px;
	text-align: center;
}

.form {
	margin-left: auto;
	margin-right: auto;
	width: 170px;
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
}
</style>

