<script setup lang="ts">
import UniRouter from "@/library/UniRouter";
import {useI18n} from "vue-i18n";
import {useConfigStore} from "@/store/config.ts";
const { t } = useI18n()
const configStore = useConfigStore();
const fontScale = computed(()=>configStore.fontScale);
function handleChangePassword() {
	UniRouter.to('/pages/user/login-password');
}
</script>

<template>
	<view class="page page-password">
		<view class="menu-list-item">
			<view class="menu-list-item-label">{{ t('pages.user.password.loginPassword') }}</view>
			<up-button type="primary" size="small" @click="handleChangePassword">{{ t('pages.user.auth.edit') }}</up-button>
		</view>
	</view>
</template>

<style scoped lang="scss">
.page{
	--font-scale:v-bind(fontScale);
}
</style>

