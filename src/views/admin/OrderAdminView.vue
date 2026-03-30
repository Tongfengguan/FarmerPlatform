<script setup>
import { computed, reactive, ref } from 'vue'
import { orderStatuses } from '../../data/mockData'
import { usePlatformStore } from '../../stores/platform'

const store = usePlatformStore()
const status = ref('全部')
const keyword = ref('')
const detailOrder = ref(null)
const shipOrderId = ref(null)
const refundOrderId = ref(null)
const shipForm = reactive({ shipCompany: '顺丰速运', shipNo: '' })

const rows = computed(() =>
  store.orders.filter((order) => {
    const statusMatch = status.value === '全部' || order.status === status.value
    const keywordMatch =
      !keyword.value || order.id.includes(keyword.value) || order.buyer.includes(keyword.value)
    return statusMatch && keywordMatch
  }),
)

const badgeClass = (value) => {
  if (value === '待发货') return 'badge-warning'
  if (value === '待收货') return 'badge-info'
  if (value === '已完成') return 'badge-success'
  if (value === '退款中') return 'badge-danger'
  return 'badge-muted'
}

const submitShip = () => {
  store.shipOrder(shipOrderId.value, shipForm.shipCompany, shipForm.shipNo)
  shipOrderId.value = null
  shipForm.shipNo = ''
}
</script>

<template>
  <section>
    <div class="admin-page-head">
      <h1>订单管理</h1>
      <p class="muted">可查看订单详情、执行发货操作，并处理退款中的订单。</p>
    </div>

    <div class="card panel">
      <div class="toolbar">
        <input v-model="keyword" class="field" placeholder="搜索订单号或买家姓名" />
      </div>

      <div class="chips">
        <button
          v-for="item in orderStatuses"
          :key="item"
          class="chip"
          :class="{ active: item === status }"
          @click="status = item"
        >
          {{ item }}
        </button>
      </div>

      <div class="table-wrap">
        <table class="table">
          <thead>
            <tr>
              <th>订单号</th>
              <th>买家</th>
              <th>商品信息</th>
              <th>实付金额</th>
              <th>下单时间</th>
              <th>状态</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="row in rows" :key="row.id">
              <td>{{ row.id }}</td>
              <td>{{ row.buyer }}</td>
              <td>{{ row.items[0].name }} x{{ row.items[0].quantity }}</td>
              <td>¥{{ row.payAmount }}</td>
              <td>{{ row.createdAt }}</td>
              <td><span class="badge" :class="badgeClass(row.status)">{{ row.status }}</span></td>
              <td>
                <div class="actions">
                  <button class="btn" @click="detailOrder = row">详情</button>
                  <button
                    v-if="row.status === '待发货'"
                    class="btn btn-primary"
                    @click="shipOrderId = row.id"
                  >
                    发货
                  </button>
                  <button
                    v-if="row.status === '退款中'"
                    class="btn btn-danger"
                    @click="refundOrderId = row.id"
                  >
                    退款
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <div v-if="detailOrder" class="modal-mask" @click.self="detailOrder = null">
      <div class="modal-panel card">
        <h2>订单详情</h2>
        <div class="order-detail-row"><span class="muted">订单号</span><strong>{{ detailOrder.id }}</strong></div>
        <div class="order-detail-row">
          <span class="muted">买家信息</span><strong>{{ detailOrder.buyer }} {{ detailOrder.phone }}</strong>
        </div>
        <div class="order-detail-row"><span class="muted">收货地址</span><strong>{{ detailOrder.receiver.address }}</strong></div>
        <div class="order-detail-row">
          <span class="muted">物流信息</span><strong>{{ detailOrder.shipCompany || '待填写' }} {{ detailOrder.shipNo }}</strong>
        </div>
        <div style="display: flex; justify-content: flex-end; margin-top: 24px">
          <button class="btn" @click="detailOrder = null">关闭</button>
        </div>
      </div>
    </div>

    <div v-if="shipOrderId" class="modal-mask" @click.self="shipOrderId = null">
      <div class="modal-panel card">
        <h2>订单发货</h2>
        <div class="modal-grid">
          <div>
            <div class="muted">物流公司</div>
            <select v-model="shipForm.shipCompany" class="select">
              <option>顺丰速运</option>
              <option>中通快递</option>
              <option>圆通快递</option>
              <option>邮政 EMS</option>
            </select>
          </div>
          <div>
            <div class="muted">物流单号</div>
            <input v-model="shipForm.shipNo" class="field" />
          </div>
        </div>
        <div style="display: flex; justify-content: flex-end; gap: 12px; margin-top: 24px">
          <button class="btn" @click="shipOrderId = null">取消</button>
          <button class="btn btn-primary" @click="submitShip">确认发货</button>
        </div>
      </div>
    </div>

    <div v-if="refundOrderId" class="modal-mask" @click.self="refundOrderId = null">
      <div class="modal-panel card">
        <h2>确认退款</h2>
        <p class="muted">退款操作将把订单状态更新为已取消，当前版本用于演示后台处理流程。</p>
        <div style="display: flex; justify-content: flex-end; gap: 12px; margin-top: 24px">
          <button class="btn" @click="refundOrderId = null">取消</button>
          <button class="btn btn-danger" @click="store.refundOrder(refundOrderId); refundOrderId = null">
            确认退款
          </button>
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

.order-detail-row {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  padding: 12px 0;
  border-bottom: 1px solid var(--line);
}
</style>
