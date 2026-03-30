<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { usePlatformStore } from '../../stores/platform'

const store = usePlatformStore()
const router = useRouter()

const selectedTotal = computed(() =>
  store.cart.reduce((sum, item) => (item.selected ? sum + item.price * item.quantity : sum), 0),
)

const handleCheckout = () => {
  const success = store.checkoutSelected()
  if (success) router.push('/orders')
}
</script>

<template>
  <section class="page-shell cart-layout">
    <div class="card cart-list">
      <div class="section-title">
        <h1>购物车</h1>
        <span class="muted">已选 {{ store.cartCount }} 件商品</span>
      </div>

      <div v-if="store.cart.length" class="cart-items">
        <div v-for="item in store.cart" :key="`${item.productId}-${item.sku}`" class="cart-item">
          <input
            type="checkbox"
            :checked="item.selected"
            @change="store.toggleCartSelection(item.productId, item.sku)"
          />
          <img :src="item.image" :alt="item.name" />
          <div class="cart-item__body">
            <strong>{{ item.name }}</strong>
            <span class="muted">规格：{{ item.sku }}</span>
          </div>
          <div>¥{{ item.price }}</div>
          <div class="cart-item__counter">
            <button class="btn" @click="store.updateCartItem(item.productId, item.sku, item.quantity - 1)">-</button>
            <span>{{ item.quantity }}</span>
            <button class="btn" @click="store.updateCartItem(item.productId, item.sku, item.quantity + 1)">+</button>
          </div>
          <div>¥{{ item.price * item.quantity }}</div>
          <button class="btn btn-danger" @click="store.removeCartItem(item.productId, item.sku)">删除</button>
        </div>
      </div>
      <div v-else class="empty">购物车为空，去商城挑点商品吧。</div>
    </div>

    <aside class="card summary">
      <h2>结算信息</h2>
      <div class="summary__row"><span>已选商品金额</span><strong>¥{{ selectedTotal }}</strong></div>
      <div class="summary__row"><span>运费说明</span><span class="muted">满 99 元包邮</span></div>
      <button class="btn btn-primary" style="width: 100%; margin-top: 18px" @click="handleCheckout">
        去结算
      </button>
    </aside>
  </section>
</template>

<style scoped>
.cart-layout {
  display: grid;
  grid-template-columns: 1.5fr 0.7fr;
  gap: 20px;
}

.cart-list,
.summary {
  padding: 24px;
}

.section-title {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 18px;
}

.section-title h1 {
  margin: 0;
}

.cart-items {
  display: grid;
  gap: 16px;
}

.cart-item {
  display: grid;
  grid-template-columns: 24px 88px 1fr 100px 170px 100px 88px;
  align-items: center;
  gap: 14px;
  padding: 14px 0;
  border-bottom: 1px solid var(--line);
}

.cart-item img {
  width: 88px;
  height: 88px;
  object-fit: cover;
  border-radius: 14px;
}

.cart-item__body {
  display: grid;
  gap: 8px;
}

.cart-item__counter {
  display: inline-flex;
  align-items: center;
  gap: 10px;
}

.summary {
  height: fit-content;
}

.summary__row {
  display: flex;
  justify-content: space-between;
  padding: 12px 0;
  border-bottom: 1px solid var(--line);
}
</style>
