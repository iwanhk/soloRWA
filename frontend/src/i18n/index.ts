import {createI18n, useI18n} from 'vue-i18n'
import en from './locale/en.json'
import zhHans from './locale/zh-Hans.json'
import zhHant from './locale/zh-Hant.json'
import {StorageKey} from "@/types/storage.ts";
import UniStorage from "@/library/UniStorage.ts";
import {updateTabBarTextsForIOS} from "@/i18n/PageTitleI18n.ts";
import {Language} from "@/types/enums.ts";
import dayjs from "dayjs";

// 语言到 dayjs locale 的映射
const languageToDayjsLocale: Record<Language, string> = {
	[Language.ZH_CN]: 'zh-cn',
	[Language.ZH_HK]: 'zh-hk',
	[Language.EN_US]: 'en',
};

const i18n = createI18n({
	locale: uni.getLocale(),
	fallbackLocale: 'en',
	legacy: false,
	messages: {
		'en': en,
		'zh-Hans': zhHans,
		'zh-Hant': zhHant,
	},
})

export function setLocale (lang: Language) {
	i18n.global.locale.value = lang;
	UniStorage.set(StorageKey.CONFIG_LANGUAGE, lang)

	uni.setLocale(lang)
	const dayjsLocale = languageToDayjsLocale[lang] || 'zh-cn';
	dayjs.locale(dayjsLocale);
	
	setTimeout(()=>{
		uni.setLocale(lang)

		// 在iOS平台下手动更新tabbar文字
		updateTabBarTextsForIOS();
	}, 500);
}

export default i18n

