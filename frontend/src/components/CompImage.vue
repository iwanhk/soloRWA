<script setup lang="ts">

import {formatImageUrl} from "@/library/Utility";

import defaultImage from '@/static/images/default.png';
import {UniPlatformType} from "@/library/getPlatform";
import ENV from "@/library/env";

const props = withDefaults(
	defineProps<{
		src?:string | null,
		alt?:string,
		uploaded?:boolean,
		autoSize?:boolean,
		defaultSrc?: string,
		mode?: 'scaleToFill'|'aspectFit' | 'aspectFill' | 'widthFix' | 'heightFix' | 'top' | 'bottom' | 'center' | 'left' | 'right' | 'top left' | 'top right' | 'bottom left' | 'bottom right',
	}>(),
	{
		mode: 'widthFix',
		uploaded: false,
		autoSize: false,
	}
)

const defaultSrc = props.defaultSrc?props.defaultSrc: defaultImage;

const imageError = ref(false);
const src = computed<string>(()=>{
	if (imageError.value) return defaultSrc;
	if (!props.src) return defaultSrc;
	if(props.uploaded){
		return formatImageUrl(props.src);
	}
	return props.src;
})

const computedStyle = reactive<{width?:string, height?:string, maxWidth?:string, maxHeight?:string}>({
	width: undefined,
	height: undefined,
});
const imageLoaded = (e:Event)=>{
	//@ts-ignore EventContent
	let detail: { height: number, width: number } = e.detail;
	if(props.autoSize){
		computedStyle.width = detail.width+'px';
		computedStyle.height = detail.height+'px';
	}
  else{
    if(ENV.platform === UniPlatformType.MP_WEIXIN){
      computedStyle.maxWidth = '100%';
      computedStyle.maxHeight = '100%';
    }
  }
}

defineExpose({
	src,
	error: imageError,
})
</script>

<template>
	<image :style="computedStyle" class="comp-image" :alt="props.alt" :src="src" :mode="props.mode" @error="imageError=true" @load="imageLoaded" />
</template>
<style lang="scss">
.comp-image{
	max-width: 100%;
	vertical-align: middle;
}
//#ifdef MP-WEIXIN
.comp-image {
	display: inline-block;
}
//#ifdef
</style>