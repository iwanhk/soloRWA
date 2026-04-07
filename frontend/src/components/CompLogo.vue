<script setup lang="ts">

import CompImage from "@/components/CompImage.vue";
import foreignLogo from "@/static/images/logo.png";
import domesticLogo from "@/static/images/llogo.png";
import ENV from "@/library/env.ts";


const logoSrc = computed(()=>{
	return ENV.foreign ? foreignLogo: domesticLogo;
})


</script>

<template>
	<CompImage :src="logoSrc"/>
</template>

<style scoped lang="scss">

</style>