import {Api} from "@/api";
import type {ApiListResult, ApiResult} from "@/types/api";

export enum ResponseFieldKey{
    SUCCESS="success",
    RECORDS="records",
    PAGE="page",
    TOTAL="total",
    TOTAL_PAGE="totalPage",
}

export enum RequestFieldKey{
    PAGE = "page",
    PAGE_SIZE = "pageSize",
}

type ResponseFieldMapRecord<U> = string | ((response:any)=> U);

export type ResponseFieldMap<ItemType> = {
    [key in ResponseFieldKey]:
        key extends ResponseFieldKey.PAGE ? ResponseFieldMapRecord<number>:
        key extends ResponseFieldKey.SUCCESS? ResponseFieldMapRecord<number>:
        key extends ResponseFieldKey.RECORDS? ResponseFieldMapRecord<ItemType[]>:
        key extends ResponseFieldKey.TOTAL ? ResponseFieldMapRecord<number> :
        key extends ResponseFieldKey.TOTAL_PAGE ? ResponseFieldMapRecord<number> :
           never
}

export type RequestFieldMap = {
    [RequestFieldKey.PAGE]:string,
    [RequestFieldKey.PAGE_SIZE]:string,
}

class PagedListHandler<ItemType = any, ApiParams extends Object = Object>{
    private readonly api: (params: ApiParams) => any;
    private readonly responseFieldMap: ResponseFieldMap<ItemType>;
    private readonly requestFieldMap: RequestFieldMap;
    public readonly state: {
        requesting: boolean | undefined;
        finished: undefined | boolean;
        page: number;
        pagedRecords: Array<ItemType[]>;
        params: ApiParams
        total: number|undefined,
    };
    private requestIndex: number;
    constructor(api: (params: ApiParams) => any, inputParams?: ApiParams) {
        this.api = api;
        this.responseFieldMap = {
            [ResponseFieldKey.SUCCESS]: (response: { code: number }) => response.code,
            [ResponseFieldKey.RECORDS]: 'data.dataList',
            [ResponseFieldKey.PAGE]: 'data.pageNo',
            [ResponseFieldKey.TOTAL]: 'data.totalCount',
            [ResponseFieldKey.TOTAL_PAGE]: 'data.totalPageCount',
        }
        this.requestFieldMap = {
            [RequestFieldKey.PAGE]: 'pageNo',
            [RequestFieldKey.PAGE_SIZE]: 'pageSize',
        }
        this.requestIndex = 0;

        this.state = reactive<{
            requesting: boolean | undefined;
            finished: undefined | boolean;
            page: number;
            pagedRecords: Array<any>;
            params: any,
            total: number | undefined,
        }>({
            page:0,
            requesting:undefined,
            finished:undefined,
            pagedRecords:[],
            params: inputParams,
            total: undefined,
        });

    }

    setRequestFieldMap(requestFieldMap: Partial<RequestFieldMap>){
        Object.assign(this.requestFieldMap, requestFieldMap);
    }

    setResponseFieldMap(responseFieldMap: Partial<ResponseFieldMap<ItemType>>){
        Object.assign(this.responseFieldMap, responseFieldMap);
    }

    async next(targetPage?: number){
        try {
            const isUpdateSpecificPage = targetPage !== undefined;
            const updatingPage = isUpdateSpecificPage ? targetPage : this.state.page + 1;

            if (this.state.finished && !isUpdateSpecificPage) return false;

            const requestParams: ApiParams = Object.assign({}, this.state.params, {
                [this.requestFieldMap[ResponseFieldKey.PAGE]]: updatingPage
            }) as ApiParams;

            if (this.state.requesting) return false;
            this.state.requesting = true;

            const requestIndex = ++this.requestIndex;

            let response;
            try {
                response = await this.api(requestParams);
            } catch (e) {
                console.error(e);
                return false;
            } finally {
                if (requestIndex === this.requestIndex) {
                    this.state.requesting = false;
                }
            }

            if (requestIndex !== this.requestIndex) {
                console.warn(`PageListHandler Skip RequestIndex for [${requestIndex}:${this.requestIndex}]`);
                return false;
            }

            const success = this.getField<number>(response, ResponseFieldKey.SUCCESS);
            if (success !== 0) {
                return false;
            }

            const responseRecords = this.getField<ItemType[]>(response, ResponseFieldKey.RECORDS);
            let responsePage = this.getField<number>(response, ResponseFieldKey.PAGE);

            if (!responseRecords) {
                if(!isUpdateSpecificPage) {
                    this.state.finished = true;
                }
                return false;
            }

            if (responsePage === undefined || this.state.pagedRecords.length < responsePage) {
                this.state.pagedRecords.push(responseRecords);
                if (!isUpdateSpecificPage) {
                    if (responsePage !== undefined) {
                        this.state.page = responsePage;
                    } else {
                        this.state.page += 1;
                    }
                }
            } else {
                this.state.pagedRecords[responsePage] = responseRecords;
                if (!isUpdateSpecificPage) {
                    this.state.page = responsePage
                }
            }

            const responseTotal = this.getField<number>(response, ResponseFieldKey.TOTAL);
            if (responseTotal !== undefined) {
                this.state.total = responseTotal;
                if (responseTotal <= this.state.pagedRecords.reduce((acc, page) => acc + page.length, 0)) {
                    this.state.finished = true;
                }
            }

            const responseTotalPage = this.getField<number>(response, ResponseFieldKey.TOTAL_PAGE);
            if (responseTotalPage !== undefined) {
                if (responseTotalPage <= this.state.page) {
                    this.state.finished = true;
                }
            }

            if (this.state.finished === undefined) {
                this.state.finished = false;
            }

            return true;
        }
        catch (e){
            return false;
        }
    }

