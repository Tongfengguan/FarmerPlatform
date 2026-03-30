<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const tab = ref('login')
const errorMessage = ref('')
const successMessage = ref('')
const forgotVisible = ref(false)

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

const handleLogin = () => {
  clearMessages()

  try {
    const role = authStore.login(loginForm)
    router.push(role === 'admin' ? '/admin/dashboard' : '/')
  } catch (error) {
    errorMessage.value = error.message
  }
}

const handleRegister = () => {
  clearMessages()

  if (registerForm.code !== registerCode.value) {
    errorMessage.value = '验证码错误，请输入页面展示的模拟验证码'
    return
  }

  try {
    authStore.register({
      account: registerForm.account,
      phone: registerForm.phone,
      password: registerForm.password || '123456',
      nickname: registerForm.account,
    })
    successMessage.value = '注册成功，已自动登录 user 角色账号'
    router.push('/')
  } catch (error) {
    errorMessage.value = error.message
  }
}

const handleResetPassword = () => {
  clearMessages()

  try {
    authStore.resetPassword({
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
  } catch (error) {
    errorMessage.value = error.message
  }
}
</script>

<template>
  <div class="auth-page">
    <div class="auth-card">
      <div class="auth-logo"></div>
      <h1>智慧三农平台</h1>

      <div class="auth-tabs">
        <button :class="{ active: tab === 'login' }" @click="tab = 'login'">密码登陆</button>
        <button :class="{ active: tab === 'register' }" @click="tab = 'register'">
          注册/快捷登陆
        </button>
      </div>

      <div v-if="tab === 'login'" class="auth-panel">
        <input
          v-model="loginForm.account"
          class="auth-input"
          placeholder="请输入账号（张大农 / tfgkk）"
        />
        <input
          v-model="loginForm.password"
          class="auth-input"
          type="password"
          placeholder="请输入密码"
        />

        <div class="auth-meta">
          <label class="remember">
            <input v-model="loginForm.remember" type="checkbox" />
            <span>记住我</span>
          </label>
          <button class="link-btn" type="button" @click="forgotVisible = true">忘记密码？</button>
        </div>

        <button class="submit-btn" @click="handleLogin">登录</button>
      </div>

      <div v-else class="auth-panel">
        <input v-model="registerForm.account" class="auth-input" placeholder="请输入注册账号" />
        <input v-model="registerForm.phone" class="auth-input" placeholder="请输入手机号" />

        <div class="auth-code-row">
          <input v-model="registerForm.code" class="auth-input" placeholder="请输入短信验证码" />
          <button class="code-btn" type="button">验证码 {{ registerCode }}</button>
        </div>

        <input
          v-model="registerForm.password"
          class="auth-input"
          type="password"
          placeholder="请设置登录密码（可选）"
        />

        <label class="remember">
          <input v-model="registerForm.remember" type="checkbox" />
          <span>记住我</span>
        </label>

        <button class="submit-btn" @click="handleRegister">注册并登录</button>
      </div>

      <div class="auth-footer">
        <div>内置账号：user 张大农，admin tfgkk</div>
        <div>默认密码：123456</div>
      </div>

      <p v-if="errorMessage" class="message error">{{ errorMessage }}</p>
      <p v-if="successMessage" class="message success">{{ successMessage }}</p>
    </div>

    <div v-if="forgotVisible" class="modal-mask" @click.self="forgotVisible = false">
      <div class="forgot-card">
        <h2>忘记密码</h2>
        <p class="modal-tip">输入账号、手机号和验证码后即可重置密码。</p>
        <div class="auth-panel">
          <input v-model="forgotForm.account" class="auth-input" placeholder="请输入账号" />
          <input v-model="forgotForm.phone" class="auth-input" placeholder="请输入手机号" />

          <div class="auth-code-row">
            <input v-model="forgotForm.code" class="auth-input" placeholder="请输入短信验证码" />
            <button class="code-btn" type="button">验证码 {{ forgotCode }}</button>
          </div>

          <input
            v-model="forgotForm.nextPassword"
            class="auth-input"
            type="password"
            placeholder="请输入新密码（至少 6 位）"
          />
        </div>

        <div class="forgot-actions">
          <button class="ghost-btn" type="button" @click="forgotVisible = false">取消</button>
          <button class="submit-btn compact" type="button" @click="handleResetPassword">
            确认重置
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.auth-page {
  min-height: 100vh;
  display: grid;
  place-items: center;
  background:
    radial-gradient(circle at top left, rgba(35, 176, 125, 0.1), transparent 34%), #163e30;
  padding: 24px;
}

.auth-card {
  width: min(100%, 520px);
  padding: 42px 34px 28px;
  border: 1px solid rgba(240, 246, 244, 0.35);
  border-radius: 18px;
  background: rgba(2, 10, 7, 0.94);
  box-shadow: 0 24px 40px rgba(0, 0, 0, 0.24);
}

.auth-logo {
  width: 62px;
  height: 62px;
  margin: 0 auto 16px;
  border-radius: 18px;
  background: linear-gradient(180deg, #27b184 0%, #239d76 100%);
}

.auth-card h1 {
  margin: 0 0 28px;
  text-align: center;
  color: #1fbb8a;
  font-size: 28px;
  font-weight: 500;
}

.auth-tabs {
  display: flex;
  gap: 22px;
  padding-bottom: 14px;
  border-bottom: 1px solid rgba(240, 246, 244, 0.3);
  margin-bottom: 28px;
}

.auth-tabs button {
  background: transparent;
  color: rgba(255, 255, 255, 0.54);
  cursor: pointer;
  padding: 0;
  font-size: 18px;
  font-weight: 600;
}

.auth-tabs button.active {
  color: #fff;
}

.auth-panel {
  display: grid;
  gap: 20px;
}

.auth-input {
  width: 100%;
  height: 54px;
  padding: 0 18px;
  border: 1px solid rgba(240, 246, 244, 0.35);
  border-radius: 14px;
  background: transparent;
  color: #ffffff;
  outline: none;
}

.auth-input::placeholder {
  color: rgba(255, 255, 255, 0.32);
}

.auth-input:focus {
  border-color: #20b485;
}

.auth-meta,
.remember {
  display: flex;
  align-items: center;
  justify-content: space-between;
  color: rgba(255, 255, 255, 0.66);
  font-size: 14px;
}

.remember {
  justify-content: flex-start;
  gap: 8px;
}

.link-btn {
  background: transparent;
  color: #20b485;
  cursor: pointer;
  padding: 0;
  font-size: 14px;
}

.submit-btn,
.code-btn,
.ghost-btn {
  border: 0;
  cursor: pointer;
}

.submit-btn {
  height: 64px;
  border-radius: 14px;
  background: #27a67d;
  color: #ffffff;
  font-size: 22px;
  font-weight: 700;
}

.submit-btn:hover {
  background: #22956f;
}

.compact {
  height: 50px;
  min-width: 160px;
  font-size: 18px;
}

.auth-code-row {
  display: grid;
  grid-template-columns: 1fr auto;
  gap: 12px;
}

.code-btn {
  min-width: 132px;
  padding: 0 18px;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.18);
  color: #20b485;
  font-weight: 700;
}

.auth-footer {
  margin-top: 24px;
  color: rgba(255, 255, 255, 0.45);
  line-height: 1.8;
  font-size: 13px;
}

.message {
  margin: 16px 0 0;
  font-size: 14px;
}

.message.error {
  color: #ff9d9d;
}

.message.success {
  color: #8bf0c8;
}

.modal-mask {
  position: fixed;
  inset: 0;
  display: grid;
  place-items: center;
  background: rgba(0, 0, 0, 0.54);
  padding: 24px;
}

.forgot-card {
  width: min(100%, 520px);
  padding: 28px;
  border-radius: 18px;
  background: rgba(2, 10, 7, 0.98);
  border: 1px solid rgba(240, 246, 244, 0.35);
}

.forgot-card h2 {
  margin: 0 0 8px;
}

.modal-tip {
  margin: 0 0 22px;
  color: rgba(255, 255, 255, 0.55);
}

.forgot-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 24px;
}

.ghost-btn {
  min-width: 100px;
  height: 50px;
  border-radius: 14px;
  background: rgba(255, 255, 255, 0.12);
  color: #ffffff;
}
</style>
