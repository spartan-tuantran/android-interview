plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.android)
  alias(libs.plugins.kotlin.compose)
  alias(libs.plugins.kotlin.serialization)
  alias(libs.plugins.ksp)
  alias(libs.plugins.hilt)
}

android {
  namespace = "com.interview.newsfeed"
  compileSdk = 35

  defaultConfig {
    applicationId = "com.interview.newsfeed"
    minSdk = 26
    targetSdk = 35
    versionCode = 1
    versionName = "1.0"

    // Plus
    buildConfigField("String", "NEWS_API_BASE_URL", "\"https://newsapi.org/v2/\"")
    buildConfigField("String", "NEWS_API_KEY", "\"daed8c44889c48afa631c125e694648c\"")
  }

  buildFeatures {
    compose = true
    buildConfig = true
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }
  kotlinOptions {
    jvmTarget = "17"
  }
}

dependencies {
  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation(libs.androidx.lifecycle.viewmodel.compose)
  implementation(libs.androidx.activity.compose)

  // Compose
  implementation(platform(libs.androidx.compose.bom))
  implementation(libs.androidx.ui)
  implementation(libs.androidx.ui.graphics)
  implementation(libs.androidx.ui.tooling.preview)
  implementation(libs.androidx.material3)
  debugImplementation(libs.androidx.ui.tooling)

  // Hilt
  implementation(libs.hilt.android)
  ksp(libs.hilt.compiler)
  implementation(libs.hilt.navigation.compose)

  // Network
  implementation(libs.retrofit)
  implementation(libs.okhttp.logging)
  implementation(libs.retrofit.kotlinx.serialization)
  implementation(libs.kotlinx.serialization.json)

  // Room
  implementation(libs.room.runtime)
  implementation(libs.room.ktx)
  ksp(libs.room.compiler)

  // Coroutines
  implementation(libs.kotlinx.coroutines.android)

  // Material Components (required for XML theme Theme.Material3.DayNight.NoActionBar)
  implementation(libs.material)

  // Coil
  implementation(libs.coil.compose)

  // Unit tests
  testImplementation(libs.junit4)
  testImplementation(libs.kotlinx.coroutines.test)
  testImplementation(libs.turbine)
}
