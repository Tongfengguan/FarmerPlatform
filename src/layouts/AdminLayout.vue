<script setup>
import { ref, computed } from 'vue'
import { RouterView, useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import {
  PieChart,
  Document,
  Box,
  Tickets,
  User as UserIcon,
  ChatLineRound,
  Fold,
  Expand,
  ArrowDown,
  Monitor,
  SwitchButton,
  Setting
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const isCollapse = ref(false)

const adminNav = [
  { label: '数据概览', to: '/admin/dashboard', icon: PieChart, group: '运营管理' },
  { label: 'AI 智能助理', to: '/admin/ai-assistant', icon: ChatLineRound, group: '运营管理' },
  { label: '资讯管理', to: '/admin/articles', icon: Document, group: '运营管理' },
  { label: '商品管理', to: '/admin/products', icon: Box, group: '运营管理' },
  { label: '订单管理', to: '/admin/orders', icon: Tickets, group: '运营管理' },
  { label: '用户管理', to: '/admin/users', icon: UserIcon, group: '系统管理' },
]

const activeMenu = computed(() => route.path)

const handleLogout = () => {
  authStore.logout()
  router.push('/auth')
}
</script>

<template>
  <el-container class="admin-layout">
    <!-- Floating Sidebar -->
    <el-aside :width="isCollapse ? '80px' : '260px'" class="admin-aside">
      <div class="aside-inner">
        <div class="brand-section">
          <div class="brand-logo">
            <el-icon color="#fff" :size="20"><Setting /></el-icon>
          </div>
          <span v-if="!isCollapse" class="brand-name">智慧三农</span>
        </div>
        
        <el-scrollbar>
          <el-menu
            :default-active="activeMenu"
            :collapse="isCollapse"
            router
            class="admin-menu"
          >
            <el-menu-item v-for="item in adminNav" :key="item.to" :index="item.to">
              <el-icon><component :is="item.icon" /></el-icon>
              <template #title>
                <span class="menu-label">{{ item.label }}</span>
              </template>
            </el-menu-item>
          </el-menu>
        </el-scrollbar>

        <div class="aside-footer" v-if="!isCollapse">
          <div class="user-card" @click="handleLogout">
            <el-avatar :size="32" class="avatar-green">{{ authStore.accountName?.slice(0, 1) || '管' }}</el-avatar>
            <div class="user-info">
              <div class="name">{{ authStore.accountName || '管理员' }}</div>
              <div class="role">退出系统</div>
            </div>
            <el-icon><SwitchButton /></el-icon>
          </div>
        </div>
      </div>
    </el-aside>

    <el-container class="content-container">
      <!-- Island Header -->
      <el-header class="admin-header">
        <div class="header-inner">
          <div class="header-left">
            <el-button 
              circle
              class="toggle-btn" 
              @click="isCollapse = !isCollapse"
            >
              <el-icon>
                <component :is="isCollapse ? Expand : Fold" />
              </el-icon>
            </el-button>
            
            <div class="page-title">
              {{ adminNav.find(n => n.to === route.path)?.label || '管理中心' }}
            </div>
          </div>

          <div class="header-right">
            <el-button round :icon="Monitor" @click="router.push('/')">返回前台</el-button>
          </div>
        </div>
      </el-header>

      <el-main class="admin-main">
        <div class="view-wrapper">
          <RouterView v-slot="{ Component }">
            <transition name="fade-transform" mode="out-in">
              <component :is="Component" />
            </transition>
          </RouterView>
        </div>
      </el-main>
    </el-container>
  </el-container>
</template>

<style scoped>
.admin-layout {
  height: 100vh;
  background-color: var(--color-background);
  padding: 16px;
  gap: 16px;
}

.admin-aside {
  transition: width 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  overflow: hidden;
}

.aside-inner {
  height: 100%;
  background: var(--color-surface);
  border-radius: var(--radius-lg);
  display: flex;
  flex-direction: column;
  box-shadow: var(--shadow-md);
  border: 1px solid var(--color-line);
}

.brand-section {
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 12px;
}

.brand-logo {
  width: 40px;
  height: 40px;
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-dark) 100%);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 8px 16px -4px rgba(16, 185, 129, 0.4);
}

.brand-name {
  font-size: 18px;
  font-weight: 800;
  color: var(--color-text);
  letter-spacing: -0.5px;
}

.admin-menu {
  border: none;
  background: transparent;
  padding: 0 12px;
}

.admin-menu :deep(.el-menu-item) {
  height: 54px;
  margin-bottom: 4px;
  border-radius: 14px;
  color: var(--color-text-soft);
  transition: all 0.3s ease;
}

.admin-menu :deep(.el-menu-item:hover) {
  background-color: var(--color-primary-light);
  color: var(--color-primary-dark);
}

.admin-menu :deep(.el-menu-item.is-active) {
  background-color: var(--color-primary);
  color: white;
  box-shadow: 0 8px 16px -4px rgba(16, 185, 129, 0.3);
}

.menu-label {
  font-weight: 600;
}

.aside-footer {
  padding: 16px;
  border-top: 1px solid var(--color-line);
}

.user-card {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  border-radius: 14px;
  cursor: pointer;
  transition: background 0.2s;
}

.user-card:hover {
  background: var(--color-line);
}

.avatar-green {
  background-color: var(--color-primary);
  font-weight: bold;
}

.user-info {
  flex: 1;
}

.user-info .name {
  font-size: 14px;
  font-weight: 700;
  color: var(--color-text);
}

.user-info .role {
  font-size: 12px;
  color: var(--color-text-mute);
}

.content-container {
  flex-direction: column;
}

.admin-header {
  height: 80px !important;
  padding: 0;
}

.header-inner {
  height: 100%;
  background: var(--color-surface);
  border-radius: var(--radius-lg);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  box-shadow: var(--shadow-md);
  border: 1px solid var(--color-line);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.toggle-btn {
  border: 1px solid var(--color-line);
  background: var(--color-background);
}

.page-title {
  font-size: 18px;
  font-weight: 700;
  color: var(--color-text);
}

.admin-main {
  padding: 16px 0 0 0;
}

.view-wrapper {
  height: 100%;
  overflow: auto;
}

/* Transitions */
.fade-transform-enter-active,
.fade-transform-leave-active {
  transition: all 0.3s;
}

.fade-transform-enter-from {
  opacity: 0;
  transform: translateX(-20px);
}

.fade-transform-leave-to {
  opacity: 0;
  transform: translateX(20px);
}
</style>
