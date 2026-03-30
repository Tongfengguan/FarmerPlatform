<script setup>
import { computed, ref } from 'vue'
import { RouterLink } from 'vue-router'
import { articleCategories } from '../../data/mockData'
import { usePlatformStore } from '../../stores/platform'

const store = usePlatformStore()
const category = ref('全部')
const keyword = ref('')

const filteredArticles = computed(() =>
  store.publishedArticles.filter((article) => {
    const categoryMatch = category.value === '全部' || article.category === category.value
    const keywordMatch =
      !keyword.value ||
      article.title.includes(keyword.value) ||
      article.content.includes(keyword.value)
    return categoryMatch && keywordMatch
  }),
)
</script>

<template>
  <section class="page-shell">
    <div class="page-head">
      <div>
        <h1>三农资讯</h1>
        <p class="muted">支持分类筛选、关键词搜索和详情阅读，是用户端最重要的信息入口。</p>
      </div>
    </div>

    <div class="filters card">
      <div class="category-row">
        <button
          v-for="item in articleCategories"
          :key="item"
          class="chip"
          :class="{ active: item === category }"
          @click="category = item"
        >
          {{ item }}
        </button>
      </div>
      <input v-model="keyword" class="field" placeholder="搜索资讯标题或正文关键词" />
    </div>

    <div class="articles-layout">
      <div class="articles-list">
        <RouterLink
          v-for="article in filteredArticles"
          :key="article.id"
          :to="`/articles/${article.id}`"
          class="article-item card"
        >
          <img :src="article.cover" :alt="article.title" />
          <div class="article-item__content">
            <div class="article-item__top">
              <span class="badge badge-success">{{ article.category }}</span>
              <span class="muted">{{ article.publishedAt }}</span>
            </div>
            <h3>{{ article.title }}</h3>
            <p class="muted">{{ article.summary }}</p>
            <div class="article-item__bottom">
              <span>{{ article.source }}</span>
              <span>阅读量 {{ article.viewCount }}</span>
            </div>
          </div>
        </RouterLink>
        <div v-if="!filteredArticles.length" class="card empty">暂无相关资讯</div>
      </div>

      <aside class="article-aside card">
        <h3>热门资讯推荐</h3>
        <RouterLink
          v-for="article in [...store.publishedArticles].sort((a, b) => b.viewCount - a.viewCount).slice(0, 4)"
          :key="article.id"
          :to="`/articles/${article.id}`"
          class="aside-link"
        >
          <strong>{{ article.title }}</strong>
          <span class="muted">{{ article.category }} · 阅读 {{ article.viewCount }}</span>
        </RouterLink>
      </aside>
    </div>
  </section>
</template>

<style scoped>
.page-head h1 {
  margin: 0;
  font-size: 36px;
}

.filters {
  margin: 20px 0 24px;
  padding: 18px;
}

.category-row {
  display: flex;
  gap: 10px;
  margin-bottom: 16px;
  flex-wrap: wrap;
}

.chip {
  padding: 10px 14px;
  border-radius: 999px;
  border: 1px solid var(--line);
  background: transparent;
  color: var(--text-soft);
  cursor: pointer;
}

.chip.active {
  background: rgba(35, 176, 125, 0.18);
  color: #9cf4cf;
}

.articles-layout {
  display: grid;
  grid-template-columns: 1.6fr 0.8fr;
  gap: 20px;
}

.articles-list {
  display: grid;
  gap: 16px;
}

.article-item {
  display: grid;
  grid-template-columns: 280px 1fr;
  gap: 18px;
  overflow: hidden;
}

.article-item img {
  width: 100%;
  height: 100%;
  min-height: 210px;
  object-fit: cover;
}

.article-item__content {
  padding: 18px 18px 18px 0;
}

.article-item__top,
.article-item__bottom {
  display: flex;
  justify-content: space-between;
  gap: 12px;
}

.article-item h3 {
  margin: 14px 0 12px;
}

.article-aside {
  height: fit-content;
  padding: 18px;
}

.aside-link {
  display: grid;
  gap: 6px;
  padding: 14px 0;
  border-bottom: 1px solid var(--line);
}

.aside-link:last-child {
  border-bottom: 0;
}
</style>
