<script setup lang="ts">
import {useI18n} from 'vue-i18n';
import type {ProjectNoticeEntity} from "@/types/entity";
import type {PopupInstance} from "@/types/popup";
import CompPopup from "@/components/popup/CompPopup.vue";
import {defaultPopupExpose} from "@/library/PopupManager";
import {$themeColor} from "@/library/GlobalVars";
import {ref} from 'vue';

const {t} = useI18n();

interface AttachFile {
	id: number;
	name: string;
	url: string;
	uid?: number;
	status?: string;
}

const popup = ref<PopupInstance>();
const notice = ref<ProjectNoticeEntity | null>(null);
const attachFiles = ref<AttachFile[]>([]);

defineExpose(defaultPopupExpose(popup, {
	open(noticeData: ProjectNoticeEntity) {
		notice.value = noticeData;
		// 解析 attachUrls
		attachFiles.value = [];
		if (noticeData.attachUrls) {
			try {
				const parsed: unknown = JSON.parse(noticeData.attachUrls);
				attachFiles.value = Array.isArray(parsed) ? parsed : [];
			} catch (e) {
				console.error('Failed to parse attachUrls:', e);
				attachFiles.value = [];
			}
		}
	}
}));
</script>

<template>
	<CompPopup ref="popup" :title="notice?.noticeTitle" closable>
		<view class="notice-detail-popup" v-if="notice">
			<!-- 通知编号 -->
			<view class="notice-no">
				<text class="label">{{ t('pages.project.components.popupNoticeDetail.noticeNo') }}</text>
				<text class="value">{{ notice.noticeNo }}</text>
			</view>

			<!-- 发布时间 -->
			<view class="publish-time">
				<text class="label">{{ t('pages.project.components.popupNoticeDetail.publishTime') }}</text>
				<text class="value">{{ new Date(notice.publishTime).toLocaleString() }}</text>
			</view>

			<!-- 通知内容 -->
			<view class="content-section">
				<text class="section-title">{{ t('pages.project.components.popupNoticeDetail.content') }}</text>
				<view class="content-body">
					<rich-text :nodes="notice.noticeContent" :selectable="true"></rich-text>
				</view>
			</view>

			<!-- 附件列表 -->
			<view class="attachments-section" v-if="attachFiles.length > 0">
				<text class="section-title">{{ t('pages.project.components.popupNoticeDetail.attachments') }}</text>
				<view class="attachments-list">
					<up-link
						v-for="(file, index) in attachFiles"
						:key="file.id || index"
						:color="$themeColor"
						:text="file.name"
						:href="file.url"
						font-size="inherit">
					</up-link>
				</view>
			</view>
		</view>
	</CompPopup>
</template>

<style scoped lang="scss">
.notice-detail-popup {
	color: $color-text-black;
	padding: 10px 0;
	min-height: 200px;
	@include fs(12);

	.notice-no,
	.publish-time {
		display: flex;
		align-items: center;

		.label {
			color: $color-gray;
			flex-shrink: 0;
			margin-right: 8px;
		}

		.value {
			flex: 1;
		}
	}

	.content-section,
	.attachments-section {
		margin-top: 16px;

		.section-title {
			display: block;
			font-weight: bold;
			margin-bottom: 8px;
			@include fs(13);
		}
	}

	.content-body {
		line-height: 1.6;
		@include en-break;
	}

	.attachments-list {
		@include fs(11);

		.u-link {
			display: block;
			margin-top: 8px;
		}
	}
}
</style>

