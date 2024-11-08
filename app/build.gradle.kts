import java.util.Properties

plugins {
//  alias(libs.plugins.android.application)
//  alias(libs.plugins.kotlin.android)
  id("com.android.application")
  id("org.jetbrains.kotlin.android")
  id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
  id("com.google.devtools.ksp")
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

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  buildTypes {
    debug {
      buildConfigField("String", "MAPS_API_KEY", "\"${project.properties["MAPS_API_KEY"]}\"")
    }
    release {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
      buildConfigField("String", "MAPS_API_KEY", "\"${project.properties["MAPS_API_KEY"]}\"")
    }
  }

  buildFeatures {
    buildConfig = true
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }
  kotlinOptions {
    jvmTarget = "11"
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
