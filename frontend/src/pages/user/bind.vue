<script lang="ts" setup>
import {useUserStore} from "@/store/user";
import {useI18n} from "vue-i18n";
import {useConfigStore} from "@/store/config.ts";
import NavBar from "@/components/NavBar.vue";
import {InfoMask} from "@/library/InfoMask.ts";

const { t } = useI18n()

const userStore = useUserStore();
const configStore = useConfigStore();
const fontScale = computed(()=>configStore.fontScale);
</script>

<template>
	<view class="page page-bind">
		<view class="menu-list-item" v-if="userStore.user?.mobile">
			<view class="menu-list-item-label">{{ t('pages.user.bind.mobile') }}</view>
			<view class="menu-list-item-value">{{ InfoMask.phone(userStore.user.mobile) }}</view>
		</view>

		<view class="menu-list-item" v-if="userStore.user?.email">
			<view class="menu-list-item-label">{{ t('pages.user.bind.email') }}</view>
			<view class="menu-list-item-value">{{ InfoMask.email(userStore.user.email) }}</view>
		</view>
	</view>
</template>

<style scoped lang="scss">
.page {
	--font-scale: v-bind(fontScale);
}
</style>
