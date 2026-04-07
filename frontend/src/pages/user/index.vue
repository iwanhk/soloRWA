<script lang="ts" setup>

import NavBar from "@/components/NavBar.vue";
import MessageCount from "@/components/MessageCount.vue";
import UserInfo from "@/components/UserInfo.vue";
import type {LabelOptions} from "@/types/common";
import {IconFontType} from "@/types/icons";
import IconFont from "@/components/icon/IconFont.vue";
import {$themeColor, accessToken, isTabbarPage, loginPage, mainPage} from "@/library/GlobalVars";
import {useUserStore} from "@/store/user";
import UniRouter from "@/library/UniRouter";
import ENV from "@/library/env";
import {AuditStatus} from "@/types/enums";
import PopupLanguage from "@/pages/user/components/PopupLanguage.vue";
import type {PopupInstance} from "@/types/popup";
import {onHide, onPullDownRefresh, onShow} from "@dcloudio/uni-app";
import PopupCurrency from "@/pages/user/components/PopupCurrency.vue";
import PopupFontSize from "@/pages/user/components/PopupFontSize.vue";
import {currencyOptions} from "@/types/options.ts";
import {dictLabel} from "@/library/dict.ts";
import {useConfigStore} from "@/store/config.ts";
import {ApiUserLogout} from "@/api/user/ApiUserLogout";
import PopupContactService from "@/components/popup/PopupContactService.vue";
import {useI18n} from "vue-i18n";
import PopupBindContact from "@/pages/user/components/PopupBindContact.vue";
const { t } = useI18n()
const enum MenuAction {
	BIND_MOBILE = 'bindMobile',
	BIND_EMAIL = 'bindEmail',
	VIEW_BIND = 'viewBind',
	AUDIT = 'audit',
	ORDER = 'order',
	BILL = 'bill',
	ADDRESS = 'address',
	CURRENCY = 'currency',
	PASSWORD = 'password',
	TWOFA = '2FA',
	HISTORY = 'history',
	ABOUT = 'about',
	PROTOCOL = 'protocol',
	THEME = 'theme',
	LANGUAGE = 'language',
	FONT_SIZE = 'fontSize',
	CONTACT = 'contact'
}

const userStore = useUserStore();
const configStore = useConfigStore();
const languagePopup = ref<PopupInstance>();
const currencyPopup = ref<PopupInstance>();
const fontSizePopup = ref<PopupInstance>();
const contactServicePopup = ref<PopupInstance>();
const bindMobilePopup = ref<PopupInstance>();
const bindEmailPopup = ref<PopupInstance>();

const fontScale = computed(()=>configStore.fontScale);

const userMenuGroups= computed<LabelOptions<MenuAction, {icon:IconFontType, count?: number, display?:string, hide?:boolean}>[]>(()=>{
	const group1: LabelOptions<MenuAction, {icon:IconFontType, count?: number, display?:string, hide?:boolean}> = [];

	if(ENV.foreign) {
		if (userStore.user) {
			if(!userStore.user.mobile) {
				group1.push({
					value: MenuAction.BIND_MOBILE,
					label: t('pages.user.index.menu.bindMobile'),
					icon: IconFontType.MOBILE
				});
			}
			else if(!userStore.user.email){
				group1.push({
					value: MenuAction.BIND_EMAIL,
					label: t('pages.user.index.menu.bindEmail'),
					icon: IconFontType.EMAIL
				});
			}
			else{
				group1.push({
					value: MenuAction.VIEW_BIND,
					label: '查看绑定',
					icon: IconFontType.USER,
				})
			}
		}
	}

	group1.push(
		{ value: MenuAction.AUDIT, label: t('pages.user.index.menu.audit'), icon: IconFontType.USER},
		{ value: MenuAction.ORDER, label: t('pages.user.index.menu.order'), icon: IconFontType.LIST},
		{ value: MenuAction.BILL, label: t('pages.user.index.menu.bill'), icon: IconFontType.MONEY_SQUARE},
	);
	
	if(ENV.foreign){
		group1.push(
			{ value: MenuAction.ADDRESS, label: t('pages.user.index.menu.address'), icon: IconFontType.LINK, count: userStore.user?.chainCount, hide: !ENV.foreign},
		)
	}
	
	group1.push(
		{ value: MenuAction.CURRENCY, label: t('pages.user.index.menu.currency'), icon: IconFontType.MONEY_ROUND, display: dictLabel(currencyOptions, configStore.currency) },
	)
	
	const group2: LabelOptions<MenuAction, {icon:IconFontType, count?: number, display?:string, hide?:boolean}> = [];
	
	group2.push(
		{ value: MenuAction.PASSWORD, label: t('pages.user.index.menu.password'), icon: IconFontType.LOCK},
		{ value: MenuAction.TWOFA, label: t('pages.user.index.menu.twofa'), icon: IconFontType.CHECKED},
		{ value: MenuAction.HISTORY, label: t('pages.user.index.menu.history'), icon: IconFontType.HISTORY},
		{ value: MenuAction.ABOUT, label: t('pages.user.index.menu.about'), icon: IconFontType.INFO_SQUARE},
		{ value: MenuAction.PROTOCOL, label: t('pages.user.index.menu.protocol'), icon: IconFontType.PROTOCOL},
		//{ value: MenuAction.THEME, label:'主题风格', icon: IconFontType.THEME},
		{ value: MenuAction.LANGUAGE, label: t('pages.user.index.menu.language'), icon: IconFontType.LANGUAGE},
		{ value: MenuAction.FONT_SIZE, label: t('pages.user.index.menu.fontSize'), icon: IconFontType.LANGUAGE},
		{ value: MenuAction.CONTACT, label: t('pages.user.index.menu.contact'), icon: IconFontType.TEL},
	)

	return [
		group1,
		group2,
	]
});

