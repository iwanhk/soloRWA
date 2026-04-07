import {Api} from "@/api";
import {ApiMethod, type ApiResult} from "@/types/api";
import type {ApiUploadFile} from "@/api/file";

export interface ApiUserUpdateProfileParams {
	nickName?: string; // 用户昵称
	avatarFile?: ApiUploadFile; // 用户头像文件
}

export class ApiUserUpdateProfile extends Api<ApiResult<boolean>>{
	constructor(params: ApiUserUpdateProfileParams) {
		super('/user/auth/update-profile', ApiMethod.UPLOAD, params);
	}
}

