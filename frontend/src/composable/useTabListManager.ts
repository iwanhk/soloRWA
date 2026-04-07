import {EmptyListHandler, type ListHandler} from "@/library/PagedListHandler.ts";
import {useUserStore} from "@/store/user.ts";
import {onPullDownRefresh, onReachBottom} from "@dcloudio/uni-app";

export function useTabListManager<ItemType>({ bottom = true , pull = true}: { bottom?: boolean|Function, pull?: boolean | Function } = { bottom: true, pull:true}){
	const tab = ref('empty');
	const lists = reactive<Record<string, ListHandler<ItemType>>>({
		'empty': EmptyListHandler,
	});
	
	const watchingUserList:Record<string, boolean> = {
		empty: true,
	};
	let provide:undefined | ((identifier:string)=>ListHandler | Promise<ListHandler>) = undefined;
	watch(() => useUserStore().user, (newValue) => {
		if (newValue) {
			for (let identifier in watchingUserList) {
				const list = lists[identifier];
				if (list && list.records === undefined) {
					list.next();
				}
			}
		}
	})
	
	function addList(identifier:string, list:ListHandler, watchUser = false){
		lists[identifier] = list;
		watchingUserList[identifier] = watchUser;
	}
	
	function removeList(identifier?: string){
		if(identifier === undefined){
			for(let key in lists){
				if(key !== 'empty'){
					delete lists[key];
				}
			}
			tab.value = 'empty'
		}
		else{
			delete lists[identifier];
			if(identifier === tab.value){
				tab.value = 'empty'
			}
		}
	}
	
	function getList(identifier?: string) {
		if (identifier === undefined) {
			identifier = tab.value;
		}

		const watching = watchingUserList[identifier];
		if (watching && !useUserStore().user) {
			return lists['empty'];
		}

		if (lists[identifier] === undefined) {
			return lists['empty'];
		}

		return lists[identifier];
	}

	async function showList(identifier?:string){
		let targetTab = tab.value;

		if(identifier !== undefined){
			targetTab = identifier;
		}
		tab.value = targetTab;
		if(!(targetTab in lists)){
			if(provide){
				lists[targetTab] = await provide(targetTab);
			}
			else{
				throw new Error(`Tab [${targetTab}] not registered`);
			}
		}
		if(lists[targetTab] === undefined){
			throw new Error(`identifier ${targetTab} not provided in TabListManager.showList`)
		}

		const watching = watchingUserList[targetTab];
		if (watching && !useUserStore().user) {
			return false;
		}

		if(lists[targetTab]?.records === undefined){
			await lists[targetTab]?.next();
		}
	}
	
	async function refreshList(identifier?:string){
        let listHandler = getList(identifier);

        listHandler?.reset();
        return listHandler?.next();
    }

	async function nextList(identifier?: string){
		let listHandler = getList(identifier);
		return listHandler?.next();
	}

	async function resetList(identifier?: string){
		let listHandler = getList(identifier);
		return listHandler?.reset();
	}

	function setParams(identifier: string| undefined, params:any){
		let listHandler = getList(identifier);
		return listHandler?.setParams(params);
	}

	function getParams(identifier?: string){
		let listHandler = getList(identifier);
		return listHandler?.params;
	}

	function setProvide(handlerFactory: (identifier: string) => ListHandler, watchUser = false){
		provide = (identifier)=> {
			watchingUserList[identifier] = watchUser;
			return handlerFactory(identifier);
		}
	}

	const list:ComputedRef<ListHandler<ItemType> | undefined> = computed(()=>{
		return getList(tab.value);
	});
	
	const status = computed(()=>{
		if(list.value === undefined)return 'loading';
		if(list.value.requesting){
			return 'loading';
		}

		switch (list.value.finished) {
			case true:
				return 'nomore';
			case false:
				return list.value.requesting?'loading':'loadmore';
			case undefined:
				return 'loadmore';
		}
	})
	const records = computed<ItemType[] | undefined>(()=>{
		return list.value?.records
	})
	
	if(bottom) {
		onReachBottom(async ()=>{
			if(typeof bottom === "function"){
				await bottom();
			}
			await nextList();
		});
	}
	if(pull) {
		onPullDownRefresh(async () => {
			try{
				if (typeof pull === "function") {
					await pull();
				}
				await refreshList()
			}
			finally {
				uni.stopPullDownRefresh()
			}
		});
	}

	const isEmpty = computed(()=>{
		return status.value === 'nomore' && (!list.value || list.value.records?.length === 0);
	})
	async function forEach(callback:(list:ListHandler<ItemType>, identifier:string)=>ItemType|Promise<ItemType>){
		const result:Array<ItemType> = [];
		for(let identifier in lists){
			if(lists[identifier]) {
				result.push(await callback(lists[identifier], identifier));
			}
		}
		return result;
	}


	return {
		tab,
		lists,
		add:addList,
		remove:removeList,
		
		get:getList,
		show:showList,
		refresh:refreshList,
		next:nextList,
		reset:resetList,
		setProvide,
		setParams,
		getParams,

		isEmpty,
		forEach,
		records,
		status,
	}
}