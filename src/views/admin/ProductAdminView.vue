<script setup>
import { computed, reactive, ref, onMounted } from 'vue'
import { productCategories } from '../../utils/constants'
import { usePlatformStore } from '../../stores/platform'
import { Plus, Edit, Search, Picture } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const store = usePlatformStore()
const filter = ref('全部')
const keyword = ref('')
const showModal = ref(false)
const editingId = ref(null)
const loading = ref(false)
const submitting = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)

const form = reactive({
  name: '',
  categoryL1: '农产品',
  categoryL2: '新鲜果蔬',
  image: 'https://images.unsplash.com/photo-1542838132-92c53300491e?auto=format&fit=crop&w=1200&q=80',
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

const fetchPage = async () => {
  loading.value = true
  try {
    await store.fetchProducts(currentPage.value - 1, pageSize.value)
  } finally {
    loading.value = false
  }
}

const handlePageChange = (val) => {
  currentPage.value = val
  fetchPage()
}

const openCreate = () => {
  editingId.value = null
  Object.assign(form, {
    name: '',
    categoryL1: '农产品',
    categoryL2: '新鲜果蔬',
    image: 'https://images.unsplash.com/photo-1542838132-92c53300491e?auto=format&fit=crop&w=1200&q=80',
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

const submit = async () => {
  const payload = { ...JSON.parse(JSON.stringify(form)), id: editingId.value }
  submitting.value = true
  try {
    if (editingId.value) {
      await store.updateProduct(payload)
      ElMessage.success('更新成功')
    } else {
      await store.addProduct(payload)
      ElMessage.success('添加成功')
    }
    showModal.value = false
  } catch (e) {
    ElMessage.error(e.message || '操作失败')
  } finally {
    submitting.value = false
  }
}

const toggleStatus = async (id) => {
  try {
    await store.toggleProductStatus(id)
    ElMessage.success('状态已更新')
  } catch (error) {
    ElMessage.error(error.message || '操作失败')
  }
}

onMounted(() => {
  fetchPage()
})
</script>

<template>
  <div class="admin-page">
    <div class="page-header">
      <div>
        <h1 class="page-title">商品管理</h1>
        <p class="page-subtitle">管理平台商品库，支持分类维护、库存预警和状态切换。</p>
      </div>
      <el-button type="primary" :icon="Plus" size="large" @click="openCreate">新增商品</el-button>
    </div>

    <el-card shadow="never" class="table-card">
      <div class="toolbar">
        <div class="filter-group">
          <el-radio-group v-model="filter" @change="currentPage = 1; fetchPage()">
            <el-radio-button v-for="item in productCategories" :key="item" :label="item" :value="item" />
          </el-radio-group>
        </div>
        <div class="search-group">
          <el-input
            v-model="keyword"
            placeholder="搜索商品名称..."
            :prefix-icon="Search"
            clearable
            style="width: 280px"
          />
        </div>
      </div>

      <el-table :data="rows" v-loading="loading" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column label="商品信息" min-width="300">
          <template #default="{ row }">
            <div class="product-info">
              <el-image 
                :src="row.image" 
                class="product-thumb" 
                fit="cover"
                :preview-src-list="[row.image]"
                preview-teleported
              >
                <template #error>
                  <div class="image-slot"><el-icon><Picture /></el-icon></div>
                </template>
              </el-image>
              <div class="product-detail">
                <div class="product-name">{{ row.name }}</div>
                <div class="product-category">{{ row.categoryL1 }} / {{ row.categoryL2 }}</div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="价格" width="120">
          <template #default="{ row }">
            <span class="price-text">¥{{ row.price }}</span>
            <div v-if="row.oldPrice" class="old-price">¥{{ row.oldPrice }}</div>
          </template>
        </el-table-column>
        <el-table-column prop="stock" label="库存" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.stock < 10 ? 'danger' : 'info'">{{ row.stock }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="salesCount" label="累计销量" width="100" align="center" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === '销售中' ? 'success' : 'info'">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button-group>
              <el-button :icon="Edit" @click="openEdit(row)">编辑</el-button>
              <el-button 
                :type="row.status === '销售中' ? 'warning' : 'success'" 
                @click="toggleStatus(row.id)"
              >
                {{ row.status === '销售中' ? '下架' : '上架' }}
              </el-button>
            </el-button-group>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="store.productTotal"
          @size-change="fetchPage"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>

    <el-dialog
      v-model="showModal"
      :title="editingId ? '编辑商品' : '新增商品'"
      width="800px"
      destroy-on-close
    >
      <el-form :model="form" label-width="100px" label-position="top">
        <el-row :gutter="20">
          <el-col :span="16">
            <el-form-item label="商品名称">
              <el-input v-model="form.name" placeholder="请输入商品全称" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="一级分类">
              <el-select v-model="form.categoryL1" style="width: 100%">
                <el-option v-for="item in productCategories.slice(1)" :key="item" :label="item" :value="item" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="二级分类">
              <el-input v-model="form.categoryL2" placeholder="例如：当季水果" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="运费模板">
              <el-select v-model="form.freightType" style="width: 100%">
                <el-option label="包邮" value="包邮" />
                <el-option label="固定运费" value="固定运费" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="销售价 (¥)">
              <el-input-number v-model="form.price" :precision="2" :step="0.1" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="划线价 (¥)">
              <el-input-number v-model="form.oldPrice" :precision="2" :step="0.1" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="库存总量">
              <el-input-number v-model="form.stock" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="商品主图 URL">
          <el-input v-model="form.image" placeholder="https://..." />
        </el-form-item>

        <el-form-item label="商品详情描述">
          <el-input v-model="form.detail" type="textarea" :rows="5" placeholder="详细介绍商品卖点、产地等信息..." />
        </el-form-item>

        <el-form-item label="销售状态">
          <el-radio-group v-model="form.status">
            <el-radio-button label="销售中" value="销售中" />
            <el-radio-button label="已下架" value="已下架" />
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showModal = false">取消</el-button>
        <el-button type="primary" @click="submit" :loading="submitting">保存并上架</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 24px;
}

.page-title {
  font-size: 28px;
  font-weight: 700;
  margin: 0 0 8px;
}

.page-subtitle {
  color: var(--el-text-color-secondary);
  margin: 0;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.product-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.product-thumb {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  flex-shrink: 0;
}

.product-name {
  font-weight: 600;
  color: var(--el-text-color-primary);
  margin-bottom: 4px;
}

.product-category {
  font-size: 12px;
  color: var(--el-text-color-secondary);
}

.price-text {
  font-weight: 700;
  color: var(--el-color-success);
  font-size: 16px;
}

.old-price {
  font-size: 12px;
  color: var(--el-text-color-secondary);
  text-decoration: line-through;
}

.pagination-wrapper {
  margin-top: 24px;
  display: flex;
  justify-content: flex-end;
}
</style>
