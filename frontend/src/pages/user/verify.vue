<script lang="ts" setup>
import CompImage from "@/components/CompImage.vue";
import CompUrlUpload from "@/components/CompUrlUpload.vue";
import CompInput from "@/components/CompInput.vue";
import type {PopupInstance} from "@/types/popup";
import {ShowInfo} from "@/library/ShowInfo";
import UniRouter from "@/library/UniRouter";
import type {UniFormsInstance, UniFormsRules} from "@/types/uni-ui";
import {ApiUserAuditSubmit, type ApiUserAuditSubmitParams} from "@/api/user/ApiUserAuditSubmit";
import {useUserStore} from "@/store/user";
import {AuditStatus} from "@/types/enums";
import {ApiUserAuditDetail} from "@/api/user/ApiUserAuditDetail";
import type {UserAuditDetail} from "@/types/entity";
import {formatTime} from "@/library/Utility";
import ProtocolConfirm from "@/components/ProtocolConfirm.vue";
import {useConfigStore} from "@/store/config.ts";
import {UploadLimit} from "@/library/limits.ts";
import {formatFileSize} from "@/library/format.ts";
import {useI18n} from "vue-i18n";
import {dictLabel} from "@/library/dict.ts";
import {auditStatusOptions} from "@/types/options.ts";
import {parsePhoneNumberWithError} from 'libphonenumber-js';
import PopupInputCode from "@/components/popup/PopupInputCode.vue";
import {CodeScene} from "@/api/generic/ApiSendSmsCode.ts";

const { t } = useI18n()

// 验证电话号码（支持带区号）
function validateContactPhone(phoneNumber: string): boolean {
	if (!phoneNumber) return false;

	try {
		const parsed = parsePhoneNumberWithError(phoneNumber);
		if(parsed.country) {
			return true;
		}
		return false;
	} catch {
		return false;
	}
}

const userStore = useUserStore();
const configStore = useConfigStore();
const fontScale = computed(()=>configStore.fontScale);
const verifyForm = ref<UniFormsInstance>();

// 表单数据
const formData = reactive<Partial<ApiUserAuditSubmitParams> & { idCardImage?: { front?: any; back?: any } }>({
	code: '',
	realName: '',
	idCard: '',
	idCardExpire: '',
	contactPhone: userStore.user?.mobile ?? '',
	bankAccount: '',
	bankName: '',
	bankAccountName: '',
	idCardFrontFile: undefined,
	idCardBackFile: undefined,
	investmentQualificationFiles: [],
	bankFlowFiles: [],
	residenceProofFiles: [],
	idCardImage: {
		front: undefined,
		back: undefined,
	},
});

const agree = ref(configStore.protocolAgree);

const fileMaxSize = UploadLimit;

