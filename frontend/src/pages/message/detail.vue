<template>
	<view class="page page-message-detail">
		<!-- Loading 状态 -->
		<view v-if="isLoading" class="loading-state">
			<up-loading-icon></up-loading-icon>
		</view>

		<!-- 消息详情 -->
		<view v-else-if="currentMessage" class="detail-container">
			<!-- 标题 -->
			<view class="detail-header">
				<text class="title">{{ currentMessage.templateTitle || currentMessage.templateCode }}</text>
			</view>

			<!-- 时间 -->
			<view class="detail-meta">
				<text class="time">{{ formatTime(currentMessage.createTime) }}</text>
			</view>

			<!-- 内容 -->
			<view class="detail-content">
				<text class="content-text">{{ currentMessage.templateContent }}</text>
			</view>

			<view v-if="currentMessage.noticeUrl && false" class="link">
				<text class="label">{{ t('pages.message.detail.viewAddress') }}</text>
				<u-link font-size="inherit" :color="$themeColor" :href="currentMessage?.noticeUrl" :text="currentMessage?.noticeUrl"></u-link>
			</view>

			<view v-if="currentMessage.orderId" class="link">
				<text class="label">{{ t('pages.message.detail.viewOrder') }}</text>
				<text class="value" @click="goOrder">{{ t('pages.message.detail.order') }}{{currentMessage?.orderId}}</text>
			</view>
		</view>

		<!-- 空状态 -->
		<view v-else class="empty-state">
			<text>{{ t('pages.message.detail.notFound') }}</text>
		</view>
	</view>
</template>

<script lang="ts" setup>
import {ref} from 'vue'
import dayjs from 'dayjs'
import {onLoad} from "@dcloudio/uni-app";
import {useI18n} from 'vue-i18n'
import {$themeColor} from "@/library/GlobalVars";
import type {MessageEntity} from "@/types/entity";
import {ApiGetMessageDetail} from "@/api/message/ApiGetMessageDetail";
import {useUserStore} from "@/store/user.ts";
import UniRouter from "@/library/UniRouter.ts";
import {useConfigStore} from "@/store/config.ts";

const userStore = useUserStore();
const configStore = useConfigStore();
const fontScale = computed(()=>configStore.fontScale);
const { t } = useI18n();

const currentMessage = ref<MessageEntity | null>(null);
const messageId = ref(0);
const isLoading = ref(false);

onLoad((query) => {
	messageId.value = parseInt(query?.id ?? '0');
	loadMessageDetail();
})

// 加载消息详情
async function loadMessageDetail() {
	if (!messageId.value) return;

	try {
		isLoading.value = true;
		const api = new ApiGetMessageDetail(messageId.value);
		const result = await api.call();
		await userStore.loadMessageCount();
		currentMessage.value = result.data;
	} catch (error) {
		console.error('Failed to fetch message detail:', error);
	} finally {
		isLoading.value = false;
	}
}

function goOrder(){
	UniRouter.to('/pages/order/detail', {id: currentMessage.value?.orderId});
}

// 格式化时间
const formatTime = (dateStr: string) => {
	return dayjs(dateStr).fromNow()
}
</script>

<style lang="scss" scoped>
.page{
	--font-scale:v-bind(fontScale);
}
.page-message-detail {
	.detail-container {
		padding: 20px 10px;
		background: $bg-color-light;
		border-radius: 8px;

		.detail-header {
			text-align: center;
			.title {
				@include fs(13);
				@include fw(medium);
				color: $color-text-white;
			}
		}

		.detail-meta {
			text-align: center;
			.time {
				@include fs(9);
				color: $color-text-white;
			}
		}

		.detail-content {
			margin-top: 10px;
			.content-text {
				@include fs(12);
				color: $color-gray;
			}
		}
		
		.link{
			@include fs(12);
			margin-top: 0.5em;
			.label{
				color: $color-text-white;
			}
			.value{
				color: $theme-color;
				cursor: pointer;
			}
			.u-link{
				display: inline;
			}
		}
	}
}
</style>

