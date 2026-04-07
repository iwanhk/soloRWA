<template>
	<view class="page page-login-history">
		<view v-if="loginLogList" class="login-log-list">
			<LoginHistoryCard v-for="log in loginLogList" :key="log.id" :log="log" />
		</view>

		<CompLoadMore :status="loginLogStatus" />
	</view>
</template>

<script lang="ts" setup>
import LoginHistoryCard from "@/components/LoginHistoryCard.vue";
import CompLoadMore from "@/components/CompLoadMore.vue";
import {useTabListManager} from "@/composable/useTabListManager.ts";
import type {LoginHistoryEntity} from "@/types/entity";
import {ApiPagedListHandlerFactory} from "@/library/PagedListHandler.ts";
import {ApiUserLoginLog} from "@/api/user/ApiUserLoginLog.ts";
import {useConfigStore} from "@/store/config.ts";

const configStore = useConfigStore();
const fontScale = computed(()=>configStore.fontScale);
const tabListManager = useTabListManager<LoginHistoryEntity>();
tabListManager.add('default', ApiPagedListHandlerFactory(ApiUserLoginLog), true);
tabListManager.show('default');

const loginLogList = tabListManager.records;
const loginLogStatus = tabListManager.status;
</script>

<style lang="scss" scoped>
@import "@/styles/definition.scss";

.page{
	--font-scale:v-bind(fontScale);
}
.page-login-history {
	padding: 0;
}

.login-log-list {
	padding: 10px;
	display: flex;
	flex-direction: column;
	gap: 10px;
}
</style>

