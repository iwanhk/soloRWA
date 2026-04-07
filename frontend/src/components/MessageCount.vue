<script setup lang="ts">

import IconFont from "@/components/icon/IconFont.vue";
import {IconFontType} from "@/types/icons";
import {$themeColor} from "@/library/GlobalVars";
import UniRouter from "@/library/UniRouter";
import {useUserStore} from "@/store/user";

const userStore = useUserStore();
const displayCount = computed(()=>{
	if(userStore.unreadCount < 100)return '+'+userStore.unreadCount;
	else return '+99';
})
function goMessageList(){
	UniRouter.to("/pages/message/index");
}

</script>

<template>
	<view class="message-count" v-if="userStore.user" @click="goMessageList">
		<view class="count" v-if="userStore.unreadCount">{{displayCount}}</view>
		<IconFont :type="IconFontType.MESSAGE" size="18" :color="$themeColor" />
	</view>
</template>

<style scoped lang="scss">
.message-count{
	@include fs(10);
	position: relative;
	padding-right: 1em;
	padding-top: 0.5em;
	.count{
		position: absolute;
		right: 0;
		top: 0;
		@include fs(8, 10);
		background: #fff;
		border-radius: 999em;
		color: $theme-color;
		width: 2.5em;
		text-align: center;
	}
}
</style>