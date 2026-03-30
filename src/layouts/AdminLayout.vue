<script setup>
import { RouterLink, RouterView, useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const adminNav = [
  { label: '数据概览', to: '/admin/dashboard', group: '运营管理' },
  { label: '资讯管理', to: '/admin/articles', group: '运营管理' },
  { label: '商品管理', to: '/admin/products', group: '运营管理' },
  { label: '订单管理', to: '/admin/orders', group: '运营管理' },
  { label: '用户管理', to: '/admin/users', group: '系统' },
]

const navGroups = Array.from(new Set(adminNav.map((item) => item.group)))

const handleLogout = () => {
  authStore.logout()
  router.push('/auth')
}
</script>

<template>
  <div class="admin-shell">
    <header class="admin-topbar">
      <div class="admin-brand">
        <span class="admin-brand__dot"></span>
        <div>
          <div class="admin-brand__title">智慧三农平台</div>
          <div class="admin-brand__sub">管理后台</div>
        </div>
      </div>
      <div class="admin-topbar__tools">
        <span class="muted">{{ authStore.accountName || '平台管理员' }}</span>
        <RouterLink class="btn btn-ghost" to="/">回到用户端</RouterLink>
        <button class="btn btn-primary" @click="handleLogout">退出登录</button>
      </div>
    </header>

    <div class="admin-layout">
      <aside class="admin-sidebar">
        <div v-for="group in navGroups" :key="group" class="admin-sidebar__group">
          <div class="admin-sidebar__label">{{ group }}</div>
          <RouterLink
            v-for="item in adminNav.filter((entry) => entry.group === group)"
            :key="item.to"
            :to="item.to"
            class="admin-sidebar__link"
            :class="{ active: route.path === item.to }"
          >
            {{ item.label }}
          </RouterLink>
        </div>
      </aside>

      <main class="admin-main">
        <RouterView />
      </main>
    </div>
  </div>
</template>

<style scoped>
.admin-shell {
  min-height: 100vh;
  background: #131816;
}

.admin-topbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  min-height: 60px;
  padding: 0 22px;
  border-bottom: 1px solid var(--line);
  background: #181d1c;
}

.admin-brand {
  display: flex;
  align-items: center;
  gap: 12px;
}

.admin-brand__dot {
  width: 16px;
  height: 16px;
  border-radius: 5px;
  background: var(--accent);
  box-shadow: 0 0 0 6px rgba(35, 176, 125, 0.12);
}

.admin-brand__title {
  font-weight: 700;
}

.admin-brand__sub {
  font-size: 12px;
  color: var(--text-mute);
}

.admin-topbar__tools {
  display: flex;
  align-items: center;
  gap: 12px;
}

.admin-layout {
  display: grid;
  grid-template-columns: 220px 1fr;
  min-height: calc(100vh - 60px);
}

.admin-sidebar {
  padding: 20px 12px;
  border-right: 1px solid var(--line);
  background: #191f1d;
}

.admin-sidebar__group + .admin-sidebar__group {
  margin-top: 24px;
}

.admin-sidebar__label {
  margin-bottom: 8px;
  padding: 0 10px;
  color: var(--text-mute);
  font-size: 12px;
}

.admin-sidebar__link {
  display: block;
  padding: 11px 12px;
  border-radius: 12px;
  color: var(--text-soft);
}

.admin-sidebar__link + .admin-sidebar__link {
  margin-top: 4px;
}

.admin-sidebar__link.active {
  background: rgba(35, 176, 125, 0.18);
  color: #92f1c8;
}

.admin-main {
  padding: 28px 30px 36px;
}
</style>
