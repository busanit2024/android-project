import java.util.Properties

plugins {
//  alias(libs.plugins.android.application)
//  alias(libs.plugins.kotlin.android)
  id("com.android.application")
  id("org.jetbrains.kotlin.android")
  id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
  id("com.google.devtools.ksp")
  id("kotlin-parcelize")
  // Add the Google services Gradle plugin
  id("com.google.gms.google-services")
}

val properties = Properties()
properties.load(project.rootProject.file("secrets.properties").inputStream())

android {
  namespace = "com.busanit.searchrestroom"
  compileSdk = 35

  defaultConfig {
    applicationId = "com.busanit.searchrestroom"
    minSdk = 24
    targetSdk = 34
    versionCode = 1
    versionName = "1.0"

    buildConfigField("String", "MAPS_API_KEY", properties.getProperty("MAPS_API_KEY") )
     //Naver
    buildConfigField("String", "CLIENT_ID", properties.getProperty("NAVER_CLIENT_ID"))
    buildConfigField("String", "CLIENT_SECRET", properties.getProperty("NAVER_CLIENT_SECRET"))

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    multiDexEnabled = true // 파이어베이스 인증, 플레이 서비스 인증 라이브러리 추가 및 앱 빌드 시 오류 막기 위해
  }

  buildTypes {
    debug {
      buildConfigField("String", "MAPS_API_KEY", "\"${project.properties["MAPS_API_KEY"]}\"")
      buildConfigField("String", "CLIENT_ID", "\"${project.properties["NAVER_CLIENT_ID"]}\"")
      buildConfigField("String", "CLIENT_SECRET", "\"${project.properties["NAVER_CLIENT_SECRET"]}\"")
    }
    release {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
      buildConfigField("String", "MAPS_API_KEY", "\"${project.properties["MAPS_API_KEY"]}\"")
      // Naver
      buildConfigField("String", "CLIENT_ID", "\"${project.properties["NAVER_CLIENT_ID"]}\"")
      buildConfigField("String", "CLIENT_SECRET", "\"${project.properties["NAVER_CLIENT_SECRET"]}\"")

    }
  }

  buildFeatures {
    buildConfig = true
  }
  composeOptions {
    kotlinCompilerExtensionVersion = "1.4.5"
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }
  kotlinOptions {
    jvmTarget = JavaVersion.VERSION_11.toString()
  }

  viewBinding.isEnabled = true
  dataBinding.isEnabled = true

}

dependencies {

  implementation("androidx.activity:activity-ktx:1.7.0") // ViewModel을 사용하려면 이 KTX 라이브러리가 필요
  implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.0") // ViewModel 및 LiveData 관련 의존성
  implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.0")

  implementation("com.google.android.flexbox:flexbox:3.0.0")
  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.appcompat)
  implementation(libs.material)
  implementation(libs.androidx.activity)
  implementation(libs.androidx.constraintlayout)
  implementation(libs.places)
  implementation(libs.androidx.databinding.runtime)
  implementation(libs.firebase.common.ktx)
  testImplementation(libs.junit)
  androidTestImplementation(libs.androidx.junit)
  androidTestImplementation(libs.androidx.espresso.core)
  implementation("com.google.android.gms:play-services-maps:19.0.0")
  implementation("com.google.android.gms:play-services-location:21.3.0")
  implementation ("com.google.android.libraries.places:places:2.4.0")

  val room_version = "2.6.1"

  implementation("androidx.room:room-runtime:$room_version")
  annotationProcessor("androidx.room:room-compiler:$room_version")
//
  // To use Kotlin Symbol Processing (KSP)
  ksp("androidx.room:room-compiler:$room_version")

  // optional - Kotlin Extensions and Coroutines support for Room
  implementation("androidx.room:room-ktx:$room_version")

  // optional - RxJava2 support for Room
  implementation("androidx.room:room-rxjava2:$room_version")

  // optional - RxJava3 support for Room
  implementation("androidx.room:room-rxjava3:$room_version")

  // optional - Guava support for Room, including Optional and ListenableFuture
  implementation("androidx.room:room-guava:$room_version")

  // optional - Test helpers
  testImplementation("androidx.room:room-testing:$room_version")

  // optional - Paging 3 Integration
  implementation("androidx.room:room-paging:$room_version")

  // Import the Firebase BoM
  implementation(platform("com.google.firebase:firebase-bom:33.5.1"))

  // Add the dependency for the Firebase Authentication library
  implementation("com.google.firebase:firebase-auth-ktx:23.1.0")

  // Also add the dependency for the Google Play services library and specify its version
  implementation("com.google.android.gms:play-services-auth:21.2.0")

  // multidex
  implementation("androidx.multidex:multidex:2.0.1")

  implementation("com.google.firebase:firebase-auth:23.1.0")

  implementation("com.google.firebase:firebase-analytics:22.1.2")

  implementation ("com.kakao.sdk:v2-all:2.20.0") // 전체 모듈 설치, 2.11.0 버전부터 지원
  implementation ("com.kakao.sdk:v2-user:2.20.0") // 카카오 로그인 API 모듈
  implementation ("com.kakao.sdk:v2-share:2.20.0") // 카카오톡 공유 API 모듈
  implementation ("com.kakao.sdk:v2-talk:2.20.0") // 카카오톡 채널, 카카오톡 소셜, 카카오톡 메시지 API 모듈
  implementation ("com.kakao.sdk:v2-friend:2.20.0") // 피커 API 모듈
  implementation ("com.kakao.sdk:v2-navi:2.20.0") // 카카오내비 API 모듈
  implementation ("com.kakao.sdk:v2-cert:2.20.0") // 카카오톡 인증 서비스 API 모듈

  // naver
  implementation(files("libs/oauth-5.10.0.aar"))
  implementation ("org.jetbrains.kotlin:kotlin-stdlib:1.6.21")
  implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.9")
  implementation ("androidx.appcompat:appcompat:1.3.1")
  implementation ("androidx.legacy:legacy-support-core-utils:1.0.0")
  implementation ("androidx.browser:browser:1.4.0")
  implementation ("androidx.constraintlayout:constraintlayout:1.1.3")
  implementation ("androidx.security:security-crypto:1.1.0-alpha06")
  implementation ("androidx.core:core-ktx:1.3.0")
  implementation ("androidx.fragment:fragment-ktx:1.3.6")
  implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.4.0")
  implementation ("com.squareup.retrofit2:retrofit:2.9.0")
  implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
  implementation ("com.squareup.moshi:moshi-kotlin:1.11.0")
  implementation ("com.squareup.okhttp3:logging-interceptor:4.2.1")
  implementation ("com.airbnb.android:lottie:3.1.0")
  implementation ("com.jakewharton.timber:timber:5.0.1") // 최신 버전으로 추가
}

secrets {
  // To add your Maps API key to this project:
  // 1. If the secrets.properties file does not exist, create it in the same folder as the local.properties file.
  // 2. Add this line, where YOUR_API_KEY is your API key:
  //        MAPS_API_KEY=YOUR_API_KEY
  propertiesFileName = "secrets.properties"

  // A properties file containing default secret values. This file can be
  // checked in version control.
  defaultPropertiesFileName = "local.defaults.properties"

  // Configure which keys should be ignored by the plugin by providing regular expressions.
  // "sdk.dir" is ignored by default.
  ignoreList.add("keyToIgnore") // Ignore the key "keyToIgnore"
  ignoreList.add("sdk.*")       // Ignore all keys matching the regexp "sdk.*"
}
