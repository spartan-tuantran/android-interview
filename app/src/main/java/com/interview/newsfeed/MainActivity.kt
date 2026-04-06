package com.interview.newsfeed

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.interview.newsfeed.presentation.ui.NewsScreen
import com.interview.newsfeed.ui.theme.NewsFeedTheme
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class NewsFeedApp : Application()

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      NewsFeedTheme {
        NewsScreen()
      }
    }
  }
}
