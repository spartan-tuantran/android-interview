package com.interview.newsfeed.data.remote.api

import com.interview.newsfeed.data.remote.dto.NewsResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApiService {

  /**
  * Fetches top headlines.
  */
  @GET("top-headlines")
  suspend fun getTopHeadlines(
    @Query("country") country: String = "us",
    @Query("pageSize") pageSize: Int = 20,
    @Query("page") page: Int = 1,
  ): NewsResponseDto
}
