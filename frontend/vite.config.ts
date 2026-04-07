import {defineConfig, loadEnv} from "vite";
import uni from "@dcloudio/vite-plugin-uni";
import AutoImport from 'unplugin-auto-import/vite'
import injectPolyfillPlugin from "./plugins/vite-plugin-polyfill-inject";
import fs from "fs";
import path from "path";
import JSON5 from 'json5'

// 修复 @dcloudio/vite-plugin-uni 生成的错误的动态导入路径
function fixDynamicImportPlugin() {
  return {
    name: 'fix-dynamic-import',
    apply: 'build',
    enforce: 'post',
    generateBundle(options:any, bundle:Record<string, { code:any }>) {
      for (const [fileName, asset] of Object.entries(bundle)) {
        if (fileName.endsWith('.js') && typeof asset.code === 'string') {
          // 修复格式：import("..-node_modules-xxx") -> import("./assets/..-node_modules-xxx")
          asset.code = asset.code.replace(
            /import\("\.\.[\-\w@\/\.]+\.js"\)/g,
            (match) => {
              // 如果已经包含 assets/ 前缀，则不修改
              if (match.includes('assets/')) {
                return match;
              }
              // 否则添加 /assets/ 前缀
              return match.replace('import("', 'import("/assets/');
            }
          );
        }
      }
    }
  }
}

// https://vitejs.dev/config/
export default defineConfig(({mode}) => {
  const env = loadEnv(mode, __dirname);
  const manifestContent = fs.readFileSync(path.resolve(__dirname, './src/manifest.json'), {encoding: 'utf8'});
  const manifest = JSON5.parse(manifestContent);

  return {
    plugins: [
      uni(),
      AutoImport({
        imports: ['vue'],
        dts: 'src/auto-imports.d.ts',
      }),
      injectPolyfillPlugin(),
      fixDynamicImportPlugin(),
    ],
    server: {
      hmr: true,
      host: true,
      port: parseInt(env.VITE_PORT!),
      proxy: {
        [env.VITE_PROXY_PREFIX!]: {
          target: env.VITE_PROXY_TARGET,
          changeOrigin: true,
          secure: false,
          rewrite: (path) => path.replace(new RegExp(`^${env.VITE_PROXY_PREFIX}/`), '/'),
        },
        [env.VITE_IMAGE_BASE_URL!]:{
          target: env.VITE_IMAGE_PROXY_TARGET,
          changeOrigin: true,
          secure: false,
          rewrite: (path) => path.replace(new RegExp(`^${env.VITE_IMAGE_BASE_URL}/`), '/'),
        }
      },
    },
    css: {
      preprocessorOptions: {
        scss: {
          // uview-plus 内部使用 @import，需要用 @import 引入 theme.scss 使变量全局可用
          additionalData: '@import  "@/styles/definition.scss";@import "@/styles/uview-plus-theme.scss";',
          silenceDeprecations: ["legacy-js-api", 'color-functions', "global-builtin", "import"],
        },
      },
    },
    define: {
      __COMPILE_TIME__: JSON.stringify(new Date().toLocaleString()),
      __APP_NAME__: JSON.stringify(manifest.name),
      __APP_VERSION_NAME__: JSON.stringify(manifest.versionName),
      __APP_VERSION_CODE__: JSON.stringify(manifest.versionCode),
      __VITE_MODE__: JSON.stringify(mode),
      __VITE_BASE_URL__: JSON.stringify(env.VITE_BASE_URL),
      __VITE_IMAGE_BASE_URL__: JSON.stringify(env.VITE_IMAGE_BASE_URL),
      __VITE_ENV_SIGNATURE__: JSON.stringify(env.VITE_ENV_SIGNATURE),
      __VITE_PROXY_PREFIX__: JSON.stringify(env.VITE_PROXY_PREFIX),
      __VITE_PROXY_TARGET__: JSON.stringify(env.VITE_PROXY_TARGET),
      __VITE_REGION__: JSON.stringify(env.VITE_REGION),
      __VITE_REOWN_PROJECT_ID__: JSON.stringify(env.VITE_REOWN_PROJECT_ID),
    },
    optimizeDeps: {
      exclude: ["pdfjs-dist"]
    }
  }
});
