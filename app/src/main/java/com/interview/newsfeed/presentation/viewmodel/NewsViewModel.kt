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
   * Collect [repository.getArticles()] and map each [FeedResult]
   * emission to the appropriate [NewsUiState].
   * Unexpected flow exceptions are caught and surfaced as [NewsUiState.Error].
   */
  private fun observeArticles() {
    viewModelScope.launch {
      repository.getArticles().catch { e ->
        _uiState.value = NewsUiState.Error(e.message ?: "Unknown error")
      }.collect { result ->
        _uiState.value = when {
          result.articles.isNotEmpty() && !result.networkFailed -> NewsUiState.Success(result.articles)
          result.articles.isNotEmpty() && result.networkFailed -> NewsUiState.Offline(result.articles)
          result.networkFailed -> NewsUiState.OfflineEmpty
          else -> NewsUiState.Loading // before first emission (empty cache, no network attempt yet)
        }
      }
    }
  }

  /**
   * Pull-to-refresh: set the refreshing flag, call repository, then reset the flag.
   * On failure, keep the current articles visible and show [NewsUiState.Offline]
   * instead of wiping the screen.
   */
  fun refresh() {
    viewModelScope.launch {
      _isRefreshing.value = true
      try {
        repository.refresh()
      } catch (_: Exception) {
        // Keep whatever is currently on screen; just add the offline banner
        val currentArticles = when (val state = _uiState.value) {
          is NewsUiState.Success -> state.articles
          is NewsUiState.Offline -> state.articles
          else -> emptyList()
        }
        _uiState.value = if (currentArticles.isNotEmpty()) {
          NewsUiState.Offline(currentArticles)
        } else {
          NewsUiState.OfflineEmpty
        }
      } finally {
        _isRefreshing.value = false
      }
    }
  }
}
