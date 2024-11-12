package com.busanit.searchrestroom

import android.app.Application
import com.google.android.libraries.places.api.Places
import com.kakao.sdk.common.KakaoSdk

class MyApplication : Application() {
  override fun onCreate() {
    super.onCreate()
    if (!Places.isInitialized()) {
      Places.initialize(applicationContext, BuildConfig.MAPS_API_KEY)
    }
    // Kakao SDK 초기화
    KakaoSdk.init(this, "d7727a5bab2cc3b7b496c87da534ffc8")
  }
}