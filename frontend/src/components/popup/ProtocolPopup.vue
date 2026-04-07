<script setup lang="ts">
import type {SimpleProtocolEntity} from '@/types/entity';
import type {PopupInstance} from '@/types/popup';
import CompPopup from '@/components/popup/CompPopup.vue';
import {defaultPopupExpose} from '@/library/PopupManager';
import {ApiGetAgreementDetail} from '@/api/user/ApiGetAgreementDetail';
import {ShowInfo} from '@/library/ShowInfo';
import {useI18n} from 'vue-i18n';
import { getProtocolTitle } from '@/library/protocol-i18n';

const { t } = useI18n();
const popup = ref<PopupInstance>();

const protocol = ref<SimpleProtocolEntity | null>(null);
const protocolContent = ref<string>('');
const isLoading = ref(false);

async function loadProtocolDetail(proto: SimpleProtocolEntity) {
	try {
		isLoading.value = true;
		const api = new ApiGetAgreementDetail(proto.id);
		const result = await api.call();
		protocolContent.value = result.data.agreementContent || '';
	} catch (error) {
		console.error('加载协议详情失败:', error);
		ShowInfo.toastError(error, t('popup.protocol.loadFailMsg'));
	} finally {
		isLoading.value = false;
	}
}

defineExpose(defaultPopupExpose(popup, {
	async open(proto: SimpleProtocolEntity) {
		protocol.value = proto;
		protocolContent.value = '';
		popup.value?.open();
		await loadProtocolDetail(proto);
	}
}));
</script>

<template>
	<CompPopup
		ref="popup"
		:title="protocol ? getProtocolTitle(protocol.agreementTitle, protocol.agreementKey) : t('popup.protocol.title')"
		closable
	>
		<view class="protocol-content">
			<view v-if="isLoading" class="loading-container">
				<up-loading-icon></up-loading-icon>
				<text class="loading-text">{{ t('popup.protocol.loadingMsg') }}</text>
			</view>
			<rich-text v-else :nodes="protocolContent" :selectable="true"></rich-text>
		</view>
	</CompPopup>
</template>

<style scoped lang="scss">
.protocol-content {
	padding: 10px 0;
	min-height: 200px;
	@include fs(12);
}

.loading-container {
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	padding: 40px 20px;
	gap: 10px;
}

.loading-text {
	@include fs(14);
	color: #999;
}
</style>

