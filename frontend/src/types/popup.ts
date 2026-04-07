import type {UniFormsRule} from "@/types/uni-ui";
import type {LabelOptions} from "@/types/common";

export interface PopupInstance<RETURN = any, DATA extends Array<any> = any[]>{
	open(...args:DATA):Promise<RETURN|undefined>,
	close():void,
	result(value: any, autoClose?:boolean): void,
}

interface PopupFieldBase{
	name: string;
	label: string;
	rules?: UniFormsRule[];
	clearable?: boolean,
	placeholder?: string,
	readonly?:boolean,
	disabled?:boolean,
}

interface PopupFieldText extends PopupFieldBase{
	type: 'text'
}
interface PopupFieldPassword extends PopupFieldBase{
	type: 'password'
}
interface PopupFieldNumber extends PopupFieldBase{
	type: 'number'
}
interface PopupFieldTextarea extends PopupFieldBase{
	type: 'textarea'
}
interface PopupFieldSelect extends PopupFieldBase{
	type: 'select'
	options: LabelOptions<any, {disabled?:boolean}>
}
interface PopupFieldRadio extends PopupFieldBase{
	type: 'radio'
	options: LabelOptions<any, {disabled?:boolean}>
}


export type PopupFormField =
	| PopupFieldText
	| PopupFieldPassword
	| PopupFieldNumber
	| PopupFieldTextarea
	| PopupFieldSelect
	| PopupFieldRadio
