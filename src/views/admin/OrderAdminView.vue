<script setup>
import { computed, reactive, ref, onMounted } from 'vue'
import { orderStatuses } from '../../utils/constants'
import { usePlatformStore } from '../../stores/platform'
import { Search, InfoFilled, Van, CircleCheck, Money } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const store = usePlatformStore()
const status = ref('全部')
const keyword = ref('')
const detailOrder = ref(null)
const shipOrderId = ref(null)
const showDetail = ref(false)
const showShip = ref(false)
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)

const shipForm = reactive({ shipCompany: '顺丰速运', shipNo: '' })

const rows = computed(() =>
  store.orders.filter((order) => {
    const statusMatch = status.value === '全部' || order.status === status.value
    const keywordMatch =
      !keyword.value || order.id.includes(keyword.value) || order.buyer.includes(keyword.value)
    return statusMatch && keywordMatch
  }),
)

const fetchPage = async () => {
  loading.value = true
  try {
    await store.fetchAdminOrders(currentPage.value - 1, pageSize.value)
  } finally {
    loading.value = false
  }
}

const handlePageChange = (val) => {
  currentPage.value = val
  fetchPage()
}

const getStatusType = (status) => {
  if (status === '待付款') return 'info'
  if (status === '待发货') return 'warning'
  if (status === '待收货') return 'primary'
  if (status === '已完成') return 'success'
  if (status === '已取消') return 'danger'
  return ''
}

const openDetail = (order) => {
  detailOrder.value = order
  showDetail.value = true
}

const openShip = (orderId) => {
  shipOrderId.value = orderId
  shipForm.shipNo = ''
  showShip.value = true
}

const submitShip = async () => {
  if (!shipForm.shipNo) return ElMessage.warning('请输入物流单号')
  await store.shipOrder(shipOrderId.value, shipForm.shipCompany, shipForm.shipNo)
  ElMessage.success('发货成功')
  showShip.value = false
}

const handleRefund = (orderId) => {
  ElMessageBox.confirm('确定要为该订单办理退款吗？操作后订单将标记为已取消。', '退款处理', {
    confirmButtonText: '确认退款',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await store.refundOrder(orderId)
    ElMessage.success('退款已完成')
  })
}

onMounted(() => {
  fetchPage()
})
</script>

