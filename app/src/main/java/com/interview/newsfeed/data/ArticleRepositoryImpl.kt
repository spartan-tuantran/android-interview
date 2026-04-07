package com.interview.newsfeed.data

import com.interview.newsfeed.data.local.dao.ArticleDao
import com.interview.newsfeed.data.local.entity.ArticleEntity
import com.interview.newsfeed.data.remote.api.NewsApiService
import com.interview.newsfeed.data.remote.dto.ArticleDto
import com.interview.newsfeed.domain.model.Article
import com.interview.newsfeed.domain.model.FeedResult
import com.interview.newsfeed.domain.repository.ArticleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
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
  override fun getArticles(): Flow<FeedResult> = channelFlow {
    val networkFailed = MutableStateFlow(false)

    // Concurrently fetch from network while Room serves the cache
    launch {
      try {
        val entities = api.getTopHeadlines().articles.mapNotNull { it.toEntity() }
        dao.upsertAll(entities)
      } catch (_: Exception) {
        // Surface the failure via FeedResult instead of throwing
        networkFailed.value = true
      }
    }

    // Room is the single source of truth; combine with the network-failure flag
    // so every cache emission carries the correct networkFailed status.
    dao.observeArticles()
      .combine(networkFailed) { entities, failed ->
        FeedResult(
          articles = entities.map { it.toDomain() },
          networkFailed = failed,
        )
      }
      .collect { send(it) }
  }

  /**
  * and let any error propagate to the caller.
  */
  override suspend fun refresh() {
    val entities = api.getTopHeadlines().articles.mapNotNull { it.toEntity() }
    dao.clearAll()
    dao.upsertAll(entities)
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
