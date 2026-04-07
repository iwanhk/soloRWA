import {createSSRApp} from "vue";
import {createPinia} from 'pinia'
import App from "./App.vue";
import i18n from './i18n'
import './library/RouterGuard'
import "@/uni_modules/uview-plus/index.scss";
import "./styles/main.scss"
// @ts-ignore
import uviewPlus from '@/uni_modules/uview-plus'

export function createApp() {
  const app = createSSRApp(App);
  const pinia = createPinia()
  app.use(pinia);
  app.use(i18n);
  app.use(uviewPlus);
  return {
    app,
  };
}