// 表单验证规则
const rules: UniFormsRules = {
	realName: {
		rules: [
			{ required: true, errorMessage: t('pages.user.verify.realNamePlaceholder') },
			{ minLength: 2, errorMessage: t('pages.user.verify.realNamePlaceholder') },
		],
		label: t('pages.user.verify.realName')
	},
	idCard: {
		rules: [
			{ required: true, errorMessage: t('pages.user.verify.idCardNumberPlaceholder') },
			//{ pattern: /^[0-9]{18}$/, errorMessage: t('pages.user.verify.idCardNumberPlaceholder') },
		],
		label: t('pages.user.verify.idCardNumber')
	},
	idCardExpire: {
		rules: [
			{ required: true, errorMessage: t('pages.user.verify.expireDatePlaceholder') },
		],
		label: t('pages.user.verify.expireDate')
	},
	idCardImage: {
		rules: [
			{
				validateFunction: (rule: any, value: any, data: any, callback: any) => {
					if (!formData.idCardFrontFile) {
						callback(t('pages.user.verify.idCardFront'));
						return false;
					}
					if (!formData.idCardBackFile) {
						callback(t('pages.user.verify.idCardBack'));
						return false;
					}
					return true;
				},
			},
		],
		label: t('pages.user.auth.idCard')
	},
	contactPhone: {
		rules: [
			{ required: true, errorMessage: t('pages.user.verify.phonePlaceholder') },
			{
				validateFunction: (rule: any, value: any, data: any, callback: any) => {
					if (!validateContactPhone(value)) {
						callback(t('pages.login.form.phoneFormatError'));
						return false;
					}
					return true;
				},
			},
		],
		label: t('pages.user.auth.phone')
	},
	bankAccount: {
		rules: [
			{ required: true, errorMessage: t('pages.user.verify.accountPlaceholder') },
			{
				validateFunction: (rule: any, value: any, data: any, callback: any) => {
					if (!formData.bankAccountName) {
						callback(t('pages.user.verify.accountNamePlaceholder'));
						return false;
					}
					if (!formData.bankName) {
						callback(t('pages.user.verify.bankNamePlaceholder'));
						return false;
					}
					return true;
				},
			},
		],
		label: t('pages.user.verify.bankCardGroup')
	},
	investmentQualificationFiles: {
		rules: [
			{ minLength: 1, errorMessage: t('pages.user.verify.qualification') },
		],
		label: t('pages.user.verify.qualification')
	},
	bankFlowFiles: {
		rules: [
			{ minLength: 1, errorMessage: t('pages.user.verify.bankFlow') },
		],
		label: t('pages.user.verify.bankFlow')
	},
	residenceProofFiles: {
		rules: [
			{ minLength: 1, errorMessage: t('pages.user.verify.residenceProof') },
		],
		label: t('pages.user.verify.residenceProof')
	},
};

const userVerifyConfirmPopup = ref<PopupInstance<[string, string, string|undefined]>>()
const isSubmitting = ref(false);

// OCR 识别状态
const ocrLoading = ref(false);
const ocrFrontLoading = ref(false);
const ocrBackLoading = ref(false);

// OCR 识别身份证
async function recognizeIdCard(side: 'front' | 'back') {
	const fileUrl = side === 'front' ? formData.idCardFrontFile : formData.idCardBackFile;

	if (!fileUrl) {
		ShowInfo.toast(t('pages.user.verify.idCardFront'));
		return;
	}

	try {
		if (side === 'front') {
			ocrFrontLoading.value = true;
		} else {
			ocrBackLoading.value = true;
		}

		// 注意：这里需要根据后端 API 的实际要求调整
		// 如果后端仍然需要 ApiUploadFile，需要从 URL 重新构建
		// 暂时注释掉 OCR 功能，因为现在 file 是 URL 字符串
		ShowInfo.toast(t('pages.user.verify.recognize'));
		return;

		// const api = new ApiUserOcrIdcard({ file, side });
		// const { data } = await api.call();

		// // 自动填充识别结果
		// if (data) {
		// 	if (side === 'front') {
		// 		formData.realName = data.name || '';
		// 		formData.idCard = data.idCardNo || '';
		// 		if (data.birth) {
		// 			formData.idCardExpire = data.validDate || '';
		// 		}
		// 	} else {
		// 		// 反面可以获取更多信息
		// 		if (data.validDate) {
		// 			formData.idCardExpire = data.validDate;
		// 		}
		// 	}
		// 	ShowInfo.toast('识别成功');
		// }
	} catch (error) {
		ShowInfo.toast(t('pages.user.verify.recognize'));
		console.error('OCR error:', error);
	} finally {
		if (side === 'front') {
			ocrFrontLoading.value = false;
		} else {
			ocrBackLoading.value = false;
		}
	}
}

