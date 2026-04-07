import type {PopupInstance} from "@/types/popup";

export function defaultPopupExpose<T,U extends any[]>(ref: Ref<PopupInstance<T,U> | undefined | null>, options?:{
    open?: (...args: any[])=>any,
    close?: (...args: any[])=>any,
    result?: (value: any, autoClose?:boolean)=>any
},defaultOptions?:{
    open?: (...args: any[])=>Promise<any>,
    close?: (...args: any[])=>any,
    result?: (value: any, autoClose?:boolean)=>any
}){
    if(!defaultOptions){
        defaultOptions = {};
    }
    if(!defaultOptions.open){
        defaultOptions.open = async (...args: U)=>{
            await options?.open?.(...args);
            return ref.value?.open(...args)
        }
    }
    if(!defaultOptions.close){
        defaultOptions.close = (...args: U)=>{
            options?.close?.(...args);
            return ref.value?.close()
        }
    }
    if(!defaultOptions.result){
        defaultOptions.result = (value: any, autoClose = true) => {
            options?.result?.(value, autoClose);
            return ref.value?.result(value, autoClose)
        }
    }

    return Object.assign({}, defaultOptions);
}