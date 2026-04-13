<script setup>
import { computed, ref } from 'vue'
import { usePlatformStore } from '../../stores/platform'
import { 
  User, 
  CircleCheck, 
  CircleClose, 
  View, 
  Search,
  Timer
} from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const store = usePlatformStore()
const statusFilter = ref('全部')
const keyword = ref('')
const detailUser = ref(null)
const showDetail = ref(false)

const rows = computed(() =>
  store.users.filter((user) => {
    const statusMatch = statusFilter.value === '全部' || user.status === statusFilter.value
    const keywordMatch =
      !keyword.value || user.name.includes(keyword.value) || user.phone.includes(keyword.value)
    return statusMatch && keywordMatch
  }),
)

const activeUsersCount = computed(() => store.users.filter(u => u.status === '正常').length)
const disabledUsersCount = computed(() => store.users.filter(u => u.status === '禁用').length)

const openDetail = (user) => {
  detailUser.ref = user // Note: current store uses .name, .orders, .spend based on previous read
  detailUser.value = user
  showDetail.value = true
}

const handleToggleStatus = (user) => {
  const action = user.status === '正常' ? '禁用' : '启用'
  ElMessageBox.confirm(`确定要${action}用户 [${user.name}] 的账户吗？`, '状态变更确认', {
    confirmButtonText: `确定${action}`,
    cancelButtonText: '取消',
    type: user.status === '正常' ? 'warning' : 'info'
  }).then(async () => {
    await store.toggleUserStatus(user.id)
    ElMessage.success(`用户已${action}`)
  })
}
</script>

<template>
  <div class="admin-page">
    <div class="page-header">
      <div>
        <h1 class="page-title">用户管理</h1>
        <p class="page-subtitle">管理平台注册用户，监控活跃度及消费情况，维护账户安全。</p>
      </div>
    </div>

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stat-row">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <el-statistic :value="store.users.length" title="注册用户总数">
            <template #prefix>
              <el-icon><User /></el-icon>
            </template>
          </el-statistic>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <el-statistic :value="activeUsersCount" title="正常账户" value-style="color: var(--el-color-success)">
            <template #prefix>
              <el-icon><CircleCheck /></el-icon>
            </template>
          </el-statistic>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <el-statistic :value="disabledUsersCount" title="禁用账户" value-style="color: var(--el-color-danger)">
            <template #prefix>
              <el-icon><CircleClose /></el-icon>
            </template>
          </el-statistic>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <el-statistic :value="342" title="今日活跃">
            <template #prefix>
              <el-icon><Timer /></el-icon>
            </template>
          </el-statistic>
        </el-card>
      </el-col>
    </el-row>

    <el-card shadow="never" class="table-card">
      <!-- 工具栏 -->
      <div class="toolbar">
        <div class="filter-group">
          <el-radio-group v-model="statusFilter">
            <el-radio-button label="全部" value="全部">全部用户</el-radio-button>
            <el-radio-button label="正常" value="正常">正常</el-radio-button>
            <el-radio-button label="禁用" value="禁用">禁用</el-radio-button>
          </el-radio-group>
        </div>
        <div class="search-group">
          <el-input
            v-model="keyword"
            placeholder="搜索昵称或手机号..."
            :prefix-icon="Search"
            clearable
            style="width: 280px"
          />
        </div>
      </div>

      <!-- 用户列表 -->
      <el-table :data="rows" style="width: 100%">
        <el-table-column label="用户信息" min-width="200">
          <template #default="{ row }">
            <div class="user-info-cell">
              <el-avatar :size="32" class="user-avatar">{{ row.name.slice(0, 1) }}</el-avatar>
              <div class="user-detail">
                <div class="user-name">{{ row.name }}</div>
                <div class="user-id">ID: {{ row.id }}</div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="手机号" width="140" />
        <el-table-column prop="createdAt" label="注册时间" width="140" />
        <el-table-column prop="orders" label="订单数" width="100" align="center" sortable />
        <el-table-column label="消费总额" width="140" sortable>
          <template #default="{ row }">
            <span class="price-text">¥{{ row.spend.toLocaleString() }}</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === '正常' ? 'success' : 'danger'" effect="dark">
              {{ row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button-group>
              <el-button :icon="View" @click="openDetail(row)">详情</el-button>
              <el-button 
                :type="row.status === '正常' ? 'danger' : 'primary'"
                @click="handleToggleStatus(row)"
              >
                {{ row.status === '正常' ? '禁用' : '启用' }}
              </el-button>
            </el-button-group>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 用户详情弹窗 -->
    <el-dialog v-model="showDetail" title="用户档案详情" width="500px">
      <div v-if="detailUser" class="user-profile-view">
        <div class="profile-header">
          <el-avatar :size="64" class="large-avatar">{{ detailUser.name.slice(0, 1) }}</el-avatar>
          <h2>{{ detailUser.name }}</h2>
          <el-tag :type="detailUser.status === '正常' ? 'success' : 'danger'">{{ detailUser.status }}</el-tag>
        </div>

        <el-descriptions :column="1" border class="profile-desc">
          <el-descriptions-item label="手机号码">{{ detailUser.phone }}</el-descriptions-item>
          <el-descriptions-item label="注册时间">{{ detailUser.createdAt }}</el-descriptions-item>
          <el-descriptions-item label="累计订单">{{ detailUser.orders }} 单</el-descriptions-item>
          <el-descriptions-item label="消费总额">¥{{ detailUser.spend.toLocaleString() }}</el-descriptions-item>
          <el-descriptions-item label="最近活跃">{{ detailUser.lastActive }}</el-descriptions-item>
        </el-descriptions>
      </div>
      <template #footer>
        <el-button type="primary" @click="showDetail = false">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.stat-row {
  margin-bottom: 20px;
}

.stat-card {
  border: none;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.user-info-cell {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-avatar {
  background-color: var(--el-color-primary-light-3);
  color: var(--el-color-white);
  font-weight: bold;
}

.user-name {
  font-weight: 600;
  color: var(--el-text-color-primary);
}

.user-id {
  font-size: 12px;
  color: var(--el-text-color-secondary);
}

.price-text {
  font-weight: 700;
  color: var(--el-color-success);
}

.user-profile-view {
  text-align: center;
}

.profile-header {
  margin-bottom: 24px;
}

.profile-header h2 {
  margin: 16px 0 8px;
}

.large-avatar {
  font-size: 28px;
  background-color: var(--el-color-primary);
}

.profile-desc {
  margin-top: 20px;
}
</style>