async function submit(){
	configStore.protocolAgree = agree.value;
	if (!agree.value) {
		ShowInfo.toast(t('pages.user.verify.agreeRequired'));
		return;
	}

	// 验证表单
	//await verifyForm.value?.validate();

	// 打开验证码弹窗
	const result = await userVerifyConfirmPopup.value?.open();

	if(!result){
		return;
	}
	
	const [smsCode, emailCode] = result;

	try {
		isSubmitting.value = true;

		// 设置验证码
		formData.code = smsCode;
		formData.emailCode = emailCode;

		delete formData.idCardImage;
		formData.idCardExpire = formatTime(formData.idCardExpire!, 'YYYY-MM-DD');

		const api = new ApiUserAuditSubmit(formData as ApiUserAuditSubmitParams);
		await api.call();

		ShowInfo.toastSuccess(t('pages.user.verify.submitButton'));
		await userStore.loadUserInfo();
		UniRouter.to('/pages/user/verify-submitted')
	} catch (error) {
		ShowInfo.toastError(error, t('pages.user.verify.submitButton'));
	} finally {
		isSubmitting.value = false;
	}
}

const userAuditDetail = ref<UserAuditDetail>()
const auditDetailLoading = ref(false);
async function loadAuditInfo(){
	try {
		auditDetailLoading.value = true;
		const api = new ApiUserAuditDetail();
		const {data} = await api.call();
		userAuditDetail.value = data;

		// 将审核数据写入 formData
		formData.realName = data.realName;
		formData.idCard = data.idCard;
		formData.idCardExpire = data.idCardExpire;
		formData.contactPhone = data.contactPhone;
		formData.idCardFrontFile = data.idCardFrontUrl;
		formData.idCardBackFile = data.idCardBackUrl;
		formData.investmentQualificationFiles = data.investmentQualificationUrls || [];
		formData.bankFlowFiles = data.bankFlowUrls || [];
		formData.residenceProofFiles = data.residenceProofUrls || [];

		if (data.bankCard) {
			formData.bankAccount = data.bankCard.bankAccount;
			formData.bankName = data.bankCard.bankName;
			formData.bankAccountName = data.bankCard.bankAccountName;
		}

		if(userAuditDetail.value.auditStatus !== userStore.user?.auditStatus){
			await userStore.loadUserInfo();
			if ([AuditStatus.AUDIT_PASSED, AuditStatus.PENDING_AUDIT].includes(userStore.user?.auditStatus!)) {
				UniRouter.to('/pages/user/auth');
			} else {
				//UniRouter.to('/pages/user/verify');
			}
		}
	}
	finally {
		auditDetailLoading.value = false;
	}
}

watch(()=>userStore.user,(user)=>{
	if(user){
		if([AuditStatus.AUDIT_PASSED, AuditStatus.PENDING_AUDIT, AuditStatus.AUDIT_REJECTED].includes(user.auditStatus) && !userAuditDetail.value){
			loadAuditInfo();
		}
	}
},{immediate:true})
</script>

