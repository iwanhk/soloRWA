import {Api, NoneTokenApi} from "@/api";
import {type ApiListParams, type ApiListResult, ApiMethod} from "@/types/api";
import type {SimpleProjectEntity} from "@/types/entity";
import {AssetType, ProjectStatus} from "@/types/enums";

export interface ApiGetProjectListParams extends ApiListParams{
	projectStatus?: ProjectStatus;
	assetType?: AssetType
}

@NoneTokenApi
export class ApiGetProjectList extends Api<ApiListResult<SimpleProjectEntity>>{
	constructor(params: ApiGetProjectListParams) {
		super('/project/info/list', ApiMethod.GET, params);
	}
}

