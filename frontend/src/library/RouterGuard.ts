import UniRouter from "@/library/UniRouter";
import {URLSearchParams} from "@/library/UrlPolyfill";
import {accessToken, loginPage, mainPage} from "@/library/GlobalVars";

const whiteList = [
    "/pages/main/login",
    "/pages/main/register",
    "/pages/main/launch",
    "/pages/project/index",
    "/pages/order/index",
    "/pages/project/detail",
    "/pages/user/index",
    "/",
    "/pages/main/protocol-list",
    "/pages/main/protocol-detail",
    "/pages/main/about",
]

function hasUserPermission(path: string){
    if(whiteList.includes(path)){
        return true;
    }
    return processor.token.value;
}


const processor = {
    token: accessToken,
    redirectUrl:'',
    
    launched(e:App.LaunchShowOption){
        return processor.invoke('/' + e.path, {})
    },
    navigated(e:{url:string, query?: AnyObject}){
        return processor.invoke(e.url, e.query)
    },
    backed(e:{delta?: number}){
        const pages = getCurrentPages();
        const page = pages[pages.length - (e.delta??1) - 1];
        
        if('/'+pages[pages.length - 1]?.route === loginPage){
            UniRouter.redirect(mainPage)
            return false
        }
        
        if(page) {
            return processor.invoke('/' + page.route, page.options)
        }
    },
    
    invoke(url:string, query?:AnyObject) {
        url = url.replace(/\?$/, '');
        const path = url.replace(/\?.*$/, '');

        uni.stopPullDownRefresh();
        //uni.showTabBar();

        let redirect = url;
        let params = new URLSearchParams();
        if(query !== undefined) {
            for (let key in query) {
                params.append(key, query[key]);
            }
        }
        if (params.toString()) {
            redirect += '?' + params.toString();
        }
        processor.redirectUrl = redirect;
        
        if (!hasUserPermission(path)) {
            processor.redirectLogin()
            return false
        }
        
        return true
    },
    redirect(){
        if(processor.redirectUrl){
            processor.redirectLogin();
            processor.redirectUrl = '';
        }
    },
    redirectLogin(){
        UniRouter.redirect(loginPage,processor.redirectUrl?{
            redirect:processor.redirectUrl,
        }:{})
    }
}

uni.addInterceptor('navigateTo', {invoke(e){return processor.navigated(e)}});
uni.addInterceptor('redirectTo', {invoke(e){return processor.navigated(e)}})
uni.addInterceptor('switchTab', {invoke(e){return processor.navigated(e)}})
uni.addInterceptor('navigateBack', {invoke(e){return processor.backed(e)}})

export default processor;