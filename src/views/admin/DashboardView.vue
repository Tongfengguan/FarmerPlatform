<script setup>
import { computed, onMounted, ref } from 'vue'
import { usePlatformStore } from '../../stores/platform'
import { 
  TrendCharts, 
  ShoppingCart, 
  Document, 
  User, 
  ArrowUp, 
  ArrowDown,
  Timer
} from '@element-plus/icons-vue'

const store = usePlatformStore()
const loading = ref(false)

// 模拟趋势数据（实际项目中可由后端返回）
const trendData = {
  visit: { value: 12.5, type: 'up' },
  sales: { value: 8.2, type: 'up' },
  orders: { value: 3.1, type: 'down' },
  articles: { value: 25.0, type: 'up' }
}

const stats = computed(() => [
  { 
    title: '今日访问量', 
    value: store.dashboard.visitToday || 0, 
    icon: User, 
    color: '#409EFF',
    trend: trendData.visit 
  },
  { 
    title: '本月销售额', 
    value: `¥${(store.dashboard.salesMonth || 0).toLocaleString()}`, 
    icon: TrendCharts, 
    color: '#67C23A',
    trend: trendData.sales 
  },
  { 
    title: '待处理订单', 
    value: store.dashboard.pendingOrders || 0, 
    icon: ShoppingCart, 
    color: '#E6A23C',
    trend: trendData.orders 
  },
  { 
    title: '已发布资讯', 
    value: store.dashboard.publishedArticles || 0, 
    icon: Document, 
    color: '#909399',
    trend: trendData.articles 
  }
])

const recentActivities = [
  { time: '10分钟前', content: '新订单: 编号 #ORD20240616001 已支付', type: 'success' },
  { time: '2小时前', content: '库存预警: “有机富士苹果” 库存低于10件', type: 'warning' },
  { time: '5小时前', content: '新用户注册: 用户 “刘老根” 加入平台', type: 'info' },
  { time: '昨天', content: '资讯发布: “2024年农机补贴政策申领指南” 已上线', type: 'primary' }
]

onMounted(async () => {
  loading.value = true
  try {
    // 确保数据已同步
    if (store.dashboard.visitToday === 0) {
      await store.bootstrapPrivate('admin')
    }
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <div class="admin-page dashboard-view">
    <div class="page-header">
      <div>
        <h1 class="page-title">数据概览</h1>
        <p class="page-subtitle">欢迎回来，系统管理员。这是平台今天的运行简报。</p>
      </div>
      <div class="header-actions">
        <el-tag type="success" effect="dark" round>
          <el-icon><Timer /></el-icon> 系统运行中
        </el-tag>
      </div>
    </div>

    <!-- 核心统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6" v-for="item in stats" :key="item.title">
        <el-card shadow="hover" class="stats-card">
          <div class="stats-content">
            <div class="stats-info">
              <div class="stats-label">{{ item.title }}</div>
              <div class="stats-value">{{ item.value }}</div>
              <div class="stats-trend" :class="item.trend.type">
                <el-icon>
                  <ArrowUp v-if="item.trend.type === 'up'" />
                  <ArrowDown v-else />
                </el-icon>
                {{ item.trend.value }}% <span>较昨日</span>
              </div>
            </div>
            <div class="stats-icon" :style="{ backgroundColor: item.color + '20', color: item.color }">
              <el-icon><component :is="item.icon" /></el-icon>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="charts-row">
      <!-- 快捷操作区 -->
      <el-col :span="16">
        <el-card shadow="never" title="常用功能">
          <template #header>
            <div class="card-header">
              <span>快捷工作台</span>
            </div>
          </template>
          <div class="quick-actions">
            <div class="action-item" @click="$router.push('/admin/products')">
              <div class="action-icon products"><el-icon><ShoppingCart /></el-icon></div>
              <span>发布商品</span>
            </div>
            <div class="action-item" @click="$router.push('/admin/articles')">
              <div class="action-icon articles"><el-icon><Document /></el-icon></div>
              <span>发布资讯</span>
            </div>
            <div class="action-item" @click="$router.push('/admin/orders')">
              <div class="action-icon orders"><el-icon><TrendCharts /></el-icon></div>
              <span>处理订单</span>
            </div>
            <div class="action-item" @click="$router.push('/admin/users')">
              <div class="action-icon users"><el-icon><User /></el-icon></div>
              <span>用户审核</span>
            </div>
          </div>
        </el-card>

        <el-card shadow="never" class="placeholder-card mt-20">
          <template #header>
            <div class="card-header">
              <span>销售趋势分析</span>
            </div>
          </template>
          <div class="empty-chart">
            <el-empty description="趋势图表加载中..." :image-size="100" />
          </div>
        </el-card>
      </el-col>

      <!-- 最近动态 -->
      <el-col :span="8">
        <el-card shadow="never" class="activity-card">
          <template #header>
            <div class="card-header">
              <span>最近动态</span>
            </div>
          </template>
          <el-timeline>
            <el-timeline-item
              v-for="(activity, index) in recentActivities"
              :key="index"
              :type="activity.type"
              :timestamp="activity.time"
            >
              {{ activity.content }}
            </el-timeline-item>
          </el-timeline>
          <div class="more-link">
            <el-button link type="primary">查看全部动态</el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<style scoped>
.dashboard-view {
  padding-bottom: 40px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.page-title {
  font-size: 28px;
  font-weight: 700;
  margin: 0 0 8px;
  background: linear-gradient(120deg, var(--el-color-primary), var(--el-color-success));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.page-subtitle {
  color: var(--el-text-color-secondary);
  margin: 0;
}

.stats-row {
  margin-bottom: 24px;
}

.stats-card {
  border: none;
  border-radius: 16px;
  transition: transform 0.3s ease;
}

.stats-card:hover {
  transform: translateY(-5px);
}

.stats-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.stats-label {
  font-size: 14px;
  color: var(--el-text-color-secondary);
  margin-bottom: 8px;
}

.stats-value {
  font-size: 24px;
  font-weight: 800;
  color: var(--el-text-color-primary);
  margin-bottom: 8px;
}

.stats-trend {
  font-size: 12px;
  display: flex;
  align-items: center;
  gap: 4px;
}

.stats-trend.up { color: var(--el-color-success); }
.stats-trend.down { color: var(--el-color-danger); }

.stats-trend span {
  color: var(--el-text-color-secondary);
  margin-left: 4px;
}

.stats-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
}

.quick-actions {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  padding: 10px 0;
}

.action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  transition: all 0.3s;
}

.action-item:hover {
  transform: scale(1.05);
}

.action-icon {
  width: 64px;
  height: 64px;
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  color: white;
  box-shadow: 0 10px 20px rgba(0,0,0,0.05);
}

.action-icon.products { background: linear-gradient(135deg, #409EFF, #64B5F6); }
.action-icon.articles { background: linear-gradient(135deg, #67C23A, #81C784); }
.action-icon.orders { background: linear-gradient(135deg, #E6A23C, #FFB74D); }
.action-icon.users { background: linear-gradient(135deg, #F56C6C, #EF5350); }

.action-item span {
  font-size: 14px;
  font-weight: 500;
  color: var(--el-text-color-primary);
}

.mt-20 { margin-top: 20px; }

.empty-chart {
  height: 250px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.activity-card {
  height: 100%;
}

.more-link {
  text-align: center;
  margin-top: 20px;
}

:deep(.el-card) {
  border-radius: 16px;
  border: 1px solid var(--el-border-color-lighter);
}

:deep(.el-card__header) {
  border-bottom: 1px solid var(--el-border-color-lighter);
  font-weight: 700;
}
</style>
