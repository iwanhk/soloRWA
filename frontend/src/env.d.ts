/// <reference types="vite/client" />

declare module '@/components/*.vue' {
	import {DefineComponent} from 'vue'
	// eslint-disable-next-line @typescript-eslint/no-explicit-any, @typescript-eslint/ban-types
  const component: DefineComponent<{}, {}, any>
  export default component
}

declare module '@/pages/*/*.vue' {
	import {DefineComponent} from 'vue'
	// eslint-disable-next-line @typescript-eslint/no-explicit-any, @typescript-eslint/ban-types
  const component: DefineComponent<{}, {}, any>
  export default component
}

// Global polyfill declarations
declare global {
  var global: any;
  var process: {
    env: Record<string, string>;
    version: string;
    platform: string;
  };
}