import type {Plugin} from 'vite';

const textEncoderCode = `
if(typeof TextEncoder === "undefined"){
	TextEncoder = class {
        encode(str) {
            const buf = new ArrayBuffer(str.length);
            const bufView = new Uint8Array(buf);
            for (let i = 0; i < str.length; i++) {
                bufView[i] = str.charCodeAt(i);
            }
            return bufView;
        }
	}
}
if(typeof TextDecoder === "undefined"){
	TextDecoder = class {
        decode(uint8Array) {
            let str = '';
            for (let i = 0; i < uint8Array.length; i++) {
                str += String.fromCharCode(uint8Array[i]);
            }
            return str;
        }
    };
}`
const SharedArrayBufferCode = `
if(typeof SharedArrayBuffer === "undefined"){
	SharedArrayBuffer = {
    prototype: {
      get byteLength() { }
    }
  };
}
`

export default function injectPolyfillPlugin(): Plugin {
	return {
		name: 'vite-plugin-polyfill-inject',

		// 在构建的 JS 文件中注入代码
		renderChunk(code, chunk) {
			if (chunk.fileName.includes('app-service.js')) {
				return `
					${textEncoderCode}
				
					${SharedArrayBufferCode}
		
		            ${code}
		        `;
			}
			return code;
		},
	};
}