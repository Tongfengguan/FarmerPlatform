<script setup>
import { computed } from 'vue'
import { usePlatformStore } from '../../stores/platform'
import { 
  User, 
  Money, 
  ShoppingCart, 
  Document,
  ArrowRight
} from '@element-plus/icons-vue'

const store = usePlatformStore()
const latestOrders = computed(() => store.orders.slice(0, 5))

const getTagType = (status) => {
  if (status === '待付款') return 'info'
  if (status === '待发货') return 'warning'
  if (status === '待收货') return 'primary'
  if (status === '已完成') return 'success'
  if (status === '已取消') return 'danger'
  return ''
}
</script>

<template>
  <div class="dashboard-container">
    <div class="page-header">
      <h1 class="page-title">数据概览</h1>
      <p class="page-subtitle">2026 年 4 月 · 实时模拟数据</p>
    </div>

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stat-row">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <template #default>
            <div class="stat-content">
              <div class="stat-icon-wrapper" style="background: rgba(64, 158, 255, 0.1); color: #409eff;">
                <el-icon><User /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-label">今日访问量</div>
                <div class="stat-value">{{ store.dashboard.visitToday }}</div>
              </div>
            </div>
          </template>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <template #default>
            <div class="stat-content">
              <div class="stat-icon-wrapper" style="background: rgba(103, 194, 58, 0.1); color: #67c23a;">
                <el-icon><Money /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-label">本月销售额</div>
                <div class="stat-value">¥{{ store.dashboard.salesMonth.toLocaleString() }}</div>
              </div>
            </div>
          </template>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <template #default>
            <div class="stat-content">
              <div class="stat-icon-wrapper" style="background: rgba(230, 162, 70, 0.1); color: #e6a23c;">
                <el-icon><ShoppingCart /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-label">待处理订单</div>
                <div class="stat-value">{{ store.dashboard.pendingOrders }}</div>
              </div>
            </div>
          </template>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <template #default>
            <div class="stat-content">
              <div class="stat-icon-wrapper" style="background: rgba(144, 147, 153, 0.1); color: #909399;">
                <el-icon><Document /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-label">已发布资讯</div>
                <div class="stat-value">{{ store.dashboard.publishedArticles }}</div>
              </div>
            </div>
          </template>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表展示 -->
    <el-row :gutter="20" class="chart-row">
      <el-col :span="12">
        <el-card shadow="never" class="chart-card">
          <template #header>
            <div class="card-header">
              <span>各分类资讯阅读量</span>
            </div>
          </template>
          <div class="chart-content">
            <div v-for="item in store.dashboard.articleReads" :key="item.name" class="progress-item">
              <div class="progress-label">
                <span>{{ item.name }}</span>
                <span>{{ item.value }} 次</span>
              </div>
              <el-progress :percentage="Math.min(100, item.value / 50)" :color="item.color" :show-text="false" />
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="never" class="chart-card">
          <template #header>
            <div class="card-header">
              <span>商品分类销售额占比</span>
            </div>
          </template>
          <div class="chart-content">
            <div v-for="item in store.dashboard.salesByCategory" :key="item.name" class="progress-item">
              <div class="progress-label">
                <span>{{ item.name }}</span>
                <span>¥{{ item.value.toLocaleString() }}</span>
              </div>
              <el-progress :percentage="Math.min(100, item.value / 800 * 100)" :color="item.color" :show-text="false" />
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 最新订单列表 -->
    <el-card shadow="never" class="table-card">
      <template #header>
        <div class="card-header">
          <span>最新订单</span>
          <el-button link type="primary" @click="$router.push('/admin/orders')">
            查看全部 <el-icon><ArrowRight /></el-icon>
          </el-button>
        </div>
      </template>
      <el-table :data="latestOrders" style="width: 100%">
        <el-table-column prop="id" label="订单号" width="180" />
        <el-table-column prop="buyer" label="买家" width="120" />
        <el-table-column label="商品">
          <template #default="{ row }">
            {{ row.items[0]?.name }}等 {{ row.items.length }} 件
          </template>
        </el-table-column>
        <el-table-column label="支付金额">
          <template #default="{ row }">
            <span class="price">¥{{ row.payAmount.toLocaleString() }}</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="getTagType(row.status)">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<style scoped>
.dashboard-container {
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  margin-bottom: 24px;
}

.page-title {
  font-size: 28px;
  font-weight: 700;
  margin: 0 0 8px;
}

.page-subtitle {
  color: var(--el-text-color-secondary);
  margin: 0;
}

.stat-row {
  margin-bottom: 20px;
}

.stat-card {
  border: none;
  background: var(--el-bg-color-overlay);
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-icon-wrapper {
  width: 54px;
  height: 54px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
}

.stat-label {
  font-size: 14px;
  color: var(--el-text-color-secondary);
  margin-bottom: 4px;
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
  color: var(--el-text-color-primary);
}

.chart-row {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 700;
}

.chart-content {
  padding: 10px 0;
}

.progress-item {
  margin-bottom: 20px;
}

.progress-item:last-child {
  margin-bottom: 0;
}

.progress-label {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
  font-size: 14px;
}

.price {
  font-weight: 600;
  color: var(--el-color-success);
}

.table-card {
  margin-bottom: 20px;
}
</style>
