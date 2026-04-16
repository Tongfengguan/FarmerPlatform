package com.tfgkk.farmer_platform.service;

import com.tfgkk.farmer_platform.common.BusinessException;
import com.tfgkk.farmer_platform.dto.platform.ArticleDto;
import com.tfgkk.farmer_platform.entity.ArticleEntity;
import com.tfgkk.farmer_platform.repository.ArticleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class ArticleService {

    private final ArticleRepository articleRepository;

    public ArticleService(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    @Transactional(readOnly = true)
    public Page<ArticleDto> listArticles(Pageable pageable) {
        return articleRepository.findAll(pageable).map(this::toArticleDto);
    }

    @Transactional
    public ArticleDto saveArticle(ArticleDto articleDto) {
        ArticleEntity entity = articleDto.id() == null ? new ArticleEntity() : requireArticle(articleDto.id());
        entity.setTitle(articleDto.title());
        entity.setCategory(articleDto.category());
        entity.setCover(articleDto.cover());
        entity.setSummary(articleDto.summary());
        entity.setContent(articleDto.content());
        entity.setSource(articleDto.source());
        entity.setIsPush(articleDto.isPush());
        entity.setStatus(articleDto.status());
        entity.setPublishedAt(articleDto.publishedAt() == null ? LocalDate.now() : LocalDate.parse(articleDto.publishedAt()));
        if (entity.getViewCount() == null) entity.setViewCount(0);
        return toArticleDto(articleRepository.save(entity));
    }

    @Transactional
    public ArticleDto toggleArticleStatus(Long articleId) {
        ArticleEntity article = requireArticle(articleId);
        article.setStatus("已发布".equals(article.getStatus()) ? "已下架" : "已发布");
        return toArticleDto(article);
    }

    @Transactional
    public void deleteArticle(Long articleId) {
        articleRepository.delete(requireArticle(articleId));
    }

    @Transactional
    public ArticleDto incrementArticleView(Long articleId) {
        ArticleEntity article = requireArticle(articleId);
        article.setViewCount(article.getViewCount() + 1);
        return toArticleDto(article);
    }

    public ArticleEntity requireArticle(Long articleId) {
        return articleRepository.findById(articleId).orElseThrow(() -> new BusinessException("Article not found"));
    }

    public ArticleDto toArticleDto(ArticleEntity article) {
        return new ArticleDto(
                article.getId(),
                article.getTitle(),
                article.getCategory(),
                article.getCover(),
                article.getSummary(),
                article.getContent(),
                article.getSource(),
                Boolean.TRUE.equals(article.getIsPush()),
                article.getStatus(),
                article.getViewCount(),
                article.getPublishedAt().toString()
        );
    }
}
