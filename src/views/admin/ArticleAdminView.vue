<script setup>
import { computed, reactive, ref } from 'vue'
import { articleCategories } from '../../data/mockData'
import { usePlatformStore } from '../../stores/platform'

const store = usePlatformStore()
const filter = ref('全部')
const keyword = ref('')
const showModal = ref(false)
const editingId = ref(null)

const form = reactive({
  title: '',
  category: '政策法规',
  source: '',
  summary: '',
  content: '',
  status: '已发布',
  isPush: false,
  cover:
    'https://images.unsplash.com/photo-1500382017468-9049fed747ef?auto=format&fit=crop&w=1200&q=80',
})

const rows = computed(() =>
  store.articles.filter((article) => {
    const categoryMatch = filter.value === '全部' || article.category === filter.value
    const keywordMatch = !keyword.value || article.title.includes(keyword.value)
    return categoryMatch && keywordMatch
  }),
)

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
    cover:
      'https://images.unsplash.com/photo-1500382017468-9049fed747ef?auto=format&fit=crop&w=1200&q=80',
  })
  showModal.value = true
}

const openEdit = (article) => {
  editingId.value = article.id
  Object.assign(form, article)
  showModal.value = true
}

const submit = () => {
  const payload = { ...form, id: editingId.value }
  if (editingId.value) store.updateArticle(payload)
  else store.addArticle(payload)
  showModal.value = false
}

const badgeClass = (value) => {
  if (value === '已发布') return 'badge-success'
  if (value === '已下架') return 'badge-danger'
  return 'badge-muted'
}
</script>

<template>
  <section>
    <div class="admin-page-head">
      <h1>资讯管理</h1>
      <p class="muted">支持发布、编辑、下架和删除资讯，并模拟推送通知开关。</p>
    </div>

    <div class="card panel">
      <div class="toolbar">
        <input v-model="keyword" class="field" placeholder="搜索资讯标题" />
        <button class="btn btn-primary" @click="openCreate">新建资讯</button>
      </div>

      <div class="chips">
        <button
          v-for="item in articleCategories"
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
              <th>标题</th>
              <th>分类</th>
              <th>发布时间</th>
              <th>阅读量</th>
              <th>状态</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="row in rows" :key="row.id">
              <td>{{ row.id }}</td>
              <td>{{ row.title }}</td>
              <td>{{ row.category }}</td>
              <td>{{ row.publishedAt }}</td>
              <td>{{ row.viewCount }}</td>
              <td><span class="badge" :class="badgeClass(row.status)">{{ row.status }}</span></td>
              <td>
                <div class="actions">
                  <button class="btn" @click="openEdit(row)">编辑</button>
                  <button class="btn" @click="store.toggleArticleStatus(row.id)">
                    {{ row.status === '已发布' ? '下架' : '发布' }}
                  </button>
                  <button class="btn btn-danger" @click="store.removeArticle(row.id)">删除</button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <div v-if="showModal" class="modal-mask" @click.self="showModal = false">
      <div class="modal-panel card">
        <h2>{{ editingId ? '编辑资讯' : '新建资讯' }}</h2>
        <div class="modal-grid">
          <div>
            <div class="muted">资讯标题</div>
            <input v-model="form.title" class="field" />
          </div>
          <div>
            <div class="muted">分类</div>
            <select v-model="form.category" class="select">
              <option v-for="item in articleCategories.slice(1)" :key="item" :value="item">
                {{ item }}
              </option>
            </select>
          </div>
        </div>
        <div style="margin-top: 16px">
          <div class="muted">摘要</div>
          <textarea v-model="form.summary" class="textarea"></textarea>
        </div>
        <div style="margin-top: 16px">
          <div class="muted">正文内容</div>
          <textarea v-model="form.content" class="textarea"></textarea>
        </div>
        <div class="modal-grid" style="margin-top: 16px">
          <div>
            <div class="muted">来源机构</div>
            <input v-model="form.source" class="field" />
          </div>
          <div>
            <div class="muted">状态</div>
            <select v-model="form.status" class="select">
              <option>草稿</option>
              <option>已发布</option>
              <option>已下架</option>
            </select>
          </div>
        </div>
        <label style="display: inline-flex; gap: 8px; margin-top: 16px; align-items: center">
          <input v-model="form.isPush" type="checkbox" />
          <span>发布时推送通知</span>
        </label>
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
