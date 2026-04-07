import {Api} from "@/api";
import {ApiMethod, type ApiResult} from "@/types/api";

export interface ProjectFile {
	id: number;
	name: string;
	url?: string;
	uid?: number;
	status?: string;
}

export class ApiGetProjectDetailFile extends Api<ApiResult<ProjectFile[]>>{
	constructor(projectId: number) {
		super('/project/info/detail-file', ApiMethod.GET, {projectId});
	}

	async call(): Promise<ApiResult<ProjectFile[]>> {
		const result = await super.call();
		if(result.code === 0){
			result.data = JSON.parse(result.data.toString())
		}
		return result;
	}
}

