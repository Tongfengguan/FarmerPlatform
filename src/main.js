import { createApp } from 'vue'
import { createPinia } from 'pinia'
import 'element-plus/dist/index.css'
import 'element-plus/theme-chalk/dark/css-vars.css'
import './styles.css'

import App from './App.vue'
import router from './router'
import { useAuthStore } from './stores/auth'
import { usePlatformStore } from './stores/platform'

const app = createApp(App)
const pinia = createPinia()

// 激活 Element Plus 暗黑模式
document.documentElement.classList.add('dark')

app.use(pinia)

const authStore = useAuthStore(pinia)
const platformStore = usePlatformStore(pinia)

await platformStore.bootstrapPublic()
await authStore.bootstrap()

if (authStore.session?.token) {
  await platformStore.bootstrapPrivate(authStore.role, authStore.session)
}

app.use(router)

app.mount('#app')
