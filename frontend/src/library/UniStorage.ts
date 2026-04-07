import {StorageKey, type StorageKeyLike, type StorageTransformer} from "@/types/storage";
import {bigIntJSONParse, bigIntJSONStringify} from "@/library/BigIntJson";
import type {WritableComputedRef} from "@vue/reactivity";

const storagePrefix=''

let cache = reactive<Record<string, any>>({});
let loaded = reactive<Record<string, boolean>>({});
const transformMap: Partial<Record<StorageKey|StorageKeyLike, StorageTransformer>> = {};

class UniStorage{
	get<T = any>(key: StorageKey|StorageKeyLike){
		if(!(key in loaded)){
			if(!this.has(key)){
				return undefined;
			}
			else {
				cache[key] = bigIntJSONParse(uni.getStorageSync(storagePrefix + key));
				if(transformMap[key]?.getter){
					cache[key] = transformMap[key].getter(cache[key]);
				}
				loaded[key] = true;
			}
		}
		return cache[key] as T | undefined;
	}
	has(key: StorageKey|StorageKeyLike){
		const storageInfo = uni.getStorageInfoSync();
		return storageInfo.keys.includes(storagePrefix + key);
	}
	set(key:StorageKey|StorageKeyLike, value:any){
		let storageValue = value;
		if(transformMap[key]?.setter){
			storageValue = transformMap[key]?.setter(value);
		}
		uni.setStorageSync(storagePrefix + key, bigIntJSONStringify(storageValue));
		cache[key] = value;
		loaded[key] = true;
	}
	delete(key: StorageKey|StorageKeyLike){
		uni.removeStorageSync(storagePrefix + key);
		delete cache[key];
		delete loaded[key];
	}
	register<T = any, R = T>(key:StorageKey|StorageKeyLike, options?:{ setter?: (value:T|undefined)=>R|undefined, getter?:(value:R|undefined)=>T|undefined }): WritableComputedRef<T|undefined>{
		if(options){
			transformMap[key] = options;
		}
		this.get<T>(key);

		return computed<T | undefined>({
			get: () => {
				return cache[key];
			},
			set: (value) => {
				this.set(key, value);
			}
		});
	}
}

export default new UniStorage();