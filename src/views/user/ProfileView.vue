<script setup>
import { computed, reactive } from 'vue'
import { usePlatformStore } from '../../stores/platform'
import { User, Location, Phone, Goods, ShoppingCart, Plus } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const store = usePlatformStore()
const user = computed(() => store.currentUser)
const addressBook = computed(() => user.value.addressBook || [])
const defaultAddress = computed(() => addressBook.value.find((item) => item.isDefault))

const showEditDialog = ref(false)
const profileForm = reactive({
  nickname: '',
  phone: '',
  password: ''
})

const openEditDialog = () => {
  profileForm.nickname = user.value.nickname
  profileForm.phone = user.value.phone
  profileForm.password = ''
  showEditDialog.value = true
}

const submitProfile = async () => {
  if (!profileForm.nickname || !profileForm.phone) {
    return ElMessage.warning('请填写完整的个人信息')
  }
  try {
    await store.updateProfile({ ...profileForm })
    ElMessage.success('个人信息更新成功')
    showEditDialog.value = false
  } catch (error) {
    ElMessage.error(error.message || '更新失败')
  }
}

const form = reactive({
  name: '',
  phone: '',
  address: '',
  isDefault: false,
})

const submitAddress = async () => {
  if (!form.name || !form.phone || !form.address) {
    return ElMessage.warning('请填写完整的收货信息')
  }
  try {
    await store.addAddress({ ...form })
    ElMessage.success('地址添加成功')
    form.name = ''
    form.phone = ''
    form.address = ''
    form.isDefault = false
  } catch (error) {
    ElMessage.error(error.message || '添加失败')
  }
}
</script>

<template>
  <div class="profile-container">
    <div class="page-header">
      <h1 class="page-title">个人中心</h1>
      <p class="page-subtitle">管理您的个人资料、收货地址与消费记录。</p>
    </div>

    <el-row :gutter="24" class="profile-layout">
      <!-- 左侧：个人信息与统计 -->
      <el-col :span="8">
        <el-card shadow="never" class="user-card">
          <div class="user-header">
            <el-avatar :size="80" class="user-avatar">{{ user.avatar }}</el-avatar>
            <h2 class="user-nickname">{{ user.nickname }}</h2>
            <div class="user-meta">
              <span class="meta-item"><el-icon><User /></el-icon>{{ user.name }}</span>
              <span class="meta-item"><el-icon><Phone /></el-icon>{{ user.phone }}</span>
            </div>
            <div class="user-actions">
              <el-button size="small" type="primary" link @click="openEditDialog">修改资料</el-button>
            </div>
          </div>

          <el-divider border-style="dashed" />

          <div class="user-stats">
            <div class="stat-item">
              <div class="stat-label">累计订单</div>
              <div class="stat-value">{{ store.currentUserOrders.length }}</div>
            </div>
            <div class="stat-item">
              <div class="stat-label">购物车商品</div>
              <div class="stat-value">{{ store.cartCount }}</div>
            </div>
          </div>

          <el-divider border-style="dashed" />

          <div class="default-address-preview">
            <div class="preview-header">默认收货地址</div>
            <div class="preview-content" v-if="defaultAddress">
              <div class="preview-name">{{ defaultAddress.name }} {{ defaultAddress.phone }}</div>
              <div class="preview-text">{{ defaultAddress.address }}</div>
            </div>
            <div class="preview-content empty-preview" v-else>
              暂无默认地址，请在右侧添加
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 右侧：地址管理 -->
      <el-col :span="16">
        <el-card shadow="never" class="address-card">
          <template #header>
            <div class="card-header">
              <span>收货地址管理</span>
            </div>
          </template>

          <div class="address-form-box">
            <h3 class="form-title">新增地址</h3>
            <el-form :model="form" label-width="80px" label-position="top">
              <el-row :gutter="20">
                <el-col :span="12">
                  <el-form-item label="收货人">
                    <el-input v-model="form.name" placeholder="请输入收货人姓名" />
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="手机号码">
                    <el-input v-model="form.phone" placeholder="请输入联系电话" />
                  </el-form-item>
                </el-col>
              </el-row>
              
              <el-form-item label="详细地址">
                <el-input 
                  v-model="form.address" 
                  type="textarea" 
                  :rows="2" 
                  placeholder="请输入省、市、区及详细街道门牌信息" 
                />
              </el-form-item>
              
              <div class="form-actions">
                <el-checkbox v-model="form.isDefault" label="设为默认地址" />
                <el-button type="primary" :icon="Plus" @click="submitAddress">保存收货地址</el-button>
              </div>
            </el-form>
          </div>

          <el-divider border-style="dashed" />

          <div class="address-list-box">
            <h3 class="list-title">已有地址</h3>
            <div class="address-list" v-if="addressBook.length">
              <div v-for="address in addressBook" :key="address.id" class="address-item">
                <div class="address-info">
                  <div class="address-contact">
                    <span class="contact-name">{{ address.name }}</span>
                    <span class="contact-phone">{{ address.phone }}</span>
                  </div>
                  <div class="address-detail"><el-icon><Location /></el-icon> {{ address.address }}</div>
                </div>
                <div class="address-status">
                  <el-tag :type="address.isDefault ? 'success' : 'info'" effect="dark" size="small">
                    {{ address.isDefault ? '默认地址' : '备用地址' }}
                  </el-tag>
                </div>
              </div>
            </div>
            <el-empty v-else description="您还没有添加过收货地址" :image-size="120" />
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 修改个人信息对话框 -->
    <el-dialog
      v-model="showEditDialog"
      title="修改个人信息"
      width="400px"
      class="custom-dialog"
      align-center
    >
      <el-form :model="profileForm" label-position="top">
        <el-form-item label="昵称">
          <el-input v-model="profileForm.nickname" placeholder="请输入新昵称" />
        </el-form-item>
        <el-form-item label="手机号码">
          <el-input v-model="profileForm.phone" placeholder="请输入新手机号" />
        </el-form-item>
        <el-form-item label="修改密码 (不改请留空)">
          <el-input v-model="profileForm.password" type="password" placeholder="请输入新密码" show-password />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="showEditDialog = false">取消</el-button>
          <el-button type="primary" @click="submitProfile">保存修改</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.profile-container {
  max-width: 1200px;
  margin: 0 auto;
  padding-bottom: 60px;
}

