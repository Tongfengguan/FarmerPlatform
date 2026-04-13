<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { usePlatformStore } from '../../stores/platform'
import { Back, Calendar, View, TopRight } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const store = usePlatformStore()

const article = computed(() =>
  store.publishedArticles.find((item) => item.id === Number(route.params.id)),
)
const related = computed(() =>
  store.publishedArticles
    .filter((item) => item.category === article.value?.category && item.id !== article.value?.id)
    .slice(0, 4),
)

if (article.value) {
  store.incrementArticleView(article.value.id)
}
</script>

<template>
  <div class="article-detail-container">
    <el-page-header @back="router.push('/articles')" title="返回资讯列表" class="detail-header">
      <template #content>
        <span class="header-title">资讯正文</span>
      </template>
    </el-page-header>

    <el-row :gutter="24" class="detail-layout" v-if="article">
      <el-col :span="16">
        <el-card shadow="never" class="main-card">
          <div class="cover-wrapper">
            <el-image :src="article.cover" fit="cover" class="cover-image" />
          </div>
          
          <div class="article-body">
            <h1 class="title">{{ article.title }}</h1>
            
            <div class="meta-row">
              <el-tag type="success" effect="plain">{{ article.category }}</el-tag>
              <div class="meta-group">
                <span class="meta-item"><el-icon><Calendar /></el-icon>{{ article.publishedAt }}</span>
                <span class="meta-item"><el-icon><View /></el-icon>{{ article.viewCount }} 次阅读</span>
                <span class="meta-item source">来源：{{ article.source }}</span>
              </div>
            </div>
            
            <el-divider border-style="dashed" />
            
            <div class="summary-box">
              <strong>【摘要】</strong> {{ article.summary }}
            </div>
            
            <div class="content-box">
              <!-- 在真实项目中这里通常使用 v-html="article.content" -->
              <p v-for="(paragraph, index) in article.content.split('\n')" :key="index">
                {{ paragraph }}
              </p>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="8">
        <el-card shadow="never" class="related-aside">
          <template #header>
            <div class="aside-header">
              <span>相关资讯</span>
              <el-icon><TopRight /></el-icon>
            </div>
          </template>
          <div class="related-list" v-if="related.length">
            <div 
              v-for="item in related" 
              :key="item.id" 
              class="related-item"
              @click="$router.push(`/articles/${item.id}`)"
            >
              <div class="related-title">{{ item.title }}</div>
              <div class="related-meta">
                <span class="date">{{ item.publishedAt }}</span>
              </div>
            </div>
          </div>
          <el-empty v-else description="暂无相关资讯" :image-size="100" />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<style scoped>
.article-detail-container {
  max-width: 1200px;
  margin: 0 auto;
  padding-bottom: 60px;
}

.detail-header {
  margin-bottom: 24px;
}

.header-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--el-text-color-primary);
}

.detail-layout {
  align-items: flex-start;
}

.main-card {
  border: none;
  background: var(--el-bg-color-overlay);
  padding: 0;
  overflow: hidden;
}

.cover-wrapper {
  height: 360px;
  width: 100%;
  background-color: var(--el-fill-color-darker);
}

.cover-image {
  width: 100%;
  height: 100%;
}

.article-body {
  padding: 32px 40px;
}

.title {
  margin: 0 0 20px;
  font-size: 32px;
  font-weight: 700;
  line-height: 1.4;
  color: var(--el-text-color-primary);
}

.meta-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.meta-group {
  display: flex;
  gap: 20px;
  color: var(--el-text-color-secondary);
  font-size: 14px;
}

.meta-item {
  display: inline-flex;
  align-items: center;
  gap: 6px;
}

.source {
  color: var(--el-color-primary);
}

.summary-box {
  background-color: var(--el-fill-color-lighter);
  padding: 20px;
  border-radius: 8px;
  border-left: 4px solid var(--el-color-primary);
  margin-bottom: 30px;
  font-size: 15px;
  line-height: 1.6;
  color: var(--el-text-color-regular);
}

.content-box {
  font-size: 16px;
  line-height: 1.8;
  color: var(--el-text-color-primary);
}

.content-box p {
  margin-bottom: 1.5em;
  text-indent: 2em;
}

.related-aside {
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

.related-list {
  display: flex;
  flex-direction: column;
}

.related-item {
  padding: 16px 0;
  border-bottom: 1px solid var(--el-border-color-lighter);
  cursor: pointer;
  transition: opacity 0.3s ease;
}

.related-item:last-child {
  border-bottom: none;
  padding-bottom: 0;
}

.related-item:first-child {
  padding-top: 0;
}

.related-item:hover {
  opacity: 0.8;
}

.related-title {
  font-size: 15px;
  font-weight: 500;
  line-height: 1.5;
  margin-bottom: 8px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.related-meta {
  font-size: 12px;
  color: var(--el-text-color-secondary);
}
</style>
