<script setup>
import { computed, ref, onMounted } from 'vue'
import { productCategories } from '../../utils/constants'
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
        <h1 class="page-title fira-code">三农商城</h1>
        <p class="page-subtitle">助力乡村振兴，精选优质农产与农机具。</p>
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
          <el-select v-model="sortBy" placeholder="排序方式" class="sort-select">
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
      <el-row :gutter="24" v-if="filteredProducts.length">
        <el-col 
          v-for="product in filteredProducts" 
          :key="product.id" 
          :xs="24" :sm="12" :md="8" :lg="6"
          class="product-col"
        >
          <el-card 
            :body-style="{ padding: '0px' }" 
            shadow="hover" 
            class="product-card clickable"
            @click="$router.push(`/products/${product.id}`)"
          >
            <div class="image-wrapper">
              <el-image 
                :src="product.image" 
                fit="cover" 
                lazy
                class="product-image"
              />
              <div class="category-tag fira-code">{{ product.categoryL2 }}</div>
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
                <el-tag size="small" :type="product.stock > 0 ? 'success' : 'danger'" effect="plain">
                  {{ product.stock > 0 ? '现货' : '缺货' }}
                </el-tag>
              </div>
              <div class="card-actions">
                <el-button type="primary" :icon="ShoppingBag" class="buy-btn">
                  加入购物车
                </el-button>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <el-empty 
        v-else 
        description="没有找到符合条件的商品" 
        :image-size="200"
      />
    </div>
  </div>
</template>

<style scoped>
.mall-container {
  max-width: 1280px;
  margin: 0 auto;
  padding: 0 var(--space-md) var(--space-3xl);
}

.page-header {
  margin-bottom: var(--space-xl);
}

.page-title {
  font-size: 36px;
  font-weight: 700;
  margin: 0 0 var(--space-xs);
  color: var(--text);
}

.page-subtitle {
  color: var(--text-soft);
  font-size: 16px;
  margin: 0;
}

.filter-card {
  margin-bottom: var(--space-2xl);
  border: 1px solid var(--line);
  background: var(--bg-soft);
  border-radius: var(--radius);
}

.filter-row {
  display: flex;
  flex-direction: column;
  gap: var(--space-lg);
}

.search-area {
  display: flex;
  gap: var(--space-md);
  align-items: center;
}

.search-input {
  flex: 1;
}

.sort-select {
  width: 160px;
}

.product-col {
  margin-bottom: var(--space-lg);
}

.product-card {
  height: 100%;
  border: 1px solid var(--line);
  background: var(--bg-soft);
  border-radius: var(--radius);
  overflow: hidden;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.clickable {
  cursor: pointer;
}

.product-card:hover {
  transform: translateY(-8px);
  box-shadow: var(--shadow-xl);
  border-color: var(--color-secondary);
}

.image-wrapper {
  position: relative;
  height: 260px;
  overflow: hidden;
}

.product-image {
  width: 100%;
  height: 100%;
  transition: transform 0.6s cubic-bezier(0.25, 1, 0.5, 1);
}

.product-card:hover .product-image {
  transform: scale(1.08);
}

.category-tag {
  position: absolute;
  top: 16px;
  right: 16px;
  background: var(--color-primary);
  color: white;
  padding: 4px 12px;
  border-radius: 8px;
  font-size: 12px;
  font-weight: 600;
}

.product-info {
  padding: var(--space-lg);
}

.brand-line {
  font-size: 12px;
  color: var(--color-secondary);
  font-weight: 600;
  margin-bottom: var(--space-xs);
  text-transform: uppercase;
  letter-spacing: 1px;
}

.product-name {
  margin: 0 0 var(--space-md);
  font-size: 20px;
  font-weight: 600;
  line-height: 1.4;
  height: 56px;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  color: var(--text);
}

.price-line {
  display: flex;
  align-items: baseline;
  gap: 4px;
  margin-bottom: var(--space-md);
}

.currency {
  font-size: 16px;
  color: var(--color-cta);
  font-weight: 700;
}

.amount {
  font-size: 28px;
  font-weight: 700;
  color: var(--color-cta);
}

.old-price {
  font-size: 14px;
  color: var(--text-mute);
  text-decoration: line-through;
  margin-left: var(--space-xs);
}

.meta-line {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 14px;
  color: var(--text-soft);
  margin-bottom: var(--space-lg);
}

.buy-btn {
  width: 100%;
  background-color: var(--color-cta) !important;
  border-color: var(--color-cta) !important;
  height: 44px;
  font-weight: 600;
  border-radius: 12px;
}

.buy-btn:hover {
  opacity: 0.9;
  transform: scale(1.02);
}

.custom-segmented {
  --el-segmented-bg-color: var(--bg);
  --el-segmented-item-selected-bg-color: var(--color-primary);
  --el-segmented-item-selected-color: white;
}

@media (min-width: 768px) {
  .filter-row {
    flex-direction: row;
    justify-content: space-between;
    align-items: center;
  }
  
  .search-area {
    min-width: 500px;
  }
}
</style>
