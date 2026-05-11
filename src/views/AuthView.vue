<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { usePlatformStore } from '../stores/platform'

const router = useRouter()
const authStore = useAuthStore()
const platformStore = usePlatformStore()

const tab = ref('login')
const errorMessage = ref('')
const successMessage = ref('')
const forgotVisible = ref(false)
const loading = ref(false)

const loginForm = reactive({
  account: '',
  password: '',
  remember: true,
})

const registerForm = reactive({
  account: '',
  phone: '',
  code: '',
  password: '',
  remember: true,
})

const forgotForm = reactive({
  account: '',
  phone: '',
  code: '',
  nextPassword: '',
})

const registerCode = ref('246810')
const forgotCode = ref('135790')

const clearMessages = () => {
  errorMessage.value = ''
  successMessage.value = ''
}

const withLoading = async (action) => {
  loading.value = true
  clearMessages()

  try {
    await action()
  } catch (error) {
    errorMessage.value = error.message || '操作失败，请稍后重试'
  } finally {
    loading.value = false
  }
}

const handleLogin = async () => {
  await withLoading(async () => {
    const role = await authStore.login(loginForm)
    await platformStore.bootstrapPrivate(role, authStore.session)
    successMessage.value = '登录成功，正在进入平台'
    router.push(role === 'admin' ? '/admin/dashboard' : '/')
  })
}

const handleRegister = async () => {
  await withLoading(async () => {
    if (registerForm.code !== registerCode.value) {
      throw new Error('验证码错误，请输入页面展示的模拟验证码')
    }

    await authStore.register({
      account: registerForm.account,
      phone: registerForm.phone,
      password: registerForm.password || '123456',
      nickname: registerForm.account,
      remember: registerForm.remember,
    })

    await platformStore.bootstrapPrivate('user', authStore.session)
    successMessage.value = '注册成功，已自动登录 user 账号'
    router.push('/')
  })
}

const handleResetPassword = async () => {
  await withLoading(async () => {
    await authStore.resetPassword({
      account: forgotForm.account,
      phone: forgotForm.phone,
      code: forgotForm.code,
      nextPassword: forgotForm.nextPassword,
      expectedCode: forgotCode.value,
    })

    forgotVisible.value = false
    loginForm.account = forgotForm.account
    loginForm.password = forgotForm.nextPassword
    successMessage.value = '密码重置成功，请使用新密码登录'
  })
}
</script>

