<script setup lang="ts">
import {useI18n} from "vue-i18n"
import {useConfigStore} from "@/store/config.ts";

const props = defineProps<{
	status: 'loadmore' | 'loading' | 'nomore'
}>()

const configStore = useConfigStore();

const { t } = useI18n()
</script>

<template>
	<u-loadmore
		:loading-text="t('components.compLoadMore.loading')"
		:loadmore-text="t('components.compLoadMore.loadmore')"
		:nomore-text="t('components.compLoadMore.nomore')"
		:status="status"
		:font-size="12 * configStore.fontScale"
	/>
</template>

<style scoped lang="scss">

</style>