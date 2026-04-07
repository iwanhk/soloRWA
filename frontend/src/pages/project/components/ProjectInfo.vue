<script setup lang="ts">

import {useI18n} from 'vue-i18n';
import ProjectDetailDocument from "@/pages/project/components/ProjectDetailDocument.vue";
import Switcher from "@/components/Switcher.vue";
import ProjectDetailIntroduction from "@/pages/project/components/ProjectDetailIntroduction.vue";
import ProjectDetailInfo from "@/pages/project/components/ProjectDetailInfo.vue";
import ProjectDetailNews from "@/pages/project/components/ProjectDetailNews.vue";
import type {OrderEntity, ProjectEntity} from "@/types/entity.ts";
import type {LabelOptions} from "@/types/common.ts";
import {accessToken} from "@/library/GlobalVars.ts";
import {ApiGetProjectNoticeList} from "@/api/project/ApiGetProjectNoticeList.ts";
import OrderContract from "@/pages/project/components/OrderContract.vue";

const {t} = useI18n();

const props = defineProps<{
	project: ProjectEntity,
	order?: OrderEntity,
}>()

type InfoType = 'info' | 'intro' |'doc' | 'news' | 'contracts';
const infoTypeOptions = computed<LabelOptions<InfoType>>(()=>{
	const result:LabelOptions<InfoType> = [
		{label: t('pages.project.components.projectInfo.issuanceInfo'), value:'info'},
		{label: t('pages.project.components.projectInfo.projectIntroduction'), value:'intro'},
		{label: t('pages.project.components.projectInfo.projectMaterials'), value:'doc'},
	];
	if(accessToken.value && projectNewsTotal.value !== null) {
		result.push({label: t('pages.project.components.projectInfo.notices'), value: 'news'})
	}
	if(props.order){
		result.push({label: t('pages.project.components.projectInfo.purchaseContracts'), value: 'contracts'})
	}

	return result;
});
const selectedType = ref<InfoType>('info');
const visitedTypes = reactive<InfoType[]>([]);

watch(selectedType, (newType)=>{
	if(!visitedTypes.includes(newType)){
		visitedTypes.push(newType)
	}
},{ immediate:true })
const projectNewsTotal = ref<number|null>(null);
watch(accessToken, async (newAccessToken)=>{
	if(newAccessToken){
		const api = new ApiGetProjectNoticeList({
			projectId: props.project.projectId,
		});
		const result = await api.call();
		projectNewsTotal.value = result.data.total;
	}
	else{
		projectNewsTotal.value = null;
	}
},{immediate:true})

</script>

<template>
	<view class="project-detail-info">
		<Switcher :options="infoTypeOptions" v-model="selectedType"></Switcher>

		<ProjectDetailInfo v-if="visitedTypes.includes('info')" :project="project" v-show="selectedType === 'info'" />
		<ProjectDetailIntroduction v-if="visitedTypes.includes('intro')" :project="project" v-show="selectedType === 'intro'" />
		<ProjectDetailDocument v-if="visitedTypes.includes('doc')" :project="project" v-show="selectedType === 'doc'" />
		<ProjectDetailNews v-if="visitedTypes.includes('news')" :project="project" v-show="selectedType === 'news'" />
		<OrderContract v-if="visitedTypes.includes('contracts') && order" :order="order" v-show="selectedType === 'contracts'" />
	</view>
</template>

<style scoped lang="scss">
.project-detail-info{
	.switcher.switcher-display-tab{
		color: $color-text-white;
		justify-content: space-between;
		margin-bottom: 10px;
		@include fs(12);
		:deep(.switcher-item){
			padding: 5px 0;
			&.active{
				.text {
					color: $theme-color;
				}
			}
		}
	}
}
</style>