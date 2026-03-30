<script setup>
import { computed, reactive, ref } from 'vue'
import { productCategories } from '../../data/mockData'
import { usePlatformStore } from '../../stores/platform'

const store = usePlatformStore()
const filter = ref('全部')
const keyword = ref('')
const showModal = ref(false)
const editingId = ref(null)

const form = reactive({
  name: '',
  categoryL1: '农产品',
  categoryL2: '新鲜果蔬',
  image:
    'https://images.unsplash.com/photo-1542838132-92c53300491e?auto=format&fit=crop&w=1200&q=80',
  detail: '',
  freightType: '包邮',
  price: 0,
  oldPrice: null,
  stock: 0,
  salesCount: 0,
  status: '销售中',
  skus: [{ name: '标准规格', price: 0, stock: 0 }],
})

const rows = computed(() =>
  store.products.filter((product) => {
    const categoryMatch = filter.value === '全部' || product.categoryL1 === filter.value
    const keywordMatch = !keyword.value || product.name.includes(keyword.value)
    return categoryMatch && keywordMatch
  }),
)

const openCreate = () => {
  editingId.value = null
  Object.assign(form, {
    name: '',
    categoryL1: '农产品',
    categoryL2: '新鲜果蔬',
    image:
      'https://images.unsplash.com/photo-1542838132-92c53300491e?auto=format&fit=crop&w=1200&q=80',
    detail: '',
    freightType: '包邮',
    price: 0,
    oldPrice: null,
    stock: 0,
    salesCount: 0,
    status: '销售中',
    skus: [{ name: '标准规格', price: 0, stock: 0 }],
  })
  showModal.value = true
}

const openEdit = (product) => {
  editingId.value = product.id
  Object.assign(form, JSON.parse(JSON.stringify(product)))
  showModal.value = true
}

const submit = () => {
  const payload = { ...JSON.parse(JSON.stringify(form)), id: editingId.value }
  if (editingId.value) store.updateProduct(payload)
  else store.addProduct(payload)
  showModal.value = false
}

const badgeClass = (value) => (value === '销售中' ? 'badge-success' : 'badge-danger')
</script>

<template>
  <section>
    <div class="admin-page-head">
      <h1>商品管理</h1>
      <p class="muted">覆盖商品上架、分类、价格、库存和 SKU 的基础维护能力。</p>
    </div>

    <div class="card panel">
      <div class="toolbar">
        <input v-model="keyword" class="field" placeholder="搜索商品名称" />
        <button class="btn btn-primary" @click="openCreate">新增商品</button>
      </div>

      <div class="chips">
        <button
          v-for="item in productCategories"
          :key="item"
          class="chip"
          :class="{ active: item === filter }"
          @click="filter = item"
        >
          {{ item }}
        </button>
      </div>

      <div class="table-wrap">
        <table class="table">
          <thead>
            <tr>
              <th>ID</th>
              <th>商品</th>
              <th>分类</th>
              <th>价格</th>
              <th>库存</th>
              <th>销量</th>
              <th>状态</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="row in rows" :key="row.id">
              <td>{{ row.id }}</td>
              <td>{{ row.name }}</td>
              <td>{{ row.categoryL1 }} / {{ row.categoryL2 }}</td>
              <td>¥{{ row.price }}</td>
              <td>{{ row.stock }}</td>
              <td>{{ row.salesCount }}</td>
              <td><span class="badge" :class="badgeClass(row.status)">{{ row.status }}</span></td>
              <td>
                <div class="actions">
                  <button class="btn" @click="openEdit(row)">编辑</button>
                  <button class="btn" @click="store.toggleProductStatus(row.id)">
                    {{ row.status === '销售中' ? '下架' : '上架' }}
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <div v-if="showModal" class="modal-mask" @click.self="showModal = false">
      <div class="modal-panel card">
        <h2>{{ editingId ? '编辑商品' : '新增商品' }}</h2>
        <div class="modal-grid">
          <div>
            <div class="muted">商品名称</div>
            <input v-model="form.name" class="field" />
          </div>
          <div>
            <div class="muted">一级分类</div>
            <select v-model="form.categoryL1" class="select">
              <option v-for="item in productCategories.slice(1)" :key="item" :value="item">
                {{ item }}
              </option>
            </select>
          </div>
        </div>
        <div class="modal-grid" style="margin-top: 16px">
          <div>
            <div class="muted">二级分类</div>
            <input v-model="form.categoryL2" class="field" />
          </div>
          <div>
            <div class="muted">运费方式</div>
            <select v-model="form.freightType" class="select">
              <option>包邮</option>
              <option>统一运费</option>
              <option>按地区</option>
            </select>
          </div>
        </div>
        <div class="modal-grid" style="margin-top: 16px">
          <div>
            <div class="muted">价格</div>
            <input v-model.number="form.price" type="number" class="field" />
          </div>
          <div>
            <div class="muted">库存</div>
            <input v-model.number="form.stock" type="number" class="field" />
          </div>
        </div>
        <div style="margin-top: 16px">
          <div class="muted">商品详情</div>
          <textarea v-model="form.detail" class="textarea"></textarea>
        </div>
        <div style="display: flex; justify-content: flex-end; gap: 12px; margin-top: 24px">
          <button class="btn" @click="showModal = false">取消</button>
          <button class="btn btn-primary" @click="submit">保存</button>
        </div>
      </div>
    </div>
  </section>
</template>

<style scoped>
.admin-page-head h1 {
  margin: 0 0 8px;
  font-size: 34px;
}

.panel {
  margin-top: 18px;
  padding: 22px;
}

.chips {
  display: flex;
  gap: 10px;
  margin-bottom: 18px;
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

.actions {
  display: flex;
  gap: 8px;
}
</style>
