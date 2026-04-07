<script setup lang="ts">
import UniRouter from '@/library/UniRouter.ts';
import {$themeColor} from '@/library/GlobalVars.ts';
import {IconFontType} from '@/types/icons.ts';
import IconFont from '@/components/icon/IconFont.vue';
import type {SimpleProtocolEntity} from '@/types/entity';
import {ApiGetAgreementList} from '@/api/user/ApiGetAgreementList';
import {ShowInfo} from "@/library/ShowInfo.ts";
import { useI18n } from 'vue-i18n';
import { getProtocolTitle } from '@/library/protocol-i18n';
import {useConfigStore} from "@/store/config.ts";

const { t } = useI18n();
const configStore = useConfigStore();
const fontScale = computed(()=>configStore.fontScale);

const protocols = ref<SimpleProtocolEntity[]>([]);

const loading = ref(false);

onMounted(() => {
	loadProtocols();
});

function handleProtocolClick(protocol: SimpleProtocolEntity) {
	UniRouter.to(`/pages/main/protocol-detail?id=${protocol.id}`);
}

async function loadProtocols() {
	try {
		loading.value = true;
		const api = new ApiGetAgreementList();
		const response = await api.call();
		protocols.value = response.data.filter(item=>item.agreementKey.startsWith(configStore.language+"_"));
	} catch (error) {
		console.error('加载协议列表失败:', error);
		ShowInfo.toastError(error, t('pages.protocol.list.loadError'));
	} finally {
		loading.value = false;
	}
}
</script>

<template>
	<view class="page page-protocol-list">
		<view class="protocol-list">
			<view
				class="menu-list-item"
				v-for="protocol of protocols"
				:key="protocol.id"
				@click="handleProtocolClick(protocol)"
			>
				<view class="menu-list-item-label">{{ getProtocolTitle(protocol.agreementTitle, protocol.agreementKey) }}</view>
				<IconFont class="menu-list-item-arrow" :color="$themeColor" :type="IconFontType.CARET_RIGHT" :size="8" />
			</view>
		</view>
	</view>
</template>

<style scoped lang="scss">
.page{
	--font-scale:v-bind(fontScale);
}
.page-protocol-list {
	padding: $page-padding;
	background: $bg-color;
}

.protocol-list {
	display: flex;
	flex-direction: column;
	gap: 0;
}
</style>

