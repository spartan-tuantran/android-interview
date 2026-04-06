package com.interview.newsfeed.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.interview.newsfeed.data.local.entity.ArticleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ArticleDao {

  /** Observe all cached articles ordered by publish date. */
  @Query("SELECT * FROM articles ORDER BY publishedAt DESC")
  fun observeArticles(): Flow<List<ArticleEntity>>

  /** Insert or replace on primary key conflict. */
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun upsertAll(articles: List<ArticleEntity>)

  /** Wipe the entire cache. */
  @Query("DELETE FROM articles")
  suspend fun clearAll()
}
