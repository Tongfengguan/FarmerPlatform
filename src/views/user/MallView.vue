<script setup>
import { computed, ref } from 'vue'
import { RouterLink } from 'vue-router'
import { productCategories } from '../../data/mockData'
import { usePlatformStore } from '../../stores/platform'

const store = usePlatformStore()
const category = ref('全部')
const keyword = ref('')
const sortBy = ref('default')

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
</script>

<template>
  <section class="page-shell">
    <div class="page-head">
      <div>
        <h1>三农商城</h1>
        <p class="muted">覆盖农产品、农业生产资料和农机设备，当前版本支持浏览、立即购买和购物车下单。</p>
      </div>
    </div>

    <div class="filters card">
      <div class="toolbar">
        <input v-model="keyword" class="field" placeholder="搜索商品名称" />
        <select v-model="sortBy" class="select" style="width: 180px">
          <option value="default">默认排序</option>
          <option value="priceAsc">价格升序</option>
          <option value="priceDesc">价格降序</option>
          <option value="sales">销量优先</option>
        </select>
      </div>
      <div class="category-row">
        <button
          v-for="item in productCategories"
          :key="item"
          class="chip"
          :class="{ active: item === category }"
          @click="category = item"
        >
          {{ item }}
        </button>
      </div>
    </div>

    <div class="mall-grid">
      <RouterLink
        v-for="product in filteredProducts"
        :key="product.id"
        :to="`/products/${product.id}`"
        class="product-card card"
      >
        <img :src="product.image" :alt="product.name" />
        <div class="product-card__body">
          <div class="muted">{{ product.categoryL1 }} · {{ product.categoryL2 }}</div>
          <h3>{{ product.name }}</h3>
          <div class="product-card__price">
            <strong>¥{{ product.price }}</strong>
            <span v-if="product.oldPrice">¥{{ product.oldPrice }}</span>
          </div>
          <div class="product-card__meta">
            <span>销量 {{ product.salesCount }}</span>
            <span :style="{ color: product.stock ? 'var(--text-soft)' : 'var(--danger)' }">
              {{ product.stock ? `库存 ${product.stock}` : '暂时缺货' }}
            </span>
          </div>
        </div>
      </RouterLink>
    </div>
  </section>
</template>

<style scoped>
.page-head h1 {
  margin: 0;
  font-size: 36px;
}

.filters {
  margin: 20px 0 24px;
  padding: 18px;
}

.category-row {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
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

.mall-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 18px;
}

.product-card {
  overflow: hidden;
}

.product-card img {
  width: 100%;
  height: 240px;
  object-fit: cover;
}

.product-card__body {
  padding: 18px;
}

.product-card__body h3 {
  margin: 10px 0;
}

.product-card__price,
.product-card__meta {
  display: flex;
  justify-content: space-between;
  margin-top: 14px;
}

.product-card__price strong {
  color: #90f0c6;
  font-size: 26px;
}

.product-card__price span {
  color: var(--text-mute);
  text-decoration: line-through;
}
</style>
