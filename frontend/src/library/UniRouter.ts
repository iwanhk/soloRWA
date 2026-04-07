import {pages, tabs} from "./UniPages";
import {RouterPageMethod, RouterPageType} from "@/types/router";
import ENV from "@/library/env";
import {URL} from "@/library/UrlPolyfill"

type UniRouteMethodOption<T extends RouterPageMethod> =
	T extends RouterPageMethod.REDIRECT ? Parameters<typeof uni[RouterPageMethod.REDIRECT]>[0] :
	T extends RouterPageMethod.RELAUNCH ? Parameters<typeof uni[RouterPageMethod.RELAUNCH]>[0] :
	T extends RouterPageMethod.TO ? Parameters<typeof uni[RouterPageMethod.TO]>[0] :
	T extends RouterPageMethod.TAB ? Parameters<typeof uni[RouterPageMethod.TAB]>[0] :
	never;

export default class UniRouter{
	static getPageType(url:string){
		const parsedUrl = new URL(ENV.baseURL + url);
		const path = parsedUrl.pathname
		let type:RouterPageType|undefined = undefined;
		if (tabs && tabs.find(tab => '/'+tab.pagePath === path)) {
			type = RouterPageType.TAB;
		} else if (pages.find(page => page.path === path)) {
			type = RouterPageType.PAGE;
		} else {
			throw new Error(`page[${path}] not found`);
		}
		return type;
	}

	static uniRoute<T extends RouterPageMethod>(method: RouterPageMethod, url: string, params: Record<string, any> | undefined, options?: Partial<UniRouteMethodOption<T>>) {
		switch (this.getPageType(url)) {
			case RouterPageType.TAB:
				return uni.switchTab(Object.assign({url}, options))
			case RouterPageType.PAGE:
				url = this.url(url,params);
				switch (method){
					case RouterPageMethod.REDIRECT:
						return uni.redirectTo(Object.assign({url}, options));
					case RouterPageMethod.RELAUNCH:
						return uni.reLaunch(Object.assign({url}, options));
					case RouterPageMethod.TO:
						return uni.navigateTo(Object.assign({url}, options));
					case RouterPageMethod.TAB:
						return uni.switchTab(Object.assign({url}, options));
				}

		}
	}

	static url(url: string, params: Record<string, any> | undefined = {}) {
		const parsedUrl = new URL(ENV.baseURL+url);
		if (params) {
			for (let key in params) {
				parsedUrl.searchParams.append(key, params[key]);
			}
		}
		return parsedUrl.toString().substring(ENV.baseURL.length);
	}

	static to(url:string, params: Record<string, any> | undefined = {}, options?: UniRouteMethodOption<RouterPageMethod.TO>){
		return this.uniRoute(RouterPageMethod.TO, url, params, options)
	}

	static redirect(url:string, params: Record<string, any> | undefined = {}, options?: UniRouteMethodOption<RouterPageMethod.REDIRECT>) {
		return this.uniRoute(RouterPageMethod.REDIRECT, url, params, options)
	}

	static relaunch(url: string, params: Record<string, any> | undefined = {}, options?: UniRouteMethodOption<RouterPageMethod.RELAUNCH>){
		return this.uniRoute(RouterPageMethod.RELAUNCH, url, params, options)
	}

	static current(){
		const pages = getCurrentPages().slice();
		if(pages.length === 0){
			return this.url('/pages/main/index');
		}
		else {
			const page = pages.pop()!;
			let path:string = '';
			let options: any;
			if(page.$vm.$page){
				path = page.$vm.$page.path;
				options = page.$vm.$page.options;
			}
			else if('$page' in page){
				path = (page.$page as { fullPath:string }).fullPath;
				options = {};
			}
			
			return this.url(path, options);
		}
	}
	
	static redirectLogin(){
		return this.to('/pages/password/login', { redirect: this.current() });
	}
	
	static back() {
		return uni.navigateBack({
			delta: 1,
		})
	}
}