<template>
  <div class="auth-page">
    <!-- Floating Background Elements -->
    <div class="bg-orb orb-1"></div>
    <div class="bg-orb orb-2"></div>
    
    <div class="auth-card">
      <div class="auth-header">
        <div class="auth-logo">
          <div class="logo-leaf"></div>
        </div>
        <h1>智慧三农</h1>
        <p class="subtitle">Connecting the Soil to the Soul</p>
      </div>

      <div class="auth-tabs">
        <button :class="{ active: tab === 'login' }" @click="tab = 'login'">密码登录</button>
        <button :class="{ active: tab === 'register' }" @click="tab = 'register'">
          快速注册
        </button>
        <div class="tab-indicator" :class="{ right: tab === 'register' }"></div>
      </div>

      <div v-if="tab === 'login'" class="auth-panel">
        <div class="input-group">
          <label>账号</label>
          <input
            v-model="loginForm.account"
            class="auth-input"
            placeholder="张大农 / tfgkk"
          />
        </div>
        <div class="input-group">
          <label>密码</label>
          <input
            v-model="loginForm.password"
            class="auth-input"
            type="password"
            placeholder="请输入您的密码"
          />
        </div>

        <div class="auth-meta">
          <label class="remember">
            <input v-model="loginForm.remember" type="checkbox" />
            <span>记住我的登录状态</span>
          </label>
          <button class="link-btn" type="button" @click="forgotVisible = true">忘记密码？</button>
        </div>

        <button class="submit-btn" :disabled="loading" @click="handleLogin">
          {{ loading ? '验证中...' : '立即登录' }}
        </button>
      </div>

      <div v-else class="auth-panel">
        <div class="input-group">
          <label>用户账号</label>
          <input v-model="registerForm.account" class="auth-input" placeholder="设置您的登录账号" />
        </div>
        <div class="input-group">
          <label>手机号码</label>
          <input v-model="registerForm.phone" class="auth-input" placeholder="用于接收通知" />
        </div>

        <div class="input-group">
          <label>验证码</label>
          <div class="auth-code-row">
            <input v-model="registerForm.code" class="auth-input" placeholder="输入右侧码" />
            <button class="code-btn" type="button">码：{{ registerCode }}</button>
          </div>
        </div>

        <div class="input-group">
          <label>登录密码</label>
          <input
            v-model="registerForm.password"
            class="auth-input"
            type="password"
            placeholder="设置您的访问密码"
          />
        </div>

        <button class="submit-btn" :disabled="loading" @click="handleRegister">
          {{ loading ? '处理中...' : '注册并登录' }}
        </button>
      </div>

      <div class="auth-footer">
        <div class="test-account-pill">
          <strong>测试账号：</strong> user: 张大农 / admin: tfgkk
        </div>
        <div class="copyright">© 2024 Farmer Platform UI/UX Redesign</div>
      </div>

      <transition name="fade">
        <p v-if="errorMessage" class="message error">{{ errorMessage }}</p>
      </transition>
      <transition name="fade">
        <p v-if="successMessage" class="message success">{{ successMessage }}</p>
      </transition>
    </div>

    <!-- Modal for forgot password -->
    <transition name="scale">
      <div v-if="forgotVisible" class="modal-mask" @click.self="forgotVisible = false">
        <div class="forgot-card">
          <div class="modal-header">
            <h2>重置密码</h2>
            <p>输入信息以找回您的账号访问权限</p>
          </div>
          <div class="auth-panel">
            <input v-model="forgotForm.account" class="auth-input" placeholder="请输入您的账号" />
            <input v-model="forgotForm.phone" class="auth-input" placeholder="绑定手机号" />

            <div class="auth-code-row">
              <input v-model="forgotForm.code" class="auth-input" placeholder="验证码" />
              <button class="code-btn" type="button">{{ forgotCode }}</button>
            </div>

            <input
              v-model="forgotForm.nextPassword"
              class="auth-input"
              type="password"
              placeholder="新密码"
            />
          </div>

          <div class="forgot-actions">
            <button class="ghost-btn" type="button" @click="forgotVisible = false">取消</button>
            <button class="submit-btn compact" :disabled="loading" type="button" @click="handleResetPassword">
              确认重置
            </button>
          </div>
        </div>
      </div>
    </transition>
  </div>
</template>

<style scoped>
.auth-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: var(--color-background);
  position: relative;
  overflow: hidden;
  padding: 24px;
}

.bg-orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  z-index: 0;
}

.orb-1 {
  width: 400px;
  height: 400px;
  background: rgba(16, 185, 129, 0.15);
  top: -100px;
  left: -100px;
}

.orb-2 {
  width: 500px;
  height: 500px;
  background: rgba(245, 158, 11, 0.1);
  bottom: -150px;
  right: -100px;
}

.auth-card {
  width: 100%;
  max-width: 480px;
  background: var(--color-surface);
  border-radius: var(--radius-xl);
  padding: 48px;
  box-shadow: var(--shadow-xl);
  border: 1px solid var(--color-line);
  position: relative;
  z-index: 10;
}

.auth-header {
  text-align: center;
  margin-bottom: 40px;
}

.auth-logo {
  width: 64px;
  height: 64px;
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-dark) 100%);
  border-radius: 20px;
  margin: 0 auto 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 12px 24px -6px rgba(16, 185, 129, 0.4);
}

.logo-leaf {
  width: 32px;
  height: 32px;
  background: white;
  mask: radial-gradient(circle at 0 100%, transparent 40%, black 40%);
  border-radius: 0 20px 0 20px;
}

.auth-header h1 {
  font-size: 28px;
  font-weight: 800;
  color: var(--color-text);
  margin-bottom: 4px;
}

.subtitle {
  color: var(--color-text-mute);
  font-size: 14px;
  font-weight: 500;
}

.auth-tabs {
  display: flex;
  background: var(--color-line);
  padding: 6px;
  border-radius: 16px;
  margin-bottom: 32px;
  position: relative;
}

