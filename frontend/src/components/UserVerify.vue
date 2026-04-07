<script setup lang="ts">

import CompImage from "@/components/CompImage.vue";
import ENV from "@/library/env";
import UniRouter from "@/library/UniRouter";
import {useUserStore} from "@/store/user";
import {AuditStatus} from "@/types/enums";
import {useI18n} from "vue-i18n";

function goVerify(){
	UniRouter.to('/pages/user/verify');
}

const userStore = useUserStore()
const { t } = useI18n()

const isAuditPending = computed(() => userStore.user?.auditStatus === AuditStatus.PENDING_AUDIT);

</script>

<template>
	<view class="user-verify" v-if="userStore.user && userStore.user.auditStatus !== AuditStatus.AUDIT_PASSED">
		<view class="icon">
			<CompImage src="/static/images/user/verify-icon.png" />
		</view>
		<view class="title">
				<view class="main-title">{{ ENV.foreign ? t('pages.project.index.welcomeTitleRWA') : t('pages.project.index.welcomeTitle') }}</view>
			<view class="sub-title">{{ t('components.userVerify.subtitle', {rwa:ENV.foreign?'RWA':''}) }}</view>
		</view>
		<view class="action">
			<up-button type="primary" shape="circle" size="small" @click="goVerify">
				<template v-if="isAuditPending">
					{{ t('components.userVerify.auditPending') }}
				</template>
				<template v-else>{{ t('components.userVerify.verify') }}</template>
			</up-button>
		</view>
	</view>
</template>

<style scoped lang="scss">
.user-verify{
	.icon{
		text-align: center;
		.comp-image{
			width: 146px;
		}
	}
	.title{
		text-align: center;
		.main-title{
			color: $theme-color;
			@include fs(18);
		}
		.sub-title{
			@include en-break;
			margin-top: 0.5em;
			@include fs(12);
			color: $color-text-white;
		}
	}
	.action{
		padding: 20px;
		.u-button{
			height: 2em;
			min-width: 178px;
			@include fs(16);
		}
	}
}
</style>