<script setup lang="ts">
import CompPopup from "@/components/popup/CompPopup.vue";
import {defaultPopupExpose} from "@/library/PopupManager.ts";
import type {PopupInstance} from "@/types/popup.ts";
import {ShowInfo} from "@/library/ShowInfo.ts";
import ENV from "@/library/env";
import {UniPlatformType} from "@/library/getPlatform";
import {useI18n} from "vue-i18n";

const { t } = useI18n();

interface ShareData {
	title?: string;
	content?: string;
	url?: string;
	imageUrl?: string;
}

interface ShareOption {
	index: number;
	text: string;
	icon: string;
	name: string;
}

const dialog = ref<PopupInstance>();
const shareData = ref<ShareData>({});

const bottomData = computed<ShareOption[]>(() => {
	let data: ShareOption[] = [
		{
			index: 3,
			text: t('popup.share.copyLink'),
			icon: '/static/images/share/link.png',
			name: 'copy'
		}
	];
	if (ENV.platform === UniPlatformType.APP) {
		data.unshift(
			{
				index: 2,
				text: 'QQ',
				icon: '/static/images/share/QQ.png',
				name: 'qq'
			}
		);
		data.unshift({
			index: 1,
			text: '朋友圈',
			icon: '/static/images/share/timeline.png',
			name: 'timeline'
		});
		data.unshift(
			{
				index: 0,
				text: '微信',
				icon: '/static/images/share/wechat.png',
				name: 'wx'
			}
		);
	}
	return data;
});

defineExpose(defaultPopupExpose(dialog, {
	async open(data: ShareData) {
		shareData.value = data;
	}
}));

async function btnShare(ind: number) {
	let strProvider = "";
	let strScene = "";
	let mytype: number | string = '';

	switch (ind) {
		case 0:
			strProvider = "weixin";
			strScene = "WXSceneSession";
			mytype = 0;
			break;
		case 1:
			strProvider = "weixin";
			strScene = "WXSenceTimeline";
			mytype = 0;
			break;
		case 2:
			strProvider = "qq";
			mytype = 1;
			break;
		case 3:
			uni.setClipboardData({
				data: shareData.value.url || '',
				complete() {
					ShowInfo.toast(t('popup.share.copiedMsg'));
					dialog.value?.close();
				}
			});
			return;
	}

	if (strProvider !== "") {
		//@ts-ignore
		uni.share({
			provider: strProvider,
			scene: strScene,
			type: mytype,
			href: shareData.value.url || '',
			title: shareData.value.title || '',
			summary: shareData.value.content || '',
			imageUrl: shareData.value.imageUrl || '',
			success: function(res) {
				console.log("success:" + JSON.stringify(res));
				dialog.value?.close();
			},
			fail: function(err) {
				console.log("fail:" + JSON.stringify(err));
				ShowInfo.toast(t('popup.share.failMsg'));
			}
		});
	}
}
</script>

<template>
	<CompPopup ref="dialog" :title="t('popup.share.title')" closable>
		<view class="share-content">
			<view class="uni-share">
				<view class="uni-share-content">
					<view
						v-for="item in bottomData"
						:key="item.index"
						class="uni-share-content-box"
						@click="btnShare(item.index)"
					>
						<view class="uni-share-content-image">
							<image :src="item.icon" class="content-image" />
						</view>
						<text class="uni-share-content-text">{{ item.text }}</text>
					</view>
				</view>
			</view>
		</view>
	</CompPopup>
</template>

<style scoped lang="scss">
.share-content {
	padding: 0;
}

.uni-share {
	display: flex;
	flex-direction: column;
	background-color: #fff;

	.uni-share-content {
		display: flex;
		flex-direction: row;
		flex-wrap: nowrap;
		justify-content: space-around;
		justify-content: space-evenly;
		overflow-x: scroll;
		padding: 15px;

		.uni-share-content-box {
			display: flex;
			flex-direction: column;
			align-items: center;
			justify-content: space-between;

			&:nth-last-child(1) {
				margin-right: 0;
			}

			.uni-share-content-image {
				display: flex;
				flex-direction: row;
				justify-content: center;
				align-items: center;
				width: 90rpx;
				height: 90rpx;
				overflow: hidden;
				border-radius: 10rpx;

				.content-image {
					width: 90rpx;
					height: 90rpx;
				}
			}

			&:nth-last-child(1) {
				.uni-share-content-image .content-image {
					width: 50rpx !important;
					height: 50rpx !important;
				}
			}

			.uni-share-content-text {
				@include fs(13);
				color: #333;
				padding-top: 5px;
				padding-bottom: 10px;
			}
		}
	}
}
</style>

