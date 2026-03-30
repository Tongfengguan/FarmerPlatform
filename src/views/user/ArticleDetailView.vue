<script setup>
import { computed } from 'vue'
import { RouterLink, useRoute } from 'vue-router'
import { usePlatformStore } from '../../stores/platform'

const route = useRoute()
const store = usePlatformStore()

const article = computed(() =>
  store.publishedArticles.find((item) => item.id === Number(route.params.id)),
)
const related = computed(() =>
  store.publishedArticles
    .filter((item) => item.category === article.value?.category && item.id !== article.value?.id)
    .slice(0, 3),
)

if (article.value) {
  store.incrementArticleView(article.value.id)
}
</script>

<template>
  <section v-if="article" class="page-shell detail-layout">
    <article class="detail card">
      <img class="detail__cover" :src="article.cover" :alt="article.title" />
      <div class="detail__body">
        <RouterLink class="muted" to="/articles">返回资讯列表</RouterLink>
        <h1>{{ article.title }}</h1>
        <div class="detail__meta">
          <span>{{ article.source }}</span>
          <span>{{ article.publishedAt }}</span>
          <span>阅读 {{ article.viewCount }}</span>
        </div>
        <div class="detail__content">
          <p>{{ article.summary }}</p>
          <p>{{ article.content }}</p>
        </div>
      </div>
    </article>

    <aside class="detail-side card">
      <h3>相关资讯</h3>
      <RouterLink
        v-for="item in related"
        :key="item.id"
        :to="`/articles/${item.id}`"
        class="aside-link"
      >
        <strong>{{ item.title }}</strong>
        <span class="muted">{{ item.publishedAt }}</span>
      </RouterLink>
    </aside>
  </section>
</template>

<style scoped>
.detail-layout {
  display: grid;
  grid-template-columns: 1.5fr 0.7fr;
  gap: 20px;
}

.detail {
  overflow: hidden;
}

.detail__cover {
  width: 100%;
  height: 320px;
  object-fit: cover;
}

.detail__body {
  padding: 24px;
}

.detail h1 {
  margin: 16px 0;
  font-size: 36px;
}

.detail__meta {
  display: flex;
  flex-wrap: wrap;
  gap: 18px;
  color: var(--text-soft);
}

.detail__content {
  margin-top: 18px;
  line-height: 1.9;
  color: #d7dfdc;
}

.detail-side {
  height: fit-content;
  padding: 18px;
}
</style>
