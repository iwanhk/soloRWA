let URL: NativeURL;
let URLSearchParams: NativeURLSearchParams;

// #ifdef H5
URL = window.URL;
URLSearchParams = window.URLSearchParams;
// #endif

// #ifdef APP-PLUS || MP-WEIXIN
import * as URLPolyfill from "whatwg-url-without-unicode";

URL = URLPolyfill.URL as unknown as NativeURL;
URLSearchParams = URLPolyfill.URLSearchParams as unknown as NativeURLSearchParams;
// #endif

export {
	URL,
	URLSearchParams
}