<script setup>
import { computed, ref } from 'vue'
import { usePlatformStore } from '../../stores/platform'

const store = usePlatformStore()
const status = ref('全部')
const keyword = ref('')
const detailUser = ref(null)

const rows = computed(() =>
  store.users.filter((user) => {
    const statusMatch = status.value === '全部' || user.status === status.value
    const keywordMatch =
      !keyword.value || user.name.includes(keyword.value) || user.phone.includes(keyword.value)
    return statusMatch && keywordMatch
  }),
)
</script>

<template>
  <section>
    <div class="admin-page-head">
      <h1>用户管理</h1>
      <p class="muted">展示用户基础档案、累计消费与账户状态，支持启用和禁用操作。</p>
    </div>

    <div class="stats">
      <div class="card stat-card"><span class="muted">注册用户总数</span><strong>{{ store.users.length }}</strong></div>
      <div class="card stat-card"><span class="muted">正常账户</span><strong>{{ store.users.filter((user) => user.status === '正常').length }}</strong></div>
      <div class="card stat-card"><span class="muted">禁用账户</span><strong>{{ store.users.filter((user) => user.status === '禁用').length }}</strong></div>
      <div class="card stat-card"><span class="muted">今日活跃</span><strong>342</strong></div>
    </div>

    <div class="card panel">
      <div class="toolbar">
        <input v-model="keyword" class="field" placeholder="搜索用户昵称或手机号" />
      </div>

      <div class="chips">
        <button class="chip" :class="{ active: status === '全部' }" @click="status = '全部'">全部用户</button>
        <button class="chip" :class="{ active: status === '正常' }" @click="status = '正常'">正常</button>
        <button class="chip" :class="{ active: status === '禁用' }" @click="status = '禁用'">禁用</button>
      </div>

      <div class="table-wrap">
        <table class="table">
          <thead>
            <tr>
              <th>用户</th>
              <th>手机号</th>
              <th>注册时间</th>
              <th>订单数</th>
              <th>消费总额</th>
              <th>状态</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="row in rows" :key="row.id">
              <td>{{ row.name }}</td>
              <td>{{ row.phone }}</td>
              <td>{{ row.createdAt }}</td>
              <td>{{ row.orders }}</td>
              <td>¥{{ row.spend }}</td>
              <td><span class="badge" :class="row.status === '正常' ? 'badge-success' : 'badge-danger'">{{ row.status }}</span></td>
              <td>
                <div class="actions">
                  <button class="btn" @click="detailUser = row">详情</button>
                  <button
                    class="btn"
                    :class="row.status === '正常' ? 'btn-danger' : 'btn-primary'"
                    @click="store.toggleUserStatus(row.id)"
                  >
                    {{ row.status === '正常' ? '禁用' : '启用' }}
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <div v-if="detailUser" class="modal-mask" @click.self="detailUser = null">
      <div class="modal-panel card">
        <h2>用户详情</h2>
        <div class="detail-row"><span class="muted">用户 ID</span><strong>{{ detailUser.id }}</strong></div>
        <div class="detail-row"><span class="muted">姓名</span><strong>{{ detailUser.name }}</strong></div>
        <div class="detail-row"><span class="muted">手机号</span><strong>{{ detailUser.phone }}</strong></div>
        <div class="detail-row"><span class="muted">注册时间</span><strong>{{ detailUser.createdAt }}</strong></div>
        <div class="detail-row"><span class="muted">累计订单</span><strong>{{ detailUser.orders }}</strong></div>
        <div class="detail-row"><span class="muted">累计消费</span><strong>¥{{ detailUser.spend }}</strong></div>
        <div class="detail-row"><span class="muted">最近活跃</span><strong>{{ detailUser.lastActive }}</strong></div>
        <div style="display: flex; justify-content: flex-end; margin-top: 24px">
          <button class="btn" @click="detailUser = null">关闭</button>
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

.stats {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 16px;
  margin-top: 18px;
}

.stat-card {
  padding: 18px 20px;
}

.stat-card strong {
  display: block;
  margin-top: 12px;
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

.detail-row {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  padding: 12px 0;
  border-bottom: 1px solid var(--line);
}
</style>
