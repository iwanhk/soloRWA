import {Api, NoneTokenApi} from "@/api";
import {ApiMethod, type ApiResult} from "@/types/api";
import type {ProjectEntity} from "@/types/entity";

@NoneTokenApi
export class ApiGetProjectDetail extends Api<ApiResult<ProjectEntity>>{
	constructor(projectId: number) {
		super('/project/info/detail', ApiMethod.GET, {projectId});
	}

	async call(): Promise<ApiResult<ProjectEntity>> {
		const result = await super.call();
		
		if(result.data.lockStartTime){
			result.data.lockStartTime = transformDate(result.data.lockStartTime);
		}
		if(result.data.lockEndTime){
			result.data.lockEndTime = transformDate(result.data.lockEndTime);
		}
		if(result.data.firstDividendDate){
			result.data.firstDividendDate = transformDate(result.data.firstDividendDate);
		}
		
		return result;
	}
}

function transformDate(date: [number, number, number] | Date | undefined): Date{
	if(date !== undefined && Array.isArray(date)){
		return new Date(date[0], date[1]-1, date[2]).getTime() as unknown as Date;
	}
	return date as Date;
}
