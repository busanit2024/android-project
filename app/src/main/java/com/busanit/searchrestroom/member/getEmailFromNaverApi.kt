package com.busanit.searchrestroom.member

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

suspend fun getEmailFromNaverApi(accessToken: String): String? {
    return withContext(Dispatchers.IO) {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://openapi.naver.com/v1/nid/me")
            .addHeader("Authorization", "Bearer $accessToken")
            .build()

        val response = client.newCall(request).execute()
        if (response.isSuccessful) {
            val responseBody = response.body?.string()
            responseBody?.let {
                val jsonObject = JSONObject(it)
                val responseObj = jsonObject.getJSONObject("response")
                return@withContext responseObj.getString("email")
            }
        } else {
            null
        }
    }
}