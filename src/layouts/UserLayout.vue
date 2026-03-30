<script setup>
import { computed } from 'vue'
import { RouterLink, RouterView, useRoute, useRouter } from 'vue-router'
import { usePlatformStore } from '../stores/platform'
import { useAuthStore } from '../stores/auth'

const route = useRoute()
const router = useRouter()
const store = usePlatformStore()
const authStore = useAuthStore()

const navItems = [
  { label: '首页', to: '/' },
  { label: '三农资讯', to: '/articles' },
  { label: '三农商城', to: '/mall' },
  { label: '订单', to: '/orders' },
  { label: '个人中心', to: '/profile' },
]

const latestTip = computed(() => store.latestArticleTip)
const cartCount = computed(() => store.cartCount)

const handleLogout = () => {
  authStore.logout()
  router.push('/auth')
}
</script>

<template>
  <div class="user-shell">
    <header class="user-header">
      <div class="page-shell user-header__inner">
        <RouterLink class="brand" to="/">智慧三农平台</RouterLink>
        <nav class="user-nav">
          <RouterLink
            v-for="item in navItems"
            :key="item.to"
            :to="item.to"
            class="user-nav__link"
            :class="{ active: route.path === item.to }"
          >
            {{ item.label }}
          </RouterLink>
        </nav>
        <div class="user-tools">
          <RouterLink class="btn btn-ghost" to="/cart">购物车 {{ cartCount }}</RouterLink>
          <RouterLink
            v-if="authStore.role === 'admin'"
            class="btn btn-primary"
            to="/admin/dashboard"
          >
            进入管理端
          </RouterLink>
          <RouterLink v-if="!authStore.isLoggedIn" class="btn btn-primary" to="/auth">
            登录 / 注册
          </RouterLink>
          <template v-else>
            <span class="account-tag">{{ authStore.accountName }}</span>
            <button class="btn btn-ghost" @click="handleLogout">退出</button>
          </template>
        </div>
      </div>
    </header>

    <div v-if="latestTip" class="page-shell tip-banner card">
      <div>
        <div class="tip-banner__title">最新资讯提醒</div>
        <div class="muted">你有一条新的平台推送资讯：{{ latestTip.title }}</div>
      </div>
      <div class="tip-banner__actions">
        <RouterLink class="btn btn-primary" :to="`/articles/${latestTip.id}`">立即查看</RouterLink>
        <button class="btn" @click="store.dismissLatestTip()">稍后提醒</button>
      </div>
    </div>

    <main class="user-main">
      <RouterView />
    </main>
  </div>
</template>

<style scoped>
.user-shell {
  min-height: 100vh;
  background:
    radial-gradient(circle at top left, rgba(35, 176, 125, 0.22), transparent 32%),
    linear-gradient(180deg, #101716 0%, #151f1d 100%);
}

.user-header {
  position: sticky;
  top: 0;
  z-index: 20;
  backdrop-filter: blur(18px);
  background: rgba(16, 23, 22, 0.88);
  border-bottom: 1px solid var(--line);
}

.user-header__inner {
  display: flex;
  align-items: center;
  justify-content: space-between;
  min-height: 84px;
  gap: 24px;
}

.brand {
  font-size: 32px;
  font-weight: 800;
  letter-spacing: 1px;
}

.user-nav {
  display: flex;
  align-items: center;
  gap: 8px;
}

.user-nav__link {
  min-width: 112px;
  padding: 14px 18px;
  border-radius: 14px;
  text-align: center;
  color: var(--text-soft);
}

.user-nav__link.active {
  background: rgba(69, 145, 230, 0.24);
  color: white;
}

.user-tools {
  display: flex;
  gap: 12px;
  align-items: center;
}

.account-tag {
  padding: 10px 14px;
  border-radius: 999px;
  background: rgba(35, 176, 125, 0.14);
  color: #9cf4cf;
  font-size: 13px;
  font-weight: 700;
}

.tip-banner {
  margin-top: 20px;
  padding: 18px 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 20px;
}

.tip-banner__title {
  font-size: 18px;
  font-weight: 700;
  margin-bottom: 4px;
}

.tip-banner__actions {
  display: flex;
  gap: 12px;
}

.user-main {
  padding: 28px 0 40px;
}
</style>
