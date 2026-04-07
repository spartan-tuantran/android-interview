package com.interview.newsfeed.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.interview.newsfeed.data.local.dao.ArticleDao
import com.interview.newsfeed.data.local.entity.ArticleEntity

@Database(
  entities = [ArticleEntity::class],
  version = 1,
  exportSchema = false,
)

abstract class NewsDatabase : RoomDatabase() {
  abstract fun articleDao(): ArticleDao
}
