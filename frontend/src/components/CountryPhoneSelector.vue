<script setup lang="ts">
import { getAllCountries, type CountryInfo } from '@/library/PhoneNumberUtil'
import { useConfigStore } from '@/store/config'
import PopupCountrySelector from '@/components/popup/PopupCountrySelector.vue'
import ENV from '@/library/env'
import type {PopupInstance} from "@/types/popup.ts";

const configStore = useConfigStore()

const modelValue = defineModel<string>({ default: '' })
const countryCode = defineModel<string>('countryCode', { default: 'CN' })

const countries = ref<CountryInfo[]>([])
const popup = ref<PopupInstance<CountryInfo>>()

onMounted(() => {
	let countryList = getAllCountries()
	// 如果是 foreign 模式，过滤掉中国
	if (ENV.foreign) {
		countryList = countryList.filter(c => c.code !== 'CN')
	}
	else{
		countryList = countryList.filter(c => c.code === 'CN' || c.code === 'HK')
	}
	countries.value = countryList
})

const selectedCountry = computed(() => {
	return countries.value.find(c => c.code === countryCode.value)
})

const dialCode = computed(() => {
	return selectedCountry.value?.dialCode || ''
})

async function openCountryPicker() {
	const result = await popup.value?.open(countryCode.value);
	if(result){
		countryCode.value = result.code
		modelValue.value = result.dialCode
	}
}
</script>

<template>
	<view class="country-phone-selector">
		<!-- 国家选择器 -->
		<view class="country-selector" @click="openCountryPicker">
			<view class="country-display">
				<text class="flag">{{ selectedCountry?.flag }}</text>
				<text class="dial-code">{{ dialCode }}</text>
				<text class="icon">▼</text>
			</view>
		</view>

		<!-- 国家选择弹窗 -->
		<PopupCountrySelector ref="popup" />
	</view>
</template>

<style scoped lang="scss">
.country-phone-selector {
	width: 6em;
	display: inline-block;
}

.country-selector {
	.country-display {
		@include flex-row(center, center);
		gap: 4px;
		padding: 0 12px;
		border-radius: 4px;
		cursor: pointer;

		.flag{
			display: none;
			color: $color-text-white;
		}
		
		.dial-code {
			@include fs(14);
			color: $color-text-white;
			min-width: 40px;
		}

		.icon {
			font-size: 12px;
			color: #999;
		}
	}
}
</style>

