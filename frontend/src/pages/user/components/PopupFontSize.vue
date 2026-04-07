<script setup lang="ts">
import CompPopup from "@/components/popup/CompPopup.vue";
import {defaultPopupExpose} from "@/library/PopupManager";
import type {PopupInstance} from "@/types/popup";
import {useConfigStore} from "@/store/config";
import {fontScaleOptions} from "@/types/options";
import {useI18n} from "vue-i18n";
const { t } = useI18n()
const configStore = useConfigStore();
const selectedFontScale = ref<number>(1);

const dialog = ref<PopupInstance<number>>();
defineExpose(defaultPopupExpose(dialog, {
	open() {
		// 从 configStore 获取当前字体缩放
		selectedFontScale.value = configStore.fontScale || 1;
	}
}));

function confirmFontSize() {
	configStore.setFontScale(selectedFontScale.value);
	dialog.value?.result(selectedFontScale.value);
}

</script>

<template>
	<CompPopup ref="dialog" :title="t('pages.user.fontSize.title')">
		<view class="font-size-content">
			<view
				v-for="option of fontScaleOptions"
				:key="option.value"
				class="font-size-option"
				:class="{ selected: selectedFontScale === option.value, ['size_'+option.value.toString().replace('.','_')]:true }"
				@click="selectedFontScale = option.value"
			>
				{{ option.label }}
			</view>
		</view>
		<template #actions>
			<up-button
				type="warning"
				shape="circle"
				@click="confirmFontSize"
				size="normal"
			>
				{{ t('common.button.confirm') }}
			</up-button>
		</template>
	</CompPopup>
</template>

<style scoped lang="scss">
.font-size-content {
	padding: 20px 0;
	display: flex;
	flex-direction: column;
	gap: 10px;
}

.font-size-option {
	padding: 4px 8px;
	border: 1px solid $color-text-black;
	text-align: center;
	cursor: pointer;
	transition: all 0.3s ease;
	background-color: $color-text-white;
	color: $color-text-black;
	font-size: 14px;
	&.size_1_25{
		font-size: 1.25em;
	}
	&.size_1_5{
		font-size: 1.5em;
	}

	&.selected {
		background-color: $color-text-black;
		color: $color-text-white;
	}
}
</style>

