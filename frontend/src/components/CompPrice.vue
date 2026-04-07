<script setup lang="ts">
import {formatNumber, formatPrice} from "@/library/format.ts";

const props = withDefaults(defineProps<{
	price:number|undefined|null,
	format?: boolean,
	showUnit?: boolean,
	precise?: number,
	exactPrecise?: boolean
}>(),{
	format:true,
	showUnit: true,
})

const isPriceUp = computed(()=>typeof props.price === "number" && props.price > 0);
const isPriceDown = computed(()=>typeof props.price === "number" && props.price < 0);
const priceText = computed(()=>{
	if(typeof props.price === "number") {
		if(props.format){
			return (isPriceUp.value?'+':'')+formatPrice(props.price, props.showUnit, props.precise, props.exactPrecise)
		}
		else{
			if(isNaN(props.price)){
				return '--'
			}
			return formatNumber(props.price, props.precise, props.exactPrecise);
		}
	}
	else{
		return '--'
	}
})


</script>

<template>
	<text class="comp-price" :class="{ 'price-up':isPriceUp, 'price-down':isPriceDown }">
		<slot name="prefix"></slot>{{priceText}}<slot name="suffix"></slot>
	</text>
</template>

<style scoped lang="scss">
.price-up{
	color: $color-red;
}
.price-down{
	color: $color-green;
}
</style>