async function logout(){
	try {
		const api = new ApiUserLogout();
		await api.call();
	} catch (error) {
		console.error('登出API调用失败:', error);
	} finally {
		userStore.logout();
		UniRouter.to(mainPage);
	}
}

function handleMenuClick(action: MenuAction) {
	if(!accessToken.value){
		switch(action) {
			case MenuAction.BIND_MOBILE:
			case MenuAction.BIND_EMAIL:
			case MenuAction.AUDIT:
			case MenuAction.ADDRESS:
			case MenuAction.PASSWORD:
			case MenuAction.TWOFA:
			case MenuAction.ORDER:
			case MenuAction.BILL:
			case MenuAction.HISTORY:
			case MenuAction.CONTACT:
			case MenuAction.CURRENCY:
				UniRouter.to(loginPage);
				return;
		}
	}
	
	
	switch(action) {
		case MenuAction.BIND_MOBILE:
			bindMobilePopup.value?.open();
			break;
		case MenuAction.BIND_EMAIL:
			bindEmailPopup.value?.open();
			break;
		case MenuAction.VIEW_BIND:
			UniRouter.to('/pages/user/bind');
			break;
		case MenuAction.AUDIT:
			if ([AuditStatus.AUDIT_PASSED, AuditStatus.PENDING_AUDIT].includes(userStore.user?.auditStatus!)) {
				UniRouter.to('/pages/user/auth');
			} else {
				UniRouter.to('/pages/user/verify');
			}
			break;
		case MenuAction.ADDRESS:
			UniRouter.to('/pages/user/chain-address');
			break;
		case MenuAction.PASSWORD:
			UniRouter.to('/pages/user/password');
			break;
		case MenuAction.TWOFA:
			UniRouter.to('/pages/user/2fa');
			break;
		case MenuAction.ABOUT:
			UniRouter.to('/pages/main/about');
			break;
		case MenuAction.PROTOCOL:
			UniRouter.to('/pages/main/protocol-list');
			break;
		case MenuAction.LANGUAGE:
			languagePopup.value?.open();
			break;
		case MenuAction.FONT_SIZE:
			fontSizePopup.value?.open();
			break;
		case MenuAction.CURRENCY:
			currencyPopup.value?.open();
			break;
		case MenuAction.ORDER:
			UniRouter.to("/pages/order/list");
			break;
		case MenuAction.BILL:
			UniRouter.to("/pages/bill/list");
			break;
		case MenuAction.HISTORY:
			UniRouter.to("/pages/user/login-history");
			break;
		case MenuAction.CONTACT:
			contactServicePopup.value?.open();
			break;
		// 其他菜单项的处理可以在这里添加
		default:
			break;
	}
}


onShow(()=>{
	isTabbarPage.value = true
})
onHide(()=>{
	isTabbarPage.value = false
})

onPullDownRefresh(async ()=>{
	await userStore.loadUserInfo();
	await userStore.loadMessageCount();
	uni.stopPullDownRefresh()
})
</script>

<template>
	<NavBar>
		<template #right>
			<view class="message-wrap">
				<MessageCount />
			</view>
		</template>
	</NavBar>
	<view class="page page-user">
		<UserInfo />
		
		<view class="user-menu-group" v-for="userMenus of userMenuGroups">
			<view class="menu-list-item" v-for="userMenu of userMenus" @click="handleMenuClick(userMenu.value)">
				<view class="menu-list-item-icon">
					<IconFont :type="userMenu.icon" size="15" color="#FBFBFB" />
				</view>
				<view class="menu-list-item-label">
					{{userMenu.label}}<template v-if="userMenu.count !== undefined">（{{userMenu.count}}）</template>
				</view>
				<view class="menu-list-item-value" v-if="userMenu.display !==undefined">{{userMenu.display}}</view>
				<IconFont class="menu-list-item-arrow" :color="$themeColor" :type="IconFontType.CARET_RIGHT" :size="8 * configStore.fontScale" />
			</view>
		</view>
		
		<view class="logout-action" v-if="accessToken">
			<up-button type="primary" shape="circle" @click="logout">{{ t('pages.user.index.logout') }}</up-button>
		</view>
	</view>
	<!-- 语言切换弹窗 -->
	<PopupLanguage ref="languagePopup" />
	<PopupCurrency ref="currencyPopup" />
	<!-- 字体大小弹窗 -->
	<PopupFontSize ref="fontSizePopup" />
	<!-- 联系客服弹窗 -->
	<PopupContactService ref="contactServicePopup" />
	<!-- 绑定手机/邮箱弹窗 -->
	<PopupBindContact ref="bindMobilePopup" type="Mobile" />
	<PopupBindContact ref="bindEmailPopup" type="Email" />
</template>

<style scoped lang="scss">
.page{
	--font-scale:v-bind(fontScale);
}
.nav-bar{
	:deep(.uni-navbar__header-btns){
		overflow: visible;
	}
}
.user-info{
	margin-bottom: 30px;
}
.user-menu-group{
	+.user-menu-group{
		margin-top: 20px;
	}
}
.logout-action{
	margin-top: 20px;
	.u-button{
		width: 240px;
	}
}
</style>

