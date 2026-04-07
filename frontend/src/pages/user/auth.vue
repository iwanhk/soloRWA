<script lang="ts" setup>
import {ApiUserAuditDetail} from "@/api/user/ApiUserAuditDetail";
import type {UserAuditDetail} from "@/types/entity";
import {ShowInfo} from "@/library/ShowInfo";
import type {PopupFormField, PopupInstance} from "@/types/popup";
import PopupForm from "@/components/popup/PopupForm.vue";
import {ApiUserUpdateBankInfo, type ApiUserUpdateBankInfoParams} from "@/api/user/ApiUserUpdateBankInfo";
import {formatTime, previewImage} from "@/library/Utility";
import {AuditStatus} from "@/types/enums";
import {dictLabel} from "@/library/dict.ts";
import {auditStatusOptions} from "@/types/options.ts";
import {useUserStore} from "@/store/user.ts";
import UniRouter from "@/library/UniRouter.ts";
import {useI18n} from "vue-i18n";
import {useConfigStore} from "@/store/config.ts";
const { t } = useI18n()
const configStore = useConfigStore();
const fontScale = computed(()=>configStore.fontScale);
const auditDetail = ref<UserAuditDetail>();
const loading = ref(false);
const userStore = useUserStore();

async function loadAuditInfo() {
	try {
		loading.value = true;
		const api = new ApiUserAuditDetail();
		const {data} = await api.call();
		auditDetail.value = data;
		
		if(auditDetail.value.auditStatus !== userStore.user?.auditStatus){
			await userStore.loadUserInfo();
			if (([AuditStatus.AUDIT_PASSED, AuditStatus.PENDING_AUDIT] as unknown as AuditStatus[]).includes(userStore.user?.auditStatus!)) {
				//UniRouter.to('/pages/user/auth');
			} else {
				UniRouter.to('/pages/user/verify');
			}
		}
	} catch (error) {
		ShowInfo.toast(t('pages.user.auth.loadError'));
	} finally {
		loading.value = false;
	}
}

function previewImages(urls: string[]) {
	uni.previewImage({
		urls: urls,
		current: 0
	});
}

const formPopup = ref<PopupInstance<ApiUserUpdateBankInfoParams, [PopupFormField[], Record<string, any>|void, { title?:string, submit?:string }|void]>>();
const fields: PopupFormField[] = [
	{
		name: 'bankAccountName',
		label: t('common.payment.payee'),
		type: 'text',
		placeholder: t('pages.user.verify.accountNamePlaceholder'),
		rules: [
			{ required: true, errorMessage: t('pages.user.verify.accountNamePlaceholder') }
		]
	},
	{
		name: 'bankAccount',
		label: t('common.payment.account'),
		type: 'text',
		placeholder: t('pages.user.verify.accountPlaceholder'),
		rules: [
			{ required: true, errorMessage: t('pages.user.verify.accountPlaceholder') }
		]
	},
	{
		name: 'bankName',
		label: t('common.payment.bankName'),
		type: 'text',
		placeholder: t('pages.user.verify.bankNamePlaceholder'),
		rules: [
			{ required: true, errorMessage: t('pages.user.verify.bankNamePlaceholder') }
		]
	}
];

async function handleUpdateBankInfo() {
	const bankCard = auditDetail.value!.bankCard!;
	const defaultValue = {
		id: bankCard.id,
		bankAccountName: bankCard.bankAccountName,
		bankAccount: bankCard.bankAccount,
		bankName: bankCard.bankName
	};

	const result = await formPopup.value?.open(fields, defaultValue, {
		title: t('pages.user.auth.editBankInfo'),
		submit: t('pages.user.auth.submitAudit')
	});

	if (result) {
		result.id = defaultValue.id;
		try {
			const api = new ApiUserUpdateBankInfo(result);
			await api.call();
			ShowInfo.toastSuccess(t('pages.user.auth.edit'))
			await loadAuditInfo();
		} catch (error) {
			ShowInfo.toast(error);

		}
	}
}

onMounted(() => {
	loadAuditInfo();
});
</script>

