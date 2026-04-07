<script setup lang="ts">
import type {OrderEntity, ProjectEntity} from "@/types/entity";
import {$themeColor, accessToken, loginPage} from "@/library/GlobalVars";
import {ref} from 'vue';
import {useI18n} from 'vue-i18n';
import {ApiGetProjectDetailFile, type ProjectFile} from "@/api/project/ApiGetProjectDetailFile";
import UniRouter from "@/library/UniRouter.ts";

const {t} = useI18n();

const props = defineProps<{
	order:OrderEntity,
}>()

const fileList = computed<ProjectFile[]>(()=>{
	return JSON.parse(props.order.auditFiles) as ProjectFile[];
});

// 处理文件点击
function handleFileClick(file: ProjectFile) {
	if (file.url) {
		// 有URL则打开链接
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
</script>

<template>
	<view class="order-contract">
		<view v-if="fileList.length > 0" class="list">
			<view
				v-for="(file, index) in fileList"
				:key="file.id || index"
				class="file-item"
				@click="handleFileClick(file)">
				<u-link
					v-if="file.url"
					:color="$themeColor"
					:text="file.name"
					:href="file.url"
					font-size="inherit">
				</u-link>
				<text v-else class="file-text">{{ file.name }}</text>
			</view>
		</view>

		<!-- 无文件 -->
		<view v-else class="empty-state">
			<text>{{ t('pages.project.components.orderContract.noContracts') }}</text>
		</view>
	</view>
</template>

<style scoped lang="scss">
.order-contract{
	min-height: 176px;
	@include fs(12);

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
				color: $color-gray;
				text-decoration: none;
			}
		}
	}
	.empty{
		color: $color-gray;
		@include fs(12);
		text-align: center;
		padding: 20px;
	}
}
</style>