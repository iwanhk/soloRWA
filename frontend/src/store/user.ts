import {defineStore} from "pinia";
import type {ChainAddressEntity, UserEntity} from "@/types/entity";
import type {LoginResult} from "@/api/user/ApiUserLoginByPassword";
import {accessToken} from "@/library/GlobalVars";
import {ApiGetUserInfo} from "@/api/user/ApiGetUserInfo";
import {ApiGetUserChainList} from "@/api/user/ApiGetUserChainList";
import {ApiGetMessageUnreadCount} from "@/api/message/ApiGetMessageUnreadCount";
import UniStorage from "@/library/UniStorage";
import {StorageKey} from "@/types/storage";
import timer from "@/library/Timer.ts";
import {useConfigStore} from "@/store/config.ts";

export const useUserStore = defineStore('user', () => {
	const userInfo = UniStorage.register<UserEntity>(StorageKey.USER_INFO);
	const address = ref<ChainAddressEntity[] | undefined>(undefined);
	const unreadCount = ref(10);

	/**
	 * 登录
	 * @param loginResult 登录结果，包含 accessToken 等信息
	 */
	async function login(loginResult: LoginResult) {
		try {
			// 设置 accessToken
			accessToken.value = loginResult.accessToken;

			// 加载用户信息
			await loadUserInfo();
		} catch (error) {
			console.error('登录失败:', error);
			throw error;
		}
	}

	/**
	 * 加载消息未读数
	 */
	async function loadMessageCount() {
		unreadCount.value = 0;
		const api = new ApiGetMessageUnreadCount();
		const {data} = await api.call();
		unreadCount.value = data;
	}

	/**
	 * 加载用户链地址列表
	 */
	async function loadUserChainAddressList() {
		address.value = [];
		const api = new ApiGetUserChainList();
		const result = await api.call();
		address.value = result.data;
	}

	/**
	 * 加载用户信息
	 */
	async function loadUserInfo() {
		const api = new ApiGetUserInfo();
		const result = await api.call();
		userInfo.value = result.data;

		const configStore = useConfigStore();
		configStore.currency = result.data.defaultCurrency;
		
		// 如果用户的 chainCount 大于零且链地址列表未初始化，则自动加载
		if (result.data.chainCount > 0 && !address.value) {
			await loadUserChainAddressList();
		}
	}

	/**
	 * 登出
	 */
	function logout() {
		// 清除用户信息
		userInfo.value = undefined;
		unreadCount.value = 0;

		// 清除 accessToken
		accessToken.value = undefined;
	}
	
	watch(accessToken, (newToken)=>{
		if(newToken){
			loadUserInfo();
			loadMessageCount();
		}
		else{
			userInfo.value = undefined;
			unreadCount.value = 0;
		}
	},{immediate: true})

	watch(timer.minute, ()=>{
		if(accessToken.value){
			loadUserInfo();
			loadMessageCount()
		}
	})
	
	return {
		user: userInfo,
		address,
		unreadCount,
		login,
		logout,
		loadUserInfo,
		loadUserChainAddressList,
		loadMessageCount,
	};
});

