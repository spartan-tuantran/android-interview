package com.interview.newsfeed.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NewsResponseDto(
  val status: String,
  val totalResults: Int = 0,
  val articles: List<ArticleDto> = emptyList(),
)

@Serializable
data class ArticleDto(
  val title: String?,
  val description: String?,
  @SerialName("urlToImage") val imageUrl: String?,
  val url: String?,
  val publishedAt: String?,
  val source: SourceDto?,
)

@Serializable
data class SourceDto(
  val id: String?,
  val name: String?,
)
