import { defineStore } from 'pinia'
import { usePlatformStore } from './platform.js'
import { getJson, postJson } from '../utils/http.js'

const storageKey = 'farmer-platform-auth'

const getStoredSession = () => {
  const raw = localStorage.getItem(storageKey)
  if (!raw) return null

  try {
    return JSON.parse(raw)
  } catch {
    return null
  }
}

const defaultAddress = (account, phone) => ({
  id: 1,
  name: account,
  phone,
  address: '请在个人中心补充收货地址',
  isDefault: true,
})

export const useAuthStore = defineStore('auth', {
  state: () => ({
    session: getStoredSession(),
  }),
  getters: {
    isLoggedIn(state) {
      return Boolean(state.session?.token)
    },
    role(state) {
      return state.session?.role ?? 'guest'
    },
    accountName(state) {
      return state.session?.account ?? ''
    },
  },
  actions: {
    persistSession() {
      localStorage.setItem(storageKey, JSON.stringify(this.session))
    },
    applySession(accountInfo, remember = true) {
      this.session = {
        id: accountInfo.id,
        account: accountInfo.account,
        nickname: accountInfo.nickname,
        phone: accountInfo.phone,
        role: accountInfo.role,
        token: accountInfo.token || this.session?.token || '',
        remember,
      }

      if (remember) this.persistSession()
      else localStorage.removeItem(storageKey)
    },
    syncUserProfile(accountInfo) {
      const platformStore = usePlatformStore()
      const existingUser = platformStore.users.find((item) => item.phone === accountInfo.phone)
      const existingAddressBook =
        platformStore.currentUser.phone === accountInfo.phone
          ? platformStore.currentUser.addressBook || []
          : accountInfo.role === 'user'
            ? [defaultAddress(accountInfo.account, accountInfo.phone)]
            : []

      if (accountInfo.role === 'user' && !existingUser) {
        platformStore.users.unshift({
          id: accountInfo.id ?? Date.now(),
          name: accountInfo.account,
          phone: accountInfo.phone,
          status: '正常',
          createdAt: new Date().toISOString().slice(0, 10),
          orders: 0,
          spend: 0,
          lastActive: new Date().toISOString().slice(0, 10),
        })
      }

      platformStore.syncCurrentUser(accountInfo, existingAddressBook)
    },
    updateSession(updatedInfo) {
      this.session = {
        ...this.session,
        nickname: updatedInfo.nickname || this.session.nickname,
        phone: updatedInfo.phone || this.session.phone,
      }
      this.persistSession()
    },
    async login({ account, password, remember = true }) {
      const data = await postJson('/api/auth/login', {
        account: account.trim(),
        password,
      })

      this.applySession(data, remember)
      this.syncUserProfile(data)
      return data.role
    },
    async register({ account, phone, password, nickname, remember = true }) {
      const data = await postJson('/api/auth/register', {
        account: account.trim(),
        phone: phone.trim(),
        password: password || '123456',
        nickname: nickname || account.trim(),
      })

      this.applySession(data, remember)
      this.syncUserProfile(data)
      return data
    },
    async resetPassword({ account, phone, code, nextPassword, expectedCode }) {
      if (code !== expectedCode) {
        throw new Error('验证码错误，请输入页面展示的模拟验证码')
      }

      await postJson('/api/auth/reset-password', {
        account: account.trim(),
        phone: phone.trim(),
        nextPassword,
      })

      return true
    },
    async bootstrap() {
      if (this.session && !this.session.token) {
        this.logout()
        return
      }

      if (!this.session?.token) return

      try {
        const profile = await getJson('/api/auth/me')
        this.applySession(profile, this.session.remember ?? true)
        this.syncUserProfile(profile)
      } catch {
        this.logout()
      }
    },
    logout() {
      const platformStore = usePlatformStore()
      platformStore.resetClientState()
      this.session = null
      localStorage.removeItem(storageKey)
    },
  },
})
