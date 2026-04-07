<template>
	<view class="login-history-card">
		<view class="card-row">
			<text class="label">{{ t('components.loginHistoryCard.loginType') }}</text>
			<text class="value">{{ dictLabel(loginTypeOptions, log.loginType) }}</text>
		</view>
		<view class="card-row">
			<text class="label">{{ t('components.loginHistoryCard.loginTime') }}</text>
			<text class="value">{{ formatTime(log.loginTime) }}</text>
		</view>
		<view class="card-row">
			<text class="label">{{ t('components.loginHistoryCard.loginCity') }}</text>
			<text class="value">{{ log.loginCity }}</text>
		</view>
		<view class="card-row">
			<text class="label">{{ t('components.loginHistoryCard.loginIp') }}</text>
			<text class="value">{{ log.loginIp }}</text>
		</view>
	</view>
</template>

<script lang="ts" setup>
import type {LoginHistoryEntity} from "@/types/entity";
import {formatTime} from "@/library/Utility.ts";
import {dictLabel} from "@/library/dict.ts";
import {loginTypeOptions} from "@/types/options.ts";
import {useI18n} from "vue-i18n";

interface Props {
	log: LoginHistoryEntity;
}

const props = defineProps<Props>();
const { t } = useI18n();
</script>

<style lang="scss" scoped>
@import "@/styles/definition.scss";

.login-history-card {
	background: $bg-color-light;
	padding: 8px;
	border-radius: 8px;
	display: flex;
	flex-direction: column;
	gap: 4px;
	@include fs(10);
	color: $color-gray;
}

.card-row {
	display: flex;
	align-items: center;
	gap: 4px;
}

.label {
	flex: none;
}

</style>

