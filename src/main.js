import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'
import { useAuthStore } from './stores/auth'
import { usePlatformStore } from './stores/platform'
import './styles.css'

const app = createApp(App)
const pinia = createPinia()

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
