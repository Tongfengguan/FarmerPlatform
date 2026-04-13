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
    link: '/mall'
  },
  {
    title: '三农资讯，实时掌握',
    desc: '权威政策解读，行业动态追踪，助您科学决策。',
    image: 'https://images.unsplash.com/photo-1464226184884-fa280b87c399?auto=format&fit=crop&w=1600&q=80',
    link: '/articles'
  }
]
</script>

<template>
  <div class="home-container">
    <!-- 轮播图 Banner -->
    <el-carousel height="460px" class="hero-carousel" motion-blur>
      <el-carousel-item v-for="(item, index) in banners" :key="index">
        <div class="carousel-content" :style="{ backgroundImage: `linear-gradient(rgba(0,0,0,0.3), rgba(0,0,0,0.6)), url(${item.image})` }">
          <div class="hero-text">
            <div class="hero-eyebrow">乡村振兴 · 数字服务</div>
            <h1>{{ item.title }}</h1>
            <p>{{ item.desc }}</p>
            <div class="hero-actions">
              <el-button type="primary" size="large" @click="$router.push(item.link)">立即前往</el-button>
              <el-button size="large" plain @click="$router.push('/mall')">进入商城</el-button>
            </div>
          </div>
        </div>
      </el-carousel-item>
    </el-carousel>

    <!-- 快捷统计 -->
    <el-row :gutter="20" class="stat-banner">
      <el-col :span="8">
        <el-card shadow="hover">
          <el-statistic :value="47" title="本月政策发布">
            <template #prefix><el-icon><List /></el-icon></template>
          </el-statistic>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <el-statistic :value="18" title="今日上新商品">
            <template #prefix><el-icon><Goods /></el-icon></template>
          </el-statistic>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <el-statistic :value="23" title="待处理咨询">
            <template #prefix><el-icon><TrendCharts /></el-icon></template>
          </el-statistic>
        </el-card>
      </el-col>
    </el-row>

    <!-- 最新资讯 -->
    <section class="home-section">
      <div class="section-head">
        <div class="title-area">
          <el-icon class="section-icon"><Reading /></el-icon>
          <h2>最新三农资讯</h2>
        </div>
        <el-button link @click="$router.push('/articles')">查看更多 <el-icon><ArrowRight /></el-icon></el-button>
      </div>
      
      <el-row :gutter="20">
        <el-col v-for="article in latestArticles" :key="article.id" :span="8">
          <el-card :body-style="{ padding: '0px' }" shadow="hover" class="article-card" @click="$router.push(`/articles/${article.id}`)">
            <div class="card-image-wrap">
              <el-image :src="article.cover" fit="cover" class="card-image" />
              <el-tag class="card-tag" type="success" effect="dark">{{ article.category }}</el-tag>
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
          <h2>严选热销农资</h2>
        </div>
        <el-button link @click="$router.push('/mall')">进入商城 <el-icon><ArrowRight /></el-icon></el-button>
      </div>

      <el-row :gutter="20">
        <el-col v-for="product in hotProducts" :key="product.id" :span="6">
          <el-card :body-style="{ padding: '0px' }" shadow="hover" class="product-card" @click="$router.push(`/products/${product.id}`)">
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
                <el-tag size="small" :type="product.stock > 0 ? 'info' : 'danger'">
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
  max-width: 1200px;
  margin: 0 auto;
  padding-bottom: 60px;
}

.hero-carousel {
  border-radius: 24px;
  overflow: hidden;
  margin-bottom: 30px;
}

.carousel-content {
  height: 100%;
  background-size: cover;
  background-position: center;
  display: flex;
  align-items: center;
  padding: 0 60px;
}

.hero-text {
  max-width: 600px;
  color: white;
}

.hero-eyebrow {
  display: inline-block;
  padding: 6px 16px;
  background: var(--el-color-primary);
  border-radius: 99px;
  font-size: 14px;
  font-weight: 600;
  margin-bottom: 20px;
}

.hero-text h1 {
  font-size: 48px;
  margin: 0 0 20px;
  line-height: 1.2;
}

.hero-text p {
  font-size: 18px;
  opacity: 0.9;
  margin-bottom: 32px;
}

.hero-actions {
  display: flex;
  gap: 16px;
}

.stat-banner {
  margin-bottom: 40px;
}

.home-section {
  margin-bottom: 40px;
}

.section-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.title-area {
  display: flex;
  align-items: center;
  gap: 12px;
}

.section-icon {
  font-size: 24px;
  color: var(--el-color-primary);
}

.section-head h2 {
  margin: 0;
  font-size: 24px;
}

.card-image-wrap {
  position: relative;
  height: 200px;
  overflow: hidden;
}

.card-image {
  width: 100%;
  height: 100%;
  transition: transform 0.5s ease;
}

.article-card:hover .card-image,
.product-card:hover .card-image {
  transform: scale(1.05);
}

.card-tag {
  position: absolute;
  top: 12px;
  left: 12px;
}

.card-body {
  padding: 20px;
}

.card-title {
  margin: 0 0 12px;
  font-size: 18px;
  line-height: 1.4;
  height: 50px;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.card-desc {
  font-size: 14px;
  color: var(--el-text-color-secondary);
  margin-bottom: 16px;
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
  color: var(--el-text-color-placeholder);
}

.price-row {
  margin-bottom: 16px;
  display: flex;
  align-items: baseline;
  gap: 8px;
}

.price {
  font-size: 24px;
  font-weight: 700;
  color: var(--el-color-success);
}

.old-price {
  font-size: 14px;
  color: var(--el-text-color-placeholder);
  text-decoration: line-through;
}

.article-card, .product-card {
  cursor: pointer;
  border: none;
  background: var(--el-bg-color-overlay);
}
</style>
