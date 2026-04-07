<script setup lang="ts">
import CompImage from "@/components/CompImage.vue";
import { useI18n } from 'vue-i18n';
import {useConfigStore} from "@/store/config.ts";

const { t } = useI18n();
const configStore = useConfigStore();
const fontScale = computed(()=>configStore.fontScale);
</script>

<template>
	<view class="page page-about">
		<view class="content-wrapper">
			<!-- 一、平台定位与目标 -->
			<view class="section">
				<view class="main-title">{{ t('pages.about.section1.title') }}</view>
				<view class="section-content">
					<text>{{ t('pages.about.section1.content1') }}</text>
				</view>
				<view class="section-content">
					<text>{{ t('pages.about.section1.content2') }}</text>
				</view>
				<view class="section-image">
					<CompImage src="/static/images/about/about-1.png" />
				</view>
			</view>

			<!-- 二、适用监管框架 -->
			<view class="section">
				<view class="main-title">{{ t('pages.about.section2.title') }}</view>
				<view class="sub-section">
					<view class="sub-title">{{ t('pages.about.section2.subsection1.title') }}</view>
					<view class="section-content">
						<text>{{ t('pages.about.section2.subsection1.content1') }}</text>
					</view>
					<view class="bullet-list">
						<view class="bullet-item">{{ t('pages.about.section2.subsection1.bullet1') }}</view>
						<view class="bullet-item">{{ t('pages.about.section2.subsection1.bullet2') }}</view>
						<view class="bullet-item">{{ t('pages.about.section2.subsection1.bullet3') }}</view>
						<view class="bullet-item">{{ t('pages.about.section2.subsection1.bullet4') }}</view>
					</view>
					<view class="section-content">
						<text>{{ t('pages.about.section2.subsection1.content2') }}</text>
					</view>
					<view class="bullet-list">
						<view class="bullet-item">{{ t('pages.about.section2.subsection1.bullet5') }}</view>
						<view class="bullet-item">{{ t('pages.about.section2.subsection1.bullet6') }}</view>
						<view class="bullet-item">{{ t('pages.about.section2.subsection1.bullet7') }}</view>
						<view class="bullet-item">{{ t('pages.about.section2.subsection1.bullet8') }}</view>
						<view class="bullet-item">{{ t('pages.about.section2.subsection1.bullet9') }}</view>
					</view>
					<view class="section-content">
						<text>{{ t('pages.about.section2.subsection1.content3') }}</text>
					</view>
				</view>
				<view class="section-image">
					<CompImage src="/static/images/about/about-2.png" />
				</view>
			</view>

			<!-- 三、核心功能概述 -->
			<view class="section">
				<view class="main-title">{{ t('pages.about.section3.title') }}</view>
				<view class="sub-section">
					<view class="sub-title">{{ t('pages.about.section3.subsection1.title') }}</view>
					<view class="section-content">
						<text>{{ t('pages.about.section3.subsection1.content1') }}</text>
					</view>
					<view class="bullet-list">
						<view class="bullet-item">{{ t('pages.about.section3.subsection1.bullet1') }}</view>
						<view class="bullet-item">{{ t('pages.about.section3.subsection1.bullet2') }}</view>
						<view class="bullet-item">{{ t('pages.about.section3.subsection1.bullet3') }}</view>
						<view class="bullet-item">{{ t('pages.about.section3.subsection1.bullet4') }}</view>
					</view>
					<view class="section-content">
						<text>{{ t('pages.about.section3.subsection1.content2') }}</text>
					</view>
					<view class="bullet-list">
						<view class="bullet-item">{{ t('pages.about.section3.subsection1.bullet5') }}</view>
						<view class="bullet-item">{{ t('pages.about.section3.subsection1.bullet6') }}</view>
						<view class="bullet-item">{{ t('pages.about.section3.subsection1.bullet7') }}</view>
						<view class="bullet-item">{{ t('pages.about.section3.subsection1.bullet8') }}</view>
						<view class="bullet-item">{{ t('pages.about.section3.subsection1.bullet9') }}</view>
					</view>
					<view class="section-content">
						<text>{{ t('pages.about.section3.subsection1.content3') }}</text>
					</view>
				</view>
				<view class="section-image">
					<CompImage src="/static/images/about/about-3.png" />
				</view>
			</view>
		</view>
	</view>
</template>

<style scoped lang="scss">
.page{
	--font-scale:v-bind(fontScale);
}
.page-about {
	padding: $page-padding;
	background: $bg-color;
}

.content-wrapper {
	display: flex;
	flex-direction: column;
	gap: 24px;
}

.section {
	display: flex;
	flex-direction: column;
	gap: 12px;
}

.main-title {
	@include fs(12);
	color: $color-text-white;
	font-weight: 500;
}

.sub-section {
	display: flex;
	flex-direction: column;
	gap: 8px;
}

.sub-title {
	@include fs(10);
	color: #d9d9d9;
	font-weight: 500;
}

.section-content {
	@include fs(10);
	color: $color-text-gray;
	line-height: 1.6;
	text-align: justify;
}

.bullet-list {
	display: flex;
	flex-direction: column;
	gap: 6px;
	margin-left: 12px;
}

.bullet-item {
	@include fs(10);
	color: $color-text-gray;
	line-height: 1.5;
	position: relative;
	padding-left: 8px;

	&::before {
		content: '•';
		position: absolute;
		left: 0;
	}
}
</style>

