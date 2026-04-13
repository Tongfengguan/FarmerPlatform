<script setup>
import { computed, ref, onMounted } from 'vue'
import { productCategories } from '../../data/mockData'
import { usePlatformStore } from '../../stores/platform'
import { Search, Sort, ShoppingBag } from '@element-plus/icons-vue'

const store = usePlatformStore()
const category = ref('全部')
const keyword = ref('')
const sortBy = ref('default')
const loading = ref(false)

const filteredProducts = computed(() => {
  const list = store.activeProducts.filter((product) => {
    const categoryMatch = category.value === '全部' || product.categoryL1 === category.value
    const keywordMatch = !keyword.value || product.name.includes(keyword.value)
    return categoryMatch && keywordMatch
  })

  if (sortBy.value === 'priceAsc') return [...list].sort((a, b) => a.price - b.price)
  if (sortBy.value === 'priceDesc') return [...list].sort((a, b) => b.price - a.price)
  if (sortBy.value === 'sales') return [...list].sort((a, b) => b.salesCount - a.salesCount)
  return list
})

onMounted(async () => {
  loading.value = true
  await store.fetchProducts(0, 50) // Fetch a larger batch for mall browsing
  loading.value = false
})
</script>

<template>
  <div class="mall-container">
    <div class="page-header">
      <div>
        <h1 class="page-title">三农商城</h1>
        <p class="page-subtitle">助力乡村振兴，精选优质农资与地道农产品。</p>
      </div>
    </div>

    <!-- 筛选工具栏 -->
    <el-card shadow="never" class="filter-card">
      <div class="filter-row">
        <div class="category-area">
          <el-segmented
            v-model="category"
            :options="productCategories"
            size="large"
            class="custom-segmented"
          />
        </div>
        <div class="search-area">
          <el-input
            v-model="keyword"
            placeholder="搜索您心仪的商品..."
            :prefix-icon="Search"
            clearable
            class="search-input"
          />
          <el-select v-model="sortBy" placeholder="排序方式" style="width: 140px">
            <el-option label="默认排序" value="default" />
            <el-option label="价格升序" value="priceAsc" />
            <el-option label="价格降序" value="priceDesc" />
            <el-option label="销量优先" value="sales" />
          </el-select>
        </div>
      </div>
    </el-card>

    <!-- 商品列表 -->
    <div v-loading="loading" class="mall-main">
      <el-row :gutter="20" v-if="filteredProducts.length">
        <el-col 
          v-for="product in filteredProducts" 
          :key="product.id" 
          :xs="24" :sm="12" :md="8" :lg="6"
          class="product-col"
        >
          <el-card 
            :body-style="{ padding: '0px' }" 
            shadow="hover" 
            class="product-card"
            @click="$router.push(`/products/${product.id}`)"
          >
            <div class="image-wrapper">
              <el-image 
                :src="product.image" 
                fit="cover" 
                lazy
                class="product-image"
              />
              <div class="category-tag">{{ product.categoryL2 }}</div>
            </div>
            <div class="product-info">
              <div class="brand-line">{{ product.categoryL1 }}</div>
              <h3 class="product-name">{{ product.name }}</h3>
              <div class="price-line">
                <span class="currency">¥</span>
                <span class="amount">{{ product.price }}</span>
                <span v-if="product.oldPrice" class="old-price">¥{{ product.oldPrice }}</span>
              </div>
              <div class="meta-line">
                <span class="sales">销量 {{ product.salesCount }}</span>
                <span :class="['stock', product.stock > 0 ? '' : 'out']">
                  {{ product.stock > 0 ? `库存 ${product.stock}` : '暂时缺货' }}
                </span>
              </div>
              <div class="card-actions">
                <el-button type="primary" :icon="ShoppingBag" round class="buy-btn">立即购买</el-button>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <el-empty 
        v-else 
        description="没有找到符合条件的商品，换个关键词试试吧" 
        :image-size="200"
      />
    </div>
  </div>
</template>

<style scoped>
.mall-container {
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

.filter-card {
  margin-bottom: 30px;
  border: none;
  background: var(--el-bg-color-overlay);
}

.filter-row {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.search-area {
  display: flex;
  gap: 12px;
  align-items: center;
}

.search-input {
  flex: 1;
}

.product-col {
  margin-bottom: 20px;
}

.product-card {
  height: 100%;
  cursor: pointer;
  border: none;
  background: var(--el-bg-color-overlay);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.product-card:hover {
  transform: translateY(-5px);
}

.image-wrapper {
  position: relative;
  height: 240px;
  overflow: hidden;
  background-color: var(--el-fill-color-darker); /* 统一深色背景 */
}

.product-image {
  width: 100%;
  height: 100%;
  transition: transform 0.6s cubic-bezier(0.25, 1, 0.5, 1);
  backface-visibility: hidden;
  transform: translateZ(0);
}

.product-card:hover .product-image {
  transform: scale(1.08);
}

.category-tag {
  position: absolute;
  bottom: 12px;
  right: 12px;
  background: rgba(0, 0, 0, 0.5);
  backdrop-filter: blur(4px);
  color: white;
  padding: 4px 10px;
  border-radius: 6px;
  font-size: 12px;
}

.product-info {
  padding: 16px;
}

.brand-line {
  font-size: 12px;
  color: var(--el-color-primary);
  font-weight: 600;
  margin-bottom: 6px;
  text-transform: uppercase;
  letter-spacing: 1px;
}

.product-name {
  margin: 0 0 12px;
  font-size: 18px;
  font-weight: 600;
  line-height: 1.4;
  height: 50px;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  color: var(--el-text-color-primary);
}

.price-line {
  display: flex;
  align-items: baseline;
  gap: 4px;
  margin-bottom: 12px;
}

.currency {
  font-size: 14px;
  color: var(--el-color-success);
  font-weight: 700;
}

.amount {
  font-size: 24px;
  font-weight: 700;
  color: var(--el-color-success);
}

.old-price {
  font-size: 14px;
  color: var(--el-text-color-placeholder);
  text-decoration: line-through;
  margin-left: 4px;
}

.meta-line {
  display: flex;
  justify-content: space-between;
  font-size: 13px;
  color: var(--el-text-color-secondary);
  margin-bottom: 16px;
}

.stock.out {
  color: var(--el-color-danger);
}

.buy-btn {
  width: 100%;
}

.custom-segmented {
  --el-segmented-bg-color: var(--el-fill-color-darker);
  --el-segmented-item-selected-bg-color: var(--el-color-primary);
  --el-segmented-item-selected-color: white;
}

@media (min-width: 768px) {
  .filter-row {
    flex-direction: row;
    justify-content: space-between;
    align-items: center;
  }
  
  .search-area {
    min-width: 450px;
  }
}
</style>
