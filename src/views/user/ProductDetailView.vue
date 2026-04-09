<script setup>
import { computed, ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { usePlatformStore } from '../../stores/platform'

const route = useRoute()
const router = useRouter()
const store = usePlatformStore()

const product = computed(() => store.products.find((item) => item.id === Number(route.params.id)))
const selectedSku = ref(product.value?.skus[0]?.name ?? '')
const quantity = ref(1)

const handleAddCart = () => {
  if (!product.value || !product.value.stock) return
  store.addToCart(product.value, selectedSku.value, quantity.value)
  router.push('/cart')
}

const handleBuyNow = async () => {
  if (!product.value || !product.value.stock) return
  await store.buyNow(product.value, selectedSku.value, quantity.value)
  router.push('/orders')
}
</script>

<template>
  <section v-if="product" class="page-shell product-detail">
    <div class="product-detail__main card">
      <img :src="product.image" :alt="product.name" />
      <div class="product-detail__info">
        <div class="muted">{{ product.categoryL1 }} / {{ product.categoryL2 }}</div>
        <h1>{{ product.name }}</h1>
        <div class="price-row">
          <strong>¥{{ product.price }}</strong>
          <span v-if="product.oldPrice">¥{{ product.oldPrice }}</span>
        </div>
        <p class="muted">{{ product.detail }}</p>

        <div>
          <div class="label">规格选择</div>
          <div class="sku-row">
            <button
              v-for="sku in product.skus"
              :key="sku.name"
              class="chip"
              :class="{ active: selectedSku === sku.name }"
              @click="selectedSku = sku.name"
            >
              {{ sku.name }}
            </button>
          </div>
        </div>

        <div class="counter">
          <div class="label">购买数量</div>
          <div class="counter__box">
            <button class="btn" @click="quantity = Math.max(1, quantity - 1)">-</button>
            <span>{{ quantity }}</span>
            <button class="btn" @click="quantity += 1">+</button>
          </div>
        </div>

        <div class="actions">
          <button class="btn btn-primary" :disabled="!product.stock" @click="handleAddCart">加入购物车</button>
          <button class="btn btn-ghost" :disabled="!product.stock" @click="handleBuyNow">立即购买</button>
        </div>
      </div>
    </div>

    <div class="card detail-block">
      <h2>商品详情</h2>
      <p class="muted">{{ product.detail }}</p>
      <ul>
        <li>运费方式：{{ product.freightType }}</li>
        <li>SKU 数量：{{ product.skus.length }}</li>
        <li>累计销量：{{ product.salesCount }}</li>
      </ul>
    </div>
  </section>
</template>

<style scoped>
.product-detail {
  display: grid;
  gap: 20px;
}

.product-detail__main {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 22px;
  padding: 24px;
}

.product-detail__main img {
  width: 100%;
  height: 460px;
  object-fit: cover;
  border-radius: 18px;
}

.product-detail__info h1 {
  margin: 12px 0;
  font-size: 36px;
}

.price-row {
  display: flex;
  align-items: baseline;
  gap: 12px;
  margin-bottom: 16px;
}

.price-row strong {
  color: #90f0c6;
  font-size: 42px;
}

.price-row span {
  text-decoration: line-through;
  color: var(--text-mute);
}

.label {
  margin-bottom: 10px;
  font-weight: 700;
}

.sku-row {
  display: flex;
  gap: 10px;
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

.counter {
  margin-top: 20px;
}

.counter__box {
  display: inline-flex;
  align-items: center;
  gap: 12px;
}

.actions {
  display: flex;
  gap: 12px;
  margin-top: 28px;
}

.detail-block {
  padding: 24px;
}
</style>
