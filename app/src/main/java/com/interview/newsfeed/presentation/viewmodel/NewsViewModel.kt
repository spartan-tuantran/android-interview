package com.interview.newsfeed.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.interview.newsfeed.domain.model.Article
import com.interview.newsfeed.domain.model.FeedResult
import com.interview.newsfeed.domain.repository.ArticleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

// ---------------------------------------------------------------------------
// UI state — sealed interface models every possible screen state explicitly.
// The UI renders exactly what the state dictates; no boolean flags needed.
// ---------------------------------------------------------------------------

sealed interface NewsUiState {
  data object Loading : NewsUiState

  data class Success(val articles: List<Article>) : NewsUiState

  /**
  * Network failed but we have cached data to show.
  * The UI should display articles AND an offline banner.
  */
  data class Offline(val articles: List<Article>) : NewsUiState

  /**
  * Network failed AND cache is empty — nothing to show at all.
  */
  data object OfflineEmpty : NewsUiState

  data class Error(val message: String) : NewsUiState
}

// ---------------------------------------------------------------------------
// ViewModel
// ---------------------------------------------------------------------------

@HiltViewModel
class NewsViewModel @Inject constructor(
  private val repository: ArticleRepository,
) : ViewModel() {

  private val _uiState = MutableStateFlow<NewsUiState>(NewsUiState.Loading)
  val uiState: StateFlow<NewsUiState> = _uiState.asStateFlow()

  private val _isRefreshing = MutableStateFlow(false)
  val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

  init {
    observeArticles()
  }

  /**
  * TODO (candidate): Collect [repository.getArticles()] and map each [FeedResult]
  * emission to the appropriate [NewsUiState].
  */
  private fun observeArticles() {
    TODO("Collect repository.getArticles() and map each FeedResult to NewsUiState")
  }

  /**
  * TODO (candidate): Implement refresh, use pull-to-refresh for triggering?
  */
  fun refresh() {
    TODO("Implement refresh")
  }
}
