<script setup lang="ts">
import defaultAvatar from "@/static/images/demo/avatar.png";
import {useUserStore} from "@/store/user";
import {formatImageUrl} from "@/library/Utility.ts";
import {IconFontType} from "@/types/icons";
import IconFont from "@/components/icon/IconFont.vue";
import PopupEditUserInfo from "@/components/popup/PopupEditUserInfo.vue";
import type {PopupInstance} from "@/types/popup";
import {useI18n} from "vue-i18n";
import UniRouter from "@/library/UniRouter.ts";
import {loginPage} from "@/library/GlobalVars.ts";

const userStore = useUserStore();
const editUserInfoPopup = ref<PopupInstance>();

const {t} = useI18n();

const avatarUrl = computed(()=>{
	if(userStore.user?.avatar){
		return formatImageUrl(userStore.user.avatar);
	}
	else{
		return defaultAvatar;
	}
})

const displayName = computed(()=>userStore.user?.realName || userStore.user?.nickName)

// 根据字符串生成深色颜色
function generateDarkColor(str: string): string {
	if (!str) return '#333333';

	let hash = 0;
	for (let i = 0; i < str.length; i++) {
		hash = str.charCodeAt(i) + ((hash << 5) - hash);
	}

	// 生成深色颜色（RGB值在50-150之间）
	const hue = Math.abs(hash) % 360;
	const saturation = 70 + (Math.abs(hash) % 20);
	const lightness = 35 + (Math.abs(hash) % 20);

	return `hsl(${hue}, ${saturation}%, ${lightness}%)`;
}

const avatarBgColor = computed(() => {
	return displayName.value ? generateDarkColor(displayName.value) : '#333333';
})

const avatarText = computed(() => {
	return displayName.value ? displayName.value.charAt(0).toUpperCase() : '';
})

function openEditUserInfo() {
	editUserInfoPopup.value?.open();
}

function goLogin(){
	UniRouter.to(loginPage);
}

</script>

<template>
	<view class="user-info">
		<view class="avatar" v-if="displayName !== undefined" :style="{ backgroundColor: avatarBgColor }">
			{{ avatarText }}
		</view>
		<view class="name-container" v-if="userStore.user">
			<view class="name">
				{{displayName}}
			</view>
		</view>
		<view class="name-container" v-else>
			<u-button @click="goLogin" class="action" type="primary" shape="circle" size="normal">{{ t('pages.user.index.loginRegister') }}</u-button>
		</view>
		
		<PopupEditUserInfo ref="editUserInfoPopup" />
	</view>
</template>

<style scoped lang="scss">
.user-info{
	.avatar{
		margin-left: auto;
		margin-right:auto;
		@include flex-center;
		margin-bottom: 5px;
		width: 53px;
		height: 53px;
		border-radius: 50%;
		color: white;
		font-size: 28px;
		font-weight: bold;
		transition: background-color 0.3s ease;
	}
	.name-container{
		@include flex-center;
		gap: 6px;
		.action{
			width: 10em;
		}
		.name{
			text-align: center;
			color: $color-text-white;
			@include fs(12);
		}
		.edit-btn{
			@include flex-center;
			width: 20px;
			height: 20px;
			background: rgba(255, 255, 255, 0.2);
			border-radius: 50%;
			cursor: pointer;
		}
	}
	.identifier{
		margin-top: 3px;
		@include flex-center;
		.badge{
			background: $theme-color;
			color: $color-text-white;
			@include fs(8, 8);
			padding: 1px 4px;
			border-radius: 4px;
		}
	}
}
</style>