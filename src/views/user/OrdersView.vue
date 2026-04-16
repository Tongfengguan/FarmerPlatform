<script setup>
import { computed, ref } from 'vue'
import { orderStatuses } from '../../utils/constants'
import { usePlatformStore } from '../../stores/platform'
import { Money, CircleClose, Check, InfoFilled } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const store = usePlatformStore()
const status = ref('全部')

const orders = computed(() =>
  store.currentUserOrders.filter((order) => status.value === '全部' || order.status === status.value),
)

const getStatusType = (status) => {
  if (status === '待付款') return 'info'
  if (status === '待发货') return 'warning'
  if (status === '待收货') return 'primary'
  if (status === '已完成') return 'success'
  if (status === '已取消') return 'danger'
  return ''
}

const handlePay = async (orderId) => {
  await store.payOrder(orderId)
  ElMessage.success('支付成功，等待商家发货')
}

const handleCancel = (orderId) => {
  ElMessageBox.confirm('确定要取消该订单吗？', '取消订单', {
    confirmButtonText: '确定取消',
    cancelButtonText: '暂不',
    type: 'warning',
  }).then(async () => {
    await store.cancelOrder(orderId)
    ElMessage.success('订单已取消')
  })
}

const handleConfirm = (orderId) => {
  ElMessageBox.confirm('请确认您已收到商品且无质量问题', '确认收货', {
    confirmButtonText: '确认收货',
    cancelButtonText: '取消',
    type: 'success',
  }).then(async () => {
    await store.confirmOrder(orderId)
    ElMessage.success('交易完成，感谢您的购买')
  })
}
</script>

<template>
  <div class="orders-container">
    <div class="page-header">
      <h1 class="page-title">我的订单</h1>
      <p class="page-subtitle">查看所有购买记录，处理待付款或待收货订单。</p>
    </div>

    <el-card shadow="never" class="filter-card">
      <div class="status-tabs">
        <el-radio-group v-model="status" size="large">
          <el-radio-button v-for="item in orderStatuses" :key="item" :label="item" :value="item" />
        </el-radio-group>
      </div>
    </el-card>

    <div class="orders-list">
      <template v-if="orders.length">
        <el-card 
          v-for="order in orders" 
          :key="order.id" 
          shadow="hover" 
          class="order-card"
          :body-style="{ padding: '0' }"
        >
          <div class="order-header">
            <div class="order-meta">
              <span class="order-id">订单号：{{ order.id }}</span>
              <span class="order-date">{{ order.createdAt }}</span>
            </div>
            <el-tag :type="getStatusType(order.status)" effect="dark" size="small">{{ order.status }}</el-tag>
          </div>

          <div class="order-body">
            <div class="order-items">
              <div 
                v-for="item in order.items" 
                :key="`${order.id}-${item.productId}-${item.sku}`" 
                class="order-item"
              >
                <div class="item-main">
                  <div class="item-name">{{ item.name }}</div>
                  <div class="item-sku">规格：{{ item.sku }}</div>
                </div>
                <div class="item-price-qty">
                  <span class="item-price">¥{{ item.price }}</span>
                  <span class="item-qty">x{{ item.quantity }}</span>
                </div>
              </div>
            </div>
            
            <div class="order-summary-action">
              <div class="order-total">
                <span class="label">实付金额：</span>
                <span class="amount">¥{{ order.payAmount }}</span>
              </div>
              
              <div class="order-actions">
                <el-button 
                  v-if="order.status === '待付款'" 
                  type="primary" 
                  :icon="Money"
                  @click="handlePay(order.id)"
                >
                  继续支付
                </el-button>
                <el-button 
                  v-if="order.status === '待付款'" 
                  plain 
                  type="danger" 
                  :icon="CircleClose"
                  @click="handleCancel(order.id)"
                >
                  取消订单
                </el-button>
                <el-button 
                  v-if="order.status === '待收货'" 
                  type="success" 
                  :icon="Check"
                  @click="handleConfirm(order.id)"
                >
                  确认收货
                </el-button>
                <el-button 
                  v-if="order.status === '已完成' || order.status === '已取消'" 
                  plain 
                  :icon="InfoFilled"
                >
                  订单详情
                </el-button>
              </div>
            </div>
          </div>
        </el-card>
      </template>

      <el-empty 
        v-else 
        description="当前状态下暂无订单记录" 
        :image-size="200"
      >
        <el-button type="primary" @click="$router.push('/mall')">去逛逛</el-button>
      </el-empty>
    </div>
  </div>
</template>

<style scoped>
.orders-container {
  max-width: 1000px;
  margin: 0 auto;
  padding-bottom: 60px;
}

.page-header {
  margin-bottom: 24px;
}

.page-title {
  font-size: 32px;
  font-weight: 700;
  margin: 0 0 8px;
}

.page-subtitle {
  color: var(--el-text-color-secondary);
  margin: 0;
}

.filter-card {
  border: none;
  background: var(--el-bg-color-overlay);
  margin-bottom: 24px;
  padding: 16px 20px;
}

.orders-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.order-card {
  border: none;
  background: var(--el-bg-color-overlay);
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  background-color: var(--el-fill-color-darker);
  border-bottom: 1px solid var(--el-border-color-lighter);
}

.order-meta {
  display: flex;
  gap: 24px;
  align-items: center;
  font-size: 13px;
  color: var(--el-text-color-regular);
}

.order-id {
  font-weight: 500;
}

.order-date {
  color: var(--el-text-color-secondary);
}

.order-body {
  padding: 0;
  display: flex;
  flex-direction: column;
}

.order-items {
  display: flex;
  flex-direction: column;
  padding: 0 24px;
}

.order-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 0;
  border-bottom: 1px solid var(--el-border-color-lighter);
}

.item-main {
  flex: 1;
}

.item-name {
  font-weight: 600;
  font-size: 15px;
  color: var(--el-text-color-primary);
  margin-bottom: 6px;
}

.item-sku {
  font-size: 12px;
  color: var(--el-text-color-secondary);
}

.item-price-qty {
  text-align: right;
  min-width: 120px;
}

.item-price {
  font-weight: 600;
  font-size: 15px;
  display: block;
  margin-bottom: 4px;
}

.item-qty {
  font-size: 13px;
  color: var(--el-text-color-secondary);
}

.order-summary-action {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  background-color: var(--el-fill-color-blank);
}

.order-total {
  font-size: 14px;
}

.order-total .label {
  color: var(--el-text-color-regular);
}

.order-total .amount {
  font-size: 24px;
  font-weight: 700;
  color: var(--el-color-danger);
}

.order-actions {
  display: flex;
  gap: 12px;
}
</style>
