<script setup lang="ts">
import CompPopup from "@/components/popup/CompPopup.vue";
import {defaultPopupExpose} from "@/library/PopupManager.ts";
import type {PopupInstance} from "@/types/popup.ts";
import {setClipboard} from "@/library/Utility.ts";
import {useI18n} from "vue-i18n";

interface ContactInfo {
	type: string;
	label: string;
	value: string;
	icon: string;
}

const { t } = useI18n();
const dialog = ref<PopupInstance>();

const contactList = computed(() => [
	{
		type: 'email',
		label: t('popup.contactService.email'),
		value: 'cs@solo.com.hk',
		icon: '📧'
	},
	{
		type: 'wechat',
		label: t('popup.contactService.wechat'),
		value: 'SoloFinancialGroup',
		icon: '💬'
	},
]);

defineExpose(defaultPopupExpose(dialog, {
	open() {
		// 打开客服联系信息弹窗
	}
}));

async function copyContact(contact: ContactInfo) {
	await setClipboard(contact.value, contact.label);
}
</script>

<template>
	<CompPopup ref="dialog" :title="t('popup.contactService.title')" closable>
		<view class="service-content">
			<view class="contact-list">
				<view
					v-for="contact in contactList"
					:key="contact.type"
					class="contact-item"
					@click="copyContact(contact)"
				>
					<text class="contact-icon">{{ contact.icon }}</text>
					<view class="contact-info">
						<text class="contact-label">{{ contact.label }}</text>
						<text class="contact-value">{{ contact.value }}</text>
					</view>
					<text class="copy-hint">{{ t('popup.contactService.copyHint') }}</text>
				</view>
			</view>
		</view>
	</CompPopup>
</template>

<style scoped lang="scss">
.service-content {
	padding: 20px 0;
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	min-height: 200px;
}

.contact-list {
	width: 100%;
	display: flex;
	flex-direction: column;
	gap: 12px;
	padding: 0 20px;
}

.contact-item {
	display: flex;
	align-items: center;
	gap: 12px;
	padding: 12px;
	background-color: #f5f5f5;
	border-radius: 8px;
	cursor: pointer;
	transition: background-color 0.2s;

	&:active {
		background-color: #e8e8e8;
	}
}

.contact-icon {
	font-size: 24px;
	min-width: 30px;
	text-align: center;
}

.contact-info {
	flex: 1;
	display: flex;
	flex-direction: column;
	gap: 4px;
}

.contact-label {
	@include fs(14);
	color: #666;
	font-weight: 500;
}

.contact-value {
	@include fs(12);
	color: #999;
}

.copy-hint {
	@include fs(12);
	color: #999;
}
</style>

