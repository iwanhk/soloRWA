<script setup lang="ts">
import CompPopup from "@/components/popup/CompPopup.vue";
import {defaultPopupExpose} from "@/library/PopupManager";
import type {PopupInstance} from "@/types/popup";
import {useConfigStore} from "@/store/config";
import {currencyOptions} from "@/types/options";
import {Currency} from "@/types/enums.ts";
import ENV from "@/library/env.ts";
import {useI18n} from "vue-i18n";
const { t } = useI18n()
const configStore = useConfigStore();
const selectedCurrency = ref<Currency>(Currency.USD);

const dialog = ref<PopupInstance<Currency>>();
defineExpose(defaultPopupExpose(dialog, {
	open() {
		// 从 configStore 获取当前货币
		selectedCurrency.value = configStore.currency || Currency.USD;
	}
}));

async function confirmCurrency() {
	try {
		await configStore.setCurrency(selectedCurrency.value);
		dialog.value?.result(selectedCurrency.value);
	} catch (error) {
		console.error('设置货币失败:', error);
		uni.showToast({
			title: t('pages.user.currency.setError'),
			icon: 'error'
		});
	}
}

</script>

<template>
	<CompPopup ref="dialog" :title="t('pages.user.currency.title')">
		<view class="currency-content">
			<template v-for="option of currencyOptions">
				<view
					v-if="option.value !== Currency.CNY || !ENV.foreign"
					:key="option.value"
					class="currency-option"
					:class="{ selected: selectedCurrency === option.value }"
					@click="selectedCurrency = option.value"
				>
					{{ option.label }}
				</view>
			</template>
		</view>
		<template #actions>
			<up-button
				type="warning"
				shape="circle"
				@click="confirmCurrency"
				size="normal"
			>
				{{ t('common.button.confirm') }}
			</up-button>
		</template>
	</CompPopup>
</template>

<style scoped lang="scss">
.currency-content {
	padding: 20px 0;
	display: flex;
	flex-direction: column;
	gap: 10px;
}

.currency-option {
	padding: 4px 8px;
	border: 1px solid $color-text-black;
	text-align: center;
	cursor: pointer;
	transition: all 0.3s ease;
	background-color: $color-text-white;
	color: $color-text-black;
	@include fs(14);

	&.selected {
		background-color: $color-text-black;
		color: $color-text-white;
	}
}
</style>

