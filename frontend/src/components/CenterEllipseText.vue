<template>
	<view class="center-ellipse-text" :class="{center}" :title="text">
		<view class="center-ellipse-text-wrap">
			<!-- #ifndef MP-WEIXIN -->
			<text class="origin">{{ text }}</text>
			<text class="ghost">
				<text class="narrow" :data-otitle="text" :data-vtitle="reverseText"></text>
				<text class="wide" :data-otitle="text"></text>
			</text>
			<!-- #endif -->
			<!-- #ifdef MP-WEIXIN -->
			<view class="origin">{{text}}</view>
			<view class="ghost">
				<view class="narrow" :data-otitle="text" :data-vtitle="reverseText"></view>
				<view class="wide" :data-otitle="text"></view>
			</view>
			<!-- #endif -->
		</view>
	</view>
</template>

<script lang="ts" setup>
const props = defineProps<{
	text:string | null | undefined,
	center?:boolean,
}>();

const reverseText = computed(()=>{
	let text = props.text
	if(text){
		text = text.split('').reverse().join('');
	}
	return text;
})
</script>

<style scoped lang="scss">
.center-ellipse-text{
	position: relative;
	width: 96%;
	//#ifdef MP-WEIXIN
	height: 1.3em;
	//#endif
	overflow: hidden;
	&:before{
		content: attr(title);
		display: block;
		visibility: hidden;
		white-space: nowrap;
		text-overflow: ellipsis;
		width: 1em;
	}
	
	&.center{
		.center-ellipse-text-wrap{
			.wide{
				justify-content: center;
			}
		}
	}
}
.center-ellipse-text-wrap{
	position: absolute;
	left: 0;
	right: 0;
	top: 0;
	height: 100%;

	:deep(> span) {
		display: block;
		height: 100%;
	}

	.origin {
		width: 100%;
		display: block;
		max-height: 200%;
		overflow: hidden;
		visibility: hidden;
	}

	//#ifdef MP-WEIXIN
	.ghost{
		height: 100%;
	}
	//#endif

	.wide{
		@include flex();
		transform: translateY(-200%);
		height: 100%;
		overflow: hidden;
		&::before {
			content: attr(data-otitle);
		}
	}
	.narrow {
		@include flex();
		transform: translateY(-200%);
		height: 100%;
		overflow: hidden;

		&::before, &::after {
			content: attr(data-otitle);
			overflow: hidden;
			height: 200%;
		}

		&::before {
			text-align: right;
			width:calc(50% - 0.5em);
		}

		&::after {
			content: attr(data-vtitle);
			width:calc(50% + 0.5em);
			white-space: nowrap;
			direction: rtl;
			text-overflow: ellipsis;
			unicode-bidi: bidi-override;
		}
	}
}
</style>
<style lang="scss">
// #ifdef MP-WEIXIN
center-ellipse-text {
	width: 100%;
}
// #endif
</style>
