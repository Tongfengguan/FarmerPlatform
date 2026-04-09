import { defineStore } from 'pinia'
import { currentUser, dashboardStats, initialArticles, initialOrders, initialProducts, initialUsers } from '../data/mockData.js'
import { deleteJson, getJson, patchJson, postJson, putJson } from '../utils/http.js'

const orderId = () => `${Date.now()}${Math.floor(Math.random() * 1000)}`

export const usePlatformStore = defineStore('platform', {
  state: () => ({
    dashboard: dashboardStats,
    currentUser: JSON.parse(JSON.stringify(currentUser)),
    articles: initialArticles,
    products: initialProducts,
    users: initialUsers,
    orders: initialOrders,
    cart: [],
    latestArticleTip: initialArticles.find((article) => article.isPush && article.status === '已发布') ?? null,
  }),
  getters: {
    publishedArticles(state) {
      return state.articles.filter((article) => article.status === '已发布')
    },
    activeProducts(state) {
      return state.products.filter((product) => product.status === '销售中')
    },
    currentUserOrders(state) {
      return state.orders.filter((order) => order.userId === state.currentUser.id)
    },
    cartTotal(state) {
      return state.cart.reduce((sum, item) => sum + item.price * item.quantity, 0)
    },
    cartCount(state) {
      return state.cart.reduce((sum, item) => sum + item.quantity, 0)
    },
  },
  actions: {
    syncCurrentUser(accountInfo, addressBook = null) {
      const nextAddressBook = addressBook ?? this.currentUser.addressBook ?? []
      this.currentUser = {
        id: accountInfo.id ?? this.currentUser.id,
        name: accountInfo.account ?? this.currentUser.name,
        phone: accountInfo.phone ?? this.currentUser.phone,
        nickname: accountInfo.nickname || accountInfo.account || this.currentUser.nickname,
        avatar: (accountInfo.account || this.currentUser.name || '农').slice(0, 1),
        addressBook: nextAddressBook,
      }
    },
    applyBootstrap(data) {
      this.dashboard = data.dashboard ?? this.dashboard
      this.articles = data.articles ?? this.articles
      this.products = data.products ?? this.products
      this.users = data.users ?? this.users
      this.orders = data.orders ?? this.orders
      this.latestArticleTip =
        this.articles.find((article) => article.isPush && article.status === '已发布') ?? null
    },
    async bootstrapPublic() {
      const data = await getJson('/api/platform/bootstrap')
      this.applyBootstrap(data)
    },
    async bootstrapPrivate(role = 'user', accountInfo = null) {
      const addresses = await getJson('/api/platform/addresses')
      if (accountInfo) {
        this.syncCurrentUser(accountInfo, addresses)
      } else {
        this.currentUser.addressBook = addresses
      }
      this.orders = await getJson('/api/platform/orders')

      if (role === 'admin') {
        const adminData = await getJson('/api/admin/bootstrap')
        this.applyBootstrap(adminData)
      }
    },
    setCurrentUser(profile) {
      this.currentUser = JSON.parse(JSON.stringify(profile))
    },
    dismissLatestTip() {
      this.latestArticleTip = null
    },
    async incrementArticleView(id) {
      this.articles = await patchJson(`/api/platform/articles/${id}/view`)
    },
    async addArticle(payload) {
      this.articles = await postJson('/api/admin/articles', payload)
      this.latestArticleTip =
        this.articles.find((article) => article.isPush && article.status === '已发布') ?? null
    },
    async updateArticle(payload) {
      this.articles = await putJson(`/api/admin/articles/${payload.id}`, payload)
    },
    async removeArticle(id) {
      this.articles = await deleteJson(`/api/admin/articles/${id}`)
    },
    async toggleArticleStatus(id) {
      this.articles = await patchJson(`/api/admin/articles/${id}/toggle-status`)
    },
    async addProduct(payload) {
      this.products = await postJson('/api/admin/products', payload)
    },
    async updateProduct(payload) {
      this.products = await putJson(`/api/admin/products/${payload.id}`, payload)
    },
    async toggleProductStatus(id) {
      this.products = await patchJson(`/api/admin/products/${id}/toggle-status`)
    },
    addToCart(product, skuName, quantity) {
      const existing = this.cart.find((item) => item.productId === product.id && item.sku === skuName)
      if (existing) existing.quantity += quantity
      else {
        this.cart.push({
          productId: product.id,
          name: product.name,
          image: product.image,
          sku: skuName,
          quantity,
          price: product.skus.find((sku) => sku.name === skuName)?.price ?? product.price,
          selected: true,
        })
      }
    },
    updateCartItem(productId, sku, quantity) {
      const item = this.cart.find((entry) => entry.productId === productId && entry.sku === sku)
      if (item) item.quantity = Math.max(1, quantity)
    },
    toggleCartSelection(productId, sku) {
      const item = this.cart.find((entry) => entry.productId === productId && entry.sku === sku)
      if (item) item.selected = !item.selected
    },
    removeCartItem(productId, sku) {
      this.cart = this.cart.filter((item) => !(item.productId === productId && item.sku === sku))
    },
    async checkoutSelected() {
      const selectedItems = this.cart.filter((item) => item.selected)
      if (!selectedItems.length) return false
      this.orders = await postJson('/api/platform/orders', {
        items: selectedItems.map((item) => ({
          productId: item.productId,
          name: item.name,
          sku: item.sku,
          quantity: item.quantity,
          price: item.price,
        })),
      })
      this.cart = this.cart.filter((item) => !item.selected)
      return true
    },
    async buyNow(product, skuName, quantity) {
      const price = product.skus.find((sku) => sku.name === skuName)?.price ?? product.price
      this.orders = await postJson('/api/platform/orders', {
        items: [{ productId: product.id, name: product.name, sku: skuName, quantity, price }],
      })
    },
    async payOrder(id) {
      this.orders = await patchJson(`/api/platform/orders/${id}/pay`)
    },
    async cancelOrder(id) {
      this.orders = await patchJson(`/api/platform/orders/${id}/cancel`)
    },
    async confirmOrder(id) {
      this.orders = await patchJson(`/api/platform/orders/${id}/confirm`)
    },
    async shipOrder(id, shipCompany, shipNo) {
      this.orders = await patchJson(`/api/admin/orders/${id}/ship`, { shipCompany, shipNo })
    },
    async refundOrder(id) {
      this.orders = await patchJson(`/api/admin/orders/${id}/refund`)
    },
    async toggleUserStatus(id) {
      this.users = await patchJson(`/api/admin/users/${id}/toggle-status`)
    },
    async addAddress(address) {
      const addresses = await postJson('/api/platform/addresses', address)
      this.syncCurrentUser(this.currentUser, addresses)
    },
    resetClientState() {
      this.cart = []
      this.currentUser = JSON.parse(JSON.stringify(currentUser))
      this.orders = []
      this.users = []
    },
  },
})
