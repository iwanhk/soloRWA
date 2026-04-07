<script setup lang="ts">
import {useI18n} from 'vue-i18n';
import type {ProjectEntity} from "@/types/entity";
import IconFont from "@/components/icon/IconFont.vue";
import {IconFontType} from "@/types/icons";
import UniRouter from "@/library/UniRouter";
import {useUserStore} from "@/store/user.ts";
import {AuditStatus, ProjectState} from "@/types/enums.ts";
import {ShowInfo} from "@/library/ShowInfo.ts";
import PopupShare from "@/components/popup/PopupShare.vue";
import PopupContactService from "@/components/popup/PopupContactService.vue";
import {projectTypeClosed, projectTypeOpen} from "@/types/options.ts";
import dayjs from "dayjs";

const {t} = useI18n();

const props = defineProps<{
	project:ProjectEntity,
	projectState: ProjectState,
}>()

const userStore = useUserStore();
const sharePopup = ref<{open(data: any): Promise<any>}>();
const contactServicePopup = ref<{open(): Promise<any>}>();

async function goPurchase(){
	if(userStore.user?.auditStatus !== AuditStatus.AUDIT_PASSED){
		const result = await ShowInfo.confirm(t('pages.project.components.projectDetailBuy.notVerified'),t('common.dialog.confirm'),{
			confirmText:'跳转',
			cancelText:'取消',
		});

		if(result){
			await UniRouter.to('/pages/user/verify');
		}
		return;
	}

	// 检查剩余数量
	if(props.project.remainingQuantity <= 0){
		ShowInfo.toast(t('pages.project.components.projectDetailBuy.insufficientQuantity'));
		return;
	}

	// 根据项目类型判断是否可以购买
	const now = Date.now();
	const lockStartTime = props.project.lockStartTime?dayjs(props.project.lockStartTime).valueOf(): undefined;
	const lockEndTime = props.project.lockEndTime?dayjs(props.project.lockEndTime).valueOf(): undefined;

	if (props.project.projectType === projectTypeOpen) {
		// 开放型项目：判断是否到达 lockEndTime
		if (lockEndTime && now >= lockEndTime) {
			ShowInfo.toast(t('pages.project.components.projectDetailBuy.notInSubscription'));
			return;
		}
	} else if (props.project.projectType === projectTypeClosed) {
		// 封闭型项目：判断是否到达 lockStartTime
		if (lockStartTime && now >= lockStartTime) {
			ShowInfo.toast(t('pages.project.components.projectDetailBuy.notInSubscription'));
			return;
		}
	}

	await UniRouter.to('/pages/project/buy', { id: props.project.projectId });
}

async function handleShare() {
	const currentUrl = window.location.href;
	const images = props.project.projectImageUrls?.split(',')??[];

	await sharePopup.value?.open({
		title: props.project.projectName || t('pages.project.components.projectDetailBuy.projectShare'),
		content: `查看${props.project.projectName}投资项目`,
		url: currentUrl,
		imageUrl: images[0] || ''
	});
}

async function handleContactService() {
	await contactServicePopup.value?.open();
}
</script>

<template>
	<view class="project-detail-buy">
		<view class="actions">
			<view class="action" @click="handleShare">
				<IconFont :type="IconFontType.SHARE" size="20" color="#FBFBFB" />
				<text class="text">{{ t('pages.project.components.projectDetailBuy.share') }}</text>
			</view>
			<view class="action" @click="handleContactService">
				<IconFont :type="IconFontType.CHAT" size="20" color="#FBFBFB" />
				<text class="text">{{ t('pages.project.components.projectDetailBuy.customerService') }}</text>
			</view>
		</view>
		<up-button @click="goPurchase" class="purchase-button" type="primary" shape="circle">{{ t('pages.project.buy.buyButton') }}</up-button>
		<PopupShare ref="sharePopup" />
		<PopupContactService ref="contactServicePopup" />
	</view>
</template>

<style scoped lang="scss">
.project-detail-buy{
	max-width: $page-max-width;
	margin-left: auto;
	margin-right: auto;
	background-color: $bg-color;
	border-top: 1px solid #4E4E50;
	position: fixed;
	z-index: 1;
	padding-left: $page-padding;
	padding-right: $page-padding;
	left: 0;
	right: 0;
	bottom: 0;
	height: 60px;
	@include flex-row(center);
	gap: 20px;
	.actions{
		@include flex-row(center);
		gap: 20px;
		@include fs(10);
	}
	.action{
		text-align: center;
		.text{
			color: $color-text-white;
			display: block;
			white-space: nowrap;
		}
	}
	.purchase-button{
		@include fs(14);
	}
}
</style>