<template>
	<view :class="['app-root']">
		<slot />
	</view>
</template>

<script setup lang="ts">
import {onHide, onLaunch, onShow} from "@dcloudio/uni-app";
import processor from "@/library/RouterGuard";
import RouterGuard from "@/library/RouterGuard";
import {NwEvent} from "@/types/api";
import {useUserStore} from "@/store/user";

const userStore = useUserStore();

uni.$on(NwEvent.TOKEN_EXPIRED, function (){
	userStore.logout();
	RouterGuard.redirectLogin();
})

onLaunch((e) => {
	if(e) {
		processor.launched(e)
	}
});
onShow(() => {
  //console.log("App Show");
});
onHide(() => {
  //console.log("App Hide");
});
</script>
<style></style>
