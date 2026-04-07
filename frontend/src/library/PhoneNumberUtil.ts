import { parsePhoneNumberWithError, isValidPhoneNumber } from 'libphonenumber-js'
import countries from 'i18n-iso-countries'
import zh from 'i18n-iso-countries/langs/zh.json'
import en from 'i18n-iso-countries/langs/en.json'
import { allCountries } from 'country-telephone-data'
import ENV from '@/library/env.ts'

// 注册多语言
countries.registerLocale(zh)
countries.registerLocale(en)

// 国家代码和信息
export interface CountryInfo {
	code: string // 国家代码，如 'CN', 'HK'
	nameZh: string // 中文名称
	nameEn: string // 英文名称
	dialCode: string // 拨号代码，如 '+86'
	flag: string // 国旗 emoji
}

// 缓存国家列表
let cachedCountries: CountryInfo[] | null = null

// 获取所有支持的国家列表
export function getAllCountries(): CountryInfo[] {
	if (cachedCountries) {
		return cachedCountries
	}

	const countryList = allCountries.map(item => {
		const iso2 = item.iso2.toUpperCase()
		return {
			code: iso2,
			nameZh: countries.getName(iso2, 'zh') || item.name,
			nameEn: countries.getName(iso2, 'en') || item.name,
			dialCode: '+' + item.dialCode,
			flag: getCountryFlag(iso2)
		}
	})

	cachedCountries = countryList
	return countryList
}

// 获取国家旗帜 emoji
function getCountryFlag(code: string): string {
	const codePoints = code
		.toUpperCase()
		.split('')
		.map(char => 127397 + char.charCodeAt(0))
	return String.fromCodePoint(...codePoints)
}

// 验证手机号格式
export function validatePhoneNumber(phoneNumber: string, countryCode?: string): boolean {
	if (!phoneNumber || (!phoneNumber.startsWith('+') && !countryCode)) return false

	// 如果是 foreign 模式，不能发送中国大陆
	if (ENV.foreign && (countryCode === 'CN' || phoneNumber.startsWith('+86'))) {
		return false
	}

	try {
		return isValidPhoneNumber(phoneNumber, countryCode as any)
	} catch {
		return false
	}
}

// 格式化手机号（添加国家前缀）
export function formatPhoneNumber(phoneNumber: string, countryCode: string): string {
	if (!phoneNumber || !countryCode) return phoneNumber

	try {
		const parsed = parsePhoneNumberWithError(phoneNumber, countryCode as any)
		return parsed?.number || phoneNumber
	} catch {
		return phoneNumber
	}
}

// 获取国家拨号代码
export function getDialCode(countryCode: string): string {
	const countryList = getAllCountries()
	const country = countryList.find(c => c.code === countryCode)
	return country?.dialCode || ''
}

// 根据拨号代码获取国家代码
export function getCountryCodeByDialCode(dialCode: string): string | null {
	const countryList = getAllCountries()
	const country = countryList.find(c => c.dialCode === dialCode)
	return country?.code || null
}

// 获取国家名称（支持多语言）
export function getCountryName(countryCode: string, language: 'zh' | 'en' = 'zh'): string {
	const countryList = getAllCountries()
	const country = countryList.find(c => c.code === countryCode)
	if (!country) return countryCode
	return language === 'zh' ? country.nameZh : country.nameEn
}

// 注意：i18n-iso-countries 只支持 'zh' (简体中文)，不支持 'zh-Hant' (繁体中文)
// 繁体中文使用简体中文的翻译作为备选方案
// 如果需要真正的繁体中文翻译，可以考虑使用其他库或手动维护翻译表

