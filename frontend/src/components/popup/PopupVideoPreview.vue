<script setup lang="ts">
import type {PopupInstance} from '@/types/popup';
import CompPopup from '@/components/popup/CompPopup.vue';
import {defaultPopupExpose} from '@/library/PopupManager';

const popup = ref<PopupInstance>();
const title = ref<string>('');
const videoUrl = ref<string>('');
const videoKey = ref(0);

defineExpose(defaultPopupExpose(popup, {
	open(popupTitle: string, url: string) {
		title.value = popupTitle;
		videoUrl.value = url;
		videoKey.value += 1;
	},
	close() {
		videoUrl.value = '';
	}
}));
</script>

<template>
	<CompPopup
		ref="popup"
		:title="title"
		closable
		class="video-preview-popup"
	>
		<view class="video-content">
			<video
				v-if="videoUrl"
				:key="videoKey"
				:src="videoUrl"
				class="video-player"
				controls
				autoplay
				enable-play-gesture
				show-progress
				show-center-play-btn
				show-fullscreen-btn
				page-gesture
				playsinline
				object-fit="contain"
			></video>
		</view>
	</CompPopup>
</template>

<style scoped lang="scss">
.video-preview-popup {
	:deep(.nw-popup-content){
		margin-left: 0!important;
		margin-right: 0!important;
		width: 100%!important;
		margin-bottom: 0!important;
	}
	:deep(.popup-container) {
		height: 90vh;
		max-height: 90vh;
		padding-bottom: 0;
	}

	padding: 10px 0;
	@include fs(12);
	color: $color-text-black;

	.video-content {
		height: 100%;
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		gap: 15px;
		background: #000;
		overflow: hidden;
	}

	.video-player {
		width: 100%;
		height: 100%;
		flex: 1;
	}
}
</style>