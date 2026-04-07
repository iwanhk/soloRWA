import {getPlatform} from "./getPlatform";

let MODE = __VITE_MODE__;

let VITE_BASE_URL = __VITE_BASE_URL__;
let VITE_IMAGE_BASE_URL = __VITE_IMAGE_BASE_URL__;
let VITE_ENV_SIGNATURE = __VITE_ENV_SIGNATURE__;
let VITE_PROXY_PREFIX = __VITE_PROXY_PREFIX__;

const systemInfo = uni.getSystemInfoSync();
const platform = getPlatform();

const ENV = {
	dev: MODE === 'development',
	apiUrl: VITE_PROXY_PREFIX,
	baseURL: VITE_BASE_URL,
	imageURL: VITE_IMAGE_BASE_URL,
	platform,
	systemInfo,
	appPlatform: systemInfo.platform,
	envSignature: VITE_ENV_SIGNATURE,
	foreign: __VITE_REGION__ === 'foreign',
	reownProjectId: __VITE_REOWN_PROJECT_ID__,
}

// #ifdef APP-PLUS
ENV.apiUrl = __VITE_PROXY_TARGET__;
// #endif

export default ENV;