    private getField<T>(response:any, key:ResponseFieldKey):undefined|T{
        if(!response || typeof response !== 'object')return undefined;
        try {
            if (typeof this.responseFieldMap[key] === "string") {
                const parts = (this.responseFieldMap[key] as string).split('.');
                let target = response;
                for (let part of parts) {
                    target = target[part];
                }
                return target as T;
            }
            else{
                return (this.responseFieldMap[key] as (response:any)=>T)(response);
            }
        }
        catch (e){
            return undefined;
        }
    }

    async updateItem (item: ItemType){
        const targetIndex = this.state.pagedRecords.findIndex(page => page.includes(item));
        if (targetIndex === -1) {
            return false;
        }
        return await this.next(targetIndex + 1);
    }

    setParams(params: ApiParams){
        this.state.params = params;
        this.reset();
    }

    reset(){
        this.state.finished = undefined;
        this.state.page = 0;
        this.state.requesting = undefined;
        this.state.pagedRecords = [];
        this.requestIndex += 1;
    }
}

export function PagedListHandlerFactory<ItemType = any, ApiParams extends Object = Object>(
    api: (params: ApiParams) => any,
    inputParams?: ApiParams,
    inputResponseFieldMap?: Partial<ResponseFieldMap<ItemType>>,
    inputRequestFieldMap?: Partial<RequestFieldMap>,
): ListHandler<ItemType> {
    const handler = new PagedListHandler<ItemType, ApiParams>(api, inputParams);

    if(inputResponseFieldMap !== undefined){
        handler.setResponseFieldMap(inputResponseFieldMap);
    }
    if(inputRequestFieldMap){
        handler.setRequestFieldMap(inputRequestFieldMap);
    }

    const next = (targetPage?:number)=>{
        return handler.next(targetPage);
    }

    const updateItem = async (item:ItemType)=>{
        return handler.updateItem(item);
    }

    const reset = ()=>{
        return handler.reset()
    }

    const setParams = (params:any)=>{
        handler.setParams(params);
    }

    return reactive({
        page: computed(()=> handler.state.page),
        records: computed<ItemType[] | undefined>(() => {
            if (handler.state.requesting === undefined) return undefined;
            return handler.state.pagedRecords.flat();
        }),
        total: computed(()=>handler.state.total),
        requesting: computed(() => handler.state.requesting),
        finished: computed(() => handler.state.finished),
        params: computed(() => handler.state.params),

        next,
        reset,
        updateItem,
        setParams,
    });
}

export type ListHandler<ItemType = any, ApiParams = any> = {
    requesting: boolean | undefined;
    finished: undefined | boolean;
    page: number;
    records: ItemType[] | undefined;
    total: number|undefined,
    params: ApiParams

    next:(targetPage?: number)=>Promise<boolean>,
    updateItem:(item: ItemType)=> Promise<boolean>,
    setParams:(params:any)=>void,
    reset:()=>void
};

export const EmptyListHandler = PagedListHandlerFactory<any,any>(
    ()=>({ code:0, page: 1, totalPage:1  })
);

type ListResultItemType<ApiType extends {
    new(...args: any[]): Api<ApiListResult<any>>;
}> = ApiType extends {
    new(...args: any[]): Api<ApiListResult<infer ItemType>>;
}? ItemType:never;

type NoneListResultItemType<ApiType extends {
    new(...args: any[]): Api<ApiResult<any[]>>;
}> = ApiType extends {
    new(...args: any[]): Api<ApiResult<Array<infer ItemType>>>;
} ? ItemType : never;

export function ApiPagedListHandlerFactory<ApiType extends {
    new (...args:any[]):Api<ApiListResult<any>>;
}>(
        api:ApiType,
        params?: ConstructorParameters<ApiType>[0],
        inputResponseFieldMap: Partial<ResponseFieldMap<any>> = {
            records: 'data.list',
            total: 'data.total'
        },
        inputRequestFieldMap?: Partial<RequestFieldMap>,
):ListHandler<ListResultItemType<ApiType>>{
    if(!params) params = {};
    if(!('pageNo' in (params as unknown as object))){
        params.pageNo = 1;
    }
    if(!('pageSize' in (params as unknown as object))){
        params.pageSize = 20;
    }

    return PagedListHandlerFactory<ListResultItemType<ApiType>, ConstructorParameters<ApiType>[0]>(
        (params)=> new api(params).call(),
        params,
        inputResponseFieldMap,
        inputRequestFieldMap,
    );
}

export function ApiNonPagedListHandlerFactory<ApiType extends {
    new(...args: any[]): Api<ApiResult<any[]>>;
}>(
    api: ApiType,
    params?: ConstructorParameters<ApiType>[0]
): ListHandler<NoneListResultItemType<ApiType>> {
    if (!params) params = {};
    if (!('pageNo' in (params as unknown as object))) {
        params.pageNo = 1;
    }
    if (!('pageSize' in (params as unknown as object))) {
        params.pageSize = 20;
    }

    return PagedListHandlerFactory<NoneListResultItemType<ApiType>, ConstructorParameters<ApiType>[0]>(
        (params) => new api(params).call(),
        params,
        {
            records: 'data',
            page: () => 1,
            totalPage: () => 1,
        }
    );
}
