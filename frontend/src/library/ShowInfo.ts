import type {LabelOptions} from "@/types/common";
import i18n from "@/i18n";

let LoadingStack:Array<{ promise: Promise<any>, text?: string }> = [];
let LoadingInstance:undefined| true = undefined;

const parseMessage = (e: any)=>{
	if(!e){
		return false
	}
	else if(typeof e === "string"){
		return e;
	}
	else if (e instanceof Error) {
		return e.message
	} else {
		if ("msg" in e) {
			return e.msg;
		} else if ("message" in e) {
			return e.message;
		}
		return `${i18n.global.t('common.message.unknown')}:${JSON.stringify(e)}`
	}
}


export class ShowInfo{
	static toast(e:any, type?: "success" | "error" | "none" | "loading", options?: Parameters<(typeof uni)["showToast"]>[0]){
		const message = parseMessage(e);
		if(!message)return;

		if(type === undefined){
			type = 'none';
		}

		return uni.showToast(Object.assign({
			icon: type,
			title: message,
		}, options));
	}
	
	static toastSuccess(message:any, withIcon?:boolean){
		message = parseMessage(message);

		this.toast(message + i18n.global.t('common.message.success'), withIcon?'success':'none');
	}
	static toastFail(message: any, withIcon?:boolean){
		message = parseMessage(message);

		this.toast(message + i18n.global.t('common.message.fail'), withIcon?'error':'none');
	}
	static toastError(message: any, title?:string, withIcon?:boolean){
		message = parseMessage(message);
		if(title !== undefined){
			message = title + i18n.global.t('common.message.error') + ':'+ message;
		}

		this.toast(message, withIcon?'error':'none', {duration: 5000});
	}

	static modal(e: any, title?:string, options?: Parameters<(typeof uni)["showModal"]>){
		const message = parseMessage(e);
		if (!message) return;

		return uni.showModal(Object.assign({
			title,
			content: message,
			showCancel: false,
		}, options))
	}

	static async confirm(content:string, title?:string, options?: Parameters<(typeof uni)["showModal"]>[0]){
		const {confirm} = await uni.showModal(Object.assign({
			title: title ?? i18n.global.t('common.button.confirm'),
			content
		}, options))

		return confirm;
	}

	static async prompt(message: string, title?: string, options?: Parameters<(typeof uni)["showModal"]>[0]){
		let {confirm, content} = await uni.showModal(Object.assign({
			title,
			content:message,
			editable: true,
		}, options));

		if (confirm) return content;
		return undefined;
	}

	static loading<T>(promise:Promise<T>, text?: string):Promise<T>{
		this.showLoading({promise, text});
		return promise;
	}

	private static showLoading(stack?: { promise: Promise<any>, text?: string }){
		if(stack){
			LoadingStack.push(stack);
		}
		else {
			if (!LoadingStack.length) {
				if (LoadingInstance) {
					uni.hideLoading();
					LoadingInstance = undefined;
				}
				return;
			}
		}

		if(!stack || (stack && !LoadingInstance)){
			const loadingConfig = LoadingStack.shift();
			if(LoadingInstance){
				uni.hideLoading();
				uni.showLoading({
					title: loadingConfig!.text,
				});
			}
			else{
				uni.showLoading({
					title: loadingConfig?.text,
				});
				LoadingInstance = true;
			}
			loadingConfig!.promise.finally(() => this.showLoading());
		}
	}

	static async choosAction<T>(actions: LabelOptions<T>): Promise<T | undefined>{
		return new Promise((resolve) => {
			uni.showActionSheet({
				itemList: actions.map(a => a.label),
				success: (res) => resolve(actions[res.tapIndex]!.value),
				fail: ()=>resolve(undefined)
			})
		})
	}
	
	static clearLoading(){
		if(LoadingInstance){
			uni.hideLoading()
			LoadingInstance = undefined;
		}
		LoadingStack = [];
	}
}
