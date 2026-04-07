package com.interview.newsfeed.presentation.ui

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.interview.newsfeed.domain.model.Article
import com.interview.newsfeed.presentation.viewmodel.NewsUiState
import com.interview.newsfeed.presentation.viewmodel.NewsViewModel
import com.interview.newsfeed.ui.theme.NewsFeedTheme

// ---------------------------------------------------------------------------
// Entry point — wired to ViewModel via Hilt
// ---------------------------------------------------------------------------

@Composable
fun NewsScreen(
  viewModel: NewsViewModel = hiltViewModel(),
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()

  NewsScreenContent(
    uiState = uiState,
    isRefreshing = isRefreshing,
    onRefresh = viewModel::refresh,
  )
}

// ---------------------------------------------------------------------------
// Stateless content composable — easier to preview and test
// ---------------------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsScreenContent(
  uiState: NewsUiState,
  isRefreshing: Boolean,
  onRefresh: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Scaffold(
    topBar = {
      TopAppBar(title = { Text("Top Headlines") })
    },
    modifier = modifier,
  ) { padding ->
    PullToRefreshBox(
      isRefreshing = isRefreshing,
      onRefresh = onRefresh,
      modifier = Modifier
        .fillMaxSize()
        .padding(padding),
    ) {
      when (val state = uiState) {
        is NewsUiState.Loading -> LoadingState()

        is NewsUiState.Success -> ArticleList(articles = state.articles)

        is NewsUiState.Offline -> {
          // TODO (candidate): Show the article list AND an offline banner.
          // The banner should be non-blocking — articles are still visible.
          Column {
            OfflineBanner()
            ArticleList(articles = state.articles)
          }
        }

        is NewsUiState.OfflineEmpty -> OfflineEmptyState(onRetry = onRefresh)

        is NewsUiState.Error -> ErrorState(
          message = state.message,
          onRetry = onRefresh,
        )
      }
    }
  }
}

// ---------------------------------------------------------------------------
// Sub-composables — implement the TODOs below
// ---------------------------------------------------------------------------

@Composable
private fun ArticleList(
  articles: List<Article>,
  modifier: Modifier = Modifier,
) {
  LazyColumn(
    modifier = modifier.fillMaxSize(),
    contentPadding = PaddingValues(vertical = 8.dp),
  ) {
    items(
      items = articles,
      key = { it.id },   // stable keys prevent recomposition on refresh
    ) { article ->
      ArticleCard(article = article)
      HorizontalDivider()
    }
  }
}

@Composable
private fun ArticleCard(
  article: Article,
  modifier: Modifier = Modifier,
) {
  val context = LocalContext.current
  Row(
    modifier = modifier
      .fillMaxWidth()
      .clickable {
        val intent = Intent(Intent.ACTION_VIEW, article.url.toUri())
        context.startActivity(intent)
      }
      .padding(16.dp),
    horizontalArrangement = Arrangement.spacedBy(12.dp),
    verticalAlignment = Alignment.Top,
  ) {
    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = article.title,
        style = MaterialTheme.typography.bodyMedium,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
      )
      Spacer(Modifier.height(4.dp))
      Text(
        text = "${article.sourceName} · ${article.publishedAt.take(10)}",
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
    }
    // Thumbnail: show AsyncImage when URL is available, grey box as fallback
    Box(
      modifier = Modifier
        .size(72.dp)
        .clip(MaterialTheme.shapes.small)
        .background(MaterialTheme.colorScheme.surfaceVariant),
    ) {
      if (article.imageUrl != null) {
        AsyncImage(
          model = article.imageUrl,
          contentDescription = null,
          modifier = Modifier.fillMaxSize(),
          contentScale = ContentScale.Crop,
        )
      }
    }
  }
}

@Composable
private fun OfflineBanner(modifier: Modifier = Modifier) {
  Surface(
    modifier = modifier.fillMaxWidth(),
    color = MaterialTheme.colorScheme.errorContainer,
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      Icon(
        imageVector = Icons.Default.Info,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.onErrorContainer,
        modifier = Modifier.size(16.dp),
      )
      Text(
        text = "You're offline · showing cached content",
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onErrorContainer,
      )
    }
  }
}