<template>
  <div class="admin-page">
    <div class="page-header">
      <div>
        <h1 class="page-title">订单管理</h1>
        <p class="page-subtitle">监控平台交易流向，处理待发货、退款及异常订单。</p>
      </div>
    </div>

    <el-card shadow="never" class="table-card">
      <div class="toolbar">
        <div class="filter-group">
          <el-radio-group v-model="status" @change="currentPage = 1; fetchPage()">
            <el-radio-button v-for="item in orderStatuses" :key="item" :label="item" :value="item" />
          </el-radio-group>
        </div>
        <div class="search-group">
          <el-input
            v-model="keyword"
            placeholder="搜索订单号或买家..."
            :prefix-icon="Search"
            clearable
            style="width: 280px"
          />
        </div>
      </div>

      <el-table :data="rows" v-loading="loading" style="width: 100%">
        <el-table-column prop="id" label="订单号" width="160" />
        <el-table-column label="买家信息" width="180">
          <template #default="{ row }">
            <div class="buyer-info">
              <div class="buyer-name">{{ row.buyer }}</div>
              <div class="buyer-phone">{{ row.phone }}</div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="商品摘要" min-width="250">
          <template #default="{ row }">
            <div class="order-items-summary">
              <span v-for="(item, index) in row.items.slice(0, 2)" :key="index" class="item-tag">
                {{ item.name }} x{{ item.quantity }}
              </span>
              <span v-if="row.items.length > 2" class="muted">等 {{ row.items.length }} 件</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="实付金额" width="120">
          <template #default="{ row }">
            <span class="price-text">¥{{ row.payAmount }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="下单时间" width="170" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button-group>
              <el-button :icon="InfoFilled" @click="openDetail(row)">详情</el-button>
              <el-button 
                v-if="row.status === '待发货'"
                type="primary" 
                :icon="Van"
                @click="openShip(row.id)"
              >
                发货
              </el-button>
              <el-button 
                v-if="row.status === '退款中'"
                type="danger" 
                :icon="Money"
                @click="handleRefund(row.id)"
              >
                退款
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
          :total="store.orderTotal"
          @size-change="fetchPage"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>

    <!-- 详情弹窗 -->
    <el-dialog v-model="showDetail" title="订单详情" width="600px">
      <div v-if="detailOrder" class="order-detail-view">
        <div class="detail-section">
          <h3><el-icon><InfoFilled /></el-icon> 基础信息</h3>
          <el-descriptions :column="1" border>
            <el-descriptions-item label="订单号">{{ detailOrder.id }}</el-descriptions-item>
            <el-descriptions-item label="买家姓名">{{ detailOrder.buyer }}</el-descriptions-item>
            <el-descriptions-item label="联系电话">{{ detailOrder.phone }}</el-descriptions-item>
            <el-descriptions-item label="下单时间">{{ detailOrder.createdAt }}</el-descriptions-item>
          </el-descriptions>
        </div>

        <div class="detail-section">
          <h3><el-icon><Van /></el-icon> 收货地址</h3>
          <div class="address-box">
            <div class="address-contact">{{ detailOrder.receiverName }} {{ detailOrder.receiverPhone }}</div>
            <div class="address-text">{{ detailOrder.receiverAddress }}</div>
          </div>
        </div>

        <div class="detail-section">
          <h3><el-icon><ShoppingCart /></el-icon> 商品列表</h3>
          <el-table :data="detailOrder.items" size="small" border>
            <el-table-column prop="name" label="名称" />
            <el-table-column prop="sku" label="规格" width="100" />
            <el-table-column prop="price" label="单价" width="80" />
            <el-table-column prop="quantity" label="数量" width="70" align="center" />
          </el-table>
        </div>

        <div class="detail-section" v-if="detailOrder.shipNo">
          <h3><el-icon><CircleCheck /></el-icon> 物流信息</h3>
          <el-descriptions :column="1" border>
            <el-descriptions-item label="物流公司">{{ detailOrder.shipCompany }}</el-descriptions-item>
            <el-descriptions-item label="物流单号">{{ detailOrder.shipNo }}</el-descriptions-item>
          </el-descriptions>
        </div>
      </div>
    </el-dialog>

    <!-- 发货弹窗 -->
    <el-dialog v-model="showShip" title="订单发货" width="450px">
      <el-form :model="shipForm" label-position="top">
        <el-form-item label="物流公司">
          <el-select v-model="shipForm.shipCompany" style="width: 100%">
            <el-option label="顺丰速运" value="顺丰速运" />
            <el-option label="中通快递" value="中通快递" />
            <el-option label="圆通快递" value="圆通快递" />
            <el-option label="邮政 EMS" value="邮政 EMS" />
          </el-select>
        </el-form-item>
        <el-form-item label="物流单号">
          <el-input v-model="shipForm.shipNo" placeholder="请输入快递单号" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showShip = false">取消</el-button>
        <el-button type="primary" @click="submitShip">确认发货</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.page-header {
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

.buyer-info {
  line-height: 1.4;
}

.buyer-name {
  font-weight: 600;
}

.buyer-phone {
  font-size: 12px;
  color: var(--el-text-color-secondary);
}

.item-tag {
  display: inline-block;
  background: var(--el-fill-color-light);
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
  margin-right: 6px;
  margin-bottom: 4px;
}

.price-text {
  font-weight: 700;
  color: var(--el-color-success);
}

.pagination-wrapper {
  margin-top: 24px;
  display: flex;
  justify-content: flex-end;
}

.detail-section {
  margin-bottom: 24px;
}

.detail-section h3 {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  margin-bottom: 12px;
  border-bottom: 1px solid var(--el-border-color-lighter);
  padding-bottom: 8px;
}

.address-box {
  background: var(--el-fill-color-blank);
  padding: 16px;
  border-radius: 8px;
  border: 1px dashed var(--el-border-color);
}

.address-contact {
  font-weight: 700;
  margin-bottom: 6px;
}
</style>
