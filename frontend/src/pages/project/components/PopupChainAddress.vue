<script setup lang="ts">
import {useI18n} from 'vue-i18n';
import CompPopup from "@/components/popup/CompPopup.vue";
import {defaultPopupExpose} from "@/library/PopupManager";
import type {PopupInstance} from "@/types/popup";
import type {ChainAddressEntity} from "@/types/entity";
import CenterEllipseText from "@/components/CenterEllipseText.vue";
import {ChainStatus} from "@/types/enums.ts";
import {dictLabel} from "@/library/dict.ts";
import {chainStatusOptions} from "@/types/options.ts";

const {t} = useI18n();

const selectedChainAddressId = ref<number>()
const chainAddressOptions = ref<ChainAddressEntity[]>([])

const dialog = ref<PopupInstance<number>>();
defineExpose(defaultPopupExpose(dialog, {
	open(options: ChainAddressEntity[]) {
		chainAddressOptions.value = options;
		selectedChainAddressId.value = undefined;
	}
}));

function selectChainAddress(addressId: number) {
	if(chainAddressOptions.value.find(c => c.id === addressId)?.chainStatus === ChainStatus.SUCCESS) {
		selectedChainAddressId.value = addressId;
	}
}

function confirmChainAddress() {
	if(selectedChainAddressId.value) {
		dialog.value?.result(selectedChainAddressId.value);
	}
}

</script>

<template>
	<CompPopup ref="dialog" :title="t('pages.project.components.popupChainAddress.title')">
		<view class="chain-address-content">
			<view
				v-for="option in chainAddressOptions"
				:key="option.id"
				class="chain-address-option"
				:class="{ selected: selectedChainAddressId === option.id, disabled: option.chainStatus !== ChainStatus.SUCCESS }"
				@click="selectChainAddress(option.id)"
			>
				<CenterEllipseText :text="option.chainAddress"/>
				<view class="status" v-if="option.chainStatus !== ChainStatus.SUCCESS">{{dictLabel(chainStatusOptions, option.chainStatus)}}</view>
			</view>
		</view>
		<template #actions>
			<up-button
				type="warning"
				shape="circle"
				@click="confirmChainAddress"
				size="normal"
			>
				{{ t('common.button.confirm') }}
			</up-button>
		</template>
	</CompPopup>
</template>

<style scoped lang="scss">
.chain-address-content {
	padding: 20px 0;
	display: flex;
	flex-direction: column;
	gap: 10px;
}

.chain-address-option {
	padding: 4px 8px;
	border: 1px solid $color-text-black;
	text-align: center;
	cursor: pointer;
	transition: all 0.3s ease;
	background-color: $color-text-white;
	color: $color-text-black;
	@include fs(14);

	&.selected {
		background-color: $color-text-black;
		color: $color-text-white;
	}
	
	&.disabled {
		@include flex-row();
		.status{
			flex: none;
			margin-left: 10px;
			@include fs(12);
			opacity: 0.5;
		}
		line-height: 1.5em;
		background-color: $color-light-gray;
		border-color: $color-light-gray;
		.center-ellipse-text{
			flex: 1;
			opacity: 0.5;
		}
	}
}
</style>