<template>
	<page-meta page-style="background-color:#fff;"></page-meta>
	<view class="page page-verify" v-if="userStore.user">
		<!-- 审核状态提示 -->
		<view class="audit-status-banner" v-if="userAuditDetail && userAuditDetail.auditStatus !== AuditStatus.NOT_SUBMITTED" :class="`status-${userAuditDetail?.auditStatus}`">
			<view class="status-text">{{ dictLabel(auditStatusOptions, userAuditDetail.auditStatus) }}</view>
			<view v-if="userAuditDetail.auditRemark" class="remark">{{ userAuditDetail.auditRemark }}</view>
		</view>
		
		<uni-forms v-if="userStore.user.auditStatus === AuditStatus.AUDIT_REJECTED || userStore.user.auditStatus === AuditStatus.NOT_SUBMITTED" ref="verifyForm" class="form theme-light" label-position="left" label-width="6em" err-show-type="toast" :rules="rules" :model-value="formData">
			<uni-forms-item label-position="top" label-width="auto" :label="t('pages.user.verify.uploadIdCard')" name="idCardImage">
				<template #label>
					<view class="uni-forms-item__label">
						<text>{{ t('pages.user.verify.uploadIdCard') }}</text>
						<text class="note">{{ t('pages.user.verify.formatNote') }}</text>
					</view>
				</template>
				<view class="idcard-uploader">
					<view class="idcard-upload-item">
						<CompUrlUpload v-model="formData.idCardFrontFile" :ratio="0.6666" imageMode="aspectFit" preview>
							<CompImage class="upload-indicator" src="/static/images/user/verify-upload-idcard1.png"></CompImage>
						</CompUrlUpload>
						<view class="note">{{ t('pages.user.verify.idCardFront') }}</view>
						<view class="ocr-button-container" v-if="false">
							<up-button
								v-if="formData.idCardFrontFile"
								size="small"
								type="primary"
								shape="round"
								@click="recognizeIdCard('front')"
								:loading="ocrFrontLoading"
								:disabled="ocrFrontLoading"
							>
								{{ ocrFrontLoading ? t('pages.user.verify.recognizing') : t('pages.user.verify.recognize') }}
							</up-button>
						</view>
					</view>
					<view class="idcard-upload-item">
						<CompUrlUpload v-model="formData.idCardBackFile" :ratio="0.6666" imageMode="aspectFit" preview>
							<CompImage class="upload-indicator" src="/static/images/user/verify-upload-idcard2.png"></CompImage>
						</CompUrlUpload>
						<view class="note">{{ t('pages.user.verify.idCardBack') }}</view>
						<view class="ocr-button-container" v-if="false">
							<up-button
								v-if="formData.idCardBackFile"
								size="small"
								type="primary"
								shape="round"
								@click="recognizeIdCard('back')"
								:loading="ocrBackLoading"
								:disabled="ocrBackLoading"
							>
								{{ ocrBackLoading ? t('pages.user.verify.recognizing') : t('pages.user.verify.recognize') }}
							</up-button>
						</view>
					</view>
				</view>
			</uni-forms-item>

			<uni-forms-item :label="t('pages.user.verify.realName')" name="realName">
				<CompInput v-model="formData.realName" theme="light" :placeholder="t('pages.user.verify.realNamePlaceholder')"></CompInput>
			</uni-forms-item>

			<uni-forms-item :label="t('pages.user.verify.idCardNumber')" name="idCard">
				<CompInput v-model="formData.idCard" theme="light" :placeholder="t('pages.user.verify.idCardNumberPlaceholder')"></CompInput>
			</uni-forms-item>

			<uni-forms-item :label="t('pages.user.verify.expireDate')" name="idCardExpire">
				<u-datetime-picker
					:placeholder="t('pages.user.verify.expireDatePlaceholder')"
					format="YYYY-MM-DD"
					maxDate="2099-12-31"
					mode="date"
					v-model="formData.idCardExpire"
					has-input
					:input-props="{ border:'none' }"
				>
				</u-datetime-picker>
			</uni-forms-item>

			<uni-forms-item label-position="top" label-width="auto" :label="t('pages.user.verify.qualification')" name="investmentQualificationFiles">
				<template #label>
					<view class="uni-forms-item__label">
						<text>{{ t('pages.user.verify.qualification') }}</text>
						<text class="note">{{ t('pages.user.verify.qualificationNote', { size: formatFileSize(fileMaxSize) }) }}</text>
					</view>
				</template>
				<CompUrlUpload v-model="formData.investmentQualificationFiles" :ratio="0.3145" imageMode="aspectFit" multiple :maxCount="6" :max-size="fileMaxSize">
					<CompImage class="upload-indicator" src="/static/images/user/verify-upload-invest.png"></CompImage>
				</CompUrlUpload>
			</uni-forms-item>

			<uni-forms-item label-position="top" label-width="auto" :label="t('pages.user.verify.bankFlow')" name="bankFlowFiles">
				<template #label>
					<view class="uni-forms-item__label">
						<text>{{ t('pages.user.verify.bankFlow') }}</text>
						<text class="note">{{ t('pages.user.verify.bankFlowNote', { size: formatFileSize(fileMaxSize) }) }}</text>
					</view>
				</template>
				<CompUrlUpload v-model="formData.bankFlowFiles" :ratio="0.3145" imageMode="aspectFit" multiple :maxCount="6" :max-size="fileMaxSize">
					<CompImage class="upload-indicator" src="/static/images/user/verify-upload-bank.png"></CompImage>
				</CompUrlUpload>
			</uni-forms-item>

			<uni-forms-item label-position="top" label-width="auto" :label="t('pages.user.verify.residenceProof')" name="residenceProofFiles">
				<template #label>
					<view class="uni-forms-item__label">
						<text>{{ t('pages.user.verify.residenceProof') }}</text>
						<text class="note">{{ t('pages.user.verify.residenceProofNote', { size: formatFileSize(fileMaxSize) }) }}</text>
					</view>
				</template>
				<CompUrlUpload v-model="formData.residenceProofFiles" :ratio="0.3145" imageMode="aspectFit" multiple :maxCount="6" :max-size="fileMaxSize">
					<CompImage class="upload-indicator" src="/static/images/user/verify-upload-address.png"></CompImage>
				</CompUrlUpload>
			</uni-forms-item>

			<uni-forms-item :label="t('pages.user.auth.phone')" name="contactPhone">
				<CompInput v-model="formData.contactPhone" theme="light" type="tel" :placeholder="t('pages.user.verify.phonePlaceholder')"></CompInput>
			</uni-forms-item>

			<uni-forms-item label-position="top" label-width="auto" :label="t('pages.user.verify.bankCardGroup')" name="bankAccount">
				<view class="bank-card-group">
					<view class="bank-card-item">
						<view class="label">{{ t('pages.user.verify.accountName') }}</view>
						<view class="value">
							<CompInput v-model="formData.bankAccountName" theme="light" border="bottom" :placeholder="t('pages.user.verify.accountNamePlaceholder')" name="bankAccountName"></CompInput>
						</view>
					</view>
					<view class="bank-card-item">
						<view class="label">{{ t('pages.user.verify.account') }}</view>
						<view class="value">
							<CompInput v-model="formData.bankAccount" theme="light" type="number" border="bottom" :placeholder="t('pages.user.verify.accountPlaceholder')" name="bankAccount"></CompInput>
						</view>
					</view>
					<view class="bank-card-item">
						<view class="label">{{ t('pages.user.verify.bankName') }}</view>
						<view class="value">
							<CompInput v-model="formData.bankName" theme="light" border="bottom" name="bankName" :placeholder="t('pages.user.verify.bankNamePlaceholder')"></CompInput>
						</view>
					</view>
				</view>
			</uni-forms-item>

			<ProtocolConfirm v-model="agree" />

			<view class="submit-action">
				<up-button color="#000" class="submit" shape="circle" size="large" @click="submit" :disabled="isSubmitting" :loading="isSubmitting">{{ t('pages.user.verify.submitButton') }}</up-button>
			</view>
		</uni-forms>

		<uni-forms v-else class="form theme-light">
			<view v-if="auditDetailLoading" class="loading-container">
				<up-loading-icon></up-loading-icon>
			</view>

			<template v-else-if="userAuditDetail">
				<!-- 身份证图片 -->
				<uni-forms-item label-position="top" label-width="auto" :label="t('pages.user.verify.uploadIdCard')">
					<view class="idcard-display">
						<view v-if="userAuditDetail.idCardFrontUrl" class="idcard-item">
							<CompImage :src="userAuditDetail.idCardFrontUrl" class="idcard-image"></CompImage>
							<view class="note">{{ t('pages.user.verify.idCardFront') }}</view>
						</view>
						<view v-if="userAuditDetail.idCardBackUrl" class="idcard-item">
							<CompImage :src="userAuditDetail.idCardBackUrl" class="idcard-image"></CompImage>
							<view class="note">{{ t('pages.user.verify.idCardBack') }}</view>
						</view>
					</view>
				</uni-forms-item>

				<uni-forms-item :label="t('pages.user.verify.realName')" name="realName">
					<view class="display-value">{{ userAuditDetail.realName }}</view>
				</uni-forms-item>

				<uni-forms-item :label="t('pages.user.verify.idCardNumber')" name="idCard">
					<view class="display-value">{{ userAuditDetail.idCard }}</view>
				</uni-forms-item>

				<uni-forms-item :label="t('pages.user.verify.expireDate')" name="idCardExpire">
					<view class="display-value">{{ userAuditDetail.idCardExpire }}</view>
				</uni-forms-item>

				<!-- 投资资质 -->
				<uni-forms-item v-if="userAuditDetail.investmentQualificationUrls?.length" label-position="top" label-width="auto" :label="t('pages.user.verify.qualification')">
					<view class="files-display">
						<view v-for="(url, index) in userAuditDetail.investmentQualificationUrls" :key="index" class="file-item">
							<CompImage :src="url" class="file-image"></CompImage>
						</view>
					</view>
				</uni-forms-item>

				<!-- 银行流水 -->
				<uni-forms-item v-if="userAuditDetail.bankFlowUrls?.length" label-position="top" label-width="auto" :label="t('pages.user.verify.bankFlow')">
					<view class="files-display">
						<view v-for="(url, index) in userAuditDetail.bankFlowUrls" :key="index" class="file-item">
							<CompImage :src="url" class="file-image"></CompImage>
						</view>
					</view>
				</uni-forms-item>

				<!-- 住址证明 -->
				<uni-forms-item v-if="userAuditDetail.residenceProofUrls?.length" label-position="top" label-width="auto" :label="t('pages.user.verify.residenceProof')">
					<view class="files-display">
						<view v-for="(url, index) in userAuditDetail.residenceProofUrls" :key="index" class="file-item">
							<CompImage :src="url" class="file-image"></CompImage>
						</view>
					</view>
				</uni-forms-item>

				<uni-forms-item :label="t('pages.user.auth.phone')" name="contactPhone">
					<view class="display-value">{{ userAuditDetail.contactPhone }}</view>
				</uni-forms-item>

				<!-- 银行卡 -->
				<uni-forms-item label-position="top" label-width="auto" :label="t('pages.user.verify.bankCardGroup')">
					<view v-if="userAuditDetail.bankCard" class="bank-card-group">
						<view class="bank-card-item">
							<view class="label">{{ t('pages.user.verify.accountName') }}</view>
							<view class="value">{{ userAuditDetail.bankCard.bankAccountName }}</view>
						</view>
						<view class="bank-card-item">
							<view class="label">{{ t('pages.user.verify.account') }}</view>
							<view class="value">{{ userAuditDetail.bankCard.bankAccount }}</view>
						</view>
						<view v-if="userAuditDetail.bankCard.bankName" class="bank-card-item">
							<view class="label">{{ t('pages.user.verify.bankName') }}</view>
							<view class="value">{{ userAuditDetail.bankCard.bankName }}</view>
						</view>
					</view>
				</uni-forms-item>

				<!-- 提交时间 -->
				<view class="submit-time">
					<text>{{ t('pages.user.auth.submitTime') }}{{ formatTime(userAuditDetail.createTime) }}</text>
				</view>
			</template>
		</uni-forms>
		
		<PopupInputCode :sms-scene="CodeScene.AUDIT" :need2-fa="false" ref="userVerifyConfirmPopup"></PopupInputCode>
	</view>
