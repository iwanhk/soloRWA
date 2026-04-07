import HttpRequest, {type HttpRequestConfig, type HttpResponse} from "luch-request";
import {ApiError} from "@/api/error";
import {API_ERROR_CODE, ApiMethod, type ApiResult, NwEvent} from "@/types/api";
import {WrapPromise} from "@/library/WrapPromise";
import {apiHttp} from "@/api/service";
import {accessToken} from "@/library/GlobalVars";
import {ApiUploadFile} from "@/api/file";

export const enum ApiCustomKey{
    showLoading= 'showLoading',
    encryptionKey = 'encryptionKey'
}

const overlayPromises: Record<string, WrapPromise> = {};

export function NoneTokenApi(constructor: Function){
    Object.defineProperty(constructor.prototype, 'withToken', {
        get() {
            return false;
        }
    })
}
export function OptionalTokenApi(constructor: Function){
    Object.defineProperty(constructor.prototype, 'withToken', {
        get() {
            return "optional";
        }
    })
}

export class Api<TResult = ApiResult> {
    protected readonly params: AnyObject | undefined;
    protected readonly method: ApiMethod;
    protected readonly url: string;
    protected readonly custom: {
        showLoading? : UniNamespace.ShowLoadingOptions,
        encryptionKey? : {
            privateKey:string,
            publicKey:string
        }
    };
    protected encryptionKey?:{
        privateKey:string,
        publicKey:string
    }
    
    constructor(url:string, method: ApiMethod, params?: AnyObject) {
        this.url = url;
        this.params = params;
        this.method = method;
        this.custom = {};
    }

    get apiSource(): "api" {
        return "api";
    }

    get withToken():boolean | 'optional'{
        return true;
    }

    get preventOverlay() {
        return false;
    }
    
    setEncryptionKey(encryptionKey?:{
        privateKey:string,
        publicKey:string
    }){
        this.custom[ApiCustomKey.encryptionKey] = encryptionKey;
    }

    async call() : Promise<TResult>{
        try {
            if (this.preventOverlay) {
                if (this.constructor.name in overlayPromises) {
                    console.log('Prevent Api call overlay', this.constructor.name)
                    return overlayPromises[this.constructor.name];
                }
                overlayPromises[this.constructor.name] = new WrapPromise<any>();
            }

            const requestConfig: HttpRequestConfig<UniApp.RequestTask | UniApp.UploadTask> = {};

            requestConfig.header = {
                locale: uni.getLocale(),
            }
            
            if (this.withToken) {
                if(accessToken.value) {
                    requestConfig.header['Authorization'] = `Bearer ${accessToken.value}`
                }
                else if(!accessToken.value && this.withToken !== "optional"){
                    uni.$emit(NwEvent.TOKEN_EXPIRED);
                    throw new Error(`Api[${this.url}] need token`);
                }
            }
            
            let http: HttpRequest = apiHttp;

            let response: Promise<HttpResponse<ApiResult>>;

            const url = this.url.replace(/\{[^}]+}/g, (matched) => {
                let property = matched.substring(1, matched.length - 1);
                if (this.params) {
                    if (this.params.hasOwnProperty(property)) {
                        const replacement = this.params[property];
                        delete this.params[property];
                        return replacement;
                    } else {
                        throw new Error(`required param ${property} not provided`);
                    }
                }
            })
            switch (this.method) {
                case ApiMethod.GET:
                    response = http.get(url, {...requestConfig, custom: this.custom, params: this.params});
                    break;
                case ApiMethod.POST:
                    response = http.post(url, this.params, {...requestConfig, custom: this.custom});
                    break; 
                case ApiMethod.PUT:
                    response = http.put(url, this.params, {...requestConfig, custom: this.custom});
                    break;
                case ApiMethod.DELETE:
                    response = http.delete(url, this.params, {...requestConfig, custom: this.custom});
                    break;
                case ApiMethod.UPLOAD:
                    const uploadConfig: HttpRequestConfig<UniApp.UploadTask> = {
                        files: [],
                        formData: {},
                    };
                    let hasFile = false;
                    for(let key in this.params){
                        let isFile = false;
                        if(typeof this.params[key] === 'object'){
                            if(Array.isArray(this.params[key]) && this.params[key].length){
                                if(this.params[key][0] instanceof ApiUploadFile){
                                    isFile = true;
                                    for(let uploadFile of this.params[key]) {
                                        let file: ApiUploadFile = uploadFile;
                                        uploadConfig.files!.push({
                                            name: key,
                                            file: file.file,
                                            uri: file.url,
                                        })
                                    }
                                }
                            }
                            else if(this.params[key] instanceof ApiUploadFile){
                                let file = this.params[key] as ApiUploadFile;
                                isFile = true;
                                uploadConfig.files!.push({
                                    name: key,
                                    file: file.file,
                                    uri: file.url,
                                })
                            }
                        }
                        if(!isFile){
                            uploadConfig.formData![key] = this.params[key];
                        }
                        else{
                            hasFile = true;
                        }
                    }
                    
                    if(hasFile) {
                        response = http.upload(this.url, {
                            ...(requestConfig as HttpRequestConfig<UniApp.UploadTask>), custom: this.custom,
                            ...uploadConfig
                        });
                    }
                    else{
                        response = http.post(url, this.params, {...requestConfig, custom: this.custom});
                    }
                    break;
                default:
                    throw new Error(`Api Method Not Implemented:${this.method}`);
            }

            let requestResult = await response as unknown as TResult;
            requestResult = this.processResponse(requestResult);
            
            if (this.preventOverlay && this.constructor.name in overlayPromises) {
                overlayPromises[this.constructor.name]?.resolve(requestResult);
                delete overlayPromises[this.constructor.name];
            }
            return requestResult as TResult;
        }
        catch (e){
            if (this.preventOverlay && this.constructor.name in overlayPromises) {
                overlayPromises[this.constructor.name]?.reject(e);
                delete overlayPromises[this.constructor.name];
            }
            throw e;
        }
    }
    
    protected processResponse(requestResult: TResult){
        let requestTypedResult: ApiResult = requestResult as unknown as ApiResult;
        
        if (requestTypedResult.code !== API_ERROR_CODE.OK) {
            if (requestTypedResult.code === API_ERROR_CODE.TOKEN_EXPIRED) {
                uni.$emit(NwEvent.TOKEN_EXPIRED);
            }

            throw new ApiError(requestTypedResult.code, requestTypedResult.msg, requestTypedResult.data);
        }
        
        return requestResult;
    }

    setLoadingTitle(title:string):Api<TResult>{
        if(title) {
            this.custom[ApiCustomKey.showLoading] = {
                title,
            };
        }
        else{
            delete this.custom[ApiCustomKey.showLoading];
        }
        return this;
    }
}



