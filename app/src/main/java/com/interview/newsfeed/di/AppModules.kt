package com.interview.newsfeed.di

import android.content.Context
import androidx.room.Room
import com.interview.newsfeed.BuildConfig
import com.interview.newsfeed.data.ArticleRepositoryImpl
import com.interview.newsfeed.data.local.NewsDatabase
import com.interview.newsfeed.data.local.dao.ArticleDao
import com.interview.newsfeed.data.remote.api.NewsApiService
import com.interview.newsfeed.domain.repository.ArticleRepository
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

  @Provides
  @Singleton
  fun provideJson(): Json = Json {
    ignoreUnknownKeys = true
    coerceInputValues = true
  }

  @Provides
  @Singleton
  fun provideOkHttpClient(): OkHttpClient {
    return OkHttpClient.Builder()
      .addInterceptor { chain ->
        val request = chain.request().newBuilder()
          .addHeader("X-Api-Key", BuildConfig.NEWS_API_KEY)
          .build()
        chain.proceed(request)
      }
      .addInterceptor(
        HttpLoggingInterceptor().apply {
          level = if (BuildConfig.DEBUG)
            HttpLoggingInterceptor.Level.BODY
          else
            HttpLoggingInterceptor.Level.NONE
        }
      )
      .build()
  }

  @Provides
  @Singleton
  fun provideRetrofit(client: OkHttpClient, json: Json): Retrofit =
    Retrofit.Builder()
      .baseUrl(BuildConfig.NEWS_API_BASE_URL)
      .client(client)
      .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
      .build()

  @Provides
  @Singleton
  fun provideNewsApiService(retrofit: Retrofit): NewsApiService =
    retrofit.create(NewsApiService::class.java)
}

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

  @Provides
  @Singleton
  fun provideDatabase(@ApplicationContext context: Context): NewsDatabase =
    Room.databaseBuilder(context, NewsDatabase::class.java, "news.db")
      .fallbackToDestructiveMigration()
      .build()

  @Provides
  fun provideArticleDao(db: NewsDatabase): ArticleDao = db.articleDao()
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

  // Binds the concrete impl to the interface — ViewModel sees only ArticleRepository
  @Binds
  @Singleton
  abstract fun bindArticleRepository(impl: ArticleRepositoryImpl): ArticleRepository
}
