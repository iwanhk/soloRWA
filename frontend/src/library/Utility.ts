import {UniPlatformType} from "@/library/getPlatform";
import {Buffer} from "buffer";
import dayjs from "dayjs";
import ENV from "@/library/env";
import {ShowInfo} from "@/library/ShowInfo";

export function getTimeoutPromise(time: number) {
	return new Promise<void>(resolve => setTimeout(resolve, time));
}

export function getNextTickPromise() {
	return new Promise<void>(resolve => nextTick(resolve));
}

export function readClipboard() {
	return new Promise<string | undefined>((resolve, reject) => {
		uni.getClipboardData({
			success: function (res) {
				resolve(res.data);
			},
			fail(res: { errMsg: string }) {
				reject(new Error(res.errMsg));
			}
		});
	})
}
export function setClipboard(value: string | undefined, title: string | false = '') {
	return new Promise<void>((resolve, reject) => {
		uni.setClipboardData({
			data: value ?? '', // e是你要保存的内容
			success: function () {
				if (title !== false) {
					ShowInfo.toast(`${title}复制成功`)
				}
				resolve();
			},
			fail: function (e) {
				reject(new Error(e));
			}
		})
	})
}

export function formatImageUrl<T extends string | undefined | null>(url: T): T {
	if (!url) return url;

	if(ENV.dev && url){
		url = url.replace(/^http:\/\/127.0.0.1:9000/, '') as T;
	}
	if (url!.startsWith('http://') || url!.startsWith('https://')) return url;
	return ENV.imageURL + url as T;
}

export function toWei(value: number | undefined, precise = 18) {
	if (value === undefined) return BigInt("0");

	const intPart = Math.floor(value)
	const decimalPart = (value - intPart) * 1e12

	const intBig = BigInt(intPart) * BigInt('1'.padEnd(precise + 1, '0'))
	const decimalBig = BigInt(Math.round(decimalPart * Math.pow(10, precise - 12)))

	return intBig + decimalBig
}

export function fromWei(value: string | bigint | undefined, precise: number | undefined) {
	if (value === undefined) return 0;
	if (precise === undefined) precise = 18;

	if (typeof value === 'string') {
		value = BigInt(value)
	}

	let deltaBigInt = precise > 12 ? BigInt('1'.padEnd(precise - 12 + 1, '0')) : BigInt(1);
	let deltaInt = precise > 12 ? 1e12 : Math.pow(10, precise);

	const intPart = value / BigInt('1'.padEnd(precise + 1, '0'));
	const decimalPart = (value - intPart * BigInt('1'.padEnd(precise + 1, '0'))) / deltaBigInt;

	return parseInt(intPart.toString()) + parseInt(decimalPart.toString()) / deltaInt;
}

export async function downloadFile(data: Buffer, filename: string, mimeType: string = 'application/octet-stream') {
	switch (ENV.platform) {
		case UniPlatformType.H5:
			// For H5, create a blob and use download attribute
			const blob = new Blob([data as BlobPart], {type: mimeType});
			const url = URL.createObjectURL(blob);
			const a = document.createElement('a');
			a.href = url;
			a.download = filename;
			document.body.appendChild(a);
			a.click();
			document.body.removeChild(a);
			URL.revokeObjectURL(url);
			return "";

		case UniPlatformType.APP:
			// For APP, save to app documents directory then open/share
			const tempFilePath = `${plus.io.convertLocalFileSystemURL('_doc')}/${filename}`;
			return new Promise<string>((resolve, reject) => {
				plus.io.resolveLocalFileSystemURL('_doc', (entry) => {
					entry.getFile(filename, {create: true, exclusive: false}, (fileEntry) => {
						fileEntry.createWriter((writer) => {
							writer.onwrite = () => {
								resolve(tempFilePath);
							};
							writer.onerror = (e) => {
								reject(new Error('Failed to write file: ' + e.toString()));
							};
							writer.write(Buffer.from(data).toString("binary"));
						});
					}, reject);
				}, reject);
			});
		default:
			throw new Error('Platform not supported for file download');
	}
}

function numberToLocaleString(
	value: number | undefined,
	options: {
		maximumFractionDigits?: number;
		maximumSignificantDigits?: number;
	}
): string | undefined {
	if (value === 0) return "0";
	if (value === undefined) return undefined;
	if (isNaN(value) || !isFinite(value)) {
		return value.toString();
	}

	let result = Math.abs(value);
	// 处理 maximumSignificantDigits
	if (options.maximumSignificantDigits !== undefined) {
		const magnitude = Math.floor(Math.log10(Math.abs(result))) + 1;
		if (value === 0) console.log("numberToLocaleString magnitude", magnitude);
		if (magnitude > options.maximumSignificantDigits) {
			const factor = Math.pow(10, magnitude - options.maximumSignificantDigits);
			result = Math.round(result / factor) * factor;
		} else {
			const precision = options.maximumSignificantDigits - magnitude;
			if (precision > 0) {
				result = Math.round(result * Math.pow(10, precision)) / Math.pow(10, precision);
			}
		}
	}
	// 处理 maximumFractionDigits
	if (options.maximumFractionDigits !== undefined) {
		result = Math.round(result * Math.pow(10, options.maximumFractionDigits)) / Math.pow(10, options.maximumFractionDigits);
	}
	// 转换为字符串并添加千位分隔符
	let resultStr = result.toString();
	const parts = resultStr.split('.');
	const integerPart = parts[0]!;
	const decimalPart = parts[1] || '';

	// 添加千位分隔符
	let formatted = integerPart.replace(/\B(?=(\d{3})+(?!\d))/g, ',');
	if (decimalPart) {
		formatted += '.' + decimalPart;
	}

	// 添加负号
	if (value < 0) {
		formatted = '-' + formatted;
	}
	return formatted;
}

export function formatNumber(value: number | undefined, digits?: number, totalDigits?: number) {
	if (value === undefined) return undefined;

	return numberToLocaleString(value, {
		maximumFractionDigits: digits,
		maximumSignificantDigits: totalDigits,
	})
}

export function formatTime(time: string | number | undefined | null | Date, format: string = 'YYYY-MM-DD HH:mm:ss') {
	if(time === undefined || time === null)return '';
	const momentInstance = dayjs(time);
	return momentInstance.format(format);
}

export function previewImage(url: string|string[]) {
	uni.previewImage({
		urls: typeof url === "string"? [url]: url,
		current: 0,
		indicator: 'default',
	});
}