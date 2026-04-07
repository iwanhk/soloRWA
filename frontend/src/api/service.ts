import Request, {type HttpRequestConfig, type HttpResponse} from "luch-request";
import ENV from "@/library/env";
import {ApiCustomKey} from "./index";

const serviceFactory = (baseURL:string)=>{
	const request = new Request()
	request.setConfig((config) => {
		config.baseURL = baseURL
		return config
	})

	request.interceptors.request.use((config:HttpRequestConfig) => {
		/* 请求之前拦截器。可以使用async await 做异步操作 */
		let showLoading = config.custom?.[ApiCustomKey.showLoading];
		if (showLoading) {
			uni.showLoading(showLoading)
		}
		if(!config.header){
			config.header = {}
		}
		
		return config
	}, (config) => {
		return Promise.reject(config)
	})

	request.interceptors.response.use(async (response: HttpResponse) => {
		if (response.config.custom?.showLoading) {
			uni.hideLoading()
		}

		return response.data;
	}, async (error) => {
		if (error.config.custom?.showLoading) {
			uni.hideLoading()
		}
		
		if (error.statusCode !== 200){
			console.log('apiError', error);
			throw new Error(`Api Error:Status code ${error.statusCode}`)
		}
		else {
			if (error.config) {
				throw new Error(`Api Error:${error.data?.message ?? error.errMsg}`);
			} else {
				throw new Error(`Api Error:${(error as unknown as Error).message}`)
			}
		}
	})

	return request;
}

export const apiHttp = serviceFactory(ENV.apiUrl);