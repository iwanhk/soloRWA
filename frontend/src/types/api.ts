export const enum NwEvent{
	TOKEN_EXPIRED = 'token-expired',
	ORDER_CHANGED = 'order-changed',
}

export const enum ApiMethod{
	POST = 'post',
	GET = 'get',
	PUT = 'put',
	UPLOAD = 'upload',
	DELETE = 'delete',
}

export interface ApiListParams {
	pageNo?:number,
	pageSize?: number
}

export type ApiListResult<T> = ApiResult<{
	list: T[],
	total: number
}>

export interface ApiResult<DataType = any> {
	code: number;
	data: DataType;
	msg: string;
}

export const enum API_ERROR_CODE {
	OK = 0,
	TOKEN_EXPIRED = 401,
	SMS_CODE_NOT_FOUND = 1_002_014_000,
	SMS_CODE_EXPIRED = 1_002_014_001,
	SMS_CODE_USED = 1_002_014_002,
	SMS_CODE_EXCEED_SEND_MAXIMUM_QUANTITY_PER_DAY = 1_002_014_004,
	SMS_CODE_SEND_TOO_FAST = 1_002_014_005,
}