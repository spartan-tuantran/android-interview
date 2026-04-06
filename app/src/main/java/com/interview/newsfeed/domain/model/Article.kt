package com.interview.newsfeed.domain.model

/**
* Pure domain model — no Android or framework dependencies.
* This is what the UI and ViewModel work with.
*/
data class Article(
  val id: String,          // use url as stable ID
  val title: String,
  val description: String?,
  val imageUrl: String?,
  val sourceName: String,
  val publishedAt: String,
  val url: String,
)
