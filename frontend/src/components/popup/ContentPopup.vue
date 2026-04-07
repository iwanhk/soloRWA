<script setup lang="ts">
import type {PopupInstance} from '@/types/popup';
import CompPopup from '@/components/popup/CompPopup.vue';
import {defaultPopupExpose} from '@/library/PopupManager';

const popup = ref<PopupInstance>();

const title = ref<string>('');
const content = ref<string>('');
const isHtml = ref(true);

defineExpose(defaultPopupExpose(popup, {
	open(popupTitle: string, popupContent: string, _isHtml=true) {
		title.value = popupTitle;
		content.value = popupContent;
		isHtml.value = _isHtml;
		popup.value?.open();
	}
}));
</script>

<template>
	<CompPopup
		ref="popup"
		:title="title"
		closable
	>
		<view class="content-popup">
			<rich-text v-if="isHtml" :nodes="content" :selectable="true"></rich-text>
			<template v-else>{{content}}</template>
		</view>
	</CompPopup>
</template>

<style scoped lang="scss">
.content-popup {
	padding: 10px 0;
	min-height: 200px;
	@include fs(12);
	color: $color-text-black;
}
</style>

