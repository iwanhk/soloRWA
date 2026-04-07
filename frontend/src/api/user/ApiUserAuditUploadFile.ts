import {Api} from "@/api";
import {ApiMethod, type ApiResult} from "@/types/api";
import type {ApiUploadFile} from "@/api/file";

export interface ApiUserAuditUploadFileParams {
	file: ApiUploadFile;
}

export class ApiUserAuditUploadFile extends Api<ApiResult<string>>{
	constructor(params: ApiUserAuditUploadFileParams) {
		super('/user/user-audit/upload-file', ApiMethod.UPLOAD, params);
	}
}