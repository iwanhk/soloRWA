<script setup lang="ts">
import {useI18n} from 'vue-i18n';
import type {ProjectEntity} from "@/types/entity";
import {formatTime} from "@/library/Utility";
import {useDictStore} from "@/store/dict";
import {Currency} from "@/types/enums";
import {dictLabel} from "@/library/dict";
import {formatPrice, transformCurrency} from "@/library/format.ts";
import ContentPopup from "@/components/popup/ContentPopup.vue";
import type {PopupInstance} from "@/types/popup.ts";
import {projectTypeOptions} from "@/types/options.ts";

const {t} = useI18n();

const props = defineProps<{
	project:ProjectEntity,
}>()

const dictStore = useDictStore();
const contentPopup = ref<PopupInstance<void, [string, string, void|boolean]>>();

// 将 chains 转换成 LabelOptions
const chainOptions = computed(() => {
	return dictStore.chains?.map(chain => ({
		label: chain.name,
		value: chain.id,
	})) ?? [];
})

function showRedeemRules(){
	contentPopup.value?.open(t('pages.project.components.projectDetailInfo.redemptionRules'), props.project.redemptionRules, false);
}

function showDividendRules(){
	contentPopup.value?.open(t('pages.project.components.projectDetailInfo.dividendRules'), props.project.dividendInstructions, false);
}
</script>

<template>
	<view class="project-detail-info">
		<view class="info-item wide">
			<view class="label">{{ t('pages.project.components.projectDetailInfo.projectName') }}</view>
			<view class="value">{{ project.projectName }}</view>
		</view>
		<view class="info-item">
			<view class="label">{{ t('pages.project.components.projectDetailInfo.projectType') }}</view>
			<view class="value">{{ dictLabel(projectTypeOptions, project.projectType) }}</view>
		</view>
		<view class="info-item">
			<view class="label">{{ t('pages.project.detail.issueUnitPrice') }}</view>
			<view class="value">{{ formatPrice(transformCurrency(project.issueUnitPrice, Currency.USDT, project.investmentCurrency)) }}</view>
		</view>
		<view class="info-item">
			<view class="label">{{ t('pages.project.components.projectDetailInfo.issueQuantity') }}</view>
			<view class="value">{{ project.issueQuantity }}</view>
		</view>
		<view class="info-item">
			<view class="label">{{ t('pages.project.detail.remainingQuantity') }}</view>
			<view class="value">{{ project.remainingQuantity }}</view>
		</view>
		<view class="info-item">
			<view class="label">{{ t('pages.project.detail.expectedAnnualReturn') }}</view>
			<view class="value">{{ project.expectedAnnualReturn }}%</view>
		</view>
		<view class="info-item">
			<view class="label">{{ t('pages.project.components.projectDetailInfo.minimumPurchase') }}</view>
			<view class="value">{{ project.minimumPurchase }}</view>
		</view>
		<view class="info-item">
			<view class="label">{{ t('pages.project.components.projectDetailInfo.fundDuration') }}</view>
			<view class="value">
				<template v-if="project.duration">
				{{ t('common.unit.months', { count: project.duration }) }}
				</template>
				<template v-else>{{ t('common.unit.indefinite') }}</template>
			</view>
		</view>
		<view class="info-item">
			<view class="label">{{ t('pages.project.components.projectDetailInfo.issueChain') }}</view>
			<view class="value">{{ dictLabel(chainOptions, project.issueChainId) }}</view>
		</view>
		<view class="info-item">
			<view class="label">{{ t('pages.project.components.projectDetailInfo.redemptionRules') }}</view>
			<view class="value"><text @click="showRedeemRules" class="view-link">{{ t('common.label.view') }}</text></view>
		</view>
		<view class="info-item">
			<view class="label">{{ t('pages.project.components.projectDetailInfo.dividendRules') }}</view>
			<view class="value"><text @click="showDividendRules" class="view-link">{{ t('common.label.view') }}</text></view>
		</view>
		<view class="info-item">
			<view class="label">{{ t('pages.project.components.projectDetailInfo.publisher') }}</view>
			<view class="value">{{ project.publisherCompanyName }}</view>
		</view>
		<ContentPopup ref="contentPopup" />
	</view>
</template>

<style scoped lang="scss">
.project-detail-info{
	color: $color-text-white;
	@include flex-row(stretch, flex-start, wrap);
	@include fs(10);
	gap: 6px;
	.info-item{
		min-width: calc(50% - 3px);
		@include flex-row(center, flex-start, wrap);
		gap: 6px;
		
		&.wide{
			width: 100%;
		}
	}
	.link{
		color: $theme-color;
	}
}
</style>