<script setup lang="ts">

import CompPopup from "@/components/popup/CompPopup.vue";
import type {PopupFormField, PopupInstance} from "@/types/popup";
import {defaultPopupExpose} from "@/library/PopupManager";
import type {UniFormsInstance, UniFormsRules} from "@/types/uni-ui";
import UpPickerData from "@/components/UpPickerData.vue";

const fields = ref<PopupFormField[]>([]);
const submitText = ref<string>();
const title = ref<string>()

const popup = ref<PopupInstance>();
defineExpose(defaultPopupExpose(popup, {
	open(_fields:PopupFormField[], defaultValue?:Record<string, any>, options?: { title?:string, submit?:string }){
		fields.value = _fields;
		if(defaultValue){
			Object.assign(formData, defaultValue);
		}
		submitText.value = options?.submit;
		title.value = options?.title;
	}
}));

const form = ref<UniFormsInstance>()
const formRules = computed<UniFormsRules>(()=>{
	const rules:UniFormsRules = {}
	
	for(let field of fields.value){
		if(field.rules){
			rules[field.name] = {
				label: field.label,
				rules: field.rules
			};
		}
	}
	
	return rules;
})
const formData = reactive<Record<string, any>>({});


async function submit(){
	await form.value?.validate();
	popup.value?.result(JSON.parse(JSON.stringify(formData)));
}

</script>

<template>
	<CompPopup ref="popup" :title="title">
		<uni-forms ref="form" class="popup-form" :rules="formRules" :model="formData" label-position="top" err-show-type="toast">
			<uni-forms-item v-for="field of fields" :name="field.name" :label="field.label">
				<up-input
					v-if="field.type === 'text' || field.type === 'password' || field.type === 'number'"
					:type="field.type"
					:disabled="field.disabled"
					:readonly="field.readonly"
					:clearable="field.clearable"
					:placeholder="field.placeholder"
					v-model="formData[field.name]"
				/>
				<u-textarea 
					v-else-if="field.type==='textarea'"
					:disabled="field.disabled"
					:readonly="field.readonly"
					:clearable="field.clearable"
					:placeholder="field.placeholder"
					v-model="formData[field.name]"
				/>
				<u-radio-group
					v-else-if="field.type==='radio'"
					v-model="formData[field.name]"
					placement="row"
					:disabled="field.disabled"
				>
					<u-radio
						v-for="item of field.options"
						:key="item.value"
						:label="item.label"
						:name="item.value"
					>
					</u-radio>
				</u-radio-group>
				<up-picker-data
					v-else-if="field.type==='select'"
					v-model="formData[field.name]"
					:title="field.placeholder"
					:options="field.options"
					valueKey="value"
					labelKey="label">
				</up-picker-data>
			</uni-forms-item>
		</uni-forms>
		<template #actions v-if="submitText">
			<up-button @click="submit" type="warning" shape="circle">{{submitText}}</up-button>
		</template>
	</CompPopup>
</template>

<style scoped lang="scss">
.popup-form{
	.u-input{
		background-color: $color-text-white;
	}
	
	.uni-forms-item.is-direction-top{
		margin-bottom: 10px;
		:deep(.uni-forms-item__label){
			@include fs(14);
			@include fw(medium);
			color: $color-text-black;
			padding-bottom: 0;
		}
	}
}
</style>