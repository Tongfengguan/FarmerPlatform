<script setup>
import { computed, ref, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { usePlatformStore } from '../../stores/platform'
import { ShoppingCart, Goods, Select, ArrowRight, Service } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const store = usePlatformStore()

const product = computed(() => store.products.find((item) => item.id === Number(route.params.id)))

// 确保在产品加载后设置默认 SKU
const selectedSku = ref('')
const quantity = ref(1)

watch(product, (newProduct) => {
  if (newProduct && !selectedSku.value) {
    selectedSku.value = newProduct.skus[0]?.name ?? ''
  }
}, { immediate: true })

const currentSkuPrice = computed(() => {
  if (!product.value) return 0
  const sku = product.value.skus.find(s => s.name === selectedSku.value)
  return sku ? sku.price : product.value.price
})

const currentSkuStock = computed(() => {
  if (!product.value) return 0
  const sku = product.value.skus.find(s => s.name === selectedSku.value)
  return sku ? sku.stock : product.value.stock
})

const handleAddCart = () => {
  if (!product.value || !currentSkuStock.value) return
  store.addToCart(product.value, selectedSku.value, quantity.value)
  ElMessage.success('已加入购物车')
}

const handleBuyNow = async () => {
  if (!product.value || !currentSkuStock.value) return
  try {
    await store.buyNow(product.value, selectedSku.value, quantity.value)
    ElMessage.success('订单已创建')
    router.push('/orders')
  } catch (error) {
    ElMessage.error(error.message || '购买失败')
  }
}
</script>

<template>
  <div class="product-detail-container" v-if="product">
    <el-page-header @back="router.push('/mall')" title="返回商城" class="detail-header">
      <template #content>
        <el-breadcrumb separator-icon="ArrowRight">
          <el-breadcrumb-item>{{ product.categoryL1 }}</el-breadcrumb-item>
          <el-breadcrumb-item>{{ product.categoryL2 }}</el-breadcrumb-item>
          <el-breadcrumb-item>{{ product.name }}</el-breadcrumb-item>
        </el-breadcrumb>
      </template>
    </el-page-header>

    <el-card shadow="never" class="product-main-card">
      <el-row :gutter="40">
        <!-- 左侧：商品图片 -->
        <el-col :span="10">
          <div class="image-gallery">
            <el-image 
              :src="product.image" 
              fit="cover" 
              class="main-image"
              :preview-src-list="[product.image]"
              preview-teleported
            />
          </div>
        </el-col>

        <!-- 右侧：商品信息与购买操作 -->
        <el-col :span="14">
          <div class="product-info-wrap">
            <h1 class="product-title">{{ product.name }}</h1>
            <p class="product-brief">{{ product.detail }}</p>

            <div class="price-panel">
              <div class="price-label">平台价</div>
              <div class="price-value">
                <span class="currency">¥</span>
                <span class="amount">{{ currentSkuPrice }}</span>
                <span v-if="product.oldPrice" class="old-price">原价 ¥{{ product.oldPrice }}</span>
              </div>
              <div class="sales-info">累计销量：{{ product.salesCount }}</div>
            </div>

            <div class="options-panel">
              <!-- 规格选择 -->
              <div class="option-row">
                <div class="option-label">规格选择</div>
                <div class="option-content">
                  <el-radio-group v-model="selectedSku" size="large">
                    <el-radio-button 
                      v-for="sku in product.skus" 
                      :key="sku.name" 
                      :label="sku.name" 
                      :value="sku.name" 
                      :disabled="sku.stock <= 0"
                    />
                  </el-radio-group>
                </div>
              </div>

              <!-- 运费说明 -->
              <div class="option-row">
                <div class="option-label">配送服务</div>
                <div class="option-content service-tags">
                  <el-tag size="small" type="info" effect="plain"><el-icon><Van /></el-icon> {{ product.freightType }}</el-tag>
                  <el-tag size="small" type="info" effect="plain"><el-icon><Service /></el-icon> 平台品质保障</el-tag>
                </div>
              </div>

              <!-- 购买数量 -->
              <div class="option-row">
                <div class="option-label">购买数量</div>
                <div class="option-content quantity-selector">
                  <el-input-number 
                    v-model="quantity" 
                    :min="1" 
                    :max="currentSkuStock" 
                    size="large"
                    :disabled="currentSkuStock <= 0"
                  />
                  <span class="stock-info" :class="{ 'text-danger': currentSkuStock < 10 }">
                    {{ currentSkuStock > 0 ? `库存 ${currentSkuStock} 件` : '商品已售罄' }}
                  </span>
                </div>
              </div>
            </div>

            <!-- 操作按钮 -->
            <div class="action-panel">
              <el-button 
                type="warning" 
                size="large" 
                :icon="ShoppingCart" 
                class="action-btn cart-btn"
                :disabled="currentSkuStock <= 0"
                @click="handleAddCart"
              >
                加入购物车
              </el-button>
              <el-button 
                type="danger" 
                size="large" 
                :icon="Goods" 
                class="action-btn buy-btn"
                :disabled="currentSkuStock <= 0"
                @click="handleBuyNow"
              >
                立即购买
              </el-button>
            </div>
          </div>
        </el-col>
      </el-row>
    </el-card>

    <!-- 商品详情说明 -->
    <el-card shadow="never" class="product-desc-card">
      <template #header>
        <div class="desc-header">商品图文详情</div>
      </template>
      <div class="desc-content">
        <!-- 实际项目中这里通常是富文本渲染的详细图文介绍 -->
        <el-empty description="此商品暂无更详细的图文说明" :image-size="150" />
      </div>
    </el-card>
  </div>
  <el-empty v-else description="找不到该商品信息" :image-size="200">
    <el-button type="primary" @click="$router.push('/mall')">返回商城</el-button>
  </el-empty>
</template>

<style scoped>
.product-detail-container {
  max-width: 1200px;
  margin: 0 auto;
  padding-bottom: 60px;
}

.detail-header {
  margin-bottom: 24px;
}

.product-main-card {
  border: none;
  background: var(--el-bg-color-overlay);
  margin-bottom: 24px;
  padding: 20px;
}

.image-gallery {
  width: 100%;
  aspect-ratio: 1 / 1;
  border-radius: 12px;
  overflow: hidden;
  background-color: var(--el-fill-color-darker);
  border: 1px solid var(--el-border-color-lighter);
}

.main-image {
  width: 100%;
  height: 100%;
}

.product-info-wrap {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.product-title {
  margin: 0 0 16px;
  font-size: 26px;
  font-weight: 700;
  line-height: 1.4;
  color: var(--el-text-color-primary);
}

.product-brief {
  margin: 0 0 24px;
  font-size: 14px;
  color: var(--el-text-color-secondary);
  line-height: 1.6;
}

.price-panel {
  background-color: var(--el-fill-color-lighter);
  padding: 20px 24px;
  border-radius: 8px;
  display: flex;
  align-items: baseline;
  gap: 16px;
  margin-bottom: 24px;
  position: relative;
}

.price-label {
  font-size: 14px;
  color: var(--el-text-color-regular);
}

.price-value {
  display: flex;
  align-items: baseline;
  gap: 4px;
}

.currency {
  font-size: 18px;
  font-weight: 700;
  color: var(--el-color-danger);
}

.amount {
  font-size: 36px;
  font-weight: 700;
  color: var(--el-color-danger);
  line-height: 1;
}

.old-price {
  margin-left: 12px;
  font-size: 14px;
  color: var(--el-text-color-placeholder);
  text-decoration: line-through;
}

.sales-info {
  position: absolute;
  right: 24px;
  top: 50%;
  transform: translateY(-50%);
  font-size: 13px;
  color: var(--el-text-color-secondary);
}

.options-panel {
  display: flex;
  flex-direction: column;
  gap: 24px;
  margin-bottom: 36px;
  padding: 0 12px;
}

.option-row {
  display: flex;
  align-items: flex-start;
}

.option-label {
  width: 80px;
  font-size: 14px;
  color: var(--el-text-color-regular);
  padding-top: 10px;
}

.option-content {
  flex: 1;
}

.service-tags {
  display: flex;
  gap: 12px;
  padding-top: 8px;
}

.quantity-selector {
  display: flex;
  align-items: center;
  gap: 16px;
}

.stock-info {
  font-size: 13px;
  color: var(--el-text-color-secondary);
}

.text-danger {
  color: var(--el-color-danger);
}

.action-panel {
  display: flex;
  gap: 16px;
  margin-top: auto;
  padding: 0 12px;
}

.action-btn {
  width: 180px;
  font-size: 16px;
  font-weight: 600;
}

.cart-btn {
  background-color: #ff9900;
  border-color: #ff9900;
}

.cart-btn:hover {
  background-color: #ffb340;
  border-color: #ffb340;
}

.buy-btn {
  background-color: #ff4d4f;
  border-color: #ff4d4f;
}

.product-desc-card {
  border: none;
  background: var(--el-bg-color-overlay);
}

.desc-header {
  font-size: 18px;
  font-weight: 600;
  color: var(--el-text-color-primary);
  border-left: 4px solid var(--el-color-primary);
  padding-left: 12px;
  line-height: 1;
}

.desc-content {
  min-height: 300px;
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>
