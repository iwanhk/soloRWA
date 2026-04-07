import type {ProjectEntity} from "@/types/entity.ts";
import {ApiGetProjectDetail} from "@/api/project/ApiGetProjectDetail.ts";
import {ShowInfo} from "@/library/ShowInfo.ts";
import {ProjectState} from "@/types/enums.ts";
import dayjs from "dayjs";
import {projectTypeClosed} from "@/types/options.ts";

export function useProjectDetail(){
	const project = ref<ProjectEntity | undefined>(undefined)
	const loading = ref(false);
	const projectId = ref(0);
	
	const state = computed<ProjectState | undefined>(()=>{
		if(!project.value)return undefined;
		
		const now = Date.now();
		let divideStartTime = project.value.firstDividendDate?dayjs(project.value.firstDividendDate).valueOf():undefined;
		
		let state = ProjectState.SUBSCRIPTION;
		const lockStartTime = project.value.lockStartTime?dayjs(project.value.lockStartTime).valueOf(): undefined;
		const lockEndTime = project.value.lockEndTime?dayjs(project.value.lockEndTime).valueOf(): undefined;
		
		if(lockStartTime && lockEndTime) {
			if (project.value.projectType === projectTypeClosed) {
				if (now > lockStartTime) {
					state = ProjectState.LOCK_PERIOD;
				} else {
					return state
				}
			}
		}
		
		if(divideStartTime && now >= divideStartTime) {
			state = ProjectState.DIVIDEND;
		}
		if(lockEndTime && now >= lockEndTime){
			state = ProjectState.REDEEM
		}
		return state;
	})
	
	async function load(id?:number) {
		if(id){
			projectId.value = id;
		}
		else{
			if(!projectId.value){
				throw new Error("orderID not provided")
			}
			id = projectId.value;
		}
		
		try {
			loading.value = true
			const api = new ApiGetProjectDetail(id);
			const { data } = await api.call()
			project.value = data
		} catch (error) {
			console.error('加载项目详情失败:', error)
			ShowInfo.toastError(error, '加载项目')
		} finally {
			loading.value = false
		}
	}

	return {
		project, loading, load, id:projectId, state
	}
}