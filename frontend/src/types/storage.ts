export interface StorageTransformer<T = any> {
	setter?: (value:T|undefined)=>T|undefined, 
	getter?:(value:T|undefined)=>T|undefined
}

export const enum StorageKey{
	USER_TOKEN = 'access-token',
	USER_INFO = 'user-info',
	DICT_LIST = 'dict-list',
	CHAIN_LIST = 'chain-list',
	CONFIG_LANGUAGE = 'config-language',
	CONFIG_CURRENCY = 'config-currency',
	CONFIG_ASSET_HIDDEN = 'config-asset-hidden',
	CONFIG_COUNTRY_CODE = 'config-country-code',
	CONFIG_FONT_SCALE = 'config-font-scale',
	PROTOCOL_AGREE = 'protocol-agree',
	PROTOCOL_LIST = 'protocol-list',
}

export type StorageKeyLike = StorageKey | `${StorageKey}-${string|number}`;