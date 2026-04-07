import UniStorage from "@/library/UniStorage";
import {StorageKey} from "@/types/storage";
import {DictKey} from "@/types/enums";
import type {ExactArray, StringDictKey} from "@/types/common";

export const mainPage = '/pages/project/index'
export const loginPage = '/pages/main/login'
export const accessToken = UniStorage.register<string>(StorageKey.USER_TOKEN);

export const $themeColor = "#CA7B44"
export const $bgColor = '#1B1B1D'

const defineExactStringDictKeyArray = 
	<U extends DictKey>() => <T extends readonly U[]>(arr: ExactArray<T, U>) => arr

export const stringDictType = defineExactStringDictKeyArray<StringDictKey>()([
	DictKey.PAY_TYPE,
] as const)

export const isTabbarPage = ref(false);