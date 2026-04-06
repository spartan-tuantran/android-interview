# News Feed — Interview Starter Project

## Your task (60 minutes)

Build an offline-first news feed. The scaffold is already set up — your job is to
fill in the **four core TODOs** marked in the code, plus optional UI polish.

---

## The four core TODOs

### 1. `ArticleRepositoryImpl.getArticles()` — offline-first data flow
Return a `Flow<FeedResult>` that emits cached data immediately while fetching
from the network concurrently. Surface network failures via `FeedResult.networkFailed`
rather than throwing — the ViewModel decides what to show.

### 2. `ArticleRepositoryImpl.refresh()` — forced network sync
Force a fresh network fetch, clear the old cache, insert the new results.

### 3. `NewsViewModel.observeArticles()` — map data to UI states
Collect the repository flow and map each `FeedResult` to `NewsUiState`:

| Condition | State |
|---|---|
| Waiting for first emit | `Loading` |
| Has articles, `networkFailed = false` | `Success` |
| Has articles, `networkFailed = true` | `Offline` (show banner) |
| No articles, `networkFailed = true` | `OfflineEmpty` |
| Unexpected exception from the flow | `Error` |

### 4. `NewsViewModel.refresh()` — pull-to-refresh
Set `_isRefreshing = true`, call `repository.refresh()`, then reset the flag.
Handle errors gracefully — on failure, keep the current articles visible and
show `Offline` instead of wiping the screen.

---

## Bonus UI TODOs (optional)

These are in `NewsScreen.kt` and are not required to pass the unit tests, but
finishing them shows end-to-end thinking:

- **`ArticleCard`** — build the card layout (title, source, date, thumbnail)
- **`AsyncImage`** — replace the grey placeholder box with a Coil `AsyncImage`
- **`OfflineBanner`** — implement the compact offline strip above the article list

---

## What's already done for you

- Gradle setup (Hilt, Retrofit, Room, Coroutines, Coil, Kotlinx Serialization)
- Room entity (`ArticleEntity`), DAO (`ArticleDao`), and database (`NewsDatabase`)
- Retrofit API interface (`NewsApiService`) and DTOs (`NewsResponseDto`, `ArticleDto`)
- Hilt DI modules — OkHttp client with API key interceptor wired up via `BuildConfig`
- `NewsUiState` sealed interface with all five states
- Compose scaffold — `NewsScreen`, `NewsScreenContent`, and all state branches are wired;
  `ArticleCard` and `OfflineBanner` render placeholder stubs until you fill them in
- Mapping helpers: `ArticleDto.toEntity()` and `ArticleEntity.toDomain()`
- Unit test suite (`NewsViewModelTest`) — all tests pass once TODO #3 is implemented

---

## Setup

1. Get a free API key from https://newsapi.org (takes 30 seconds)
2. Open `app/build.gradle.kts` and replace the `NEWS_API_KEY` value with your key
3. Run the app — you should see loading → articles

> The API key is injected via the OkHttp interceptor in `di/AppModules.kt`,
> not as a query parameter, so it won't appear in request logs.

---

## Evaluation focus

The interviewer is looking at:
1. Do you reach for Room as the source of truth, or do you return the network response directly?
2. Do you use `viewModelScope` and the correct `Dispatcher`?
3. How you handle the "network failed + empty cache" edge case — did you think about it?

Good luck.
