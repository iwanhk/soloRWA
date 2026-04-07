<script setup lang="ts">
import type {ChainAddressEntity} from "@/types/entity";
import {IconFontType} from "@/types/icons";
import IconFont from "@/components/icon/IconFont.vue";
import CenterEllipseText from "@/components/CenterEllipseText.vue";
import {dictLabel} from "@/library/dict.ts";
import {chainStatusOptions} from "@/types/options.ts";

const props = defineProps<{
	item: ChainAddressEntity;
}>();

const emit = defineEmits<{
	(e: 'unbind', id: number): void;
}>();

// 处理解绑
function handleUnbind() {
	emit('unbind', props.item.id);
}
</script>

<template>
	<view class="address-card">
		<view class="card-content">
			<view class="address-section">
				<CenterEllipseText :text="item.chainAddress"></CenterEllipseText>
			</view>
			<view class="status-section">
				<text class="status-text">{{ dictLabel(chainStatusOptions, item.chainStatus) }}</text>
			</view>
			<view class="action-section">
				<IconFont
					:type="IconFontType.MINUS_FILLED"
					size="16"
					color="#555656"
					@click="handleUnbind"
				></IconFont>
			</view>
		</view>
	</view>
</template>

<style scoped lang="scss">
.address-card {
	background-color: $bg-color-light;
	border-radius: 10px;
	padding: 14px;
	margin-bottom: 12px;

	.card-content {
		display: flex;
		align-items: center;
		gap: 12px;
	}

	.address-section {
		color: $color-text-white;
		@include fs(12);
		flex: 1;
		min-width: 0;
	}

	.status-section {
		display: flex;
		align-items: center;
		min-width: 50px;

		.status-text {
			@include fs(12);
			color: $color-gray;
			white-space: nowrap;
		}
	}

	.action-section {
		display: flex;
		align-items: center;
		cursor: pointer;
	}
}
</style>

