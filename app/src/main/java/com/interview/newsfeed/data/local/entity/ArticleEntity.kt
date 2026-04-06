package com.interview.newsfeed.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
* Room persistence model.
* Kept separate from the domain [Article] model on purpose —
* DB schema changes should not ripple into the domain layer.
*/
@Entity(tableName = "articles")
data class ArticleEntity(
  @PrimaryKey
  val id: String,          // url used as stable primary key
  val title: String,
  val description: String?,
  val imageUrl: String?,
  val sourceName: String,
  val publishedAt: String,
  val url: String,
  val cachedAt: Long = System.currentTimeMillis(),
)
