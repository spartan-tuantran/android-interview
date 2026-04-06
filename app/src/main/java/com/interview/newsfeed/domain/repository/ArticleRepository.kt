package com.interview.newsfeed.domain.repository

import com.interview.newsfeed.domain.model.FeedResult
import kotlinx.coroutines.flow.Flow

/**
* Contract for the data layer. The ViewModel depends only on this interface,
* never on a concrete implementation — enabling easy testing with fakes.
*
* Design intent:
*  - Returns a Flow so the UI reacts to DB changes automatically.
*  - The implementation decides when to hit the network vs serve cache.
*  - Network errors do NOT terminate the flow; they are carried inside [FeedResult]
*    so the ViewModel can decide which state to show without a try/catch here.
*/
interface ArticleRepository {

  /**
  * Returns a stream that emits the current cache alongside the outcome of the
  * latest network attempt. Room is the single source of truth.
  *
  * TODO (candidate): Implement this in ArticleRepositoryImpl
  */
  fun getArticles(): Flow<FeedResult>

  /**
  * Forces a network refresh, ignoring cache freshness.
  * Called on pull-to-refresh.
  *
  * TODO (candidate): Implement this in ArticleRepositoryImpl
  */
  suspend fun refresh()
}
