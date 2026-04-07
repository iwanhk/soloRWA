<script setup lang="ts">
import UniRouter from "@/library/UniRouter";
import {mainPage} from "@/library/GlobalVars";
import {useI18n} from "vue-i18n";
import {useConfigStore} from "@/store/config.ts";
const { t } = useI18n()
const configStore = useConfigStore();
const fontScale = computed(()=>configStore.fontScale);
function goHome() {
	UniRouter.to(mainPage)
}
</script>

<template>
	<view class="page page-verify-submitted">
		<view class="content">
			<view class="image-box">
				<view class="text">{{ t('pages.user.verifySubmitted.message') }}</view>
			</view>
		</view>
		<view class="action">
			<up-button shape="circle" @click="goHome" size="normal">{{ t('pages.user.verifySubmitted.home') }}</up-button>
		</view>
	</view>
</template>

<style scoped lang="scss">
.page{
	--font-scale:v-bind(fontScale);
}
.page-verify-submitted {
	@include flex-column(center);
	height: 100%;

	.content {
		flex: 1;
		@include flex-center;
		width: 100%;
	}

	.image-box {
		position: relative;
		width: 244px;
		height: 232px;
		background: url("@/static/images/user/verify-submitted.png") no-repeat center/cover;

		.text {
			position: absolute;
			bottom: 0;
			left: 0;
			right: 0;
			text-align: center;
			@include fs(12);
			color: $color-text-white;
		}
	}

	.action {
		padding: 20px;
		width: 100%;
		@include flex-center;

		:deep(.u-button) {
			border-radius: 16px;
		}
	}
}
</style>

