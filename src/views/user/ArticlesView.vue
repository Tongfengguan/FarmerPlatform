<script setup>
import { computed, ref, onMounted } from 'vue'
import { RouterLink } from 'vue-router'
import { articleCategories } from '../../utils/constants'
import { usePlatformStore } from '../../stores/platform'
import { Search, View, Calendar, TopRight } from '@element-plus/icons-vue'

const store = usePlatformStore()
const category = ref('全部')
const keyword = ref('')
const loading = ref(false)

const filteredArticles = computed(() =>
  store.publishedArticles.filter((article) => {
    const categoryMatch = category.value === '全部' || article.category === category.value
    const keywordMatch =
      !keyword.value ||
      article.title.includes(keyword.value) ||
      article.summary.includes(keyword.value)
    return categoryMatch && keywordMatch
  }),
)

const hotArticles = computed(() => 
  [...store.publishedArticles].sort((a, b) => b.viewCount - a.viewCount).slice(0, 4)
)

onMounted(async () => {
  loading.value = true
  await store.fetchArticles(0, 50)
  loading.value = false
})
</script>

<template>
  <div class="articles-container">
    <div class="page-header">
      <h1 class="page-title">三农资讯</h1>
      <p class="page-subtitle">最新农业政策、市场动态与技术指导。</p>
    </div>

    <el-card shadow="never" class="filter-card">
      <div class="filter-row">
        <div class="category-area">
          <el-segmented
            v-model="category"
            :options="articleCategories"
            size="large"
            class="custom-segmented"
          />
        </div>
        <div class="search-area">
          <el-input
            v-model="keyword"
            placeholder="搜索资讯标题或内容..."
            :prefix-icon="Search"
            clearable
            class="search-input"
          />
        </div>
      </div>
    </el-card>

    <el-row :gutter="24" class="content-layout">
      <!-- 资讯列表 -->
      <el-col :span="16">
        <div v-loading="loading" class="articles-list">
          <template v-if="filteredArticles.length">
            <el-card 
              v-for="article in filteredArticles" 
              :key="article.id" 
              shadow="hover" 
              class="article-item"
              :body-style="{ padding: '0' }"
              @click="$router.push(`/articles/${article.id}`)"
            >
              <div class="article-layout">
                <div class="article-image-wrap">
                  <el-image :src="article.cover" fit="cover" class="article-image" lazy />
                  <el-tag type="success" effect="dark" class="category-tag">{{ article.category }}</el-tag>
                </div>
                <div class="article-content">
                  <h3 class="article-title">{{ article.title }}</h3>
                  <p class="article-summary">{{ article.summary }}</p>
                  <div class="article-meta">
                    <span class="meta-item"><el-icon><Calendar /></el-icon>{{ article.publishedAt }}</span>
                    <span class="meta-item"><el-icon><View /></el-icon>{{ article.viewCount }}</span>
                    <span class="meta-item source">{{ article.source }}</span>
                  </div>
                </div>
              </div>
            </el-card>
          </template>
          <el-empty v-else description="暂无相关资讯，请尝试其他关键词" :image-size="200" />
        </div>
      </el-col>

      <!-- 侧边栏：热门资讯 -->
      <el-col :span="8">
        <el-card shadow="never" class="hot-aside">
          <template #header>
            <div class="aside-header">
              <span>热门资讯推荐</span>
              <el-icon><TopRight /></el-icon>
            </div>
          </template>
          <div class="hot-list">
            <div 
              v-for="article in hotArticles" 
              :key="article.id" 
              class="hot-item"
              @click="$router.push(`/articles/${article.id}`)"
            >
              <div class="hot-title">{{ article.title }}</div>
              <div class="hot-meta">
                <el-tag size="small" type="info">{{ article.category }}</el-tag>
                <span class="hot-views"><el-icon><View /></el-icon>{{ article.viewCount }}</span>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<style scoped>
.articles-container {
  max-width: 1200px;
  margin: 0 auto;
  padding-bottom: 60px;
}

.page-header {
  margin-bottom: 24px;
}

.page-title {
  font-size: 32px;
  font-weight: 700;
  margin: 0 0 8px;
}

.page-subtitle {
  color: var(--el-text-color-secondary);
  margin: 0;
}

.filter-card {
  margin-bottom: 24px;
  border: none;
  background: var(--el-bg-color-overlay);
}

.filter-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 16px;
}

.search-area {
  min-width: 300px;
}

.custom-segmented {
  --el-segmented-bg-color: var(--el-fill-color-darker);
  --el-segmented-item-selected-bg-color: var(--el-color-primary);
  --el-segmented-item-selected-color: white;
}

.content-layout {
  align-items: flex-start;
}

.articles-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.article-item {
  cursor: pointer;
  border: none;
  background: var(--el-bg-color-overlay);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.article-item:hover {
  transform: translateY(-2px);
}

.article-layout {
  display: flex;
  height: 180px;
}

.article-image-wrap {
  position: relative;
  width: 280px;
  flex-shrink: 0;
  overflow: hidden;
  background-color: var(--el-fill-color-darker);
}

.article-image {
  width: 100%;
  height: 100%;
  transition: transform 0.6s cubic-bezier(0.25, 1, 0.5, 1);
  backface-visibility: hidden;
  transform: translateZ(0);
}

.article-item:hover .article-image {
  transform: scale(1.05);
}

.category-tag {
  position: absolute;
  top: 12px;
  left: 12px;
}

.article-content {
  padding: 20px 24px;
  display: flex;
  flex-direction: column;
  flex: 1;
}

.article-title {
  margin: 0 0 12px;
  font-size: 20px;
  font-weight: 600;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.article-summary {
  margin: 0 0 16px;
  font-size: 14px;
  color: var(--el-text-color-secondary);
  line-height: 1.6;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  flex: 1;
}

.article-meta {
  display: flex;
  align-items: center;
  gap: 16px;
  font-size: 13px;
  color: var(--el-text-color-placeholder);
}

.meta-item {
  display: inline-flex;
  align-items: center;
  gap: 4px;
}

.meta-item.source {
  margin-left: auto;
  color: var(--el-text-color-regular);
}

.hot-aside {
  border: none;
  background: var(--el-bg-color-overlay);
  position: sticky;
  top: 24px;
}

.aside-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
  font-size: 16px;
}

.hot-list {
  display: flex;
  flex-direction: column;
}

.hot-item {
  padding: 16px 0;
  border-bottom: 1px solid var(--el-border-color-lighter);
  cursor: pointer;
  transition: opacity 0.3s ease;
}

.hot-item:last-child {
  border-bottom: none;
  padding-bottom: 0;
}

.hot-item:first-child {
  padding-top: 0;
}

.hot-item:hover {
  opacity: 0.8;
}

.hot-title {
  font-size: 15px;
  font-weight: 500;
  line-height: 1.5;
  margin-bottom: 10px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.hot-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.hot-views {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: var(--el-text-color-secondary);
}
</style>
