import { defineStore } from 'pinia'
import { usePlatformStore } from './platform.js'

const storageKey = 'farmer-platform-auth'
const accountsKey = 'farmer-platform-accounts'

const defaultAccounts = [
  {
    id: 'user-seed',
    account: '张大农',
    password: '123456',
    role: 'user',
    phone: '13800008821',
    nickname: '丰收老张',
    platformProfileId: 1001,
  },
  {
    id: 'admin-seed',
    account: 'tfgkk',
    password: '123456',
    role: 'admin',
    phone: '00000000000',
    nickname: '平台管理员',
    platformProfileId: null,
  },
]

const getStoredAccounts = () => {
  const raw = localStorage.getItem(accountsKey)
  if (!raw) return defaultAccounts
  try {
    return JSON.parse(raw)
  } catch {
    return defaultAccounts
  }
}

const getStoredSession = () => {
  const raw = localStorage.getItem(storageKey)
  if (!raw) return null
  try {
    return JSON.parse(raw)
  } catch {
    return null
  }
}

export const useAuthStore = defineStore('auth', {
  state: () => ({
    session: getStoredSession(),
    accounts: getStoredAccounts(),
  }),
  getters: {
    isLoggedIn(state) {
      return Boolean(state.session)
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
    persistAccounts() {
      localStorage.setItem(accountsKey, JSON.stringify(this.accounts))
    },
    login({ account, password, remember = true }) {
      const matched = this.accounts.find(
        (item) => item.account === account.trim() && item.password === password,
      )

      if (!matched) {
        throw new Error('账号或密码错误')
      }

      this.session = {
        account: matched.account,
        role: matched.role,
        remember,
      }

      const platformStore = usePlatformStore()
      if (matched.role === 'user') {
        const existingAddressBook = platformStore.currentUser.addressBook || []
        platformStore.setCurrentUser({
          id: matched.platformProfileId ?? 1001,
          name: matched.account,
          phone: matched.phone,
          nickname: matched.nickname || matched.account,
          avatar: matched.account.slice(0, 1),
          addressBook: existingAddressBook.length
            ? existingAddressBook
            : [
                {
                  id: 1,
                  name: matched.account,
                  phone: matched.phone,
                  address: '请在个人中心补充收货地址',
                  isDefault: true,
                },
              ],
        })
      }

      if (remember) this.persistSession()
      else localStorage.removeItem(storageKey)

      return matched.role
    },
    register({ account, phone, password, nickname }) {
      const normalized = account.trim()
      if (!normalized) throw new Error('账号不能为空')

      if (this.accounts.some((item) => item.account === normalized)) {
        throw new Error('该账号已存在')
      }

      const platformStore = usePlatformStore()
      const profile = platformStore.registerUserAccount({
        account: normalized,
        phone,
        nickname: nickname || normalized,
      })

      const newAccount = {
        id: `user-${Date.now()}`,
        account: normalized,
        password: password || '123456',
        role: 'user',
        phone,
        nickname: nickname || normalized,
        platformProfileId: profile.id,
      }

      this.accounts.unshift(newAccount)
      this.persistAccounts()
      this.session = {
        account: newAccount.account,
        role: newAccount.role,
        remember: true,
      }
      this.persistSession()
      return newAccount
    },
    logout() {
      this.session = null
      localStorage.removeItem(storageKey)
    },
  },
})
