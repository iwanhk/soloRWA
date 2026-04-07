<template>
	<view class="page page-message">
		<view v-if="messageList" class="message-list">
			<MessageCard
				v-for="message in messageList"
				:key="message.id"
				:message="message"
				@click="navigateToDetail(message)"
			/>
		</view>

		<CompLoadMore :status="messageListStatus" />
	</view>
</template>

<script lang="ts" setup>
import MessageCard from '@/components/MessageCard.vue'
import {useTabListManager} from "@/composable/useTabListManager.ts";
import type {MessageEntity} from "@/types/entity.ts";
import {ApiPagedListHandlerFactory} from "@/library/PagedListHandler.ts";
import {ApiGetMessageList} from "@/api/message/ApiGetMessageList.ts";
import CompLoadMore from "@/components/CompLoadMore.vue";
import {useConfigStore} from "@/store/config.ts";

const configStore = useConfigStore();
const fontScale = computed(()=>configStore.fontScale);
const tabListManager = useTabListManager<MessageEntity>();
tabListManager.add('default', ApiPagedListHandlerFactory(ApiGetMessageList), true);
tabListManager.show('default');

const messageList = tabListManager.records;
const messageListStatus = tabListManager.status;

const navigateToDetail = (message: MessageEntity) => {
	message.readStatus = true;
	uni.navigateTo({
		url: `/pages/message/detail?id=${message.id}`
	})
}
</script>

<style lang="scss" scoped>
.page{
	--font-scale:v-bind(fontScale);
}
</style>

