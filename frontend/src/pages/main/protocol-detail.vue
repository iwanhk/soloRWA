<script setup lang="ts">
import {onLoad} from "@dcloudio/uni-app";
import {ref} from 'vue';
import type {ProtocolEntity} from '@/types/entity';
import {ApiGetAgreementDetail} from '@/api/user/ApiGetAgreementDetail';
import {ShowInfo} from "@/library/ShowInfo.ts";
import { useI18n } from 'vue-i18n';
import { getProtocolTitle } from '@/library/protocol-i18n';
import {useConfigStore} from "@/store/config.ts";

const { t } = useI18n();
const configStore = useConfigStore();
const fontScale = computed(()=>configStore.fontScale);

const protocolId = ref<number | null>(null);
const loading = ref(false);

const protocolData = ref<ProtocolEntity>();

onLoad((query) => {
	if (query && query.id) {
		protocolId.value = parseInt(query.id as string);
		loadProtocolDetail();
	}
});

async function loadProtocolDetail() {
	if (!protocolId.value) return;

	try {
		loading.value = true;
		const api = new ApiGetAgreementDetail(protocolId.value);
		const response = await api.call();
		protocolData.value = response.data;
		const title = getProtocolTitle(response.data.agreementTitle, response.data.agreementKey);
		uni.setNavigationBarTitle({ title });
	} catch (error) {
		console.error('加载协议详情失败:', error);
		ShowInfo.toastError(error, t('pages.protocol.detail.loadError'));
	} finally {
		loading.value = false;
	}
}
</script>

<template>
	<view class="page page-protocol-detail">
		<view class="content-wrapper">
			<view v-if="loading" class="loading">{{ t('pages.protocol.detail.loading') }}</view>
			<view v-else-if="protocolData" class="protocol-content">
				<rich-text :nodes="protocolData.agreementContent"></rich-text>
			</view>
		</view>
	</view>
</template>

<style scoped lang="scss">
.page{
	--font-scale:v-bind(fontScale);
}
.page-protocol-detail {
	background: $bg-color;
	display: flex;
	flex-direction: column;
}

.loading {
	@include flex-center;
	height: 200px;
	@include fs(12);
	color: $color-text-gray;
}

.protocol-content {
	@include fs(10);
	color: #d9d9d9;
	line-height: 1.8;
	text-align: justify;
	@include en-break;
}
</style>

