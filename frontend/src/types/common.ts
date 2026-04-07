import {DictKey} from "@/types/enums";

export type LabelOption<T = number, U = {}> = {
	label: string,
	value: T,
} & U

export type LabelOptions<T = number, U = {}> = LabelOption<T, U>[];

export type VersionInfo = {
	name: string,
	code: number,
	description?: string,
}

export type ExactArray<T extends readonly DictKey[], U extends DictKey> =
	Exclude<U, T[number]> extends never
		? Exclude<T[number], U> extends never ? T : never
		: never;

export type StringDictKey =
	| DictKey.PAY_TYPE

export type DictValue<Key extends DictKey> = (Key extends StringDictKey?string:number) & Record<symbol, Key>;

export const enum BooleanNumber{
	FALSE = 0,
	TRUE = 1
}
export const enum ReverseBooleanNumber{
	TRUE = 0,
	FALSE = 1
}