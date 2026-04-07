<script setup lang="ts">
import { getAllCountries, type CountryInfo } from '@/library/PhoneNumberUtil'
import { useI18n } from 'vue-i18n'
import { useConfigStore } from '@/store/config'
import { Language } from '@/types/enums'
import ENV from '@/library/env'
import CompPopup from "@/components/popup/CompPopup.vue"
import type { PopupInstance } from "@/types/popup"
import { defaultPopupExpose } from "@/library/PopupManager"

const { t } = useI18n()
const configStore = useConfigStore()

const dialog = ref<PopupInstance>()
const countries = ref<CountryInfo[]>([])
const searchText = ref('')
const result = ref<string>('')
let openCountryCode = 'CN'

defineExpose(defaultPopupExpose(dialog, {
	open(countryCode: string = 'CN') {
		openCountryCode = countryCode
		searchText.value = ""
	},
}))

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

// 获取当前语言
const currentLanguage = computed(() => configStore.language)

// 获取当前语言对应的国家名称
const getCountryDisplayName = (country: CountryInfo): string => {
	if (currentLanguage.value === Language.EN_US) {
		return country.nameEn
	}
	// 繁体中文和简体中文都使用 nameZh（i18n-iso-countries 只支持简体中文）
	// 如果需要真正的繁体中文翻译，可以在这里添加转换逻辑
	return country.nameZh
}

const filteredCountries = computed(() => {
	if (!searchText.value) return countries.value
	const search = searchText.value.toLowerCase()
	return countries.value.filter(c => {
		const displayName = getCountryDisplayName(c)
		return (
			displayName.toLowerCase().includes(search) ||
			c.nameZh.toLowerCase().includes(search) ||
			c.nameEn.toLowerCase().includes(search) ||
			c.code.toLowerCase().includes(search) ||
			c.dialCode.includes(search)
		)
	})
})

const selectedCountry = computed(() => {
	return countries.value.find(c => c.code === openCountryCode)
})

function selectCountry(country: CountryInfo) {
	result.value = country.code
	openCountryCode = country.code
	configStore.setCountryCode(country.code)
	dialog.value?.result(country)
	searchText.value = ''
}
</script>

<template>
	<CompPopup ref="dialog" :title="t('components.countryPhoneSelector.selectCountry')">
		<!-- 搜索框 -->
		<view class="search-box">
			<input
				v-model="searchText"
				type="text"
				:placeholder="t('components.countryPhoneSelector.search')"
				class="search-input"
			/>
		</view>

		<!-- 国家列表 -->
		<scroll-view class="country-list" scroll-y>
			<view
				v-for="country in filteredCountries"
				:key="country.code"
				class="country-item"
				:class="{ active: country.code === openCountryCode }"
				@click="selectCountry(country)"
			>
				<view class="country-info">
					<text class="name">{{ getCountryDisplayName(country) }}</text>
					<text class="code">{{ country.code }}</text>
				</view>
				<text class="dial-code">{{ country.dialCode }}</text>
				<text v-if="country.code === openCountryCode" class="checkmark">✓</text>
			</view>
		</scroll-view>
	</CompPopup>
</template>

<style scoped lang="scss">
.search-box {
	padding: 12px 16px;
	border-bottom: 1px solid #eee;

	.search-input {
		text-align: left;
		padding: 8px 12px;
		background: #f5f5f5;
		border-radius: 4px;
		font-size: 14px;
		color: #333;

		&::placeholder {
			color: #999;
		}
		:deep(.uni-input-input){
			color: $color-text-black;
		}
	}
}

.country-list {
	text-align: left;
	flex: 1;
	overflow-y: auto;
	max-height: 55vh;

	.country-item {
		display: flex;
		align-items: center;
		gap: 12px;
		padding: 12px 16px;
		border-bottom: 1px solid #f0f0f0;
		cursor: pointer;

		&:active,
		&.active {
			background: #f5f5f5;
		}

		.flag {
			font-size: 24px;
			flex-shrink: 0;
		}

		.country-info {
			flex: 1;
			display: flex;
			flex-direction: column;
			gap: 2px;

			.name {
				font-size: 14px;
				color: #333;
				font-weight: 500;
			}

			.code {
				font-size: 12px;
				color: #999;
			}
		}

		.dial-code {
			@include fs(14);
			color: #666;
			flex-shrink: 0;
		}

		.checkmark {
			font-size: 18px;
			color: #07c160;
			flex-shrink: 0;
		}
	}
}
</style>
