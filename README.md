# News Feed — Interview Starter Project

## Your task (60 minutes)

Build an offline-first news feed. The scaffold is already set up — your job is to
fill in the **four TODOs** marked in the code.

---

## The four TODOs

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

## What's already done for you

- Gradle setup (Hilt, Retrofit, Room, Coroutines, Coil)
- Room entity, DAO, and database
- Retrofit API interface and DTOs
- Hilt DI modules (OkHttp interceptor needs the API key — see `AppModules.kt`)
- `NewsUiState` sealed interface with all five states
- Full Compose UI — all states are rendered, you just need to feed them data
- Mapping helpers: `ArticleDto.toEntity()` and `ArticleEntity.toDomain()`

---

## Setup

1. Get a free API key from https://newsapi.org (takes 30 seconds)
2. Put it in `app/build.gradle.kts` → `buildConfigField NEWS_API_KEY`
3. Run the app — you should see loading → articles

---

## Evaluation focus

The interviewer is looking at:
1. Do you reach for Room as the source of truth, or do you return the network response directly?
2. Do you use `viewModelScope` and the correct `Dispatcher`?
3. How you handle the "network failed + empty cache" edge case — did you think about it?

Good luck.
