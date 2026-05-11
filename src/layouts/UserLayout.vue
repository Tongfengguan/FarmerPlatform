<script setup>
import { computed } from 'vue'
import { RouterLink, RouterView, useRoute, useRouter } from 'vue-router'
import { usePlatformStore } from '../stores/platform'
import { useAuthStore } from '../stores/auth'
import { ShoppingCart, Monitor, User, SwitchButton, Notification } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const store = usePlatformStore()
const authStore = useAuthStore()

const navItems = [
  { label: '首页', to: '/' },
  { label: '三农资讯', to: '/articles' },
  { label: '三农商城', to: '/mall' },
  { label: '我的订单', to: '/orders' },
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
  <div class="user-layout">
    <!-- Island Navigation -->
    <header class="navbar-wrapper">
      <div class="navbar-inner page-shell">
        <div class="nav-left">
          <RouterLink class="brand" to="/">
            <div class="logo-box">
              <div class="logo-leaf"></div>
            </div>
            <span class="brand-text">智慧三农</span>
          </RouterLink>
        </div>

        <nav class="nav-center">
          <RouterLink
            v-for="item in navItems"
            :key="item.to"
            :to="item.to"
            class="nav-link"
            :class="{ active: route.path === item.to }"
          >
            {{ item.label }}
          </RouterLink>
        </nav>

        <div class="nav-right">
          <div class="action-group">
            <RouterLink class="action-btn" to="/cart">
              <el-badge :value="cartCount" :hidden="cartCount === 0" type="danger">
                <el-icon :size="20"><ShoppingCart /></el-icon>
              </el-badge>
            </RouterLink>
            
            <RouterLink
              v-if="authStore.role === 'admin'"
              class="action-btn"
              to="/admin/dashboard"
              title="管理端"
            >
              <el-icon :size="20"><Monitor /></el-icon>
            </RouterLink>
          </div>

          <div class="divider"></div>

          <div v-if="authStore.isLoggedIn" class="user-box">
            <el-dropdown trigger="click">
              <div class="user-pill">
                <el-avatar :size="24" class="avatar-sm">{{ authStore.accountName?.slice(0,1) }}</el-avatar>
                <span class="username">{{ authStore.accountName }}</span>
              </div>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="router.push('/profile')">个人资料</el-dropdown-item>
                  <el-dropdown-item divided @click="handleLogout">
                    <el-icon><SwitchButton /></el-icon>退出登录
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
          <RouterLink v-else class="login-pill" to="/auth">
            登录系统
          </RouterLink>
        </div>
      </div>
    </header>

    <!-- Notification Banner -->
    <transition name="slide-up">
      <div v-if="latestTip" class="notification-area page-shell">
        <div class="notification-pill">
          <el-icon class="pill-icon"><Notification /></el-icon>
          <div class="pill-text">
            <strong>最新动态：</strong>{{ latestTip.title }}
          </div>
          <div class="pill-actions">
            <el-button type="primary" size="small" round @click="$router.push(`/articles/${latestTip.id}`)">查看</el-button>
            <el-button link size="small" @click="store.dismissLatestTip()">忽略</el-button>
          </div>
        </div>
      </div>
    </transition>

    <main class="main-content">
      <div class="page-shell">
        <RouterView v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </RouterView>
      </div>
    </main>

    <footer class="minimal-footer">
      <div class="page-shell footer-inner">
        <div class="footer-copy">© 2024 Farmer Platform. Connecting Soil to Soul.</div>
        <div class="footer-links">
          <span>隐私政策</span>
          <span>服务条款</span>
          <span>关于我们</span>
        </div>
      </div>
    </footer>
  </div>
</template>

<style scoped>
.user-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: var(--color-background);
}

.navbar-wrapper {
  position: sticky;
  top: 16px;
  z-index: 100;
  padding: 0 16px;
}

.navbar-inner {
  height: 72px;
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.3);
  border-radius: 24px;
  box-shadow: 0 10px 25px -5px rgba(0, 0, 0, 0.05);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
}

.brand {
  display: flex;
  align-items: center;
  gap: 12px;
}

.logo-box {
  width: 32px;
  height: 32px;
  background: var(--color-primary);
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.logo-leaf {
  width: 16px;
  height: 16px;
  background: white;
  mask: radial-gradient(circle at 0 100%, transparent 40%, black 40%);
  border-radius: 0 10px 0 10px;
}

.brand-text {
  font-size: 20px;
  font-weight: 800;
  color: var(--color-text);
  letter-spacing: -0.5px;
}

.nav-center {
  display: flex;
  gap: 8px;
  background: var(--color-line);
  padding: 6px;
  border-radius: 16px;
}

.nav-link {
  padding: 8px 18px;
  border-radius: 12px;
  font-size: 14px;
  font-weight: 600;
  color: var(--color-text-soft);
  transition: all 0.2s ease;
}

.nav-link:hover {
  color: var(--color-primary);
}

.nav-link.active {
  background: white;
  color: var(--color-primary);
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.05);
}

.nav-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.action-group {
  display: flex;
  gap: 4px;
}

.action-btn {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 12px;
  color: var(--color-text-soft);
  transition: all 0.2s;
  line-height: 1;
}

.action-btn :deep(.el-badge) {
  display: flex;
  align-items: center;
  justify-content: center;
}

.action-btn:hover {
  background: var(--color-line);
  color: var(--color-primary);
}

.divider {
  width: 1px;
  height: 24px;
  background: var(--color-line);
}

.user-pill {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 6px 14px 6px 6px;
  background: var(--color-line);
  border-radius: 999px;
  cursor: pointer;
  transition: all 0.2s;
}

.user-pill:hover {
  background: var(--color-primary-light);
}

.avatar-sm {
  background-color: var(--color-primary);
  font-weight: 700;
}

.username {
  font-size: 14px;
  font-weight: 600;
  color: var(--color-text);
}

.login-pill {
  padding: 10px 24px;
  background: var(--color-primary);
  color: white;
  border-radius: 999px;
  font-weight: 700;
  font-size: 14px;
  box-shadow: 0 10px 15px -3px rgba(16, 185, 129, 0.3);
}

.notification-area {
  margin-top: 32px;
}

.notification-pill {
  background: var(--color-surface);
  border: 1px solid var(--color-line);
  border-radius: 16px;
  padding: 12px 24px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: var(--shadow-md);
}

.pill-icon {
  color: var(--color-primary);
  font-size: 20px;
}

.pill-text {
  flex: 1;
  font-size: 14px;
  color: var(--color-text-soft);
}

.pill-actions {
  display: flex;
  gap: 8px;
}

.main-content {
  flex: 1;
  padding: 40px 0 80px;
}

.minimal-footer {
  padding: 40px 0;
  border-top: 1px solid var(--color-line);
  background: white;
}

.footer-inner {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.footer-copy {
  font-size: 14px;
  color: var(--color-text-mute);
}

.footer-links {
  display: flex;
  gap: 24px;
  font-size: 14px;
  color: var(--color-text-soft);
  font-weight: 500;
}

/* Animations */
.fade-enter-active, .fade-leave-active {
  transition: opacity 0.3s ease;
}
.fade-enter-from, .fade-leave-to {
  opacity: 0;
}

.slide-up-enter-active, .slide-up-leave-active {
  transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
}
.slide-up-enter-from {
  opacity: 0;
  transform: translateY(20px);
}
.slide-up-leave-to {
  opacity: 0;
  transform: scale(0.95);
}
</style>
