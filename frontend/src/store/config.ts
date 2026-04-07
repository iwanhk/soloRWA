import {defineStore} from "pinia";
import UniStorage from "@/library/UniStorage";
import {StorageKey} from "@/types/storage";
import {Currency, Language} from "@/types/enums.ts";
import type {ExchangeRate} from "@/types/entity";
import {ApiGetExchangeRate} from "@/api/generic/ApiGetExchangeRate";
import {ApiUserSetCurrency} from "@/api/user/ApiUserSetCurrency";
import {useUserStore} from "@/store/user";
import dayjs from 'dayjs';
import relativeTime from 'dayjs/plugin/relativeTime';
import 'dayjs/locale/zh-cn';
import 'dayjs/locale/en';
import 'dayjs/locale/zh-hk.js';
import ENV from "@/library/env.ts";
import {setLocale} from "@/i18n";
import type {WritableComputedRef} from "@vue/reactivity";

// 初始化 dayjs 插件
dayjs.extend(relativeTime);



export const useConfigStore = defineStore('config', () => {
	// language 从 uniapp 读取，如果没有则使用 uniapp 的语言设置
	const language = UniStorage.register<Language>(StorageKey.CONFIG_LANGUAGE)

	// currency 如果没有则使用 USD
	const currency = UniStorage.register<Currency>(StorageKey.CONFIG_CURRENCY);

	// 资产隐藏状态
	const assetHidden = UniStorage.register<boolean>(StorageKey.CONFIG_ASSET_HIDDEN);

	// 国家代码
	const countryCode = UniStorage.register<string>(StorageKey.CONFIG_COUNTRY_CODE);

	// 字体缩放系数
	const fontScale = UniStorage.register<number>(StorageKey.CONFIG_FONT_SCALE) as WritableComputedRef<number>;

	// 汇率数据
	const exchangeRate = ref<ExchangeRate | undefined>(undefined);
	const exchangeRateLoading = ref(false);

	//const protocolAgree = UniStorage.register<boolean>(StorageKey.PROTOCOL_AGREE)
	const protocolAgree = ref(false);


	if(!language.value){
		language.value = uni.getLocale() as Language;
	}
	if(!currency.value){
		currency.value = ENV.foreign?Currency.USD:Currency.CNY;
	}
	if(!countryCode.value){
		countryCode.value = ENV.foreign?'HK':'CN';
	}
	if(!fontScale.value){
		fontScale.value = 1;
	}

	/**
	 * 加载汇率数据
	 */
	async function loadExchangeRate() {
		if (exchangeRateLoading.value) return;

		try {
			exchangeRateLoading.value = true;
			const api = new ApiGetExchangeRate();
			const result = await api.call();
			exchangeRate.value = result.data;
		} catch (error) {
			console.error('加载汇率失败:', error);
		} finally {
			exchangeRateLoading.value = false;
		}
	}

	/**
	 * 更新语言设置并同步 dayjs locale
	 */
	function setLanguage(lang: Language) {
		language.value = lang;
	}

	/**
	 * 设置货币并同步到服务器
	 */
	async function setCurrency(curr: Currency) {
		currency.value = curr;
		const api = new ApiUserSetCurrency(curr);
		api.call();
	}

	/**
	 * 设置国家代码
	 */
	function setCountryCode(code: string) {
		countryCode.value = code;
	}

	/**
	 * 设置字体缩放系数
	 */
	function setFontScale(scale: number) {
		fontScale.value = scale;
	}

	/**
	 * 定期加载汇率（每5分钟）
	 */
	function scheduleLoadExchangeRate() {
		loadExchangeRate();
		setTimeout(() => {
			scheduleLoadExchangeRate();
		}, 5 * 60 * 1000);
	}

	scheduleLoadExchangeRate();

	// 监听语言变化，同步更新 dayjs locale 和 i18n locale
	watch(language, (newLang) => {
		if (newLang) {
			setLocale(newLang);
		}
	},{immediate:true});

	// 监听用户信息中的 defaultCurrency，自动设置 currency
	const userStore = useUserStore();
	watch(() => userStore.user?.defaultCurrency, (defaultCurrency) => {
		if (defaultCurrency && defaultCurrency !== currency.value) {
			currency.value = defaultCurrency;
		}
	}, {immediate: true});

	return {
		language,
		currency,
		assetHidden,
		countryCode,
		fontScale,
		exchangeRate,
		exchangeRateLoading,
		loadExchangeRate,
		protocolAgree,
		setLanguage,
		setCurrency,
		setCountryCode,
		setFontScale,
	};
});

