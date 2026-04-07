import type {LabelOptions} from "@/types/common";

export function dictLabel<T>(options:LabelOptions<T> | ComputedRef<LabelOptions<T>>, value: T|undefined){
	if('value' in options){
		options = options.value;
	}
	return options.find(option=>option.value === value)?.label
}

export function dictKey<T>(options:LabelOptions<any, {[key]: any}>| ComputedRef<LabelOptions<any, {[key]: any}>>, value: T|undefined, key: string){
	if('value' in options){
		options = options.value;
	}
	return options.find(option=>option.value === value)?.[key];
}