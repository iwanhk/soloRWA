<script setup lang="ts" xmlns="">
import {useSlots} from "vue";
import IconFont from "@/components/icon/IconFont.vue";
import {IconFontType} from "@/types/icons";
import type {PopupInstance} from "@/types/popup";
import {WrapPromise} from "@/library/WrapPromise";
import {isTabbarPage} from "@/library/GlobalVars";
import {useConfigStore} from "@/store/config.ts";

const configStore = useConfigStore();
const fontScale = computed(()=>configStore.fontScale);

const props = withDefaults(defineProps<{
	position?: 'bottom'|'center',
	theme?: 'common' | string | string[],
	title?: string,
	closable?: boolean,
	hideClose ?: boolean,
}>(),{
	position: "bottom",
	theme: "common",
	closable: true,
	hideClose: false,
})
const slots = useSlots();

const emits = defineEmits<{
	(e:'show'):void,
	(e:'hide',v:any):void,
	(e:'scroll', v:any):void,
}>()

const popup = ref<PopupInstance>();
const handlePopupStatusChanged = ({show}:{show:boolean})=>{
	if(show) isAnimating.value = true;
	if(!show){
		if(value instanceof Error){
			resultPromise?.reject(value);
		}
		else{
			resultPromise?.resolve(value);
		}
		value = undefined;
		resultPromise = undefined;
	}
}

const isAnimating = ref(false);
const handleAnimationEnded = ({detail}: { detail:true })=>{
	isAnimating.value = false;
}

let value:any = undefined;
let resultPromise: WrapPromise | undefined

const open = () => {
	if(resultPromise) return resultPromise;
	popup.value?.open();
	return (resultPromise = new WrapPromise())
}
const close = ()=>{
	isAnimating.value = true;
	popup.value?.close();
}

const result = (newValue:any, autoClose:boolean = true) => {
	value = newValue;
	if(autoClose){
		close()
	}
}

defineExpose<{
	open():void,
	close():void,
	result(value:any, auto?:boolean):void,
}>({
	open,
	close,
	result,
});

</script>

<template>
	<uni-popup :is-mask-click="closable" @maskClick="isAnimating = true" @animationEnd="handleAnimationEnded" class="uni-popup-z-index" :class="{'non-animating':!isAnimating, 'has-tabbar':isTabbarPage}" ref="popup" :type="props.position" @change="handlePopupStatusChanged">
		<view class="popup-container" :class="[...(Array.isArray(theme)?theme: [theme]).map(theme=>`nw-popup-${theme}`),`popup-${props.position}`]">
			<view class="nw-popup-title" v-if="title || (closable && !hideClose)">
				<template v-if="title">
					<slot name="header">
						<slot name="title-left"></slot>
						<view class="title"><slot name="title">{{ title }}</slot></view>
						<view class="nw-popup-close" v-if="closable && !hideClose" @click="close">
							<IconFont :size="16 * configStore.fontScale" :type="IconFontType.CLOSE"/>
						</view>
					</slot>
				</template>
				<template v-else>
					<slot name="header"></slot>
					<view class="nw-popup-close not-title-close" v-if="closable && !hideClose" @click="close">
						<IconFont :size="16 * configStore.fontScale" :type="IconFontType.CLOSE"/>
					</view>
				</template>
			</view>

			<div v-if="slots.default" class="nw-popup-content">
				<scroll-view scroll-y  @scroll="emits('scroll', $event)">
					<slot name="default"></slot>
				</scroll-view>
			</div>
			<view v-if="slots.actions" class="nw-popup-actions group-actions">
				<slot name="actions"></slot>
			</view>
		</view>
	</uni-popup>
</template>

<style scoped lang="scss">
.uni-popup{
	--font-scale: v-bind(fontScale);
}

.popup-container {
	/* #ifndef APP-NVUE */
	max-height: calc(100vh - 50px);
	overflow: auto;
	overscroll-behavior: contain;
	/* #endif */
	padding-bottom: 10px;
	@include flex-column();
}
.nw-popup-title{
	flex: none;
}
.nw-popup-content{
	flex: 1;
	scroll-view{
		height: 100%;
	}
}
.nw-popup-actions{
	flex: none;
}

.popup-bottom {
	border-radius: 10px 10px 0 0;
}
.popup-center {
	border-radius: 10px
}
.nw-popup-common {
	background-color: #fff;
	z-index: 9999 !important;

	.nw-popup-close {
		position: absolute;
		right: 0.5em;
		top: 0;
		bottom: 0;
		/* #ifndef APP-NVUE */
		margin: auto;
		/* #endif */
		width: 24px;
		height: 24px;
		background: #fff;
		border-radius: 50%;
		display: flex;
		justify-content: center;
		align-items: center;
		/* #ifndef APP-NVUE */
		box-sizing: border-box;
		/* #endif */
		.close-icon {
			width: 15px;
			height: 15px
		}
	}
	.not-title-close {
		right: 0.7em;
		top: 1em;
	}

	.nw-popup-title {
		z-index: 5;
		padding-top: 20px;
		padding-bottom: 20px;
		margin-bottom: -10px;
		margin-left: 10px;
		margin-right: 10px;
		/* #ifndef APP-NVUE */
		min-height: 10px;
		/* #endif */
		background: #fff;
		position: sticky;
		top: 0;
		text-align: center;
		@include fs(16);
		font-weight: 600;
		color: $color-text-black;
		@include flex-center;
	}
	.nw-popup-content{
		margin: 10px;
		width: calc(100% - 20px);
		color: $color-text-black;
	}

	.nw-popup-actions{
		margin-top: -10px;
		margin-bottom: -10px;
		position: sticky;
		bottom: -10px;
		background: #fff;
		z-index: 1;
		display: flex;
		align-items: center;
		gap: 10px;
		flex-wrap: wrap;
		padding: 10px $side-gutter 25px;
		justify-content: space-around;
		:deep(.u-button){
			flex: 1;
			width: auto;
		}
	}
}
.popup-padding {
	padding: 28px;
}
:deep(.uni-popup .bottom) {
	z-index:9999 !important;
}
</style>
