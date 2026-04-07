<script setup lang="ts">
import type {UVPUploadFile, UVPUploadFileInfo} from "@/types/uview-plus";
import {ApiUploadFile} from "@/api/file";
import {UploadLimit} from "@/library/limits.ts";

const props = withDefaults(defineProps<{
	ratio?: number,
	maxCount?: number,
	maxSize?: number,
	multiple?: boolean,
	preview?: boolean
}>(), {
	maxCount: 1,
	maxSize: UploadLimit,
	multiple: false,
	preview: true,
})

const modelValue = defineModel<ApiUploadFile|ApiUploadFile[]>();
const fileList = ref<UVPUploadFile[]>([]);

let committing = false;

function checkMultipleUploadFile(file:UVPUploadFile | UVPUploadFile[], isMultiple: boolean):file is UVPUploadFile[] {
	return isMultiple && Array.isArray(file);
}

function afterRead(file:UVPUploadFile|UVPUploadFile[], info:UVPUploadFileInfo){
	if (checkMultipleUploadFile(file, props.multiple)) {
		const newFileList = fileList.value.slice();
		// 多文件模式
		newFileList.push(...file);
		fileList.value = newFileList;
		committing = true;
		modelValue.value = fileList.value.map(f => new ApiUploadFile(f));
		nextTick(()=>committing = false)
	} else {
		// 单文件模式
		fileList.value = [file];
		committing = true;
		modelValue.value = new ApiUploadFile(file);
		nextTick(()=>committing = false)
	}
}
function handleDelete(info:UVPUploadFileInfo){
	if (props.multiple) {
		fileList.value.splice(info.index, 1);
		modelValue.value = fileList.value.map(f => new ApiUploadFile(f));
	} else {
		fileList.value = [];
		modelValue.value = undefined;
	}
}

watch(modelValue,(newValue)=>{
	if(committing)return;
	if(newValue) {
		if (props.multiple) {
			fileList.value = newValue as ApiUploadFile[];
		} else {
			fileList.value = [newValue as ApiUploadFile]
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
	<view class="comp-upload" :class="{'has-preview':preview, 'is-multiple': multiple}">
		<u-upload
			v-model:fileList="fileList"
			v-bind="$attrs"
			:afterRead="afterRead" @delete="handleDelete"
			:maxCount="maxCount"
			:maxSize="maxSize"
			:multiple="multiple"
			:preview-image="preview"
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
.comp-upload{
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