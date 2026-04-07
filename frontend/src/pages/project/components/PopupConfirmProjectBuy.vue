<script setup lang="ts">
import {ref} from 'vue';
import {useI18n} from 'vue-i18n';
import type {ProjectEntity} from "@/types/entity";
import type {PopupInstance} from "@/types/popup";
import CompPopup from "@/components/popup/CompPopup.vue";
import ProtocolConfirm from "../../../components/ProtocolConfirm.vue";
import {defaultPopupExpose} from "@/library/PopupManager";
import {ShowInfo} from "@/library/ShowInfo";
import {useConfigStore} from "@/store/config.ts";

const {t} = useI18n();

const configStore = useConfigStore();

const popup = ref<PopupInstance>();
defineExpose(defaultPopupExpose(popup, {
	open(proj: ProjectEntity, qty: number, chainId: number){
		project.value = proj;
		quantity.value = qty;
		selectedChainAddressId.value = chainId;
		popup.value?.open();
	}
}));
const project = ref<ProjectEntity>();
const quantity = ref(0);
const selectedChainAddressId = ref<number>();

const handleConfirm = () => {
	configStore.protocolAgree = agree.value;
	if(!agree.value){
		return ShowInfo.toast(t('pages.project.components.popupConfirmProjectBuy.agreeRequired'));
	}

	popup.value?.result(true);
};

const agree = ref(configStore.protocolAgree);
</script>

<template>
	<CompPopup ref="popup" type="bottom" :is-mask-click="false">
		<ProtocolConfirm v-model="agree" :with-buy="true" />
		<view class="button-container">
			<up-button @click="handleConfirm" type="warning" shape="circle">{{ t('pages.project.components.popupConfirmProjectBuy.confirmBuy') }}</up-button>
		</view>
	</CompPopup>
</template>

<style scoped lang="scss">

.button-container {
	width: 100%;
	margin-top: 12px;
}

.protocol-confirm{
	margin-bottom: 10px;
}
</style>

