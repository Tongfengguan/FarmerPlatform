<script setup>
import { computed } from 'vue'
import { RouterLink } from 'vue-router'
import { usePlatformStore } from '../../stores/platform'

const store = usePlatformStore()

const latestArticles = computed(() => store.publishedArticles.slice(0, 3))
const hotProducts = computed(() =>
  [...store.activeProducts].sort((a, b) => b.salesCount - a.salesCount).slice(0, 3),
)
</script>

<template>
  <section class="page-shell">
    <div class="hero card">
      <div class="hero__content">
        <div class="hero__eyebrow">乡村振兴数字服务平台</div>
        <h1>把政策资讯、农资商城和订单服务放进一个入口。</h1>
        <p class="muted">
          这个基础版本优先覆盖 PRD 中最核心的三条主线：看资讯、买商品、查订单，并保留管理端运营入口。
        </p>
        <div class="hero__actions">
          <RouterLink class="btn btn-primary" to="/articles">查看三农资讯</RouterLink>
          <RouterLink class="btn btn-ghost" to="/mall">进入三农商城</RouterLink>
        </div>
      </div>
      <div class="hero__stats">
        <div class="hero-stat card">
          <span class="muted">本月政策发布</span>
          <strong>47</strong>
        </div>
        <div class="hero-stat card">
          <span class="muted">热销商品</span>
          <strong>18</strong>
        </div>
        <div class="hero-stat card">
          <span class="muted">在线订单</span>
          <strong>23</strong>
        </div>
      </div>
    </div>

    <section class="section">
      <div class="section__head">
        <h2>最新资讯</h2>
        <RouterLink class="muted" to="/articles">查看更多</RouterLink>
      </div>
      <div class="article-grid">
        <RouterLink
          v-for="article in latestArticles"
          :key="article.id"
          :to="`/articles/${article.id}`"
          class="article-card card"
        >
          <img :src="article.cover" :alt="article.title" />
          <div class="article-card__body">
            <span class="badge badge-success">{{ article.category }}</span>
            <h3>{{ article.title }}</h3>
            <p class="muted">{{ article.summary }}</p>
            <div class="article-card__meta">
              <span>{{ article.publishedAt }}</span>
              <span>阅读 {{ article.viewCount }}</span>
            </div>
          </div>
        </RouterLink>
      </div>
    </section>

    <section class="section">
      <div class="section__head">
        <h2>热销商品</h2>
        <RouterLink class="muted" to="/mall">进入商城</RouterLink>
      </div>
      <div class="product-grid">
        <RouterLink
          v-for="product in hotProducts"
          :key="product.id"
          :to="`/products/${product.id}`"
          class="product-card card"
        >
          <img :src="product.image" :alt="product.name" />
          <div class="product-card__body">
            <h3>{{ product.name }}</h3>
            <p class="muted">{{ product.detail }}</p>
            <div class="product-card__price">
              <strong>¥{{ product.price }}</strong>
              <span v-if="product.oldPrice">¥{{ product.oldPrice }}</span>
            </div>
            <div class="product-card__meta">
              <span>销量 {{ product.salesCount }}</span>
              <span>库存 {{ product.stock }}</span>
            </div>
          </div>
        </RouterLink>
      </div>
    </section>
  </section>
</template>

<style scoped>
.hero {
  display: grid;
  grid-template-columns: 1.4fr 0.8fr;
  gap: 24px;
  padding: 28px;
}

.hero__eyebrow {
  display: inline-flex;
  margin-bottom: 16px;
  padding: 8px 12px;
  border-radius: 999px;
  background: rgba(35, 176, 125, 0.14);
  color: #9cf4cf;
  font-size: 13px;
}

.hero h1 {
  font-size: 44px;
  line-height: 1.15;
  margin: 0 0 14px;
}

.hero__actions {
  display: flex;
  gap: 12px;
  margin-top: 24px;
}

.hero__stats {
  display: grid;
  gap: 16px;
}

.hero-stat {
  padding: 20px;
}

.hero-stat strong {
  display: block;
  margin-top: 10px;
  font-size: 34px;
}

.section {
  margin-top: 28px;
}

.section__head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.article-grid,
.product-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 18px;
}

.article-card,
.product-card {
  overflow: hidden;
}

.article-card img,
.product-card img {
  width: 100%;
  height: 220px;
  object-fit: cover;
}

.article-card__body,
.product-card__body {
  padding: 18px;
}

.article-card h3,
.product-card h3 {
  margin: 12px 0 10px;
}

.article-card__meta,
.product-card__meta,
.product-card__price {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  margin-top: 14px;
  color: var(--text-soft);
}

.product-card__price strong {
  color: #90f0c6;
  font-size: 26px;
}

.product-card__price span {
  text-decoration: line-through;
  color: var(--text-mute);
}
</style>
