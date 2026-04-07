import {useConfigStore} from "@/store/config";
import {type CryptoCurrency, Currency, type ExtendCurreny} from "@/types/enums";
import {dictKey} from "@/library/dict.ts";
import {currencyOptions} from "@/types/options.ts";
import ENV from "@/library/env.ts";
import type {CurrencyInfo} from "@/types/entity";

/**
 * 格式化价格
 * @param price USDT 价格
 * @param showUnit 是否显示货币符号，默认 true
 * @param precise 小数最大位数，默认 2
 * @param exactPrecise 是否精确显示小数位数
 * @returns 格式化后的价格字符串
 */
export function formatPriceInfo(price: number | undefined | null | CurrencyInfo[] | CurrencyInfo, showUnit: boolean|Currency = true, precise = 2, exactPrecise = false):[string, Currency|undefined] {
	if(Array.isArray(price) || (typeof price  === "object" && price !== null)){
		price = transformCurrencyInfoToUsdt(price);
	}

	if (typeof price !== "number") {
		return ['--', undefined];
	}

	const configStore = useConfigStore();
	const rate = configStore.exchangeRate;
	if(!rate){
		return ['--', undefined];
	}

	let convertedPrice: number;
	const currency = typeof showUnit === 'boolean' ? configStore.currency : showUnit;

	convertedPrice = transformCurrency(price, currency);

	if(isNaN(convertedPrice)){
		return ['--', undefined];
	}
	
	// 格式化小数位数
	let formattedPrice = formatNumber(convertedPrice, precise, exactPrecise)!;

	return [formattedPrice,currency];
}

export function formatPrice(price: number | undefined | null | CurrencyInfo[] | CurrencyInfo, showUnit: boolean|Currency = true, precise = 2, exactPrecise = false){
	const [formattedPrice,currency] = formatPriceInfo(price, showUnit, precise, exactPrecise);

	let currencySymbol = dictKey(currencyOptions, currency, 'unit');
	const configStore = useConfigStore();
	// 根据 showUnit 决定是否显示货币符号
	if (showUnit) {
		return `${currencySymbol} ${formattedPrice}`;
	} else {
		return formattedPrice;
	}
}

export function formatNumber(value: number | undefined | null, precise = 2, exactPrecise = false){
	if(typeof value !== "number")return value;
	
	let formated = value.toFixed(precise);
	if(!exactPrecise){
		formated = Number(formated).toString();
	}
	return formated;
}

export function transformCurrency(amount: number, toCurrency?: ExtendCurreny, fromCurrency: ExtendCurreny = Currency.USDT){
	const configStore = useConfigStore();
	if(!toCurrency){
		toCurrency = configStore.currency ?? (ENV.foreign ? Currency.USD : Currency.CNY);
	}
	const rate = configStore.exchangeRate;

	if (!rate) {
		return Number.NaN;
	}

	let toRate: number | undefined = undefined;
	switch (toCurrency){
		case Currency.CNY:
			toRate = rate.cny;
			break;
		case Currency.HKD:
			toRate = rate.hkd;
			break;
		case Currency.USD:
			toRate = rate.usd;
			break;
		case Currency.USDT:
			toRate = 1;
			break;
		default:
			if(rate[toCurrency.toLocaleLowerCase() as Lowercase<CryptoCurrency>]){
				toRate = rate[toCurrency.toLocaleLowerCase() as Lowercase<CryptoCurrency>];
				break;
			}
	}
	
	let fromRate: number | undefined = undefined;
	switch (fromCurrency){
		case Currency.CNY:
			fromRate = rate.cny;
			break;
		case Currency.HKD:
			fromRate = rate.hkd;
			break;
		case Currency.USD:
			fromRate = rate.usd;
			break;
		case Currency.USDT:
			fromRate = 1;
			break;
		default:
			if(rate[fromCurrency.toLocaleLowerCase() as Lowercase<CryptoCurrency>]){
				fromRate = rate[fromCurrency.toLocaleLowerCase() as Lowercase<CryptoCurrency>];
				break;
			}
	}
	
	if(fromRate === undefined || toRate === undefined){
		return Number.NaN;
	}
	
	return amount/fromRate*toRate;
}

export function formatFileSize(bytes:number) {
	if (bytes === 0) return '0 B';
	const k = 1024;
	const sizes = ['B', 'KB', 'MB', 'GB'];
	const i = Math.floor(Math.log(bytes) / Math.log(k));
	return Math.round(bytes / Math.pow(k, i) * 100) / 100 + ' ' + sizes[i];
}

export function transformCurrencyInfoToUsdt(info: CurrencyInfo[] | CurrencyInfo){
	if(Array.isArray(info)) {
		return info.reduce((acc, info) => acc + transformCurrency(info.total, Currency.USDT, info.coinCode), 0)
	}
	else{
		return transformCurrency(info.total, Currency.USDT, info.coinCode)
	}
}