.auth-tabs button {
  flex: 1;
  border: none;
  background: transparent;
  padding: 12px;
  font-size: 15px;
  font-weight: 700;
  color: var(--color-text-soft);
  cursor: pointer;
  z-index: 1;
  transition: color 0.3s;
}

.auth-tabs button.active {
  color: var(--color-primary-dark);
}

.tab-indicator {
  position: absolute;
  top: 6px;
  left: 6px;
  width: calc(50% - 6px);
  height: calc(100% - 12px);
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 10px rgba(0,0,0,0.05);
  transition: transform 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.tab-indicator.right {
  transform: translateX(100%);
}

.auth-panel {
  display: grid;
  gap: 20px;
}

.input-group label {
  display: block;
  font-size: 13px;
  font-weight: 700;
  color: var(--color-text-soft);
  margin-bottom: 8px;
  margin-left: 4px;
}

.auth-input {
  width: 100%;
  height: 52px;
  padding: 0 18px;
  border: 2px solid var(--color-line);
  border-radius: 14px;
  background: var(--color-background);
  color: var(--color-text);
  font-weight: 500;
  outline: none;
  transition: all 0.2s;
}

.auth-input:focus {
  border-color: var(--color-primary);
  background: white;
  box-shadow: 0 0 0 4px var(--color-primary-light);
}

.auth-meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.remember {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: var(--color-text-soft);
  cursor: pointer;
}

.link-btn {
  background: transparent;
  border: none;
  color: var(--color-primary-dark);
  font-weight: 700;
  font-size: 13px;
  cursor: pointer;
}

.submit-btn {
  height: 56px;
  background: var(--color-primary);
  color: white;
  border: none;
  border-radius: 16px;
  font-size: 16px;
  font-weight: 800;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 10px 15px -3px rgba(16, 185, 129, 0.3);
}

.submit-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 20px 25px -5px rgba(16, 185, 129, 0.4);
}

.submit-btn:disabled {
  opacity: 0.6;
  transform: none;
}

.auth-code-row {
  display: grid;
  grid-template-columns: 1fr auto;
  gap: 12px;
}

.code-btn {
  height: 52px;
  padding: 0 20px;
  background: var(--color-secondary-light);
  color: var(--color-secondary);
  border: none;
  border-radius: 14px;
  font-weight: 800;
  font-size: 14px;
  cursor: default;
}

.auth-footer {
  margin-top: 32px;
  text-align: center;
}

.test-account-pill {
  display: inline-block;
  padding: 8px 16px;
  background: var(--color-line);
  border-radius: 999px;
  font-size: 12px;
  color: var(--color-text-soft);
  margin-bottom: 12px;
}

.copyright {
  font-size: 12px;
  color: var(--color-text-mute);
}

.message {
  text-align: center;
  margin-top: 16px;
  font-size: 14px;
  font-weight: 600;
}

.message.error { color: var(--color-danger); }
.message.success { color: var(--color-success); }

/* Modal */
.modal-mask {
  position: fixed;
  inset: 0;
  background: rgba(15, 23, 42, 0.4);
  backdrop-filter: blur(8px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 100;
  padding: 24px;
}

.forgot-card {
  width: 100%;
  max-width: 440px;
  background: white;
  border-radius: 24px;
  padding: 32px;
  box-shadow: var(--shadow-xl);
}

.modal-header {
  text-align: center;
  margin-bottom: 24px;
}

.modal-header h2 {
  font-size: 20px;
  font-weight: 800;
  margin-bottom: 4px;
}

.modal-header p {
  font-size: 14px;
  color: var(--color-text-mute);
}

.forgot-actions {
  display: flex;
  gap: 12px;
  margin-top: 24px;
}

.ghost-btn {
  flex: 1;
  height: 50px;
  background: var(--color-line);
  border: none;
  border-radius: 14px;
  font-weight: 700;
  color: var(--color-text-soft);
  cursor: pointer;
}

.compact {
  flex: 2;
  height: 50px;
}

/* Animations */
.fade-enter-active, .fade-leave-active { transition: opacity 0.3s; }
.fade-enter-from, .fade-leave-to { opacity: 0; }

.scale-enter-active, .scale-leave-active { transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1); }
.scale-enter-from { opacity: 0; transform: scale(0.9); }
.scale-leave-to { opacity: 0; transform: scale(0.95); }
</style>
