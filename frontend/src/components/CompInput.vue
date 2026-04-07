<script setup lang="ts">
const modelValue = defineModel();
const props=withDefaults(defineProps<{
	theme?:'dark'|'light',
	border?: 'none'|'bottom'|'surround',
}>(),{
	theme:'light',
	border: 'none',
})

const padding = computed(()=>{
	if(props.border === 'none'){
		return '4px 9px';
	}
	else{
		return '0';
	}
})

</script>

<template>
	<view class="comp-input" :class="`theme-${theme}`">
		<up-input v-model="modelValue" v-bind="$attrs" :border="border">
			<template v-if="$slots.prefix" #prefix>
				<slot name="prefix" />
			</template>

			<template v-if="$slots.suffix" #suffix>
				<slot name="suffix" />
			</template>
		</up-input>
	</view>
</template>

<style scoped lang="scss">
.comp-input{
	padding: v-bind(padding);
	&.theme-light{
		color: $color-text-black;
	}
	&.theme-dark{
		color: $color-text-white;
	}
}
</style>