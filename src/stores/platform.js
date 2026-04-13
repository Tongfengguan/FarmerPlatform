import { defineStore } from 'pinia'
import { currentUser, dashboardStats, initialArticles, initialOrders, initialProducts, initialUsers } from '../data/mockData.js'
import { deleteJson, getJson, patchJson, postJson, putJson } from '../utils/http.js'

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
      const updated = await patchJson(`/api/platform/articles/${id}/view`)
      const index = this.articles.findIndex((a) => a.id === id)
      if (index !== -1) this.articles[index] = updated
    },
    async addArticle(payload) {
      const added = await postJson('/api/admin/articles', payload)
      this.articles.unshift(added)
      this.latestArticleTip =
        this.articles.find((article) => article.isPush && article.status === '已发布') ?? null
    },
    async updateArticle(payload) {
      const updated = await putJson(`/api/admin/articles/${payload.id}`, payload)
      const index = this.articles.findIndex((a) => a.id === updated.id)
      if (index !== -1) this.articles[index] = updated
    },
    async removeArticle(id) {
      await deleteJson(`/api/admin/articles/${id}`)
      this.articles = this.articles.filter((a) => a.id !== id)
    },
    async toggleArticleStatus(id) {
      const updated = await patchJson(`/api/admin/articles/${id}/toggle-status`)
      const index = this.articles.findIndex((a) => a.id === id)
      if (index !== -1) this.articles[index] = updated
    },
    async addProduct(payload) {
      const added = await postJson('/api/admin/products', payload)
      this.products.unshift(added)
    },
    async updateProduct(payload) {
      const updated = await putJson(`/api/admin/products/${payload.id}`, payload)
      const index = this.products.findIndex((p) => p.id === updated.id)
      if (index !== -1) this.products[index] = updated
    },
    async toggleProductStatus(id) {
      const updated = await patchJson(`/api/admin/products/${id}/toggle-status`)
      const index = this.products.findIndex((p) => p.id === id)
      if (index !== -1) this.products[index] = updated
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
      const newOrder = await postJson('/api/platform/orders', {
        items: selectedItems.map((item) => ({
          productId: item.productId,
          name: item.name,
          sku: item.sku,
          quantity: item.quantity,
          price: item.price,
        })),
      })
      this.orders.unshift(newOrder)
      this.cart = this.cart.filter((item) => !item.selected)
      return true
    },
    async buyNow(product, skuName, quantity) {
      const price = product.skus.find((sku) => sku.name === skuName)?.price ?? product.price
      const newOrder = await postJson('/api/platform/orders', {
        items: [{ productId: product.id, name: product.name, sku: skuName, quantity, price }],
      })
      this.orders.unshift(newOrder)
    },
    async payOrder(id) {
      const updated = await patchJson(`/api/platform/orders/${id}/pay`)
      const index = this.orders.findIndex((o) => o.id === id)
      if (index !== -1) this.orders[index] = updated
    },
    async cancelOrder(id) {
      const updated = await patchJson(`/api/platform/orders/${id}/cancel`)
      const index = this.orders.findIndex((o) => o.id === id)
      if (index !== -1) this.orders[index] = updated
    },
    async confirmOrder(id) {
      const updated = await patchJson(`/api/platform/orders/${id}/confirm`)
      const index = this.orders.findIndex((o) => o.id === id)
      if (index !== -1) this.orders[index] = updated
    },
    async shipOrder(id, shipCompany, shipNo) {
      const updated = await patchJson(`/api/admin/orders/${id}/ship`, { shipCompany, shipNo })
      const index = this.orders.findIndex((o) => o.id === id)
      if (index !== -1) this.orders[index] = updated
    },
    async refundOrder(id) {
      const updated = await patchJson(`/api/admin/orders/${id}/refund`)
      const index = this.orders.findIndex((o) => o.id === id)
      if (index !== -1) this.orders[index] = updated
    },
    async toggleUserStatus(id) {
      const updated = await patchJson(`/api/admin/users/${id}/toggle-status`)
      const index = this.users.findIndex((u) => u.id === id)
      if (index !== -1) this.users[index] = updated
    },
    async addAddress(address) {
      const added = await postJson('/api/platform/addresses', address)
      const addresses = await getJson('/api/platform/addresses') // Refresh addresses for simplicity and default status handling
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
