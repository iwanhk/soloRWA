<template>
	<view :class="{[`switcher-display-${display}`]:true, split}" class="switcher">
		<view v-for="option of options"
		      :key="option.value"
		      :class="{active:modelValue === option.value}"
		      class="switcher-item"
		      @click="switchTab(option)">
			<text class="text">{{ option.label }}</text>
		</view>
	</view>
</template>

<script lang="ts" setup generic="T extends string|number|undefined">
import {ShowInfo} from "@/library/ShowInfo";
import type {LabelOption, LabelOptions} from "@/types/common";

const props = withDefaults(defineProps<{
	options: LabelOptions<T, { disabled?: boolean | string }>,
	display?: 'button' | 'tab'
	split?: boolean,
}>(), {
	display: 'tab',
	split: false,
})

const modelValue = defineModel<T>()

const switchTab = function (option: LabelOption<T, { disabled?: boolean | string }>) {
	if (option.disabled) {
		if (typeof option.disabled === "boolean") {
		} else {
			ShowInfo.toast(option.disabled);
		}
		return;
	}
	modelValue.value = option.value
}
</script>

<style lang="scss" scoped>
.switcher {
	&.switcher-display-button {
		height: 30px;
		@include flex-row(center, space-around);
		color: $color-text-white;
		@include fw(bold);
		padding: 2px;

		.switcher-item {
			flex: 1;
			height: 30px;
			@include flex-center;

			&.active {
				.text {
					color: $theme-color;
				}
			}

			+ .switcher-item {
				margin-left: 0.5em;
			}
		}

		&.split {
			.switcher-item {
				border-radius: 999em;
			}
		}
	}

	&.switcher-display-tab {
		display: flex;
		align-items: center;
		flex-direction: row;

		overflow-x: auto;

		.switcher-item {
			flex-shrink: 0;
			position: relative;

			&:not(:last-child) {
				margin-right: 1em;
			}

			&.active {
				&::after {
					content: "";
					width: 100%;
					height: 2px;
					display: block;
					position: absolute;
					bottom: 0;
					left: 0;
					background-color: $theme-color;
				}
			}
		}
	}
}
</style>