<template>
	<view class="page page-auth" v-if="auditDetail">
		<!-- 身份证 -->
		<view class="menu-list-item">
			<view class="menu-list-item-label">{{ t('pages.user.auth.idCard') }}</view>
			<up-button type="primary" size="small" @click="previewImage([auditDetail.idCardFrontUrl!,auditDetail.idCardBackUrl!])">{{ t('common.label.view') }}</up-button>
		</view>

		<!-- 职业资质 -->
		<view class="menu-list-item" v-if="auditDetail.investmentQualificationUrls?.length">
			<view class="menu-list-item-label">{{ t('pages.user.auth.qualification') }}</view>
			<up-button type="primary" size="small" @click="previewImages(auditDetail.investmentQualificationUrls!)">{{ t('common.label.view') }}</up-button>
		</view>

		<!-- 银行流水单 -->
		<view class="menu-list-item" v-if="auditDetail.bankFlowUrls?.length">
			<view class="menu-list-item-label">{{ t('pages.user.auth.bankFlow') }}</view>
			<up-button type="primary" size="small" @click="previewImages(auditDetail.bankFlowUrls!)">{{ t('common.label.view') }}</up-button>
		</view>

		<!-- 住址证明 -->
		<view class="menu-list-item" v-if="auditDetail.residenceProofUrls?.length">
			<view class="menu-list-item-label">{{ t('pages.user.verify.residenceProof') }}</view>
			<up-button type="primary" size="small" @click="previewImages(auditDetail.residenceProofUrls!)">{{ t('common.label.view') }}</up-button>
		</view>

		<!-- 联系电话 -->
		<view class="menu-list-item">
			<view class="menu-list-item-label">{{ t('pages.user.auth.phone') }}</view>
			<view class="menu-list-item-value">{{ auditDetail.contactPhone }}</view>
		</view>

		<!-- 银行卡 -->
		<view class="menu-list-item menu-list-item-bank" v-if="auditDetail.bankCard">
			<view class="menu-list-item-label">{{ t('pages.user.auth.bankCard') }}</view>
			<up-button type="primary" size="small" @click="handleUpdateBankInfo">{{ t('pages.user.auth.edit') }}</up-button>
		</view>
		<view class="bank-info" v-if="auditDetail.bankCard">
			<view class="bank-info-row">
				<text class="bank-info-label">{{ t('common.payment.payee') }}</text>
				<text class="bank-info-value">{{ auditDetail.bankCard.bankAccountName }}</text>
			</view>
			<view class="bank-info-row">
				<text class="bank-info-label">{{ t('common.payment.account') }}</text>
				<text class="bank-info-value">{{ auditDetail.bankCard.bankAccount }}</text>
			</view>
			<view class="bank-info-row">
				<text class="bank-info-label">{{ t('common.payment.bankName') }}</text>
				<text class="bank-info-value">{{ auditDetail.bankCard.bankName }}</text>
			</view>
			<!-- 银行卡审核状态 -->
			<view v-if="auditDetail.bankCard.auditStatus !== AuditStatus.AUDIT_PASSED" class="bank-card-status" :class="`status-${auditDetail.bankCard.auditStatus}`">
				<text>{{ dictLabel(auditStatusOptions, auditDetail.bankCard.auditStatus) }}</text>
			</view>
			<view v-if="auditDetail.bankCard.auditStatus === AuditStatus.AUDIT_REJECTED" class="bank-card-note">
				<text class="label">{{ t('pages.user.auth.reason') }}</text><text class="reason">{{ auditDetail.bankCard?.auditRemark }}</text>
			</view>
		</view>

		<!-- 提交时间和审核状态 -->
		<view class="submit-time">
			<view v-if="auditDetail.auditStatus !== AuditStatus.NOT_SUBMITTED" class="status-info" :class="`status-${auditDetail.auditStatus}`">
				<view class="status-text">{{ dictLabel(auditStatusOptions, auditDetail.auditStatus) }}</view>
				<view v-if="auditDetail.auditRemark" class="remark">{{ auditDetail.auditRemark }}</view>
			</view>
			<text>{{ t('pages.user.auth.submitTime') }}{{ formatTime(auditDetail.createTime) }}</text>
		</view>
	</view>

	<PopupForm ref="formPopup" />
</template>

<style scoped lang="scss">
.page{
	--font-scale:v-bind(fontScale);
}
.page-auth {
	.menu-list-item-bank {
		margin-bottom: 0;
		border-radius: 4px 4px 0 0;

		+ .bank-info {
			margin-top: 0;
			border-radius: 0 0 4px 4px;
		}
	}

	.bank-info {
		background: $bg-color-light;
		padding: 4px 8px;
		margin-bottom: 5px;
		margin-top: 0;

		&-row {
			@include flex-row(center);
			margin-bottom: 4px;
			@include fs(10);
			color: $color-gray;
		}

		&-label {
			min-width: 60px;
		}

		&-value {
			flex: 1;
		}
	}

	.bank-card-status {
		width: 100%;
		margin-top: 8px;
		padding-top: 8px;
		padding-bottom: 8px;
		border-top: 1px solid $color-gray;
		text-align: center;
		@include fs(11);

		// 待审核 - 黄色
		&.status-1 {
			color: $color-text-white;
		}

		// 不通过 - 红色
		&.status-3 {
			color: $color-red;
		}
	}
	
	.bank-card-note{
		color: $color-gray;
		.reason {
			color: $color-red;
		}
		@include fs(11);
	}

	.submit-time {
		margin-top: 20px;
		padding-top: 20px;
		border-top: 1px solid $color-gray;
		text-align: center;
		@include fs(12);
		color: $color-text-gray;

		.status-info {
			margin-bottom: 12px;

			.status-text {
				@include fs(12);
				margin-bottom: 4px;
			}

			.remark {
				@include fs(11);
				opacity: 0.8;
			}

			// 待审核 - 黄色
			&.status-1 {
				color: $color-text-white;
			}

			// 通过 - 绿色
			&.status-2 {
				color: $color-green;
			}

			// 不通过 - 红色
			&.status-3 {
				color: $color-red;
			}
		}
	}
}
</style>