@Composable
private fun LoadingState(modifier: Modifier = Modifier) {
  Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
    CircularProgressIndicator()
  }
}

@Composable
private fun OfflineEmptyState(onRetry: () -> Unit, modifier: Modifier = Modifier) {
  Column(
    modifier = modifier.fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center,
  ) {
    Text("No internet connection", style = MaterialTheme.typography.titleMedium)
    Spacer(Modifier.height(8.dp))
    Text(
      text = "No cached articles available.",
      style = MaterialTheme.typography.bodyMedium,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
    Spacer(Modifier.height(24.dp))
    Button(onClick = onRetry) { Text("Try again") }
  }
}

@Composable
private fun ErrorState(
  message: String,
  onRetry: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Column(
    modifier = modifier.fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center,
  ) {
    Text("Something went wrong", style = MaterialTheme.typography.titleMedium)
    Spacer(Modifier.height(8.dp))
    Text(
      text = message,
      style = MaterialTheme.typography.bodySmall,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
    Spacer(Modifier.height(24.dp))
    Button(onClick = onRetry) { Text("Retry") }
  }
}

// ---------------------------------------------------------------------------
// Previews
// ---------------------------------------------------------------------------

private val previewArticles = listOf(
  Article(
    id = "1",
    title = "Breaking: Jetpack Compose reaches 2.0 with major performance improvements",
    description = "Google announced today that Jetpack Compose 2.0 is now stable.",
    imageUrl = null,
    sourceName = "Android Developers Blog",
    publishedAt = "2026-04-05T09:00:00Z",
    url = "https://example.com/1",
  ),
  Article(
    id = "2",
    title = "Kotlin 2.1 ships with new language features and faster compilation",
    description = "JetBrains releases Kotlin 2.1 with improved type inference.",
    imageUrl = null,
    sourceName = "Kotlin Blog",
    publishedAt = "2026-04-04T14:30:00Z",
    url = "https://example.com/2",
  ),
  Article(
    id = "3",
    title = "Material Design 3 expands expressive component library",
    description = "Google updates Material Design 3 with new adaptive components.",
    imageUrl = null,
    sourceName = "Material Design",
    publishedAt = "2026-04-03T11:00:00Z",
    url = "https://example.com/3",
  ),
)

@Preview(name = "Loading", showBackground = true)
@Composable
private fun PreviewLoading() {
  NewsFeedTheme {
    NewsScreenContent(
      uiState = NewsUiState.Loading,
      isRefreshing = false,
      onRefresh = {},
    )
  }
}

@Preview(name = "Success", showBackground = true)
@Composable
private fun PreviewSuccess() {
  NewsFeedTheme {
    NewsScreenContent(
      uiState = NewsUiState.Success(previewArticles),
      isRefreshing = false,
      onRefresh = {},
    )
  }
}

@Preview(name = "Offline with articles", showBackground = true)
@Composable
private fun PreviewOffline() {
  NewsFeedTheme {
    NewsScreenContent(
      uiState = NewsUiState.Offline(previewArticles),
      isRefreshing = false,
      onRefresh = {},
    )
  }
}

@Preview(name = "Offline empty", showBackground = true)
@Composable
private fun PreviewOfflineEmpty() {
  NewsFeedTheme {
    NewsScreenContent(
      uiState = NewsUiState.OfflineEmpty,
      isRefreshing = false,
      onRefresh = {},
    )
  }
}

@Preview(name = "Error", showBackground = true)
@Composable
private fun PreviewError() {
  NewsFeedTheme {
    NewsScreenContent(
      uiState = NewsUiState.Error("HTTP 500 · Internal Server Error"),
      isRefreshing = false,
      onRefresh = {},
    )
  }
}

@Preview(name = "Refreshing", showBackground = true)
@Composable
private fun PreviewRefreshing() {
  NewsFeedTheme {
    NewsScreenContent(
      uiState = NewsUiState.Success(previewArticles),
      isRefreshing = true,
      onRefresh = {},
    )
  }
}