.page-header {
  margin-bottom: 24px;
}

.page-title {
  font-size: 32px;
  font-weight: 700;
  margin: 0 0 8px;
}

.page-subtitle {
  color: var(--el-text-color-secondary);
  margin: 0;
}

.profile-layout {
  align-items: flex-start;
}

.user-card, .address-card {
  border: none;
  background: var(--el-bg-color-overlay);
}

.user-card {
  position: sticky;
  top: 24px;
}

.user-header {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px 0;
}

.user-avatar {
  background-color: var(--el-color-primary-light-3);
  color: var(--el-color-white);
  font-size: 36px;
  font-weight: 700;
  margin-bottom: 16px;
}

.user-nickname {
  margin: 0 0 12px;
  font-size: 24px;
  font-weight: 700;
  color: var(--el-text-color-primary);
}

.user-meta {
  display: flex;
  gap: 16px;
  color: var(--el-text-color-secondary);
  font-size: 14px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 6px;
}

.user-actions {
  margin-top: 12px;
}

.user-stats {
  display: flex;
  justify-content: space-around;
  padding: 10px 0;
}

.stat-item {
  text-align: center;
}

.stat-label {
  font-size: 13px;
  color: var(--el-text-color-secondary);
  margin-bottom: 8px;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: var(--el-text-color-primary);
}

.default-address-preview {
  padding: 10px 20px 20px;
}

.preview-header {
  font-size: 14px;
  font-weight: 600;
  margin-bottom: 12px;
  color: var(--el-text-color-primary);
}

.preview-content {
  background-color: var(--el-fill-color-darker);
  padding: 16px;
  border-radius: 8px;
}

.preview-name {
  font-weight: 600;
  margin-bottom: 6px;
  color: var(--el-text-color-primary);
}

.preview-text {
  font-size: 13px;
  color: var(--el-text-color-secondary);
  line-height: 1.5;
}

.empty-preview {
  color: var(--el-text-color-placeholder);
  text-align: center;
  padding: 24px 16px;
}

.card-header {
  font-size: 18px;
  font-weight: 600;
}

.form-title, .list-title {
  font-size: 16px;
  font-weight: 600;
  margin: 0 0 20px;
  color: var(--el-text-color-primary);
}

.address-form-box {
  padding: 10px 0;
}

.form-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 10px;
}

.address-list-box {
  padding: 10px 0;
}

.address-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.address-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  background-color: var(--el-fill-color-blank);
  border-radius: 8px;
  border: 1px solid var(--el-border-color-lighter);
  transition: border-color 0.3s;
}

.address-item:hover {
  border-color: var(--el-color-primary-light-5);
}

.address-info {
  flex: 1;
}

.address-contact {
  margin-bottom: 10px;
}

.contact-name {
  font-weight: 600;
  font-size: 16px;
  margin-right: 12px;
  color: var(--el-text-color-primary);
}

.contact-phone {
  color: var(--el-text-color-secondary);
  font-size: 14px;
}

.address-detail {
  display: flex;
  align-items: flex-start;
  gap: 6px;
  font-size: 13px;
  color: var(--el-text-color-regular);
  line-height: 1.5;
}

.address-detail .el-icon {
  margin-top: 2px;
  color: var(--el-text-color-secondary);
}

.address-status {
  margin-left: 24px;
}
</style>
