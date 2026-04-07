import {defineStore} from "pinia";
import type {ChainEntity, DictItemEntity, SimpleProtocolEntity} from "@/types/entity";
import {ApiGetDictList} from "@/api/generic/ApiGetDictList";
import {ApiGetChainList} from "@/api/generic/ApiGetChainList";
import UniStorage from "@/library/UniStorage";
import {StorageKey} from "@/types/storage";
import {type DictKey} from "@/types/enums";
import {stringDictType} from "@/library/GlobalVars";
import type {DictValue, LabelOptions, StringDictKey} from "@/types/common";
import {ApiGetAgreementList} from "@/api/user/ApiGetAgreementList.ts";

export const useDictStore = defineStore('dict', () => {
	const dictList = UniStorage.register<Record<DictKey, DictItemEntity[]>>(StorageKey.DICT_LIST);
	const chains = UniStorage.register<ChainEntity[]>(StorageKey.CHAIN_LIST);

	const protocols = UniStorage.register<SimpleProtocolEntity[]>(StorageKey.PROTOCOL_LIST);
	
	/**
	 * 加载字典列表
	 */
	async function loadDictList() {
		try {
			const api = new ApiGetDictList();
			const {data} = await api.call();
			let dict: Partial<Record<DictKey, DictItemEntity[]>> = {};
			for(let entry of data){
				dict[entry.type] = entry.dataList;
			}
			dictList.value = dict as Record<DictKey, DictItemEntity[]>;
		} catch (error) {
			console.error('加载字典列表失败:', error);
			throw error;
		}
	}

	/**
	 * 加载链列表
	 */
	async function loadChainList() {
		try {
			const api = new ApiGetChainList();
			const {data} = await api.call();
			chains.value = data;
		} catch (error) {
			console.error('加载链列表失败:', error);
			throw error;
		}
	}
	
	async function loadProtocols(){
		try{
			const api = new ApiGetAgreementList();
			const {data} = await api.call();
			protocols.value = data;
		}
		catch (e){
			console.error('加载协议列表失败:', e);
			throw e;
		}
	}

	type DictKeyCache = {
		[K in DictKey]: ComputedRef<LabelOptions<DictValue<K>>>;
	}
	
	const computedRepo: Partial<DictKeyCache> = {}
	
	function dictOptions<Key extends DictKey>(key: Key): LabelOptions<DictValue<Key>>{
		if(stringDictType.includes(key as StringDictKey)){
			return dictList.value?.[key].map(item => ({
				label: item.label,
				value: item.value as DictValue<Key>,
			})) ?? [];
		}
		else{
			return dictList.value?.[key].map(item => ({
				label: item.label,
				value: parseInt(item.value) as DictValue<Key>,
			})) ?? [];
		}
	}

	function computedDictOptions<Key extends DictKey> (key:Key){
		if(!computedRepo[key]){
			//@ts-ignore
			computedRepo[key] = computed(() => dictOptions(key))
		}
		return computedRepo[key]!;
	}
	
	loadDictList();
	loadChainList();
	loadProtocols();

	return {
		computedDictOptions,
		chains,
		protocols,
	};
});

