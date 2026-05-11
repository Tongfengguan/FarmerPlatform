<script setup>
import { computed } from 'vue'
import { RouterLink } from 'vue-router'
import { usePlatformStore } from '../../stores/platform'
import { 
  ArrowRight, 
  TrendCharts, 
  Goods, 
  List,
  Reading
} from '@element-plus/icons-vue'

const store = usePlatformStore()

const latestArticles = computed(() => store.publishedArticles.slice(0, 3))
const hotProducts = computed(() =>
  [...store.activeProducts].sort((a, b) => b.salesCount - a.salesCount).slice(0, 4),
)

const banners = [
  {
    title: '智慧农业，助力乡村振兴',
    desc: '集成政策资讯、农资商城与专家指导的一站式服务平台。',
    image: 'https://images.unsplash.com/photo-1500382017468-9049fed747ef?auto=format&fit=crop&w=1600&q=80',
    link: '/mall',
    btnText: '立即选购'
  },
  {
    title: '三农资讯，实时掌握',
    desc: '权威政策解读，行业动态追踪，助您科学决策。',
    image: 'https://images.unsplash.com/photo-1464226184884-fa280b87c399?auto=format&fit=crop&w=1600&q=80',
    link: '/articles',
    btnText: '阅读资讯'
  }
]
</script>

<template>
  <div class="home-container">
    <!-- 轮播图 Banner -->
    <el-carousel height="520px" class="hero-carousel" indicator-position="outside">
      <el-carousel-item v-for="(item, index) in banners" :key="index">
        <div class="carousel-content" :style="{ backgroundImage: `linear-gradient(rgba(76, 29, 149, 0.4), rgba(15, 22, 21, 0.8)), url(${item.image})` }">
          <div class="hero-text">
            <div class="hero-eyebrow">乡村振兴 · 数字服务</div>
            <h1 class="fira-code">{{ item.title }}</h1>
            <p>{{ item.desc }}</p>
            <div class="hero-actions">
              <el-button class="cta-button" type="primary" size="large" @click="$router.push(item.link)">
                {{ item.btnText }}
              </el-button>
              <el-button class="secondary-button" size="large" @click="$router.push('/articles')">
                了解更多
              </el-button>
            </div>
          </div>
        </div>
      </el-carousel-item>
    </el-carousel>

    <!-- 快捷统计 -->
    <el-row :gutter="24" class="stat-banner">
      <el-col :span="8">
        <el-card shadow="hover" class="stat-card">
          <el-statistic :value="47" title="本月政策发布">
            <template #prefix><el-icon class="stat-icon"><List /></el-icon></template>
          </el-statistic>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover" class="stat-card">
          <el-statistic :value="18" title="今日上新商品">
            <template #prefix><el-icon class="stat-icon"><Goods /></el-icon></template>
          </el-statistic>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover" class="stat-card">
          <el-statistic :value="23" title="待处理咨询">
            <template #prefix><el-icon class="stat-icon"><TrendCharts /></el-icon></template>
          </el-statistic>
        </el-card>
      </el-col>
    </el-row>

    <!-- 最新资讯 -->
    <section class="home-section">
      <div class="section-head">
        <div class="title-area">
          <el-icon class="section-icon"><Reading /></el-icon>
          <h2 class="fira-code">最新三农资讯</h2>
        </div>
        <el-button link class="view-more" @click="$router.push('/articles')">
          查看更多 <el-icon><ArrowRight /></el-icon>
        </el-button>
      </div>
      
      <el-row :gutter="24">
        <el-col v-for="article in latestArticles" :key="article.id" :span="8">
          <el-card :body-style="{ padding: '0px' }" shadow="hover" class="article-card clickable" @click="$router.push(`/articles/${article.id}`)">
            <div class="card-image-wrap">
              <el-image :src="article.cover" fit="cover" class="card-image" />
              <el-tag class="card-tag" effect="dark">{{ article.category }}</el-tag>
            </div>
            <div class="card-body">
              <h3 class="card-title">{{ article.title }}</h3>
              <p class="card-desc">{{ article.summary }}</p>
              <div class="card-footer">
                <span class="date">{{ article.publishedAt }}</span>
                <span class="views">阅读 {{ article.viewCount }}</span>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </section>

    <!-- 热销商品 -->
    <section class="home-section">
      <div class="section-head">
        <div class="title-area">
          <el-icon class="section-icon"><Goods /></el-icon>
          <h2 class="fira-code">严选热销农资</h2>
        </div>
        <el-button link class="view-more" @click="$router.push('/mall')">
          进入商城 <el-icon><ArrowRight /></el-icon>
        </el-button>
      </div>

      <el-row :gutter="24">
        <el-col v-for="product in hotProducts" :key="product.id" :span="6">
          <el-card :body-style="{ padding: '0px' }" shadow="hover" class="product-card clickable" @click="$router.push(`/products/${product.id}`)">
            <div class="card-image-wrap">
              <el-image :src="product.image" fit="cover" class="card-image" />
            </div>
            <div class="card-body">
              <h3 class="card-title">{{ product.name }}</h3>
              <div class="price-row">
                <span class="price">¥{{ product.price }}</span>
                <span v-if="product.oldPrice" class="old-price">¥{{ product.oldPrice }}</span>
              </div>
              <div class="card-footer">
                <span>销量 {{ product.salesCount }}</span>
                <el-tag size="small" :type="product.stock > 0 ? 'success' : 'danger'" effect="plain">
                  {{ product.stock > 0 ? '现货' : '补货中' }}
                </el-tag>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </section>
  </div>
