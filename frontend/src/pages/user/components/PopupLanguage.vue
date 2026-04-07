<script setup lang="ts">
import CompPopup from "@/components/popup/CompPopup.vue";
import {defaultPopupExpose} from "@/library/PopupManager";
import type {PopupInstance} from "@/types/popup";
import {useConfigStore} from "@/store/config";
import {Language} from "@/types/enums.ts";
import {useI18n} from "vue-i18n";
import ENV from "@/library/env.ts";
import {languageOptions} from "@/types/options.ts";

const { t } = useI18n()

const configStore = useConfigStore();
const selectedLanguage = ref<Language>()

const availableLanguageOptions = languageOptions;


const dialog = ref<PopupInstance<Language>>();
defineExpose(defaultPopupExpose(dialog, {
	open() {
		// 从 configStore 获取当前语言
		selectedLanguage.value = configStore.language;
	}
}));

function selectLanguage(lang: Language){
	selectedLanguage.value = lang;
}

function confirmLanguage() {
	if(selectedLanguage.value) {
		configStore.setLanguage(selectedLanguage.value);
		dialog.value?.result(selectedLanguage.value);
	}
}

</script>

<template>
	<CompPopup ref="dialog" :title="t('pages.user.language.title')">
		<view class="language-content">
			<view
				v-for="option in availableLanguageOptions"
				:key="option.value"
				class="language-option"
				:class="{ selected: selectedLanguage === option.value }"
				@click="selectLanguage(option.value)"
			>
				{{ option.label }}
			</view>
		</view>
		<template #actions>
			<up-button
				type="warning"
				shape="circle"
				@click="confirmLanguage"
				size="normal"
			>
				{{ t('common.button.confirm') }}
			</up-button>
		</template>
	</CompPopup>
</template>

<style scoped lang="scss">
.language-content {
	padding: 20px 0;
	display: flex;
	flex-direction: column;
	gap: 10px;
}

.language-option {
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

