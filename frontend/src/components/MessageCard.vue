<script lang="ts" setup>
import type {MessageEntity} from '@/types/entity'
import dayjs from 'dayjs'
import {useI18n} from 'vue-i18n'

interface Props {
	message: MessageEntity
}

defineProps<Props>()

const { t } = useI18n()

// 格式化时间为阅读友好模式
const formatTime = (dateStr: string) => {
	return dayjs(dateStr).fromNow()
}
</script>

<template>
	<view :class="{'unread': !message.readStatus}" class="message-card">
		<!-- 第一行：标题、已读状态、时间 -->
		<view class="card-header">
			<view class="header-left">
				<text class="title">{{ message.templateTitle }}</text>
				<view class="read-badge">
					{{ message.readStatus ? t('components.messageCard.read') : t('components.messageCard.unread') }}
				</view>
			</view>
			<text class="time">{{ formatTime(message.createTime) }}</text>
		</view>

		<!-- 第二行：内容 -->
		<view class="card-content">
			<text class="content-text">{{ message.templateContent }}</text>
		</view>
	</view>
</template>

<style lang="scss" scoped>
@import '@/styles/definition.scss';

.message-card {
	padding: 10px;
	background: #1a1a1e;
	border-radius: 4px;

	.card-header {
		@include flex-row(center, space-between);
		margin-bottom: 4px;

		.header-left {
			@include flex-row(center);
			flex: 1;
			gap: 4px;
			min-width: 0;

			.title {
				@include fs(13);
				@include fw(medium);
				color: #FBFBFB;
				@include ellipsis(auto, 1);
			}

			.read-badge {
				@include fs(10);
				@include fw(medium);
				padding: 1px 4px;
				border-radius: 2px;
				white-space: nowrap;
				flex-shrink: 0;
				background: $color-gray;
				color: #1B1B1D;
				@include fs(8);
			}
		}

		.time {
			@include fs(12);
			color: $color-gray;
			margin-left: 6px;
			white-space: nowrap;
			flex-shrink: 0;
		}
	}

	.card-content {
		.content-text {
			@include fs(13);
			color: $color-gray;
			@include ellipsis();
		}
	}
	
	&.unread{
		.card-header{
			.header-left{
				.title{
					color: $theme-color;
				}
				.read-badge {
					background: $theme-color;
					color: $color-text-white;
				}
			}
		}
	}
}
</style>

