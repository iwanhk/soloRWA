<script setup lang="ts">
import {useI18n} from 'vue-i18n';
import type {ProjectEntity} from "@/types/entity";
import {accessToken} from "@/library/GlobalVars";
import {ref} from 'vue';
import {ApiGetProjectDetailFile, type ProjectFile} from "@/api/project/ApiGetProjectDetailFile";
import PopupPdfPreview from "@/components/popup/PopupPdfPreview.vue";
import PopupVideoPreview from "@/components/popup/PopupVideoPreview.vue";
import ENV from "@/library/env.ts";

const {t} = useI18n();

const props = defineProps<{
	project:ProjectEntity,
}>()

const fileList = ref<ProjectFile[]>([]);
const loading = ref(false);
const hasError = ref(false);
const pdfPreviewPopup = ref();
const videoPreviewPopup = ref();
const videoExtensions = ['mp4', 'mov', 'm4v', 'webm'];

// 加载项目文件
async function loadProjectFiles() {
	if (!accessToken.value) {
		return;
	}

	try {
		loading.value = true;
		hasError.value = false;
		const api = new ApiGetProjectDetailFile(props.project.projectId);
		const result = await api.call();
		fileList.value = result.data || [];
	} catch (error) {
		console.error('Failed to load project files:', error);
		hasError.value = true;
		fileList.value = [];
	} finally {
		loading.value = false;
	}
}

// 获取文件后缀
function getFileExtension(url: string): string {
	const normalizedUrl = url.split('?')[0]?.split('#')[0] || '';
	const fileName = normalizedUrl.split('/').pop() || '';
	const dotIndex = fileName.lastIndexOf('.');
	return dotIndex >= 0 ? fileName.substring(dotIndex + 1).toLowerCase() : '';
}

// 检查是否是 PDF 文件
function isPdfFile(url: string): boolean {
	return getFileExtension(url) === 'pdf';
}

// 检查是否是视频文件
function isVideoFile(url: string): boolean {
	return videoExtensions.includes(getFileExtension(url));
}

// 处理文件点击
function handleFileClick(file: ProjectFile) {
	if (file.url) {
		// 如果是 PDF 文件，使用 PDF 预览
		/*if (isPdfFile(file.url)) {
			pdfPreviewPopup.value?.open(file.name, file.url);
		} else*/ if (isVideoFile(file.url)) {
			videoPreviewPopup.value?.open(file.name, file.url);
		} else {
			// 其他文件类型使用原有的打开方式
			uni.openDocument({
				filePath: file.url,
				showMenu: true,
				success: () => {
					console.log('打开文件成功');
				},
				fail: () => {
					uni.showToast({
						title: '打开文件失败',
						icon: 'error'
					});
				}
			});
		}
	}
}

// 监听 accessToken 变化，自动加载文件
watch(accessToken, (newToken) => {
	if (newToken) {
		loadProjectFiles();
	} else {
		fileList.value = [];
	}
}, {immediate: true})
</script>

<template>
	<view class="project-detail-document">
		<view class="note" v-if="false">项目资质如下，在您认购项目后向您披露正文</view>

		<!-- 加载中 -->
		<view v-if="loading" class="empty">
			<text>{{ t('pages.project.components.projectDetailDocument.loading') }}</text>
		</view>

		<!-- 加载失败 -->
		<view v-else-if="hasError" class="empty">
			<text>{{ t('pages.project.components.projectDetailDocument.loadFailed') }}</text>
		</view>

		<!-- 有文件列表 -->
		<view v-else-if="fileList.length > 0" class="list">
			<view
				v-for="(file, index) in fileList"
				:key="file.id || index"
				class="file-item"
				@click="handleFileClick(file)">
				<text :class="{'file-text': !!file.url}">{{ file.name }}</text>
			</view>
		</view>

		<!-- 无文件 -->
		<view v-else class="empty-state">
			<text>{{ t('pages.project.components.projectDetailDocument.noMaterials') }}</text>
		</view>
		<!-- PDF 预览 Popup -->
		<PopupPdfPreview ref="pdfPreviewPopup" />
			<PopupVideoPreview ref="videoPreviewPopup" />
	</view>
</template>

<style scoped lang="scss">
.project-detail-document{
	min-height: 176px;
	@include fs(12);
	color: $color-text-white;

	.note{
		color: $color-gray;
		@include fs(11);
		margin-bottom: 12px;
	}
	.list{
		@include fs(10);
		.file-item{
			display: block;
			margin-top: 8px;
			cursor: pointer;

			.u-link{
				display: block;
			}

			.file-text{
				color: $theme-color;
				text-decoration: none;
			}
		}
	}
}
</style>