export enum UniPlatformType {
	APP= 'APP',
	WEIXIN= 'H5-WEIXIN',
	H5='H5',
	MP_WEIXIN='MP-WEIXIN',
	MP_ALIPAY= 'MP-ALIPAY',
	MP_BAIDU= 'MP-BAIDU',
	MP_TOUTIAO = 'MP-TOUTIAO',
	MP_LARK = 'MP-LARK',
	MP_QQ = 'MP-QQ',
	MP_KUAISHOU = 'MP-KUAISHOU',
	MP_360 = 'MP-360',
	QUICKAPP_WEBVIEW='QUICKAPP-WEBVIEW',
	UNKNOWN="UNKNOWN"
}

export function getPlatform (): UniPlatformType {
	let platform = UniPlatformType.UNKNOWN;
	
// #ifdef APP-PLUS
	platform = UniPlatformType.APP
// #endif
// #ifdef APP-PLUS-NVUE
	platform = UniPlatformType.APP
// #endif
// #ifdef H5
	platform = /MicroMessenger/i.test(navigator.userAgent) ? UniPlatformType.WEIXIN : UniPlatformType.H5
// #endif
// #ifdef MP-WEIXIN
	platform = UniPlatformType.MP_WEIXIN
// #endif
// #ifdef MP-ALIPAY
	platform = UniPlatformType.MP_ALIPAY
// #endif
// #ifdef MP-BAIDU
	platform = UniPlatformType.MP_BAIDU
// #endif
// #ifdef MP-TOUTIAO
	platform = UniPlatformType.MP_TOUTIAO
// #endif
// #ifdef MP-LARK
	platform = UniPlatformType.MP_LARK
// #endif
// #ifdef MP-QQ
	platform = UniPlatformType.MP_QQ
// #endif
// #ifdef MP-KUAISHOU
	platform = UniPlatformType.MP_KUAISHOU
// #endif
// #ifdef MP-360
	platform = UniPlatformType.MP_360
// #endif
// #ifdef QUICKAPP-WEBVIEW
	platform = UniPlatformType.QUICKAPP_WEBVIEW
// #endif
	return platform
}