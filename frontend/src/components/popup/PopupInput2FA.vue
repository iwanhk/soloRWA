<script setup lang="ts">
import CompPopup from "@/components/popup/CompPopup.vue";
import {defaultPopupExpose} from "@/library/PopupManager";
import type {PopupInstance} from "@/types/popup";
import {ShowInfo} from "@/library/ShowInfo";
import {useI18n} from "vue-i18n";

const { t } = useI18n();
const isSubmitting = ref(false);
const verifyCode = ref('');

const dialog = ref<PopupInstance>();
defineExpose(defaultPopupExpose(dialog, {
	open(){
		verifyCode.value = '';
	}
}))

// 提交验证码
async function submitVerifyCode(){
	if(!verifyCode.value?.match(/^\d{6}$/)){
		ShowInfo.toast(t('popup.input2fa.codeInputHint'));
		return;
	}

	try {
		isSubmitting.value = true;
		// 返回验证码给父组件
		dialog.value?.result(verifyCode.value);
	} finally {
		isSubmitting.value = false;
	}
}

</script>

<template>
	<CompPopup ref="dialog" :title="t('popup.input2fa.title')">
		<view class="content">
			<view class="verify-title">{{ t('popup.input2fa.verifyCodeHint') }}</view>
			<view class="code-input">
				<up-code-input :maxlength="6" :size="30" color="#000000" v-model="verifyCode"></up-code-input>
			</view>
		</view>
		<template #actions>
			<up-button
				type="primary"
				shape="circle"
				@click="submitVerifyCode"
				size="normal"
				:disabled="isSubmitting"
				:loading="isSubmitting"
			>
				{{ t('popup.input2fa.confirmBtn') }}
			</up-button>
		</template>
	</CompPopup>
</template>

<style scoped lang="scss">
.content {
	text-align: center;

	.verify-title {
		@include fs(12, 18);
		text-align: center;
		margin-bottom: 20px;
	}
}
</style>

