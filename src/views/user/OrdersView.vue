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
  try {
    await store.payOrder(orderId)
    ElMessage.success('支付成功，等待商家发货')
  } catch (error) {
    ElMessage.error(error.message || '支付失败')
  }
}

const handleCancel = (orderId) => {
  ElMessageBox.confirm('确定要取消该订单吗？', '取消订单', {
    confirmButtonText: '确定取消',
    cancelButtonText: '暂不',
    type: 'warning',
  }).then(async () => {
    try {
      await store.cancelOrder(orderId)
      ElMessage.success('订单已取消')
    } catch (error) {
      ElMessage.error(error.message || '取消失败')
    }
  }).catch(() => {})
}

const handleConfirm = (orderId) => {
  ElMessageBox.confirm('请确认您已收到商品且无质量问题', '确认收货', {
    confirmButtonText: '确认收货',
    cancelButtonText: '取消',
    type: 'success',
  }).then(async () => {
    try {
      await store.confirmOrder(orderId)
      ElMessage.success('交易完成，感谢您的购买')
    } catch (error) {
      ElMessage.error(error.message || '操作失败')
    }
  }).catch(() => {})
}

const showDetail = ref(false)
const selectedOrder = ref(null)

const handleDetail = (order) => {
  selectedOrder.value = order
  showDetail.value = true
}
</script>

<template>
  <div class="orders-container">
    <div class="page-header">
      <h1 class="page-title">我的订单</h1>
      <p class="page-subtitle">查看所有购买记录，处理待付款或待收货订单。</p>
    </div>

    <el-card shadow="never" class="filter-card">
      <el-radio-group v-model="status" size="large">
        <el-radio-button v-for="item in orderStatuses" :key="item" :label="item" :value="item" />
      </el-radio-group>
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
                  plain 
                  :icon="InfoFilled"
                  @click="handleDetail(order)"
                >
                  订单详情
                </el-button>
              </div>
            </div>
          </div>
        </el-card>
      </template>

      <el-empty v-else description="暂无订单记录" :image-size="200">
        <el-button type="primary" @click="$router.push('/mall')">去逛逛</el-button>
      </el-empty>
    </div>

    <!-- 详情侧边栏 -->
    <el-drawer
      v-model="showDetail"
      title="订单详情"
      size="500px"
      destroy-on-close
    >
      <div v-if="selectedOrder" class="drawer-inner">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="订单编号">{{ selectedOrder.id }}</el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ selectedOrder.createdAt }}</el-descriptions-item>
          <el-descriptions-item label="当前状态">
            <el-tag :type="getStatusType(selectedOrder.status)">{{ selectedOrder.status }}</el-tag>
          </el-descriptions-item>
        </el-descriptions>

        <h3 class="drawer-title">收货人信息</h3>
        <el-descriptions :column="1" border v-if="selectedOrder.receiver">
          <el-descriptions-item label="收货人">{{ selectedOrder.receiver?.name || '未知' }}</el-descriptions-item>
          <el-descriptions-item label="联系电话">{{ selectedOrder.receiver?.phone || '未知' }}</el-descriptions-item>
          <el-descriptions-item label="收货地址">{{ selectedOrder.receiver?.address || '未知' }}</el-descriptions-item>
        </el-descriptions>
        <el-empty v-else description="暂无收货地址数据" :image-size="60" />

        <h3 class="drawer-title">商品清单</h3>
        <!-- 使用可选链确保 items 存在 -->
        <el-table :data="selectedOrder.items || []" border stripe size="small">
          <el-table-column prop="name" label="商品名称" />
          <el-table-column prop="sku" label="规格" width="100" />
          <el-table-column label="单价/数量" width="120">
            <template #default="{ row }">
              ¥{{ row.price }} x {{ row.quantity }}
            </template>
          </el-table-column>
        </el-table>

        <div class="drawer-footer-box">
          <div class="footer-line">
            <span>实付总额</span>
            <span class="total-price">¥{{ selectedOrder.payAmount }}</span>
          </div>
          <div v-if="selectedOrder.shipNo" class="ship-info-box">
            <p><strong>物流信息：</strong>{{ selectedOrder.shipCompany }} [{{ selectedOrder.shipNo }}]</p>
          </div>
        </div>
      </div>
    </el-drawer>
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
  margin-bottom: 24px;
  border: none;
}

.orders-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.order-card {
  border: none;
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 14px 20px;
  background-color: var(--el-fill-color-light);
  border-bottom: 1px solid var(--el-border-color-lighter);
}

.order-meta {
  display: flex;
  gap: 20px;
  font-size: 13px;
  color: var(--el-text-color-regular);
}

.order-body {
  padding: 0;
}

.order-items {
  padding: 0 20px;
}

.order-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 0;
  border-bottom: 1px solid var(--el-border-color-extra-light);
}

.item-name {
  font-weight: 600;
  margin-bottom: 4px;
}

.item-sku {
  font-size: 12px;
  color: var(--el-text-color-secondary);
}

.item-price-qty {
  text-align: right;
}

.item-price {
  font-weight: 600;
  display: block;
}

.order-summary-action {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  background-color: var(--el-bg-color);
}

.amount {
  font-size: 22px;
  font-weight: 700;
  color: var(--el-color-danger);
}

.order-actions {
  display: flex;
  gap: 10px;
}

/* 抽屉样式 */
.drawer-inner {
  padding: 0 10px;
}

.drawer-title {
  margin: 24px 0 12px;
  font-size: 16px;
  font-weight: 600;
}

.drawer-footer-box {
  margin-top: 24px;
  padding: 16px;
  background-color: var(--el-fill-color-light);
  border-radius: 8px;
}

.footer-line {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.total-price {
  font-size: 24px;
  font-weight: 700;
  color: var(--el-color-danger);
}

.ship-info-box {
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid var(--el-border-color-lighter);
  font-size: 14px;
}
</style>
