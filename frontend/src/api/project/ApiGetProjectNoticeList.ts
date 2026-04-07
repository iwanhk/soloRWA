import {Api} from "@/api";
import {type ApiListParams, type ApiListResult, ApiMethod} from "@/types/api";
import type {ProjectNoticeEntity} from "@/types/entity";

export interface ApiGetProjectNoticeListParams extends ApiListParams{
	projectId: number;
}

export class ApiGetProjectNoticeList extends Api<ApiListResult<ProjectNoticeEntity>>{
	constructor(params: ApiGetProjectNoticeListParams) {
		super('/project/info/noticeList', ApiMethod.GET, params);
	}
}

