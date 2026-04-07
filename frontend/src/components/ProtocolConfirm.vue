<script setup lang="ts">
import {useI18n} from 'vue-i18n';
import {$themeColor} from "@/library/GlobalVars.ts";
import {useDictStore} from "@/store/dict.ts";
import {ProtocolKey} from "@/types/enums.ts";
import type {SimpleProtocolEntity} from "@/types/entity.ts";
import ProtocolPopup from "@/components/popup/ProtocolPopup.vue";
import { getProtocolTitle } from '@/library/protocol-i18n';
import {useConfigStore} from "@/store/config.ts";

const {t} = useI18n();

const props = defineProps<{
	withRegister?: boolean
	withBuy?: boolean
}>()

const dictStore = useDictStore();
const configStore = useConfigStore();
const modelValue = defineModel<boolean>()
const protocolPopup = ref<{open(proto: SimpleProtocolEntity): void}>();

function handleCheckboxChange(selected:boolean){
	modelValue.value = selected
}

const protocolOptions = computed(()=>{
	const selectedProtocolKeys: string[] = [
		configStore.language+"_"+ProtocolKey.SERVICE,
		configStore.language+"_"+ProtocolKey.PRIVACY
	];
	if (props.withRegister) {
		selectedProtocolKeys.unshift(configStore.language+"_"+ProtocolKey.REGISTER);
	}
	if (props.withBuy) {
		selectedProtocolKeys.push(configStore.language+"_"+ProtocolKey.BUY);
	}

	return dictStore.protocols?.filter(
		protocol=>selectedProtocolKeys.includes(protocol.agreementKey)
	) ?? [];
})

function readProtocol(protocol: SimpleProtocolEntity){
	protocolPopup.value?.open(protocol);
}
</script>

<template>
	<view class="protocol-confirm">
		<u-checkbox
			:size="12 * configStore.fontScale"
			:icon-size="10 * configStore.fontScale"
			:active-color="$themeColor"
			:checked="modelValue"
			@update:checked="handleCheckboxChange"
			shape="circle"
			used-alone
		>
			<template #label>
				{{ t('pages.project.components.protocolConfirm.agreeText') }}
				<text class="protocol-list">
					<text v-for="protocol of protocolOptions" class="protocol-link" @click.stop="readProtocol(protocol)">《{{ getProtocolTitle(protocol.agreementTitle, protocol.agreementKey) }}》</text>
				</text>
			</template>
		</u-checkbox>
		<ProtocolPopup ref="protocolPopup" />
	</view>
</template>

<style scoped lang="scss">
.u-checkbox{
	:deep(.u-checkbox__icon-wrap){
		margin-top: 3px;
		flex: none;
	}
}
.protocol-confirm{
	margin-top: 5px;
	@include flex-center;
	@include fs(12);
	.protocol-link{
		display: inline-block;
		color: $theme-color;
	}
}

</style>

