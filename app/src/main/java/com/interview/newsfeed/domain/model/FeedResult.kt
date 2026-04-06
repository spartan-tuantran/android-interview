package com.interview.newsfeed.domain.model

/**
* Bundles the cached article list with the outcome of the last network attempt
* into a single emission so the ViewModel can distinguish all four visible states
* in one collect block — no extra channels or shared flows required.
*
* Mapping guide (used in TODO #3 — NewsViewModel):
*
*   articles.isNotEmpty() && !networkFailed  →  Success
*   articles.isNotEmpty() &&  networkFailed  →  Offline  (show banner)
*   articles.isEmpty()    &&  networkFailed  →  OfflineEmpty
*   (before first emission)                  →  Loading  (initial state)
*/
data class FeedResult(
  val articles: List<Article>,
  val networkFailed: Boolean = false,
)
