import {Api} from "@/api";
import {ApiMethod, type ApiResult} from "@/types/api";
import type {ApiUploadFile} from "@/api/file";

export interface OcrIdcardResult {
	name: string; // 姓名
	idCardNo: string; // 证件号
	validDate: string; // 有效期
	gender: string; // 性别
	nation: string; // 民族
	birth: string; // 出生日期
	address: string; // 住址
	authority: string; // 签发机关
}

export interface ApiUserOcrIdcardParams {
	file: ApiUploadFile;
	side: 'front' | 'back';
}

export class ApiUserOcrIdcard extends Api<ApiResult<OcrIdcardResult>>{
	constructor(params: ApiUserOcrIdcardParams) {
		super('/user/user-audit/ocr-idcard', ApiMethod.UPLOAD, params);
	}
}

