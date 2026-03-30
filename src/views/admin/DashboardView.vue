<script setup>
import { computed } from 'vue'
import { usePlatformStore } from '../../stores/platform'

const store = usePlatformStore()
const latestOrders = computed(() => store.orders.slice(0, 4))

const badgeClass = (value) => {
  if (value === '待发货') return 'badge-warning'
  if (value === '待收货') return 'badge-info'
  if (value === '已完成') return 'badge-success'
  if (value === '退款中') return 'badge-danger'
  return 'badge-muted'
}
</script>

<template>
  <section>
    <div class="admin-page-head">
      <h1>数据概览</h1>
      <p class="muted">2025 年 3 月 · 实时模拟数据</p>
    </div>

    <div class="stat-grid">
      <div class="stat-card card">
        <span class="muted">今日访问量</span>
        <strong>{{ store.dashboard.visitToday }}</strong>
      </div>
      <div class="stat-card card">
        <span class="muted">本月销售额</span>
        <strong>¥{{ store.dashboard.salesMonth.toLocaleString() }}</strong>
      </div>
      <div class="stat-card card">
        <span class="muted">待处理订单</span>
        <strong>{{ store.dashboard.pendingOrders }}</strong>
      </div>
      <div class="stat-card card">
        <span class="muted">已发布资讯</span>
        <strong>{{ store.dashboard.publishedArticles }}</strong>
      </div>
    </div>

    <div class="chart-grid">
      <div class="card chart-card">
        <h3>各分类资讯阅读量</h3>
        <div v-for="item in store.dashboard.articleReads" :key="item.name" class="bar-row">
          <span>{{ item.name }}</span>
          <div class="bar-track">
            <div class="bar-fill" :style="{ width: `${item.value / 50}%`, background: item.color }"></div>
          </div>
          <span>{{ item.value }}</span>
        </div>
      </div>
      <div class="card chart-card">
        <h3>商品分类销售额占比</h3>
        <div v-for="item in store.dashboard.salesByCategory" :key="item.name" class="bar-row">
          <span>{{ item.name }}</span>
          <div class="bar-track">
            <div class="bar-fill" :style="{ width: `${item.value / 800}%`, background: item.color }"></div>
          </div>
          <span>¥{{ item.value.toLocaleString() }}</span>
        </div>
      </div>
    </div>

    <div class="card table-card">
      <h3>最新订单</h3>
      <div class="table-wrap">
        <table class="table">
          <thead>
            <tr>
              <th>订单号</th>
              <th>买家</th>
              <th>商品</th>
              <th>金额</th>
              <th>状态</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="order in latestOrders" :key="order.id">
              <td>{{ order.id }}</td>
              <td>{{ order.buyer }}</td>
              <td>{{ order.items[0].name }}</td>
              <td>¥{{ order.payAmount }}</td>
              <td><span class="badge" :class="badgeClass(order.status)">{{ order.status }}</span></td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </section>
</template>

<style scoped>
.admin-page-head h1 {
  margin: 0 0 8px;
  font-size: 34px;
}

.stat-grid,
.chart-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 16px;
  margin-top: 20px;
}

.chart-grid {
  grid-template-columns: repeat(2, minmax(0, 1fr));
}

.stat-card,
.chart-card,
.table-card {
  padding: 22px;
}

.stat-card strong {
  display: block;
  margin-top: 12px;
  font-size: 36px;
}

.bar-row {
  display: grid;
  grid-template-columns: 88px 1fr 80px;
  gap: 12px;
  align-items: center;
  margin-top: 16px;
}

.bar-track {
  height: 8px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.08);
  overflow: hidden;
}

.bar-fill {
  height: 100%;
  border-radius: inherit;
}

.table-card {
  margin-top: 18px;
}
</style>
