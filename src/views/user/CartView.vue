<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { usePlatformStore } from '../../stores/platform'
import { Delete, ShoppingCart, Warning } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const store = usePlatformStore()
const router = useRouter()

const selectedTotal = computed(() =>
  store.cart.reduce((sum, item) => (item.selected ? sum + item.price * item.quantity : sum), 0),
)

const handleCheckout = async () => {
  const success = await store.checkoutSelected()
  if (success) {
    ElMessage.success('结算成功，订单已生成')
    router.push('/orders')
  } else {
    ElMessage.warning('请先选择要结算的商品')
  }
}

const handleRemove = (productId, sku) => {
  ElMessageBox.confirm('确定要将该商品移出购物车吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(() => {
    store.removeCartItem(productId, sku)
    ElMessage.success('移除成功')
  })
}
</script>

<template>
  <div class="cart-container">
    <div class="page-header">
      <h1 class="page-title">购物车</h1>
      <p class="page-subtitle">管理您挑选的商品，确认无误后即可结算。</p>
    </div>

    <el-row :gutter="24" class="cart-layout">
      <!-- 购物车列表 -->
      <el-col :span="16">
        <el-card shadow="never" class="cart-list-card">
          <template #header>
            <div class="list-header">
              <span>商品列表</span>
              <span class="selected-count">已选 {{ store.cartCount }} 件</span>
            </div>
          </template>

          <div v-if="store.cart.length" class="cart-items">
            <div v-for="item in store.cart" :key="`${item.productId}-${item.sku}`" class="cart-item">
              <div class="item-checkbox">
                <el-checkbox 
                  :model-value="item.selected" 
                  size="large"
                  @change="store.toggleCartSelection(item.productId, item.sku)" 
                />
              </div>
              <div class="item-image-wrap">
                <el-image :src="item.image" fit="cover" class="item-image" />
              </div>
              <div class="item-info">
                <div class="item-name">{{ item.name }}</div>
                <div class="item-sku">规格：{{ item.sku }}</div>
              </div>
              <div class="item-price">¥{{ item.price }}</div>
              <div class="item-quantity">
                <el-input-number 
                  :model-value="item.quantity" 
                  :min="1" 
                  size="small"
                  @change="(val) => store.updateCartItem(item.productId, item.sku, val)"
                />
              </div>
              <div class="item-subtotal">¥{{ item.price * item.quantity }}</div>
              <div class="item-actions">
                <el-button type="danger" link :icon="Delete" @click="handleRemove(item.productId, item.sku)">删除</el-button>
              </div>
            </div>
          </div>
          
          <el-empty v-else description="购物车还是空的，去商城逛逛吧" :image-size="200">
            <el-button type="primary" @click="router.push('/mall')">前往商城</el-button>
          </el-empty>
        </el-card>
      </el-col>

      <!-- 结算信息 -->
      <el-col :span="8">
        <el-card shadow="never" class="summary-card">
          <template #header>
            <div class="summary-header">结算信息</div>
          </template>
          
          <div class="summary-body">
            <div class="summary-row">
              <span class="label">已选商品金额</span>
              <span class="value highlight">¥{{ selectedTotal }}</span>
            </div>
            <div class="summary-row">
              <span class="label">运费估算</span>
              <span class="value">满 99 元包邮</span>
            </div>
            
            <el-divider border-style="dashed" />
            
            <div class="summary-total">
              <span class="label">应付总额：</span>
              <span class="total-price">¥{{ selectedTotal }}</span>
            </div>

            <el-button 
              type="primary" 
              size="large" 
              class="checkout-btn" 
              :icon="ShoppingCart"
              :disabled="!store.cart.some(item => item.selected)"
              @click="handleCheckout"
            >
              提交订单
            </el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<style scoped>
.cart-container {
  max-width: 1200px;
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

.cart-layout {
  align-items: flex-start;
}

.cart-list-card, .summary-card {
  border: none;
  background: var(--el-bg-color-overlay);
}

.list-header, .summary-header {
  font-size: 16px;
  font-weight: 600;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.selected-count {
  font-size: 13px;
  color: var(--el-text-color-secondary);
  font-weight: normal;
}

.cart-items {
  display: flex;
  flex-direction: column;
}

.cart-item {
  display: flex;
  align-items: center;
  padding: 20px 0;
  border-bottom: 1px solid var(--el-border-color-lighter);
  gap: 16px;
}

.cart-item:last-child {
  border-bottom: none;
  padding-bottom: 0;
}

.cart-item:first-child {
  padding-top: 0;
}

.item-checkbox {
  width: 40px;
  text-align: center;
}

.item-image-wrap {
  width: 80px;
  height: 80px;
  border-radius: 8px;
  overflow: hidden;
  background-color: var(--el-fill-color-darker);
  flex-shrink: 0;
}

.item-image {
  width: 100%;
  height: 100%;
}

.item-info {
  flex: 1;
  min-width: 0;
}

.item-name {
  font-weight: 600;
  color: var(--el-text-color-primary);
  margin-bottom: 8px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.item-sku {
  font-size: 12px;
  color: var(--el-text-color-secondary);
}

.item-price {
  width: 100px;
  font-weight: 600;
}

.item-quantity {
  width: 130px;
}

.item-subtotal {
  width: 100px;
  font-weight: 700;
  color: var(--el-color-success);
}

.item-actions {
  width: 80px;
  text-align: right;
}

.summary-card {
  position: sticky;
  top: 24px;
}

.summary-body {
  padding: 10px 0;
}

.summary-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  font-size: 14px;
}

.summary-row .label {
  color: var(--el-text-color-regular);
}

.summary-row .value {
  color: var(--el-text-color-primary);
  font-weight: 500;
}

.summary-row .value.highlight {
  font-weight: 700;
}

.summary-total {
  display: flex;
  justify-content: space-between;
  align-items: baseline;
  margin-bottom: 24px;
}

.summary-total .label {
  font-size: 16px;
  font-weight: 600;
}

.total-price {
  font-size: 32px;
  font-weight: 700;
  color: var(--el-color-danger);
}

.checkout-btn {
  width: 100%;
  font-size: 16px;
  font-weight: 600;
}
</style>
