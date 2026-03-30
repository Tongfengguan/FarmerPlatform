<script setup>
import { computed, ref } from 'vue'
import { orderStatuses } from '../../data/mockData'
import { usePlatformStore } from '../../stores/platform'

const store = usePlatformStore()
const status = ref('全部')

const orders = computed(() =>
  store.currentUserOrders.filter((order) => status.value === '全部' || order.status === status.value),
)

const badgeClass = (value) => {
  if (value === '待发货') return 'badge-warning'
  if (value === '待收货') return 'badge-info'
  if (value === '已完成') return 'badge-success'
  if (value === '退款中') return 'badge-danger'
  return 'badge-muted'
}
</script>

<template>
  <section class="page-shell">
    <div class="page-head">
      <div>
        <h1>我的订单</h1>
        <p class="muted">支持订单状态筛选，并提供继续支付、取消订单和确认收货等基础操作。</p>
      </div>
    </div>

    <div class="status-row">
      <button
        v-for="item in orderStatuses"
        :key="item"
        class="chip"
        :class="{ active: item === status }"
        @click="status = item"
      >
        {{ item }}
      </button>
    </div>

    <div class="orders-list">
      <div v-for="order in orders" :key="order.id" class="card order-card">
        <div class="order-card__head">
          <div>
            <strong>订单号 {{ order.id }}</strong>
            <div class="muted">{{ order.createdAt }}</div>
          </div>
          <span class="badge" :class="badgeClass(order.status)">{{ order.status }}</span>
        </div>

        <div
          v-for="item in order.items"
          :key="`${order.id}-${item.productId}-${item.sku}`"
          class="order-item"
        >
          <div>
            <strong>{{ item.name }}</strong>
            <div class="muted">规格 {{ item.sku }} · 数量 {{ item.quantity }}</div>
          </div>
          <div>¥{{ item.price * item.quantity }}</div>
        </div>

        <div class="order-card__foot">
          <span class="muted">实付金额 ¥{{ order.payAmount }}</span>
          <div class="order-actions">
            <button
              v-if="order.status === '待付款'"
              class="btn btn-primary"
              @click="store.payOrder(order.id)"
            >
              继续支付
            </button>
            <button
              v-if="order.status === '待付款'"
              class="btn btn-danger"
              @click="store.cancelOrder(order.id)"
            >
              取消订单
            </button>
            <button
              v-if="order.status === '待收货'"
              class="btn btn-primary"
              @click="store.confirmOrder(order.id)"
            >
              确认收货
            </button>
          </div>
        </div>
      </div>

      <div v-if="!orders.length" class="card empty">当前筛选条件下暂无订单。</div>
    </div>
  </section>
</template>

<style scoped>
.page-head h1 {
  margin: 0;
  font-size: 36px;
}

.status-row {
  display: flex;
  gap: 10px;
  margin: 20px 0 24px;
}

.chip {
  padding: 10px 14px;
  border-radius: 999px;
  border: 1px solid var(--line);
  background: transparent;
  color: var(--text-soft);
  cursor: pointer;
}

.chip.active {
  background: rgba(35, 176, 125, 0.18);
  color: #9cf4cf;
}

.orders-list {
  display: grid;
  gap: 18px;
}

.order-card {
  padding: 22px;
}

.order-card__head,
.order-card__foot,
.order-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 14px;
}

.order-item {
  padding: 16px 0;
  border-bottom: 1px solid var(--line);
}

.order-card__foot {
  padding-top: 18px;
}

.order-actions {
  display: flex;
  gap: 10px;
}
</style>
