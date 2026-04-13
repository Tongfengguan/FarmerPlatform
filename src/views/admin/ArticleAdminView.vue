<script setup>
import { computed, reactive, ref, onMounted } from 'vue'
import { articleCategories } from '../../data/mockData'
import { usePlatformStore } from '../../stores/platform'
import { Plus, Edit, Delete, View, Search } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const store = usePlatformStore()
const filter = ref('全部')
const keyword = ref('')
const showModal = ref(false)
const editingId = ref(null)
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)

const form = reactive({
  title: '',
  category: '政策法规',
  source: '',
  summary: '',
  content: '',
  status: '已发布',
  isPush: false,
  cover: 'https://images.unsplash.com/photo-1500382017468-9049fed747ef?auto=format&fit=crop&w=1200&q=80',
})

// 过滤后的数据（前端搜索作为辅助，实际项目应结合后端搜索）
const rows = computed(() =>
  store.articles.filter((article) => {
    const categoryMatch = filter.value === '全部' || article.category === filter.value
    const keywordMatch = !keyword.value || article.title.includes(keyword.value)
    return categoryMatch && keywordMatch
  }),
)

const fetchPage = async () => {
  loading.value = true
  try {
    await store.fetchArticles(currentPage.value - 1, pageSize.value)
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
    title: '',
    category: '政策法规',
    source: '',
    summary: '',
    content: '',
    status: '已发布',
    isPush: false,
    cover: 'https://images.unsplash.com/photo-1500382017468-9049fed747ef?auto=format&fit=crop&w=1200&q=80',
  })
  showModal.value = true
}

const openEdit = (article) => {
  editingId.value = article.id
  Object.assign(form, JSON.parse(JSON.stringify(article)))
  showModal.value = true
}

const submit = async () => {
  const payload = { ...form, id: editingId.value }
  try {
    if (editingId.value) {
      await store.updateArticle(payload)
      ElMessage.success('更新成功')
    } else {
      await store.addArticle(payload)
      ElMessage.success('发布成功')
    }
    showModal.value = false
  } catch (e) {
    ElMessage.error(e.message || '操作失败')
  }
}

const handleDelete = (id) => {
  ElMessageBox.confirm('确定要删除该资讯吗？此操作不可撤销。', '删除确认', {
    confirmButtonText: '确定删除',
    cancelButtonText: '取消',
    type: 'warning',
    buttonSize: 'default'
  }).then(async () => {
    await store.removeArticle(id)
    ElMessage.success('删除成功')
  })
}

const toggleStatus = async (id) => {
  await store.toggleArticleStatus(id)
  ElMessage.success('状态已更新')
}

onMounted(() => {
  fetchPage()
})
</script>

<template>
  <div class="admin-page">
    <div class="page-header">
      <div>
        <h1 class="page-title">资讯管理</h1>
        <p class="page-subtitle">发布、编辑和维护平台资讯文章，支持模拟推送通知。</p>
      </div>
      <el-button type="primary" :icon="Plus" size="large" @click="openCreate">新建资讯</el-button>
    </div>

    <el-card shadow="never" class="table-card">
      <!-- 工具栏 -->
      <div class="toolbar">
        <div class="filter-group">
          <el-radio-group v-model="filter" @change="currentPage = 1; fetchPage()">
            <el-radio-button v-for="item in articleCategories" :key="item" :label="item" :value="item" />
          </el-radio-group>
        </div>
        <div class="search-group">
          <el-input
            v-model="keyword"
            placeholder="搜索资讯标题..."
            :prefix-icon="Search"
            clearable
            style="width: 280px"
          />
        </div>
      </div>

      <!-- 数据表格 -->
      <el-table :data="rows" v-loading="loading" style="width: 100%; margin-top: 10px">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="标题" min-width="250" show-overflow-tooltip />
        <el-table-column prop="category" label="分类" width="120">
          <template #default="{ row }">
            <el-tag effect="plain">{{ row.category }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="publishedAt" label="发布时间" width="150" />
        <el-table-column prop="viewCount" label="阅读量" width="100" align="center">
          <template #default="{ row }">
            <span class="view-count"><el-icon><View /></el-icon> {{ row.viewCount }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === '已发布' ? 'success' : 'danger'">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button-group>
              <el-button :icon="Edit" @click="openEdit(row)">编辑</el-button>
              <el-button 
                :type="row.status === '已发布' ? 'warning' : 'success'" 
                @click="toggleStatus(row.id)"
              >
                {{ row.status === '已发布' ? '下架' : '发布' }}
              </el-button>
              <el-button type="danger" :icon="Delete" @click="handleDelete(row.id)" />
            </el-button-group>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="store.articleTotal"
          @size-change="fetchPage"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>

    <!-- 编辑/创建弹窗 -->
    <el-dialog
      v-model="showModal"
      :title="editingId ? '编辑资讯' : '新建资讯'"
      width="700px"
      destroy-on-close
    >
      <el-form :model="form" label-width="80px" label-position="top">
        <el-row :gutter="20">
          <el-col :span="16">
            <el-form-item label="资讯标题">
              <el-input v-model="form.title" placeholder="输入引人注目的标题" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="分类">
              <el-select v-model="form.category" style="width: 100%">
                <option v-for="item in articleCategories.slice(1)" :key="item" :label="item" :value="item" />
                <el-option v-for="item in articleCategories.slice(1)" :key="item" :label="item" :value="item" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="封面图 URL">
          <el-input v-model="form.cover" placeholder="https://..." />
        </el-form-item>

        <el-form-item label="摘要">
          <el-input v-model="form.summary" type="textarea" :rows="3" placeholder="简短的资讯介绍" />
        </el-form-item>

        <el-form-item label="正文内容">
          <el-input v-model="form.content" type="textarea" :rows="8" placeholder="支持文本或简单的 HTML 内容" />
        </el-form-item>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="来源机构">
              <el-input v-model="form.source" placeholder="例如：农业部、新华社" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="发布状态">
              <el-select v-model="form.status" style="width: 100%">
                <el-option label="草稿" value="草稿" />
                <el-option label="已发布" value="已发布" />
                <el-option label="已下架" value="已下架" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item>
          <el-checkbox v-model="form.isPush" label="发布时向用户端推送通知" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showModal = false">取消</el-button>
          <el-button type="primary" @click="submit">确认保存</el-button>
        </span>
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

.view-count {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  color: var(--el-text-color-secondary);
}

.pagination-wrapper {
  margin-top: 24px;
  display: flex;
  justify-content: flex-end;
}
</style>
