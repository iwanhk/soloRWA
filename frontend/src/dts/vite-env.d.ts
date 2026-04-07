// <reference types="vite/client" />

declare const __COMPILE_TIME__: string
declare const __APP_NAME__: string
declare const __APP_VERSION_NAME__: string
declare const __APP_VERSION_CODE__: number
declare const __VITE_MODE__: 'production' | 'development' | 'staging'
declare const __VITE_BASE_URL__: string
declare const __VITE_IMAGE_BASE_URL__: string
declare const __VITE_ENV_SIGNATURE__: string
declare const __VITE_PROXY_PREFIX__: string
declare const __VITE_PROXY_TARGET__: string
declare const __VITE_REGION__: 'domestic' | 'foreign'
declare const __VITE_REOWN_PROJECT_ID__: string

declare module '*.png' {
	const value: string;
	export default value;
}

declare namespace Page{
	interface PageInstance{
		options?: Record<string, any>;
	}
}