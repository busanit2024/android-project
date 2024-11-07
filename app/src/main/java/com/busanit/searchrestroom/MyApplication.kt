package com.busanit.searchrestroom

import android.app.Application


import com.google.android.libraries.places.api.Places

class MyApplication : Application() {
  override fun onCreate() {
    super.onCreate()
    if (!Places.isInitialized()) {
      Places.initialize(applicationContext, BuildConfig.MAPS_API_KEY)
    }
  }
}