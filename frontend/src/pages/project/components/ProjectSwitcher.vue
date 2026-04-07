<script setup lang="ts">
import {useI18n} from 'vue-i18n';
import type {LabelOptions} from "@/types/common.ts";
import Switcher from "@/components/Switcher.vue";
import ProjectCard from "@/components/ProjectCard.vue";
import type {SimpleProjectEntity} from "@/types/entity.ts";
import {useTabListManager} from "@/composable/useTabListManager.ts";
import {ApiPagedListHandlerFactory} from "@/library/PagedListHandler.ts";
import {ApiGetProjectList} from "@/api/project/ApiGetProjectList.ts";
import {ApiGetAssetTypeCount, type AssetTypeCount} from "@/api/project/ApiGetAssetTypeCount.ts";
import {AssetType, ProjectStatus} from "@/types/enums.ts";
import {assetTypeOptions} from "@/types/options.ts";

const {t} = useI18n();

const assetTypeCounts = ref<AssetTypeCount[]>([]);

const projectTypeOptions = computed<LabelOptions<AssetType | 0>>(()=>{
	const allOptions = assetTypeOptions.value;
	const countMap = new Map(assetTypeCounts.value.map(item => [item.assetType, item.count]));

	// 只显示有数量的选项
	const filteredOptions = allOptions.filter(option => countMap.has(option.value) && countMap.get(option.value)! > 0);

	// 如果只有一个选项，不需要"全部"选项
	if(filteredOptions.length === 1){
		return filteredOptions;
	}

	return [{ label: t('pages.project.components.projectSwitcher.all'), value: 0 }].concat(filteredOptions) as LabelOptions<AssetType | 0>;
})

// 加载资产类型统计
const loadAssetTypeCounts = async () => {
	try {
		const api = new ApiGetAssetTypeCount();
		const result = await api.call();
		if(result.code === 0){
			assetTypeCounts.value = result.data;
		}
	} catch (error) {
		console.error('Failed to load asset type counts:', error);
	}
}

onMounted(() => {
	loadAssetTypeCounts();
})

const selectedType = ref<AssetType | 0>(0);

const tabListManager = useTabListManager<SimpleProjectEntity>();
tabListManager.setProvide((identifier)=>{
	const type = parseInt(identifier) as AssetType | 0;
	if(type === 0){
		return ApiPagedListHandlerFactory(ApiGetProjectList, {projectStatus: ProjectStatus.ON_SALE})
	}
	else{
		return ApiPagedListHandlerFactory(ApiGetProjectList, {projectStatus: ProjectStatus.ON_SALE, assetType: type})
	}
})


const projects = tabListManager.records;
const projectEmpty = tabListManager.isEmpty;

watch(selectedType, (newType)=>{
	tabListManager.show(newType.toString());
}, {immediate:true})

// 当选项加载完成后，如果只有一个选项，自动选择它
watch(projectTypeOptions, (options) => {
	if(options.length === 1){
		selectedType.value = options[0]!.value;
	}
})

</script>

<template>
	<view class="project-switcher">
		<Switcher :options="projectTypeOptions" v-model="selectedType" />

		<view class="project-list" v-if="projects && projects.length > 0">
			<ProjectCard v-for="project of projects" :key="project.projectId" :project="project" />
		</view>
		<view v-if="projectEmpty" class="empty-state">
			<u-empty :text="t('pages.project.components.projectSwitcher.noProjects')"></u-empty>
		</view>
	</view>
</template>

<style scoped lang="scss">
.project-switcher{
	color: $color-text-white;
	.switcher{
		@include fs(14, 28);
		&.switcher-display-tab{
			:deep(.switcher-item:not(:last-child)){
				margin-right: 1.5em;
			}
		}

		margin-bottom: 20px;
	}
}
</style>