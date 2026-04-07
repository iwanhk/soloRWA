<script setup lang="ts">
import CompPopup from "@/components/popup/CompPopup.vue";
import CompUpload from "@/components/CompUpload.vue";
import {defaultPopupExpose} from "@/library/PopupManager";
import type {PopupInstance} from "@/types/popup";
import {ShowInfo} from "@/library/ShowInfo";
import {useUserStore} from "@/store/user";
import {ApiUserUpdateProfile, type ApiUserUpdateProfileParams} from "@/api/user/ApiUserUpdateProfile";
import type {ApiUploadFile} from "@/api/file";
import {useI18n} from "vue-i18n";

const { t } = useI18n();
const userStore = useUserStore();
const dialog = ref<PopupInstance>();
const isSubmitting = ref(false);

const formData = reactive<{
	nickName: string;
	avatarFile?: ApiUploadFile;
}>({
	nickName: '',
	avatarFile: undefined,
});

defineExpose(defaultPopupExpose(dialog, {
	open() {
		formData.nickName = userStore.user?.nickName ?? '';
		formData.avatarFile = undefined;
		isSubmitting.value = false;
	}
}));

async function submit() {
	if (!formData.nickName.trim()) {
		ShowInfo.toast(t('popup.editUserInfo.nicknameHint'));
		return;
	}

	try {
		isSubmitting.value = true;

		const params: ApiUserUpdateProfileParams = {
			nickName: formData.nickName,
		};

		if (formData.avatarFile) {
			params.avatarFile = formData.avatarFile;
		}

		const api = new ApiUserUpdateProfile(params);
		await api.call();

		ShowInfo.toastSuccess(t('popup.editUserInfo.successMsg'));
		await userStore.loadUserInfo();
		dialog.value?.result(true);
	} catch (error) {
		ShowInfo.toastError(error, t('popup.editUserInfo.successMsg'));
	} finally {
		isSubmitting.value = false;
	}
}
</script>

<template>
	<CompPopup ref="dialog" :title="t('popup.editUserInfo.title')">
		<view class="edit-user-info-content">
			<view class="form-group" v-if="false">
				<view class="form-label">{{ t('popup.editUserInfo.nickname') }}</view>
				<up-input
					v-model="formData.nickName"
					:placeholder="t('popup.editUserInfo.nicknameHint')"
					:disabled="isSubmitting"
					clearable
				/>
			</view>

			<view class="form-group">
				<view class="form-label">{{ t('popup.editUserInfo.avatar') }}</view>
				<view class="upload-wrap">
				<CompUpload
					v-model="formData.avatarFile"
					:ratio="1"
					imageMode="aspectFill"
				>
					<view class="upload-placeholder">
						<view class="upload-text">{{ t('popup.editUserInfo.uploadHint') }}</view>
					</view>
				</CompUpload>
				</view>
			</view>
		</view>

		<template #actions>
			<up-button
				type="warning"
				shape="circle"
				@click="submit"
				:disabled="isSubmitting"
				:loading="isSubmitting"
			>
				{{ t('popup.editUserInfo.saveBtn') }}
			</up-button>
		</template>
	</CompPopup>
</template>

<style scoped lang="scss">
.edit-user-info-content {
	padding: 20px 0;

	.form-group {
		margin-bottom: 20px;

		.form-label {
			@include fs(12);
			color: $color-text-black;
			margin-bottom: 8px;
			font-weight: 500;
		}
	}

	.upload-wrap{
		width: 120px;
	}
	
	.upload-placeholder {
		@include flex-center;
		width: 100%;
		height: 120px;
		background: #f5f5f5;
		border-radius: 8px;

		.upload-text {
			@include fs(12);
			color: $color-gray;
		}
	}
}
</style>

