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
  Fold,
  Expand,
  ArrowDown,
  Monitor,
  SwitchButton
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const isCollapse = ref(false)

const adminNav = [
  { label: '数据概览', to: '/admin/dashboard', icon: PieChart, group: '运营管理' },
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
  <el-container class="admin-shell">
    <!-- 侧边栏 -->
    <el-aside :width="isCollapse ? '64px' : '240px'" class="admin-aside">
      <div class="admin-brand">
        <div class="brand-logo"></div>
        <span v-if="!isCollapse" class="brand-text">智慧三农</span>
      </div>
      
      <el-scrollbar>
        <el-menu
          :default-active="activeMenu"
          :collapse="isCollapse"
          router
          class="admin-menu"
          background-color="transparent"
          text-color="var(--el-text-color-regular)"
          active-text-color="var(--el-color-primary)"
        >
          <el-menu-item v-for="item in adminNav" :key="item.to" :index="item.to">
            <el-icon><component :is="item.icon" /></el-icon>
            <template #title>{{ item.label }}</template>
          </el-menu-item>
        </el-menu>
      </el-scrollbar>
    </el-aside>

    <el-container class="main-container">
      <!-- 顶部栏 -->
      <el-header class="admin-header">
        <div class="header-left">
          <el-button 
            type="primary" 
            link 
            class="collapse-btn" 
            @click="isCollapse = !isCollapse"
          >
            <el-icon :size="20">
              <component :is="isCollapse ? Expand : Fold" />
            </el-icon>
          </el-button>
          
          <el-breadcrumb separator="/" class="breadcrumb">
            <el-breadcrumb-item :to="{ path: '/admin' }">管理后台</el-breadcrumb-item>
            <el-breadcrumb-item>{{ adminNav.find(n => n.to === route.path)?.label || '当前页面' }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>

        <div class="header-right">
          <el-button link :icon="Monitor" @click="router.push('/')">预览前台</el-button>
          
          <el-divider direction="vertical" />
          
          <el-dropdown trigger="click">
            <div class="user-profile">
              <el-avatar :size="28" class="user-avatar">{{ authStore.accountName?.slice(0, 1) || '管' }}</el-avatar>
              <span class="user-name">{{ authStore.accountName || '管理员' }}</span>
              <el-icon class="el-icon--right"><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item :icon="UserIcon">个人中心</el-dropdown-item>
                <el-dropdown-item divided :icon="SwitchButton" @click="handleLogout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <!-- 内容区 -->
      <el-main class="admin-main">
        <div class="content-wrapper">
          <RouterView />
        </div>
      </el-main>
    </el-container>
  </el-container>
</template>

<style scoped>
.admin-shell {
  height: 100vh;
  background-color: var(--el-bg-color);
}

.admin-aside {
  background-color: var(--el-bg-color-overlay);
  border-right: 1px solid var(--el-border-color-lighter);
  transition: width 0.3s;
  display: flex;
  flex-direction: column;
}

.admin-brand {
  height: 60px;
  display: flex;
  align-items: center;
  padding: 0 20px;
  gap: 12px;
  border-bottom: 1px solid var(--el-border-color-lighter);
}

.brand-logo {
  width: 24px;
  height: 24px;
  background-color: var(--el-color-primary);
  border-radius: 6px;
  box-shadow: 0 0 12px var(--el-color-primary-light-5);
}

.brand-text {
  font-size: 18px;
  font-weight: 700;
  color: var(--el-text-color-primary);
  letter-spacing: 1px;
}

.admin-menu {
  border-right: none;
  padding: 10px 0;
}

.admin-menu :deep(.el-menu-item) {
  margin: 4px 12px;
  border-radius: 8px;
  height: 50px;
  line-height: 50px;
}

.admin-menu :deep(.el-menu-item.is-active) {
  background-color: var(--el-color-primary-light-9);
}

.admin-header {
  background-color: var(--el-bg-color-overlay);
  border-bottom: 1px solid var(--el-border-color-lighter);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 20px;
}

.collapse-btn {
  padding: 8px;
  font-size: 20px;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.user-profile {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 6px;
  transition: background-color 0.3s;
}

.user-profile:hover {
  background-color: var(--el-fill-color-light);
}

.user-avatar {
  background-color: var(--el-color-primary);
  font-weight: bold;
}

.user-name {
  font-size: 14px;
  color: var(--el-text-color-regular);
}

.admin-main {
  background-color: var(--el-bg-color);
  padding: 24px;
}

.content-wrapper {
  max-width: 1400px;
  margin: 0 auto;
}

/* 菜单折叠时的文字隐藏动画 */
.el-menu--collapse {
  width: 64px;
}
</style>
