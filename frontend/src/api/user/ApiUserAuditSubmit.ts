import {Api} from "@/api";
import {ApiMethod, type ApiResult} from "@/types/api";

export interface ApiUserAuditSubmitParams {
	code: string; // 手机验证码
	emailCode: string;
	realName: string; // 用户姓名
	idCard: string; // 证件号
	idCardExpire: string; // 证件号有效期
	idCardFrontFile: string; // 证件号人像面图片文件URL
	idCardBackFile: string; // 证件号国徽面图片文件URL
	investmentQualificationFiles?: string[]; // 投资资质图片文件URL列表(最多6张)
	bankFlowFiles?: string[]; // 银行流水图片文件URL列表(最多6张)
	residenceProofFiles?: string[]; // 住址证明图片文件URL列表(最多6张)
	bankAccount: string; // 银行卡号
	bankName: string; // 开户银行
	bankAccountName: string; // 开户名
	contactPhone: string; // 联系电话
}

export class ApiUserAuditSubmit extends Api<ApiResult<boolean>>{
	constructor(params: ApiUserAuditSubmitParams) {
		super('/user/user-audit/submit', ApiMethod.UPLOAD, params);
	}
}

