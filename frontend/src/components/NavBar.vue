<script setup lang="ts">
import {useSlots} from "vue";
import UniRouter from "@/library/UniRouter";
import {onLoad, onUnload} from "@dcloudio/uni-app";
import ENV from "@/library/env";
import {UniPlatformType} from "@/library/getPlatform";
import {mainPage} from "@/library/GlobalVars.ts";
import {translatePageTitle} from "@/i18n/PageTitleI18n.ts";
import {useConfigStore} from "@/store/config.ts";
//import i18n from "@/i18n";
//import {translatePageTitle} from "@/library/PageTitleI18n";

const props = withDefaults(defineProps<{
	back?:boolean,
	transparent?:boolean,
	onBackPress?: ()=>boolean|undefined|void,
	backColor?:string,
	fullscreen?: boolean,
	title?: string,
	backgroundColor?:string,
	sideWidth?: string,
}>(),{
	back:false,
	transparent:false,
	backColor:'inherit',
	fullscreen: false,
	backgroundColor:'#000000',
	sideWidth: '60px'
})

const configStore = useConfigStore();
const fontScale= computed(()=>configStore.fontScale);
const goBack = ()=>{
	if(props.onBackPress && props.onBackPress() === true){
		return;
	}
	navigateBack();
}

function navigateBack(){
	if(getCurrentPages().length > 1) {
		uni.navigateBack();
	}
	else{
		UniRouter.to(mainPage);
	}
}

const slots = useSlots();
const emits = defineEmits<{
	(e:'left-click'):void
	(e:'right-click'):void
}>()

const hideStatusBar = ref(false);

const pageTitle = computed<string>(() => {
	if(props.title !== undefined){
		return props.title;
	}
	try {
		// @ts-ignore UniApp pageContent
		const originalTitle = getCurrentPages().slice().pop().$page.meta.navigationBar.titleText;
		// 处理国际化标题（%开头和结尾的情况）
		// 通过访问i18n.global.locale.value来确保computed能够响应语言变化
		return translatePageTitle(originalTitle);
	} catch (e) {
		return "";
	}
});

const mpTopPadding = ref(0);
const mpTopHeight = ref<undefined|number>(undefined);

if(ENV.platform === UniPlatformType.MP_WEIXIN){
	const menuButtonInfo = uni.getMenuButtonBoundingClientRect();
	mpTopPadding.value = menuButtonInfo.height + menuButtonInfo.top;
	mpTopHeight.value = mpTopPadding.value + 44;
}

const screenSizeChangeHandler = (result: UniNamespace.WindowResizeResult) => {
	if(ENV.appPlatform === "ios"){
		hideStatusBar.value = result.deviceOrientation === "landscape";
	}
}

onLoad(()=>{
	// #ifdef APP-PLUS
	uni.onWindowResize(screenSizeChangeHandler)
	// #endif
})
onUnload(()=>{
	// #ifdef APP-PLUS
	uni.offWindowResize(screenSizeChangeHandler);
	// #endif
})

</script>

<template>
	<view class="nav-bar" :class="{white:!props.transparent, fullscreen: props.fullscreen}">
		<view class="status-bar" :style="{'padding-top':`${mpTopPadding}px`}" v-show="!hideStatusBar">
			<view class="top-view" :style="{'height':`${mpTopHeight?mpTopHeight+'px':undefined}`}"></view>
		</view>
		<uni-nav-bar :left-width="sideWidth" :right-width="sideWidth" fixed :border="false" :background-color="props.transparent?'transparent':backgroundColor">
			<template #default>
				<view class="nav-title">
					<slot name="default" v-if="slots.default"></slot>
					<text v-else>{{ pageTitle }}</text>
				</view>
			</template>
			
			<template #left v-if="slots.left || props.back">
				<view @click="emits('left-click')">
					<slot name="left">
						<uni-icons class="back-icon" @click="goBack" type="left" size="24" v-if="props.back" :color="props.backColor"></uni-icons>
					</slot>
				</view>
			</template>
			<template #right v-if="slots.right">
				<view @click="emits('right-click')">
					<slot name="right"></slot>
				</view>
			</template>
		</uni-nav-bar>
	</view>
</template>

<style scoped lang="scss">
.nav-bar {
	--font-scale: v-bind(fontScale);
	
	&:before{
		content: "";
		position: fixed;
		left: 0;
		right: 0;
		top: 0;
		height: 44px;
		padding-top: var(--status-bar-height);
		z-index: 97;
	}
	&.white{
		background-color: v-bind(backgroundColor);

		.status-bar {
			.top-view {
				background-color: v-bind(backgroundColor);
			}
		}
	}
	z-index: 98;
	.status-bar {
		width: 100%;
		//#ifndef MP-WEIXIN
		height: var(--status-bar-height);
		//#endif

		.top-view {
			position: fixed;
			left: 0;
			top: 0;
			right: 0;
			height: calc(var(--status-bar-height) + 44px);
			z-index: 97;
			overflow: hidden;

			&:after {
				content: '';
				position: absolute;
				left: 0;
				top: 0;
				right: 0;
				height: 100vh;
			}
		}
	}

	:deep(.uni-navbar__content){
		padding-left: env(safe-area-inset-left);
		padding-right: env(safe-area-inset-right);
	}

	&.fullscreen{
		.status-bar{
			display: none;
		}
	}

	:deep(.uni-navbar__header) {
		padding: 0 $page-padding;
	}

	.nav-title {
		width: 100%;
		@include flex-center;
		color: $color-text-white;
		@include fs(16);
		font-size: 16px;//fixed!!
		font-weight: bold;
	}

	.back-icon{
		position: relative;
		//#ifdef MP-WEIXIN
		//left: calc(-1 * ($side-gutter - 17px));
		//#endif
		//#ifndef MP-WEIXIN
		//left: calc( -1 * ( $side-gutter - 7px ) );
		//top: -2px;
		//#endif
	}
}
</style>
