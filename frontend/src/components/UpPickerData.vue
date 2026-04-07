<template>
	<view class="u-picker-data">
		<view class="u-picker-data__trigger">
			<slot name="trigger" :current="current"></slot>
			<up-input
				v-if="!hasTriggerSlot"
				:modelValue="current"
				disabled
				disabledColor="#ffffff"
				:placeholder="props.title"
				border="none"
			></up-input>
			<view @click="show = true"
			      class="u-picker-data__trigger__cover"></view>
		</view>
		<u-picker
			:show="show"
			:columns="optionsInner"
			:keyName="props.labelKey"
			:defaultIndex="defaultIndex"
			@confirm="confirm"
			@cancel="cancel"
			@close="close">
		</u-picker>
	</view>
</template>

<script setup lang="ts" generic="T extends string|number">
import {computed, getCurrentInstance, ref, useSlots, watch} from 'vue'
//@ts-ignore
import {formValidate} from '@/uni_modules/uview-plus/libs/function/index'

interface OptionItem<T> {
	[key: string]: T
}

const props = withDefaults(defineProps<{
	title?: string
	description?: string
	options?: OptionItem<T>[]
	valueKey?: string
	labelKey?: string
}>(), {
	title: '',
	description: '',
	options: () => [],
	valueKey: 'id',
	labelKey: 'name'
})

const modelValue = defineModel<string | number | undefined>()

const emit = defineEmits<{
	'cancel': []
	'close': []
	'confirm': []
}>()

const slots = useSlots()
const instance = getCurrentInstance()

const show = ref(false)
const current = ref<number|string|undefined>('');

const defaultIndex = ref<number[]>([])

const hasTriggerSlot = computed(() => !!slots.trigger)

const optionsInner = computed(() => [props.options])

function clear() {
	current.value = ''
	defaultIndex.value = []
}

function updateFromModelValue() {
	if (modelValue.value) {
		props.options.forEach((ele, index) => {
			if (ele[props.valueKey] == modelValue.value) {
				current.value = ele[props.labelKey]
				defaultIndex.value = [index]
			}
		})
	} else {
		clear()
	}
}

// 初始化
updateFromModelValue()

// 监听 modelValue 变化
watch(modelValue, () => {
	updateFromModelValue()
})

function hideKeyboard() {
	uni.hideKeyboard()
}

function cancel() {
	show.value = false
	emit('cancel')
}

function close() {
	emit('close')
}

function confirm(e: { columnIndex: number[]; index: number[]; value: OptionItem<T>[] }) {
	const { columnIndex, value } = e
	show.value = false
	modelValue.value = value[0]?.[props.valueKey]
	defaultIndex.value = columnIndex
	current.value = value[0]?.[props.labelKey]
	emit('confirm')
	// 表单验证
	formValidate(instance, 'change')
}
</script>

<style lang="scss" scoped>
.u-picker-data {
	&__trigger {
		position: relative;
		&__cover {
			position: absolute;
			top: 0;
			left: 0;
			right: 0;
			bottom: 0;
			z-index:10;
		}
	}
}
</style>