</template>

<style scoped lang="scss">
.page{
	--font-scale:v-bind(fontScale);
}
.idcard-uploader{
	@include flex-row();
	gap: 20px;
	.idcard-upload-item{
		flex: 1;
		.note{
			margin-top: 12px;
			text-align: center;
			@include fs(9);
			@include fw(medium);
		}
	}
}
.upload-indicator{
	width: 100%;
	height: 100%;
}
.bank-card-group{
	border: 1px solid #bebebe;
	border-radius: 4px;
	padding-right: 20px;
	.bank-card-item{
		padding-top: 8px;
		padding-bottom: 8px;
		@include flex-row(center);
		.label{
			@include en-break;
			color: $color-text-black;
			@include fs(13);
			@include fw(medium);
			width: 5em;
			flex: none;
			text-align: center;
		}
		.value{
			flex: 1;
		}
		:deep(.u-input){
			padding-top: 0!important;
			padding-bottom: 0!important;
			&.u-border-bottom{
				border-color: $color-gray!important;
				
				&.is-focused{
					border-color: $theme-color!important;
				}
			}
		}
	}
}
.submit{
	height: 42px;
	@include fw(medium);
	border-radius: 18px!important;
}
.loading-container{
	@include flex-center;
	padding: 40px 0;
}
.audit-status-banner{
	margin-bottom: 20px;
	padding: 12px 16px;
	border-radius: 4px;
	background-color: #f5f5f5;
	border-left: 4px solid #999;
	&.status-1{
		background-color: #fff3cd;
		border-left-color: #ffc107;
		.status-text{
			color: #856404;
		}
	}
	&.status-2{
		background-color: #d4edda;
		border-left-color: #28a745;
		.status-text{
			color: #155724;
		}
	}
	&.status-3{
		background-color: #f8d7da;
		border-left-color: #dc3545;
		.status-text{
			color: #721c24;
		}
	}
	.status-text{
		@include fs(14);
		@include fw(medium);
		margin-bottom: 4px;
	}
	.remark{
		@include fs(12);
		color: inherit;
		opacity: 0.8;
	}
}
.info-display{
	.info-item{
		@include flex-row(center);
		padding: 12px 0;
		border-bottom: 1px solid #f0f0f0;
		&:last-child{
			border-bottom: none;
		}
		.label{
			color: $color-text-black;
			@include fs(13);
			@include fw(medium);
			width: 5em;
			flex: none;
			text-align: center;
		}
		.value{
			flex: 1;
			@include fs(13);
			color: $color-text-gray;
			word-break: break-all;
		}
	}
}
.idcard-display{
	@include flex-row();
	gap: 20px;
	margin-top: 16px;
	.idcard-item{
		flex: 1;
		.idcard-image{
			width: 100%;
			height: auto;
			border-radius: 4px;
			display: block;
		}
		.note{
			margin-top: 8px;
			text-align: center;
			@include fs(12);
			color: $color-text-gray;
		}
	}
}
.files-display{
	@include flex-row();
	gap: 12px;
	flex-wrap: wrap;
	.file-item{
		width: calc(50% - 6px);
		.file-image{
			width: 100%;
			height: auto;
			border-radius: 4px;
			display: block;
		}
	}
}
.bank-card-display{
	border: 1px solid #bebebe;
	border-radius: 4px;
	padding-right: 20px;
	.bank-card-item{
		padding-top: 8px;
		padding-bottom: 8px;
		@include flex-row(center);
		border-bottom: 1px solid #f0f0f0;
		&:last-child{
			border-bottom: none;
		}
		.label{
			@include en-break;
			color: $color-text-black;
			@include fs(13);
			@include fw(medium);
			width: 5em;
			flex: none;
			text-align: center;
		}
		.value{
			flex: 1;
			@include fs(13);
			color: $color-text-gray;
			word-break: break-all;
		}
	}
}
.submit-time{
	margin-top: 20px;
	padding-top: 20px;
	border-top: 1px solid #f0f0f0;
	text-align: center;
	@include fs(12);
	color: $color-text-gray;
}
.display-value{
	@include flex-row(center);
	@include fs(13);
	height: 100%;
	padding: 0 6px;
	word-break: break-all;
}
.form {
	:deep(.u-datetime-picker .u-datetime-picker__has-input .u-input) {
		padding: 6px 9px!important;
	}
}
.ocr-button-container{
	display: none;
	@include flex-row(center, center);
	margin-top: 10px;
	.u-button{
		width: auto;
		border-radius: 999em;
		height: 2em;
		padding: 0 1em;
	}
}

.protocol-confirm{
	margin-bottom: 10px;
}
</style>

