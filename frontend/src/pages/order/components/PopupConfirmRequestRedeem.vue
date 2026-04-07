<script setup lang="ts">
import CompPopup from "@/components/popup/CompPopup.vue"
import {defaultPopupExpose} from "@/library/PopupManager"
import type {PopupInstance} from "@/types/popup"
import {ShowInfo} from "@/library/ShowInfo"
import {ApiSendSmsCode, CodeScene} from "@/api/generic/ApiSendSmsCode"
import {useUserStore} from "@/store/user"
import {formatPrice} from "@/library/format.ts"
import type {UniFormsInstance, UniFormsRules} from "@/types/uni-ui"
import {useI18n} from 'vue-i18n'

const userStore = useUserStore()
const { t } = useI18n()
const dialog = ref<PopupInstance>()
const form = ref<UniFormsInstance>()
const isSubmitting = ref(false)
const codeSent = ref(false)
const countdownTime = ref(0)
let countdownTimer: any = null

const redeemAmount = ref(0)

const formData = reactive({
	code: ''
})

const formRules = computed<UniFormsRules>(() => ({
	code: {
		rules: [
			{ required: true, errorMessage: t('common.sms.codeInputError') },
			{ pattern: /^\d{4}$/, errorMessage: t('common.sms.codeInputError') }
		]
	}
}))

defineExpose(defaultPopupExpose(dialog, {
	open(amount: number) {
		formData.code = ''
		codeSent.value = false
		countdownTime.value = 0
		redeemAmount.value = amount
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

// 提交
async function confirm() {
	try {
		await form.value?.validate()

		// 返回验证码给父组件
		dialog.value?.result({
			smsCode: formData.code
		})
	} catch (error) {
		console.error('表单验证失败:', error)
	}
}
</script>

<template>
	<CompPopup ref="dialog" :title="t('pages.order.popup.redeemTitle')">
		<template #actions>
			<up-button
				type="primary"
				shape="circle"
				@click="confirm"
				size="normal"
			>
				{{ t('pages.order.popup.redeemButton') }}
			</up-button>
		</template>
		<view class="content">
			<view class="content-block">
				<view class="info-row">
					<text class="label">{{ t('pages.order.popup.redeemAmount') }}</text>
					<text class="value">{{ formatPrice(redeemAmount) }}</text>
				</view>
			</view>

			<uni-forms ref="form" class="popup-form" :rules="formRules" :model="formData" label-position="top" err-show-type="toast">
				<view class="title-code" v-if="codeSent">{{ t('common.sms.codeSentTo', { phone: userPhone }) }}</view>

				<uni-forms-item name="code">
					<view class="code-input-wrapper">
						<up-code-input :maxlength="4" :size="30" color="#000000" v-model="formData.code"></up-code-input>
					</view>
				</uni-forms-item>

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
			</uni-forms>
		</view>
	</CompPopup>
</template>

<style scoped lang="scss">
.info-row {
	@include flex-row(space-between, center);
	@include fs(14);

	.label {
		color: $color-gray;
	}

	.value {
		margin-left: 0.5em;
		color: $color-text-black;
		font-weight: bold;
	}
}

.popup-form {
	.u-input {
		background-color: $color-text-white;
	}

	.uni-forms-item.is-direction-top {
		margin-bottom: 10px;
		:deep(.uni-forms-item__label) {
			@include fs(14);
			@include fw(medium);
			color: $color-text-black;
			padding-bottom: 0;
		}
	}
}

.title-code {
	@include fs(12, 18);
	color: $color-text-black;
	text-align: center;
	margin-bottom: 20px;
	margin-top: 20px;
}

.code-input-wrapper {
	display: flex;
	justify-content: center;
}

.send-code-action {
	margin-top: 12px;
	text-align: center;

	.countdown {
		@include fs(12);
		color: $color-gray;
	}
}
</style>

