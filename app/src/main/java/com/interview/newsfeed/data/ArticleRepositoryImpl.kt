package com.interview.newsfeed.data

import com.interview.newsfeed.data.local.dao.ArticleDao
import com.interview.newsfeed.data.local.entity.ArticleEntity
import com.interview.newsfeed.data.remote.api.NewsApiService
import com.interview.newsfeed.data.remote.dto.ArticleDto
import com.interview.newsfeed.domain.model.Article
import com.interview.newsfeed.domain.model.FeedResult
import com.interview.newsfeed.domain.repository.ArticleRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ArticleRepositoryImpl @Inject constructor(
  private val api: NewsApiService,
  private val dao: ArticleDao,
) : ArticleRepository {

  /**
  * TODO (candidate): Return a [Flow] that emits cached articles immediately and
  * concurrently fetches from the network. On network failure, surface the error
  * via [FeedResult.networkFailed] rather than throwing.
  *
  * Hint: consider a flow builder that lets you launch concurrent coroutines.
  */
  override fun getArticles(): Flow<FeedResult> {
    TODO("Implement offline-first getArticles()")
  }

  /**
  * TODO (candidate): Fetch fresh articles from the network, replace the cache,
  * and let any error propagate to the caller.
  */
  override suspend fun refresh() {
    TODO("Implement refresh()")
  }
}

// ---------------------------------------------------------------------------
// Mapping helpers — provided so the candidate doesn't waste time on boilerplate
// ---------------------------------------------------------------------------

fun ArticleDto.toEntity(): ArticleEntity? {
  val safeUrl = url ?: return null    // articles without a URL are useless
  val safeTitle = title ?: return null
  return ArticleEntity(
    id = safeUrl,
    title = safeTitle,
    description = description,
    imageUrl = imageUrl,
    sourceName = source?.name ?: "Unknown",
    publishedAt = publishedAt ?: "",
    url = safeUrl,
  )
}

fun ArticleEntity.toDomain() = Article(
  id = id,
  title = title,
  description = description,
  imageUrl = imageUrl,
  sourceName = sourceName,
  publishedAt = publishedAt,
  url = url,
)
