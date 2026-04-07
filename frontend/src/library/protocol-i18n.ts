import i18n from '@/i18n';
import { ProtocolKey } from '@/types/enums';

/**
 * 获取协议标题的国际化版本
 * 对于注册协议、用户服务协议、隐私保护协议这三个协议，
 * 使用 i18n 中 options.agreementKey 下的对应翻译
 * 其余协议保留原样
 * @param title 原始标题
 * @param agreementKey 协议类型
 * @returns 处理后的标题
 */
export function getProtocolTitle(title: string, agreementKey: ProtocolKey): string {
	// ProtocolKey 到 i18n key 的映射
	const keyMapping: Record<ProtocolKey, string | null> = {
		[ProtocolKey.REGISTER]: 'register',
		[ProtocolKey.SERVICE]: 'service',
		[ProtocolKey.PRIVACY]: 'privacy',
		[ProtocolKey.BUY]: null, // BUY 协议不需要替换
	};

	// 获取对应的 i18n key
	const i18nKey = keyMapping[agreementKey];

	// 如果有对应的 i18n key，使用翻译
	if (i18nKey) {
		const translationKey = `options.agreementKey.${i18nKey}`;
		return i18n.global.t(translationKey);
	}

	// 其余协议保留原样
	return title;
}

