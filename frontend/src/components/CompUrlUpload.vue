<script setup lang="ts">
import type {UVPUploadFile, UVPUploadFileInfo} from "@/types/uview-plus";
import {ApiUserAuditUploadFile} from "@/api/user/ApiUserAuditUploadFile";
import {ApiUploadFile} from "@/api/file";
import {ShowInfo} from "@/library/ShowInfo";

const props = withDefaults(defineProps<{
	ratio?: number,
	maxCount?: number,
	maxSize?: number,
	multiple?: boolean,
	preview?: boolean
}>(), {
	maxCount: 1,
	maxSize: 5*1024*1024,
	multiple: false,
	preview: true,
})

const modelValue = defineModel<string|string[]>();
const fileList = ref<UVPUploadFile[]>([]);
const uploading = ref(false);

let committing = false;

function checkMultipleUploadFile(file:UVPUploadFile | UVPUploadFile[], isMultiple: boolean):file is UVPUploadFile[] {
	return isMultiple && Array.isArray(file);
}

async function afterRead(file:UVPUploadFile|UVPUploadFile[], info:UVPUploadFileInfo){
	if (uploading.value) return;

	try {
		uploading.value = true;
		
		if (checkMultipleUploadFile(file, props.multiple)) {
			// 多文件模式
			const newFileList = fileList.value.slice();
			const uploadedUrls: string[] = fileList.value.map(item=>item.url);
			for (const f of file) {
				const apiFile = new ApiUploadFile(f);
				const api = new ApiUserAuditUploadFile({file: apiFile});
				const response = await api.call();
				uploadedUrls.push(response.data);
				f.url = response.data;
			}
			
			newFileList.push(...file);
			fileList.value = newFileList;
			committing = true;
			modelValue.value = uploadedUrls;
			nextTick(()=>committing = false)
		} else {
			// 单文件模式
			const apiFile = new ApiUploadFile(file as UVPUploadFile);
			const api = new ApiUserAuditUploadFile({file: apiFile});
			const response = await api.call();
			
			fileList.value = [file as UVPUploadFile];
			committing = true;
			modelValue.value = response.data;
			nextTick(()=>committing = false)
		}
	} catch (error) {
		ShowInfo.toastError(error, '上传文件');
		fileList.value = [];
		modelValue.value = props.multiple ? [] : undefined;
	} finally {
		uploading.value = false;
	}
}

function handleDelete(info:UVPUploadFileInfo){
	if (props.multiple) {
		fileList.value.splice(info.index, 1);
		const urls = modelValue.value as string[];
		urls.splice(info.index, 1);
		modelValue.value = urls;
	} else {
		fileList.value = [];
		modelValue.value = undefined;
	}
}

watch(modelValue,(newValue)=>{
	if(committing)return;
	if(newValue) {
		if (props.multiple) {
			// 多文件模式：newValue 是 string[]，但 fileList 需要 UVPUploadFile[]
			// 这里只更新 fileList 的长度以显示已上传的文件数
			if (Array.isArray(newValue)) {
				fileList.value = newValue.map((url, index) => ({
					url,
					name: `file-${index}`,
					size: 0,
					thumb: url,
					type: 'image',
					file: new File([], `file-${index}`)
				} as UVPUploadFile));
			}
		} else {
			if (newValue && typeof newValue === 'string') {
				fileList.value = [{
					url: newValue,
					name: 'file',
					size: 0,
					thumb: newValue,
					type: 'image',
					file: new File([], 'file')
				} as UVPUploadFile]
			}
		}
	}
	else{
		fileList.value = [];
	}
})

defineSlots<{
	default():any,
	trigger():any,
	clear():any,
}>()

const paddingBottom = computed(()=>{
	if(props.ratio){
		return (props.ratio*100).toFixed(2)+'%'
	}
	else{
		return undefined;
	}
})

</script>

<template>
	<view class="comp-url-upload" :class="{'has-preview':preview, 'is-multiple': multiple}">
		<u-upload
			v-model:fileList="fileList"
			v-bind="$attrs"
			:afterRead="afterRead" @delete="handleDelete"
			:maxCount="maxCount"
			:maxSize="maxSize"
			:multiple="multiple"
			:preview-image="preview"
			:disabled="uploading"
			width="100%"
			height="100%"
		>
			<template #default v-if="$slots.default || $slots.$default">
				<slot />
			</template>
		</u-upload>
		<template v-if="!preview && ( (Array.isArray( modelValue) && modelValue.length) || (!Array.isArray( modelValue) && modelValue))">
			<slot name="clear" />
		</template>
	</view>
</template>

<style scoped lang="scss">
.comp-url-upload{
	position: relative;
	&.has-preview {
		:deep(.u-upload__wrap) {
			height: 100%;
			flex: none;

			> view {
				position: relative;
				width: 100%;
				padding-bottom: v-bind(paddingBottom);
			}
			.u-upload__wrap__preview__image,.upload-indicator{
				position: absolute;
				left: 0;
				top: 0;
			}
		}
	}
	
	&:not(.is-multiple){
		&.has-preview {
			padding-bottom: v-bind(paddingBottom);
		
			.u-upload {
				position: absolute;
				left: 0;
				top: 0;
				width: 100%;
				height: 100%;
			}
		}
	}
}
</style>

