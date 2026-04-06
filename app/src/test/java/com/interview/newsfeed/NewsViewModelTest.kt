package com.interview.newsfeed

import app.cash.turbine.test
import com.interview.newsfeed.domain.model.Article
import com.interview.newsfeed.domain.model.FeedResult
import com.interview.newsfeed.domain.repository.ArticleRepository
import com.interview.newsfeed.presentation.viewmodel.NewsUiState
import com.interview.newsfeed.presentation.viewmodel.NewsViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
* Unit tests for [NewsViewModel].
*
* These tests verify that the mapping from [FeedResult] → [NewsUiState] is correct.
* They run entirely on the JVM — no Android framework required.
*
* All tests should pass once TODO #3 (observeArticles) is correctly implemented.
*/
@OptIn(ExperimentalCoroutinesApi::class)
class NewsViewModelTest {

  // Replaces Dispatchers.Main so viewModelScope.launch runs eagerly in tests
  @get:Rule
  val mainDispatcherRule = MainDispatcherRule()

  private lateinit var fakeRepo: FakeArticleRepository
  private lateinit var viewModel: NewsViewModel

  @Before
  fun setUp() {
    fakeRepo = FakeArticleRepository()
    viewModel = NewsViewModel(fakeRepo)
  }

  @Test
  fun `initial state is Loading before any emission`() = runTest {
    assertEquals(NewsUiState.Loading, viewModel.uiState.value)
  }

  @Test
  fun `non-empty articles with no network error → Success`() = runTest {
    viewModel.uiState.test {
      assertEquals(NewsUiState.Loading, awaitItem())

      fakeRepo.emit(FeedResult(articles = listOf(SAMPLE_ARTICLE), networkFailed = false))

      assertEquals(NewsUiState.Success(listOf(SAMPLE_ARTICLE)), awaitItem())
      cancelAndIgnoreRemainingEvents()
    }
  }

  @Test
  fun `non-empty articles with network error → Offline (show banner)`() = runTest {
    viewModel.uiState.test {
      assertEquals(NewsUiState.Loading, awaitItem())

      fakeRepo.emit(FeedResult(articles = listOf(SAMPLE_ARTICLE), networkFailed = true))

      assertEquals(NewsUiState.Offline(listOf(SAMPLE_ARTICLE)), awaitItem())
      cancelAndIgnoreRemainingEvents()
    }
  }

  @Test
  fun `empty cache with network error → OfflineEmpty`() = runTest {
    viewModel.uiState.test {
      assertEquals(NewsUiState.Loading, awaitItem())

      fakeRepo.emit(FeedResult(articles = emptyList(), networkFailed = true))

      assertEquals(NewsUiState.OfflineEmpty, awaitItem())
      cancelAndIgnoreRemainingEvents()
    }
  }

  @Test
  fun `unexpected exception from flow → Error`() = runTest {
    fakeRepo.shouldThrow = RuntimeException("network exploded")

    viewModel.uiState.test {
      assertEquals(NewsUiState.Loading, awaitItem())
      // Re-create ViewModel so init{} runs after shouldThrow is set
      val vm = NewsViewModel(fakeRepo)
      val errorState = vm.uiState.value
      assert(errorState is NewsUiState.Error) {
        "Expected Error but was $errorState"
      }
      cancelAndIgnoreRemainingEvents()
    }
  }
}

// ---------------------------------------------------------------------------
// Test doubles
// ---------------------------------------------------------------------------

private val SAMPLE_ARTICLE = Article(
  id = "https://example.com/1",
  title = "Test Article",
  description = "A test article",
  imageUrl = null,
  sourceName = "Test Source",
  publishedAt = "2024-01-01T00:00:00Z",
  url = "https://example.com/1",
)

/**
* Fake repository that lets tests push [FeedResult]s through the flow manually.
* Set [shouldThrow] before constructing a [NewsViewModel] to simulate a flow error.
*/
private class FakeArticleRepository : ArticleRepository {

  var shouldThrow: Throwable? = null
  private val _flow = MutableSharedFlow<FeedResult>()

  suspend fun emit(result: FeedResult) = _flow.emit(result)

  override fun getArticles(): Flow<FeedResult> {
    shouldThrow?.let { throw it }
    return _flow
  }

  override suspend fun refresh() {
    // No-op for these tests — covered separately in refresh() tests
  }
}
