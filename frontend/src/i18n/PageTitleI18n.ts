import i18n from "@/i18n";
import ENV from "@/library/env";

// 导入locale文件内容
import zhHansLocale from "@/locale/zh-Hans.json";
import zhHantLocale from "@/locale/zh-Hant.json";
import enLocale from "@/locale/en.json";
import {Language} from "@/types/enums.ts";
import {tabs} from "@/library/UniPages.ts";
import {isTabbarPage} from "@/library/GlobalVars.ts";

/**
 * 根据当前语言获取对应的locale文件内容
 */
function getCurrentLocaleData(): Record<string, string> {
	const currentLang = i18n.global.locale.value;

	switch (currentLang) {
		case Language.ZH_CN:
			return zhHansLocale;
		case Language.ZH_HK:
			return zhHantLocale;
		case Language.EN_US:
			return enLocale;
		default:
			return enLocale;
	}
}

/**
 * 处理页面标题的国际化
 * 如果标题是%key%格式，则从对应的locale文件中获取翻译
 * @param title 原始标题
 * @returns 处理后的标题
 */
export function translatePageTitle(title: string): string {
	// 检查是否是%开头和结尾的格式
	if (title.startsWith('%') && title.endsWith('%')) {
		// 提取key（去掉首尾的%）
		const key = title.slice(1, -1);

		// 获取当前语言的locale数据
		const localeData = getCurrentLocaleData();

		// 查找对应的翻译
		if (localeData[key]) {
			return localeData[key];
		}

		// 如果找不到翻译，返回key本身（去掉%符号）
		return key;
	}

	// 如果不是%格式，直接返回原标题
	return title;
}

/**
 * 在iOS平台下手动更新tabbar文字
 * 用于解决iOS平台下语言切换后tabbar文字不更新的问题
 */
export function updateTabBarTextsForIOS(): void {
	// 只在iOS平台下执行
	if (ENV.appPlatform !== 'ios') {
		return;
	}

	// 获取当前语言的locale数据
	const localeData = getCurrentLocaleData();

	// tabbar配置数组，对应pages.json中的tabBar.list
	const tabBarItems = tabs?.map((item, index) => {
		if (item.text.startsWith('%') && item.text.endsWith('%')) {
			return {index, key: item.text.slice(1, -1)}
		}
		return {index, key: item.text}
	});

	// 逐个更新tabbar项的文字
	tabBarItems?.forEach(item => {
		const text = localeData[item.key] || item.key;
		if(isTabbarPage.value) {
			uni.setTabBarItem({
				index: item.index,
				text: text
			});
		}
	});
}
