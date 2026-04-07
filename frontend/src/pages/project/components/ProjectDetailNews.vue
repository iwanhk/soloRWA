<script setup lang="ts">
import {useI18n} from 'vue-i18n';
import type {ProjectEntity, ProjectNoticeEntity} from "@/types/entity";
import {ApiGetProjectNoticeList} from "@/api/project/ApiGetProjectNoticeList";
import {onMounted, ref} from 'vue';
import PopupNoticeDetail from "@/pages/project/components/PopupNoticeDetail.vue";
import {formatTime} from "@/library/Utility.ts";

const {t} = useI18n();

const props = defineProps<{
	project:ProjectEntity,
}>()

const notices = ref<ProjectNoticeEntity[]>([]);
const loading = ref(false);
const noticeDetailPopup = ref();

// 加载项目通知公告
const loadNotices = async () => {
	if (!props.project?.projectId) return;

	loading.value = true;
	try {
		const api = new ApiGetProjectNoticeList({
			projectId: props.project.projectId,
			pageNo: 1,
			pageSize: 100
		});
		const result = await api.call();
		notices.value = result.data?.list || [];
	} catch (error) {
		console.error('Failed to load project notices:', error);
		notices.value = [];
	} finally {
		loading.value = false;
	}
}

// 打开通知详情弹框
const openNoticeDetail = (notice: ProjectNoticeEntity) => {
	noticeDetailPopup.value?.open(notice);
}

onMounted(() => {
	loadNotices();
})
</script>

<template>
	<view class="project-detail-news">
		<view v-if="loading" class="loading">
			<up-loading-icon></up-loading-icon>
		</view>
		<view v-else-if="notices.length > 0">
			<u-steps current="-1" direction="column" dot>
				<u-steps-item
					v-for="(notice, index) in notices"
					:key="notice.id"
					:title="notice.noticeTitle"
					:desc="formatTime(notice.publishTime, 'YYYY-MM-DD')"
					@click="openNoticeDetail(notice)"
					class="notice-item">
				</u-steps-item>
			</u-steps>
		</view>
		<view v-else class="empty">
			<text>{{ t('pages.project.components.projectDetailNews.noNotices') }}</text>
		</view>
		<PopupNoticeDetail ref="noticeDetailPopup"></PopupNoticeDetail>
	</view>
</template>

<style scoped lang="scss">
.project-detail-news {
	min-height: 176px;
	.loading {
		@include flex-center();
		height: 176px;
	}

	.empty {
		color: $color-gray;
		@include fs(12);
		text-align: center;
		padding: 40px 20px;
	}
}

.u-steps{
	:deep(.u-steps-item){
		&:only-child{
			.u-steps-item__line{
				display: none;
			}
		}
	}
	:deep(.u-steps-item__wrapper){
		background: none;
	}
	:deep(.u-steps-item__line){
		top: 6px!important;
		height: 32px!important;
		background: $color-text-white!important;
	}
	:deep(.u-steps-item__wrapper){
		width: 21px;
		height: 21px;
		.u-steps-item__wrapper__dot{
			background-color: $bg-color!important;
			border: 1px solid $color-text-white;
		}
	}
	:deep(.u-text__value--tips){
		color: $color-text-white;
	}
	:deep(.u-text__value--content){
		color: $color-gray;
	}
	:deep(.u-steps-item__content){
		@include flex-row(center, flex-start);
		.u-steps-item__content__desc{
			order: 1;
			@include fs(10);
			width: 6.5em;
			flex: none;
			color: $color-text-white;
			.u-text__value{
				font-size: 10px!important;
			}
		}
		.u-steps-item__content__title{
			position: relative;
			width: 100%;
			height: 100%;
			order: 2;
			color: $color-gray;
			.u-text{
				position: absolute;
				left: 0;
				top: 0;
				width: 100%;
				@include ellipsis();
			}
			.u-text__value{
				font-size: 10px!important;
				>*{
					@include ellipsis();
				}
			}
		}
	}
}

.notice-item {
	cursor: pointer;
}
</style>