</template>

<style scoped>
.home-container {
  max-width: 1280px;
  margin: 0 auto;
  padding: 0 var(--space-md) var(--space-3xl);
}

.hero-carousel {
  border-radius: var(--radius);
  overflow: hidden;
  margin-bottom: var(--space-2xl);
  box-shadow: var(--shadow-xl);
}

.carousel-content {
  height: 100%;
  background-size: cover;
  background-position: center;
  display: flex;
  align-items: center;
  padding: 0 var(--space-3xl);
  backface-visibility: hidden;
  perspective: 1000px;
  transform: translate3d(0, 0, 0);
  will-change: transform;
}

:deep(.el-carousel__item) {
  overflow: hidden;
  backface-visibility: hidden;
  transform: translateZ(0);
}

.hero-text {
  max-width: 650px;
  color: white;
  text-shadow: 0 4px 12px rgba(0,0,0,0.4);
}

.hero-eyebrow {
  display: inline-block;
  padding: 6px 16px;
  background: var(--color-primary);
  border-radius: 99px;
  font-size: 14px;
  font-weight: 600;
  margin-bottom: var(--space-lg);
  color: white;
}

.hero-text h1 {
  font-size: 56px;
  margin: 0 0 var(--space-md);
  line-height: 1.1;
  font-weight: 700;
}

.hero-text p {
  font-size: 20px;
  opacity: 0.95;
  margin-bottom: var(--space-xl);
  line-height: 1.6;
}

.hero-actions {
  display: flex;
  gap: var(--space-md);
}

.cta-button {
  background-color: var(--color-cta) !important;
  border-color: var(--color-cta) !important;
  font-weight: 600;
  padding: 12px 32px;
  height: auto;
  transition: all 0.3s ease;
}

.cta-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(34, 197, 94, 0.4);
}

.secondary-button {
  background: rgba(255,255,255,0.1) !important;
  border: 1px solid rgba(255,255,255,0.3) !important;
  color: white !important;
  backdrop-filter: blur(8px);
  padding: 12px 32px;
  height: auto;
}

.secondary-button:hover {
  background: rgba(255,255,255,0.2) !important;
}

.stat-banner {
  margin-bottom: var(--space-3xl);
}

.stat-card {
  border-radius: var(--radius);
  border: 1px solid var(--line);
  background: var(--bg-soft);
}

.stat-icon {
  font-size: 28px;
  color: var(--color-secondary);
  margin-right: var(--space-sm);
}

.home-section {
  margin-bottom: var(--space-3xl);
}

.section-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--space-lg);
}

.title-area {
  display: flex;
  align-items: center;
  gap: var(--space-md);
}

.section-icon {
  font-size: 28px;
  color: var(--color-cta);
}

.section-head h2 {
  margin: 0;
  font-size: 28px;
  font-weight: 600;
  color: var(--text);
}

.view-more {
  color: var(--color-secondary) !important;
  font-weight: 500;
}

.clickable {
  cursor: pointer;
}

.card-image-wrap {
  position: relative;
  height: 220px;
  overflow: hidden;
}

.card-image {
  width: 100%;
  height: 100%;
  transition: transform 0.6s cubic-bezier(0.25, 1, 0.5, 1);
}

.clickable:hover .card-image {
  transform: scale(1.08);
}

.card-tag {
  position: absolute;
  top: 16px;
  left: 16px;
  background-color: var(--color-primary) !important;
  border: none;
  border-radius: 6px;
}

.card-body {
  padding: var(--space-lg);
}

.card-title {
  margin: 0 0 var(--space-sm);
  font-size: 20px;
  line-height: 1.4;
  height: 56px;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  color: var(--text);
}

.card-desc {
  font-size: 14px;
  color: var(--text-soft);
  margin-bottom: var(--space-md);
  height: 40px;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 13px;
  color: var(--text-mute);
}

.price-row {
  margin-bottom: var(--space-md);
  display: flex;
  align-items: baseline;
  gap: var(--space-sm);
}

.price {
  font-size: 26px;
  font-weight: 700;
  color: var(--color-cta);
}

.old-price {
  font-size: 14px;
  color: var(--text-mute);
  text-decoration: line-through;
}

.article-card, .product-card {
  border-radius: var(--radius);
  overflow: hidden;
  transition: all 0.3s ease;
  border: 1px solid var(--line);
}

.article-card:hover, .product-card:hover {
  transform: translateY(-8px);
  box-shadow: var(--shadow-xl);
  border-color: var(--color-secondary);
}
</style>
