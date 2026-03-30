<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const tab = ref('login')
const errorMessage = ref('')
const successMessage = ref('')

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

const fakeCode = ref('246810')

const handleLogin = () => {
  errorMessage.value = ''
  successMessage.value = ''
  try {
    const role = authStore.login(loginForm)
    router.push(role === 'admin' ? '/admin/dashboard' : '/')
  } catch (error) {
    errorMessage.value = error.message
  }
}

const handleRegister = () => {
  errorMessage.value = ''
  successMessage.value = ''

  if (registerForm.code !== fakeCode.value) {
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
          <span class="hint">测试密码均为 123456</span>
        </div>

        <button class="submit-btn" @click="handleLogin">登录</button>
      </div>

      <div v-else class="auth-panel">
        <input v-model="registerForm.account" class="auth-input" placeholder="请输入注册账号" />
        <input v-model="registerForm.phone" class="auth-input" placeholder="请输入手机号" />

        <div class="auth-code-row">
          <input
            v-model="registerForm.code"
            class="auth-input"
            placeholder="请输入短信验证码"
          />
          <button class="code-btn" type="button">验证码 {{ fakeCode }}</button>
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
  </div>
</template>

<style scoped>
.auth-page {
  min-height: 100vh;
  display: grid;
  place-items: center;
  background:
    radial-gradient(circle at top left, rgba(35, 176, 125, 0.1), transparent 34%),
    #163e30;
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
  color: #ffffff;
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

.hint {
  color: #20b485;
}

.submit-btn,
.code-btn {
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
</style>
