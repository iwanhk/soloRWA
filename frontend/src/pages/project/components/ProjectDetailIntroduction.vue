<script setup lang="ts">
import {useI18n} from 'vue-i18n';
import type {ProjectEntity} from "@/types/entity";
import {computed} from 'vue';

const {t} = useI18n();

const props = defineProps<{
	project:ProjectEntity,
}>()

const html = computed(() => {
	// 如果有项目介绍，使用项目介绍；否则显示默认提示
	if (props.project?.projectIntro) {
		return props.project.projectIntro;
	}

	return `<div style="background:#12131a;padding:20px;text-align:center;">
		<div style="font-size:14px;color:rgba(233,238,248,.75);">
			${t('pages.project.components.projectDetailIntroduction.noIntroduction')}
		</div>
	</div>`;
})
</script>

<template>
	<view class="project-detail-introduction">
		<rich-text :selectable="true" :nodes="html"></rich-text>
	</view>
</template>

<style scoped lang="scss">
.project-detail-introduction{
	min-height: 176px;
	color: $color-text-white;
	@include fs(12);
	:deep(img){
		max-width: 100%!important;
		height: auto!important;
	}
}
</style>