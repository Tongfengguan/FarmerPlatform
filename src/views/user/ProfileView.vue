<script setup>
import { computed, reactive } from 'vue'
import { usePlatformStore } from '../../stores/platform'

const store = usePlatformStore()
const user = computed(() => store.currentUser)
const addressBook = computed(() => user.value.addressBook || [])
const defaultAddress = computed(() => addressBook.value.find((item) => item.isDefault))
const form = reactive({
  name: '',
  phone: '',
  address: '',
  isDefault: false,
})

const submitAddress = async () => {
  if (!form.name || !form.phone || !form.address) return
  await store.addAddress({ ...form })
  form.name = ''
  form.phone = ''
  form.address = ''
  form.isDefault = false
}
</script>

<template>
  <section class="page-shell profile-layout">
    <div class="card profile-card">
      <div class="profile-head">
        <div class="profile-avatar">{{ user.avatar }}</div>
        <div>
          <h1>{{ user.nickname }}</h1>
          <div class="muted">{{ user.name }} · {{ user.phone }}</div>
        </div>
      </div>

      <div class="profile-grid">
        <div class="profile-stat">
          <span class="muted">累计订单</span>
          <strong>{{ store.currentUserOrders.length }}</strong>
        </div>
        <div class="profile-stat">
          <span class="muted">购物车商品</span>
          <strong>{{ store.cartCount }}</strong>
        </div>
        <div class="profile-stat">
          <span class="muted">默认收货地址</span>
          <strong>{{ defaultAddress ? `${defaultAddress.address.slice(0, 8)}...` : '待补充' }}</strong>
        </div>
      </div>
    </div>

    <div class="card address-card">
      <div class="section-title">
        <h2>收货地址</h2>
        <span class="muted">这里已经接到后端，可继续新增默认地址。</span>
      </div>
      <div class="address-form">
        <input v-model="form.name" class="field" placeholder="收货人" />
        <input v-model="form.phone" class="field" placeholder="手机号" />
        <input v-model="form.address" class="field" placeholder="详细地址" />
        <label class="muted form-check">
          <input v-model="form.isDefault" type="checkbox" />
          <span>设为默认地址</span>
        </label>
        <button class="btn btn-primary" @click="submitAddress">新增地址</button>
      </div>
      <div class="address-list">
        <div v-for="address in addressBook" :key="address.id" class="address-item">
          <div>
            <strong>{{ address.name }} {{ address.phone }}</strong>
            <div class="muted">{{ address.address }}</div>
          </div>
          <span class="badge" :class="address.isDefault ? 'badge-success' : 'badge-muted'">
            {{ address.isDefault ? '默认地址' : '备用地址' }}
          </span>
        </div>
      </div>
    </div>
  </section>
</template>

<style scoped>
.profile-layout {
  display: grid;
  gap: 20px;
}

.profile-card,
.address-card {
  padding: 24px;
}

.profile-head {
  display: flex;
  align-items: center;
  gap: 18px;
}

.profile-avatar {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 74px;
  height: 74px;
  border-radius: 22px;
  background: rgba(35, 176, 125, 0.2);
  font-size: 30px;
  font-weight: 800;
  color: #a6f1cf;
}

.profile-head h1 {
  margin: 0 0 6px;
}

.profile-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 16px;
  margin-top: 22px;
}

.profile-stat {
  padding: 16px;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.03);
}

.profile-stat strong {
  display: block;
  margin-top: 12px;
  font-size: 26px;
}

.section-title {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  margin-bottom: 18px;
}

.address-list {
  display: grid;
  gap: 14px;
}

.address-form {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr)) auto;
  gap: 12px;
  margin-bottom: 18px;
}

.form-check {
  display: flex;
  align-items: center;
  gap: 8px;
}

.address-item {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: center;
  padding: 18px;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.03);
}
</style>
