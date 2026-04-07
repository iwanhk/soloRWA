<template>
  <div
    :class="prefixCls"
    class="relative h-[100%] w-[100%] overflow-hidden"
  >
    <!-- Web3 Background -->
    <div class="web3-bg absolute inset-0 z-0"></div>
    
    <!-- Content Container -->
    <div class="relative z-10 h-full w-full flex items-center justify-center">
      
      <!-- Top Right Actions -->
      <div class="absolute top-5 right-5 flex items-center space-x-4">
        <ThemeSwitch />
        <LocaleDropdown />
      </div>

      <!-- Login Card Container -->
      <Transition appear enter-active-class="animate__animated animate__fadeInUp">
        <div class="login-card-container relative w-full max-w-[480px] p-6">
          <div class="mb-8 text-center flex flex-col items-center justify-center">
             <img alt="" class="h-8 w-auto mb-4" src="@/assets/imgs/logo.png" />
             <span class="text-3xl font-bold text-transparent bg-clip-text bg-gradient-to-r from-cyan-400 to-blue-600  tracking-wider">
               {{ underlineToHump(appStore.getTitle) }}
             </span>
             <div class="mt-2 text-gray-400 text-sm tracking-widest uppercase">让价值，被全球可信链接</div>
          </div>
          
          <LoginForm class="web3-login-form" />
          
          <div class="mt-6 flex justify-center space-x-8 opacity-60">
             <!-- Social Icons placeholders or removing if not needed -->
          </div>
          
           <div class="mt-8 text-center text-xs text-gray-600">
             &copy; 2026 RWA Bamboo Cloud. All rights reserved.
           </div>
        </div>
      </Transition>
    </div>
  </div>
</template>
<script lang="ts" setup>
import { underlineToHump } from '@/utils'

import { useDesign } from '@/hooks/web/useDesign'
import { useAppStore } from '@/store/modules/app'
import { ThemeSwitch } from '@/layout/components/ThemeSwitch'
import { LocaleDropdown } from '@/layout/components/LocaleDropdown'

import { LoginForm } from './components'

defineOptions({ name: 'Login' })

const appStore = useAppStore()
const { getPrefixCls } = useDesign()
const prefixCls = getPrefixCls('login')
</script>

<style lang="scss" scoped>
$prefix-cls: #{$namespace}-login;

.#{$prefix-cls} {
  overflow: auto;
}

.web3-bg {
  background: radial-gradient(circle at 50% -20%, #1e1e2f 0%, #000000 100%);
  
  &::before {
    content: '';
    position: absolute;
    inset: 0;
    background: 
      linear-gradient(rgba(0, 242, 254, 0.03) 1px, transparent 1px),
      linear-gradient(90deg, rgba(0, 242, 254, 0.03) 1px, transparent 1px);
    background-size: 40px 40px;
    mask-image: radial-gradient(circle at 50% 50%, black 30%, transparent 80%);
    pointer-events: none;
  }

  &::after {
    content: '';
    position: absolute;
    top: 50%;
    left: 50%;
    width: 60vw;
    height: 60vw;
    background: radial-gradient(circle, rgba(79, 172, 254, 0.1) 0%, transparent 70%);
    transform: translate(-50%, -50%);
    pointer-events: none;
  }
}

.login-card-container {
  background: rgba(20, 20, 20, 0.6);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 20px;
  box-shadow: 
    0 25px 50px -12px rgba(0, 0, 0, 0.5),
    0 0 0 1px rgba(255, 255, 255, 0.05),
    0 0 30px rgba(0, 242, 254, 0.05); /* Glow */
}
</style>
