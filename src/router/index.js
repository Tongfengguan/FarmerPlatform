import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/auth',
      name: 'auth',
      component: () => import('../views/AuthView.vue'),
      meta: { guestOnly: true },
    },
    {
      path: '/',
      component: () => import('../layouts/UserLayout.vue'),
      children: [
        { path: '', name: 'home', component: () => import('../views/user/HomeView.vue') },
        {
          path: 'articles',
          name: 'articles',
          component: () => import('../views/user/ArticlesView.vue'),
        },
        {
          path: 'articles/:id',
          name: 'article-detail',
          component: () => import('../views/user/ArticleDetailView.vue'),
        },
        { path: 'mall', name: 'mall', component: () => import('../views/user/MallView.vue') },
        {
          path: 'products/:id',
          name: 'product-detail',
          component: () => import('../views/user/ProductDetailView.vue'),
        },
        {
          path: 'cart',
          name: 'cart',
          component: () => import('../views/user/CartView.vue'),
          meta: { requiresAuth: true },
        },
        {
          path: 'orders',
          name: 'orders',
          component: () => import('../views/user/OrdersView.vue'),
          meta: { requiresAuth: true },
        },
        {
          path: 'profile',
          name: 'profile',
          component: () => import('../views/user/ProfileView.vue'),
          meta: { requiresAuth: true },
        },
      ],
    },
    {
      path: '/admin',
      component: () => import('../layouts/AdminLayout.vue'),
      meta: { requiresAuth: true, requiresAdmin: true },
      children: [
        { path: '', redirect: '/admin/dashboard' },
        {
          path: 'dashboard',
          name: 'admin-dashboard',
          component: () => import('../views/admin/DashboardView.vue'),
        },
        {
          path: 'articles',
          name: 'admin-articles',
          component: () => import('../views/admin/ArticleAdminView.vue'),
        },
        {
          path: 'products',
          name: 'admin-products',
          component: () => import('../views/admin/ProductAdminView.vue'),
        },
        {
          path: 'orders',
          name: 'admin-orders',
          component: () => import('../views/admin/OrderAdminView.vue'),
        },
        {
          path: 'users',
          name: 'admin-users',
          component: () => import('../views/admin/UserAdminView.vue'),
        },
      ],
    },
  ],
  scrollBehavior() {
    return { top: 0 }
  },
})

router.beforeEach((to) => {
  const authStore = useAuthStore()

  if (to.meta.guestOnly && authStore.isLoggedIn) {
    return authStore.role === 'admin' ? '/admin/dashboard' : '/'
  }

  if (to.meta.requiresAuth && !authStore.isLoggedIn) {
    return '/auth'
  }

  if (to.meta.requiresAdmin && authStore.role !== 'admin') {
    return '/'
  }

  return true
})

export default router
