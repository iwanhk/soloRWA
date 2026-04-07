<script setup lang="ts">
import {IconFontType} from "@/types/icons";

const props = withDefaults(defineProps<{
		type:IconFontType,
		size:number | string,
		color?:string
	}>(),{
		color:'inherit',
	})
</script>

<template>
	<uni-icons class="icon-font" custom-prefix="iconfont" :type="type as string" :color="props.color" :size="size"></uni-icons>
</template>