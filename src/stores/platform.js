import { defineStore } from 'pinia'
import {
  currentUser,
  dashboardStats,
  initialArticles,
  initialOrders,
  initialProducts,
  initialUsers,
} from '../data/mockData.js'

const nextId = (items, fallback = 1) => {
  if (!items.length) return fallback
  return Math.max(...items.map((item) => Number(item.id))) + 1
}

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
    latestArticleTip:
      initialArticles.find((article) => article.isPush && article.status === '已发布') ?? null,
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
    setCurrentUser(profile) {
      this.currentUser = JSON.parse(JSON.stringify(profile))
    },
    registerUserAccount({ account, phone, nickname }) {
      const userId = nextId(this.users, 1001)
      const profile = {
        id: userId,
        name: account,
        phone,
        nickname: nickname || account,
        avatar: account.slice(0, 1),
        addressBook: [
          {
            id: 1,
            name: account,
            phone,
            address: '请在个人中心补充收货地址',
            isDefault: true,
          },
        ],
      }

      this.users.unshift({
        id: userId,
        name: account,
        phone,
        status: '正常',
        createdAt: new Date().toISOString().slice(0, 10),
        orders: 0,
        spend: 0,
        lastActive: new Date().toISOString().slice(0, 10),
      })

      this.setCurrentUser(profile)
      return profile
    },
    dismissLatestTip() {
      this.latestArticleTip = null
    },
    incrementArticleView(id) {
      const article = this.articles.find((item) => item.id === Number(id))
      if (article) article.viewCount += 1
    },
    addArticle(payload) {
      this.articles.unshift({
        id: nextId(this.articles),
        viewCount: 0,
        publishedAt: new Date().toISOString().slice(0, 10),
        ...payload,
      })
    },
    updateArticle(payload) {
      const index = this.articles.findIndex((item) => item.id === payload.id)
      if (index !== -1) this.articles[index] = { ...this.articles[index], ...payload }
    },
    removeArticle(id) {
      this.articles = this.articles.filter((item) => item.id !== id)
    },
    toggleArticleStatus(id) {
      const article = this.articles.find((item) => item.id === id)
      if (!article) return
      article.status = article.status === '已发布' ? '已下架' : '已发布'
    },
    addProduct(payload) {
      this.products.unshift({
        id: nextId(this.products, 100),
        salesCount: 0,
        ...payload,
      })
    },
    updateProduct(payload) {
      const index = this.products.findIndex((item) => item.id === payload.id)
      if (index !== -1) this.products[index] = { ...this.products[index], ...payload }
    },
    toggleProductStatus(id) {
      const product = this.products.find((item) => item.id === id)
      if (!product) return
      product.status = product.status === '销售中' ? '已下架' : '销售中'
    },
    addToCart(product, skuName, quantity) {
      const existing = this.cart.find(
        (item) => item.productId === product.id && item.sku === skuName,
      )
      if (existing) {
        existing.quantity += quantity
      } else {
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
      const item = this.cart.find(
        (entry) => entry.productId === productId && entry.sku === sku,
      )
      if (item) item.quantity = Math.max(1, quantity)
    },
    toggleCartSelection(productId, sku) {
      const item = this.cart.find(
        (entry) => entry.productId === productId && entry.sku === sku,
      )
      if (item) item.selected = !item.selected
    },
    removeCartItem(productId, sku) {
      this.cart = this.cart.filter(
        (item) => !(item.productId === productId && item.sku === sku),
      )
    },
    checkoutSelected() {
      const selectedItems = this.cart.filter((item) => item.selected)
      if (!selectedItems.length) return false
      const total = selectedItems.reduce((sum, item) => sum + item.price * item.quantity, 0)
      this.orders.unshift({
        id: orderId(),
        userId: this.currentUser.id,
        buyer: this.currentUser.name,
        phone: this.currentUser.phone,
        items: selectedItems.map((item) => ({
          productId: item.productId,
          name: item.name,
          sku: item.sku,
          quantity: item.quantity,
          price: item.price,
        })),
        payAmount: total,
        freightAmount: total >= 99 ? 0 : 12,
        status: '待付款',
        createdAt: new Date().toLocaleString('zh-CN', { hour12: false }),
        shipCompany: '',
        shipNo: '',
        receiver: this.currentUser.addressBook.find((address) => address.isDefault),
      })
      this.cart = this.cart.filter((item) => !item.selected)
      return true
    },
    buyNow(product, skuName, quantity) {
      const price = product.skus.find((sku) => sku.name === skuName)?.price ?? product.price
      this.orders.unshift({
        id: orderId(),
        userId: this.currentUser.id,
        buyer: this.currentUser.name,
        phone: this.currentUser.phone,
        items: [{ productId: product.id, name: product.name, sku: skuName, quantity, price }],
        payAmount: price * quantity,
        freightAmount: price * quantity >= 99 ? 0 : 12,
        status: '待付款',
        createdAt: new Date().toLocaleString('zh-CN', { hour12: false }),
        shipCompany: '',
        shipNo: '',
        receiver: this.currentUser.addressBook.find((address) => address.isDefault),
      })
    },
    payOrder(id) {
      const order = this.orders.find((item) => item.id === id)
      if (order && order.status === '待付款') order.status = '待发货'
    },
    cancelOrder(id) {
      const order = this.orders.find((item) => item.id === id)
      if (order && order.status === '待付款') order.status = '已取消'
    },
    confirmOrder(id) {
      const order = this.orders.find((item) => item.id === id)
      if (order && order.status === '待收货') order.status = '已完成'
    },
    shipOrder(id, shipCompany, shipNo) {
      const order = this.orders.find((item) => item.id === id)
      if (!order) return
      order.shipCompany = shipCompany
      order.shipNo = shipNo
      order.status = '待收货'
    },
    refundOrder(id) {
      const order = this.orders.find((item) => item.id === id)
      if (order) order.status = '已取消'
    },
    toggleUserStatus(id) {
      const user = this.users.find((item) => item.id === id)
      if (!user) return
      user.status = user.status === '正常' ? '禁用' : '正常'
    },
  },
})
